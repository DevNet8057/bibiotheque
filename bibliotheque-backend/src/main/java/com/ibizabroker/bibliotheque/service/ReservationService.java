package com.ibizabroker.bibliotheque.service;

import com.ibizabroker.bibliotheque.dao.BooksRepository;
import com.ibizabroker.bibliotheque.dao.ReservationRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
import com.ibizabroker.bibliotheque.entity.*;
import com.ibizabroker.bibliotheque.exceptions.ConflictException;
import com.ibizabroker.bibliotheque.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final int MAX_RESERVATIONS_ACTIVES = 3;
    private static final int DUREE_EXPIRATION_JOURS = 7;
    private static final List<ReservationStatus> STATUTS_ACTIFS = Arrays.asList(
            ReservationStatus.EN_ATTENTE, ReservationStatus.DISPONIBLE);
    private static final List<ReservationStatus> STATUTS_ANNULABLES = Arrays.asList(
            ReservationStatus.EN_ATTENTE, ReservationStatus.DISPONIBLE);
    private static final List<ReservationStatus> STATUTS_DEFINITIFS = Arrays.asList(
            ReservationStatus.ANNULEE, ReservationStatus.EXPIREE, ReservationStatus.HONOREE);

    /**
     * Créer une réservation (POST /api/reservations)
     * RG-01 : On ne peut réserver qu'un livre indisponible
     * RG-02 : Un adhérent ne peut avoir qu'une seule réservation active sur un même livre
     * RG-03 : Un adhérent ne peut pas dépasser 3 réservations actives simultanées
     * RG-04 : dateExpiration = dateReservation + 7 jours
     */
    public ReservationResponse creerReservation(ReservationRequest request, Authentication authentication) {
        if (request == null) {
            throw new IllegalArgumentException("La requête de réservation est obligatoire.");
        }
        if (request.getLivreId() == null) {
            throw new IllegalArgumentException("Le champ 'livreId' est obligatoire.");
        }
        Users utilisateurConnecte = getUtilisateurConnecte(authentication);
        Integer adherentId = determinerAdherentId(request.getAdherentId(), utilisateurConnecte, authentication);
        if (adherentId == null) {
            throw new IllegalArgumentException("Le champ 'adherentId' est obligatoire.");
        }

        Books livre = booksRepository.findById(request.getLivreId())
                .orElseThrow(() -> new NotFoundException("Livre avec l'id " + request.getLivreId() + " introuvable."));

        Users adherent = usersRepository.findById(adherentId)
                .orElseThrow(() -> new NotFoundException("Adhérent avec l'id " + adherentId + " introuvable."));

        // RG-01 : On ne peut réserver qu'un livre indisponible
        if (livre.getNoOfCopies() > 0) {
            throw new ConflictException("RG-01 : Le livre \"" + livre.getBookName() + "\" est disponible (" + livre.getNoOfCopies() + " exemplaires). Réservation refusée.");
        }

        // RG-02 : Un adhérent ne peut avoir qu'une seule réservation active sur un même livre
        List<Reservation> reservationsExistantes = reservationRepository
                .findByLivre_BookIdAndAdherent_UserIdAndStatutIn(
                        request.getLivreId(), adherentId, STATUTS_ACTIFS);
        if (!reservationsExistantes.isEmpty()) {
            throw new ConflictException("RG-02 : Vous avez déjà une réservation active pour ce livre.");
        }

        // RG-03 : Un adhérent ne peut pas dépasser 3 réservations actives simultanées
        long nbReservationsActives = reservationRepository
                .countByAdherent_UserIdAndStatutIn(adherentId, STATUTS_ACTIFS);
        if (nbReservationsActives >= MAX_RESERVATIONS_ACTIVES) {
            throw new ConflictException("RG-03 : Vous avez déjà " + nbReservationsActives + " réservation(s) active(s). Maximum autorisé : " + MAX_RESERVATIONS_ACTIVES + ".");
        }

        Reservation reservation = new Reservation();
        reservation.setLivre(livre);
        reservation.setAdherent(adherent);

        Date maintenant = new Date();
        reservation.setDateReservation(maintenant);

        // RG-04 : dateExpiration = dateReservation + 7 jours
        Calendar cal = Calendar.getInstance();
        cal.setTime(maintenant);
        cal.add(Calendar.DATE, DUREE_EXPIRATION_JOURS);
        reservation.setDateExpiration(cal.getTime());

        reservation.setStatut(ReservationStatus.EN_ATTENTE);

        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    /**
     * Lister les réservations (GET /api/reservations)
     * Filtrable par statut et par adhérent
     */
    public List<ReservationResponse> listerReservations(ReservationStatus statut, Integer adherentId, Authentication authentication) {
        List<Reservation> reservations;
        Users utilisateurConnecte = getUtilisateurConnecte(authentication);

        if (estBibliothecaire(authentication)) {
            reservations = listerReservationsBibliothecaire(statut, adherentId);
        } else if (estAdherent(authentication)) {
            reservations = statut == null
                    ? reservationRepository.findByAdherent_UserId(utilisateurConnecte.getUserId())
                    : reservationRepository.findByAdherent_UserIdAndStatut(utilisateurConnecte.getUserId(), statut);
        } else {
            throw new AccessDeniedException("Le rôle de l'utilisateur ne permet pas d'accéder aux réservations.");
        }

        return reservations.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private List<Reservation> listerReservationsBibliothecaire(ReservationStatus statut, Integer adherentId) {
        List<Reservation> reservations;
        if (statut != null && adherentId != null) {
            reservations = reservationRepository.findByAdherent_UserIdAndStatut(adherentId, statut);
        } else if (statut != null) {
            reservations = reservationRepository.findByStatut(statut);
        } else if (adherentId != null) {
            reservations = reservationRepository.findByAdherent_UserId(adherentId);
        } else {
            reservations = reservationRepository.findAll();
        }
        return reservations;
    }

    /**
     * Consulter une réservation (GET /api/reservations/{id})
     */
    public ReservationResponse consulterReservation(Integer id, Authentication authentication) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation avec l'id " + id + " introuvable."));
        verifierProprietaireOuBibliothecaire(reservation, authentication);
        return toResponse(reservation);
    }

    /**
     * Annuler une réservation (PATCH /api/reservations/{id}/annuler)
     * RG-05 : Une réservation ne peut être annulée que si son statut est EN_ATTENTE ou DISPONIBLE
     * RG-06 : Une réservation ANNULEE, EXPIREE ou HONOREE ne peut plus changer d'état
     */
    public ReservationResponse annulerReservation(Integer id, Authentication authentication) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation avec l'id " + id + " introuvable."));
        verifierProprietaireOuBibliothecaire(reservation, authentication);

        // RG-06 : Une réservation ANNULEE, EXPIREE ou HONOREE ne peut plus changer d'état
        if (STATUTS_DEFINITIFS.contains(reservation.getStatut())) {
            throw new ConflictException("RG-06 : Une réservation " + reservation.getStatut() + " ne peut plus être modifiée.");
        }

        // RG-05 : Une réservation ne peut être annulée que si son statut est EN_ATTENTE ou DISPONIBLE
        if (!STATUTS_ANNULABLES.contains(reservation.getStatut())) {
            throw new ConflictException("RG-05 : La réservation ne peut être annulée que si elle est EN_ATTENTE ou DISPONIBLE.");
        }

        reservation.setStatut(ReservationStatus.ANNULEE);
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    /**
     * Supprimer une réservation (DELETE /api/reservations/{id})
     */
    public void supprimerReservation(Integer id, Authentication authentication) {
        verifierBibliothecaire(authentication);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation avec l'id " + id + " introuvable."));
        reservationRepository.delete(reservation);
    }

    private Users getUtilisateurConnecte(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Une authentification est requise.");
        }
        return usersRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Utilisateur authentifié introuvable."));
    }

    private Integer determinerAdherentId(Integer adherentIdDemande, Users utilisateurConnecte, Authentication authentication) {
        if (estBibliothecaire(authentication)) {
            return adherentIdDemande;
        }
        if (estAdherent(authentication)) {
            return utilisateurConnecte.getUserId();
        }
        throw new AccessDeniedException("Le rôle de l'utilisateur ne permet pas de créer une réservation.");
    }

    private void verifierProprietaireOuBibliothecaire(Reservation reservation, Authentication authentication) {
        if (estBibliothecaire(authentication)) {
            return;
        }
        Users utilisateurConnecte = getUtilisateurConnecte(authentication);
        if (!estAdherent(authentication)
                || !reservation.getAdherent().getUserId().equals(utilisateurConnecte.getUserId())) {
            throw new AccessDeniedException("Vous ne pouvez accéder qu'à vos propres réservations.");
        }
    }

    private void verifierBibliothecaire(Authentication authentication) {
        if (!estBibliothecaire(authentication)) {
            throw new AccessDeniedException("Cette action est réservée au bibliothécaire.");
        }
    }

    private boolean estAdherent(Authentication authentication) {
        return aRole(authentication, "ROLE_ADHERENT");
    }

    private boolean estBibliothecaire(Authentication authentication) {
        return aRole(authentication, "ROLE_BIBLIOTHECAIRE");
    }

    private boolean aRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getReservationId());
        response.setLivreId(reservation.getLivre().getBookId());
        response.setLivreNom(reservation.getLivre().getBookName());
        response.setAdherentId(reservation.getAdherent().getUserId());
        response.setAdherentNom(reservation.getAdherent().getName());
        response.setDateReservation(reservation.getDateReservation());
        response.setDateExpiration(reservation.getDateExpiration());
        response.setStatut(reservation.getStatut());
        return response;
    }
}
