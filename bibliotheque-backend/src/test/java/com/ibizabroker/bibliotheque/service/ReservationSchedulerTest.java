package com.ibizabroker.bibliotheque.service;

import com.ibizabroker.bibliotheque.dao.BooksRepository;
import com.ibizabroker.bibliotheque.dao.ReservationRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
import com.ibizabroker.bibliotheque.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class ReservationSchedulerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BooksRepository booksRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @InjectMocks
    private ReservationService reservationService;

    private Books livre;
    private Users adherent;

    private static final List<ReservationStatus> STATUTS_ACTIFS = Arrays.asList(
            ReservationStatus.EN_ATTENTE, ReservationStatus.DISPONIBLE);

    private static final List<ReservationStatus> STATUTS_EXPIRABLES = Arrays.asList(
            ReservationStatus.EN_ATTENTE, ReservationStatus.DISPONIBLE);

    @BeforeEach
    void setUp() {
        livre = new Books();
        livre.setBookId(1);
        livre.setBookName("Le Petit Prince");
        livre.setNoOfCopies(0);

        adherent = new Users();
        adherent.setUserId(10);
        adherent.setName("Jean Dupont");
    }

    private Reservation createReservation(Integer id, ReservationStatus statut, Date dateExpiration) {
        Reservation r = new Reservation();
        r.setReservationId(id);
        r.setLivre(livre);
        r.setAdherent(adherent);
        r.setStatut(statut);
        r.setDateExpiration(dateExpiration);
        return r;
    }

    @Test
    void expirerReservations_quandDatePassee_doitPasserEnExpireree() {
        // Arrange : 2 réservations avec date d'expiration dépassée
        Date passe = new Date(System.currentTimeMillis() - 86400000); // hier

        Reservation r1 = createReservation(1, ReservationStatus.EN_ATTENTE, passe);
        Reservation r2 = createReservation(2, ReservationStatus.DISPONIBLE, passe);

        when(reservationRepository.findByStatutInAndDateExpirationBefore(eq(STATUTS_EXPIRABLES), any(Date.class)))
                .thenReturn(Arrays.asList(r1, r2));

        // Act
        reservationScheduler.expirerReservations();

        // Assert
        verify(reservationRepository, times(2)).save(any(Reservation.class));
        verify(reservationRepository).save(r1);
        verify(reservationRepository).save(r2);
    }

    @Test
    void expirerReservations_quandAucuneExpiree_doitNeRienFaire() {
        // Arrange : aucune réservation expirée
        when(reservationRepository.findByStatutInAndDateExpirationBefore(eq(STATUTS_EXPIRABLES), any(Date.class)))
                .thenReturn(Arrays.asList());

        // Act
        reservationScheduler.expirerReservations();

        // Assert
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void expirerReservations_neDoitPasToucherLesStatutsDefinitifs() {
        // Arrange : réservations déjà ANNULEE ou EXPIREE ne doivent pas apparaître
        // car la requête filtre uniquement EN_ATTENTE et DISPONIBLE
        when(reservationRepository.findByStatutInAndDateExpirationBefore(eq(STATUTS_EXPIRABLES), any(Date.class)))
                .thenReturn(Arrays.asList());

        // Act
        reservationScheduler.expirerReservations();

        // Assert : aucune sauvegarde car le repo ne retourne que des statuts actifs
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void expirerReservations_unSeulExpirer_doitSauvegarderUnSeul() {
        // Arrange : 1 seule réservation expirée
        Date passe = new Date(System.currentTimeMillis() - 172800000); // il y a 2 jours
        Reservation r1 = createReservation(5, ReservationStatus.EN_ATTENTE, passe);

        when(reservationRepository.findByStatutInAndDateExpirationBefore(eq(STATUTS_EXPIRABLES), any(Date.class)))
                .thenReturn(Arrays.asList(r1));

        // Act
        reservationScheduler.expirerReservations();

        // Assert
        verify(reservationRepository, times(1)).save(r1);
    }

    @Test
    void expirerReservations_reservationAvecDateFuture_doitEtreIgnoree() {
        // Arrange : réservation future ne doit pas apparaître dans les résultats
        // (la requête ne retourne que les dates avant "maintenant")
        Date future = new Date(System.currentTimeMillis() + 86400000); // demain
        Reservation rFuture = createReservation(10, ReservationStatus.EN_ATTENTE, future);

        // Le repo ne la retourne pas car la date est future
        when(reservationRepository.findByStatutInAndDateExpirationBefore(eq(STATUTS_EXPIRABLES), any(Date.class)))
                .thenReturn(Arrays.asList());

        // Act
        reservationScheduler.expirerReservations();

        // Assert : aucune modification
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
