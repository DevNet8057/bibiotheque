package com.ibizabroker.bibliotheque.service;

import com.ibizabroker.bibliotheque.dao.BooksRepository;
import com.ibizabroker.bibliotheque.dao.ReservationRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
import com.ibizabroker.bibliotheque.entity.*;
import com.ibizabroker.bibliotheque.exceptions.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceRG03Test {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BooksRepository booksRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Books livreIndisponible;
    private Users adherent;
    private ReservationRequest requestValide;
    private Authentication authenticationAdherent;

    private static final List<ReservationStatus> STATUTS_ACTIFS = Arrays.asList(
            ReservationStatus.EN_ATTENTE, ReservationStatus.DISPONIBLE);

    @BeforeEach
    void setUp() {
        livreIndisponible = new Books();
        livreIndisponible.setBookId(1);
        livreIndisponible.setBookName("Le Petit Prince");
        livreIndisponible.setNoOfCopies(0);

        adherent = new Users();
        adherent.setUserId(10);
        adherent.setName("Jean Dupont");
        adherent.setUsername("adherent-a");

        requestValide = new ReservationRequest();
        requestValide.setLivreId(1);
        requestValide.setAdherentId(10);

        authenticationAdherent = new UsernamePasswordAuthenticationToken(
                "adherent-a", null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADHERENT")));
        when(usersRepository.findByUsername("adherent-a")).thenReturn(Optional.of(adherent));
    }

    @Test
    void doitAutoriserTroisiemeReservationQuandAdherentEnPossedeDeuxActives() {
        // Arrange : l'adherent a deja 2 reservations actives
        when(booksRepository.findById(1)).thenReturn(Optional.of(livreIndisponible));
        when(usersRepository.findById(10)).thenReturn(Optional.of(adherent));

        // RG-01 pas de reservation existante pour ce livre
        when(reservationRepository.findByLivre_BookIdAndAdherent_UserIdAndStatutIn(1, 10, STATUTS_ACTIFS))
                .thenReturn(Collections.emptyList());

        // RG-03 : 2 reservations actives < 3 max
        when(reservationRepository.countByAdherent_UserIdAndStatutIn(10, STATUTS_ACTIFS))
                .thenReturn(2L);

        Reservation savedReservation = new Reservation();
        savedReservation.setReservationId(1);
        savedReservation.setLivre(livreIndisponible);
        savedReservation.setAdherent(adherent);
        savedReservation.setStatut(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // Act
        ReservationResponse response = reservationService.creerReservation(requestValide, authenticationAdherent);

        // Assert
        assertNotNull(response);
        assertEquals(ReservationStatus.EN_ATTENTE, response.getStatut());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void doitRefuserReservationQuandAdherentEnPossedeDejaTroisActives() {
        // Arrange
        when(booksRepository.findById(1)).thenReturn(Optional.of(livreIndisponible));
        when(usersRepository.findById(10)).thenReturn(Optional.of(adherent));

        when(reservationRepository.findByLivre_BookIdAndAdherent_UserIdAndStatutIn(1, 10, STATUTS_ACTIFS))
                .thenReturn(Collections.emptyList());

        // RG-03 : deja 3 reservations actives
        when(reservationRepository.countByAdherent_UserIdAndStatutIn(10, STATUTS_ACTIFS))
                .thenReturn(3L);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                reservationService.creerReservation(requestValide, authenticationAdherent));

        assertTrue(exception.getMessage().contains("RG-03"));
        assertTrue(exception.getMessage().contains("3"));
        assertTrue(exception.getMessage().contains("Maximum"));

        // Aucune sauvegarde ne doit avoir lieu
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void doitRefuserReservationQuandAdherentDepasseDejaLaLimiteDeTroisActives() {
        // Arrange
        when(booksRepository.findById(1)).thenReturn(Optional.of(livreIndisponible));
        when(usersRepository.findById(10)).thenReturn(Optional.of(adherent));

        when(reservationRepository.findByLivre_BookIdAndAdherent_UserIdAndStatutIn(1, 10, STATUTS_ACTIFS))
                .thenReturn(Collections.emptyList());

        // RG-03 : 5 reservations actives (cas limite depasse)
        when(reservationRepository.countByAdherent_UserIdAndStatutIn(10, STATUTS_ACTIFS))
                .thenReturn(5L);

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () ->
                reservationService.creerReservation(requestValide, authenticationAdherent));

        assertTrue(exception.getMessage().contains("RG-03"));
        assertTrue(exception.getMessage().contains("5"));

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void doitAutoriserReservationQuandAdherentNAucuneReservationActive() {
        // Arrange : premier appel, aucune reservation existante
        when(booksRepository.findById(1)).thenReturn(Optional.of(livreIndisponible));
        when(usersRepository.findById(10)).thenReturn(Optional.of(adherent));

        when(reservationRepository.findByLivre_BookIdAndAdherent_UserIdAndStatutIn(1, 10, STATUTS_ACTIFS))
                .thenReturn(Collections.emptyList());

        // RG-03 : 0 reservations actives
        when(reservationRepository.countByAdherent_UserIdAndStatutIn(10, STATUTS_ACTIFS))
                .thenReturn(0L);

        Reservation savedReservation = new Reservation();
        savedReservation.setReservationId(1);
        savedReservation.setLivre(livreIndisponible);
        savedReservation.setAdherent(adherent);
        savedReservation.setStatut(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // Act
        ReservationResponse response = reservationService.creerReservation(requestValide, authenticationAdherent);

        // Assert
        assertNotNull(response);
        assertEquals(ReservationStatus.EN_ATTENTE, response.getStatut());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void doitAutoriserReservationQuandUneAnnulationRameneLeCompteurADeux() {
        // Simule : l'adherent avait 3 reservations, en a annulee 1, donc 2 restent
        when(booksRepository.findById(1)).thenReturn(Optional.of(livreIndisponible));
        when(usersRepository.findById(10)).thenReturn(Optional.of(adherent));

        when(reservationRepository.findByLivre_BookIdAndAdherent_UserIdAndStatutIn(1, 10, STATUTS_ACTIFS))
                .thenReturn(Collections.emptyList());

        // Apres annulation : 2 reservations actives
        when(reservationRepository.countByAdherent_UserIdAndStatutIn(10, STATUTS_ACTIFS))
                .thenReturn(2L);

        Reservation savedReservation = new Reservation();
        savedReservation.setReservationId(1);
        savedReservation.setLivre(livreIndisponible);
        savedReservation.setAdherent(adherent);
        savedReservation.setStatut(ReservationStatus.EN_ATTENTE);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // Act
        ReservationResponse response = reservationService.creerReservation(requestValide, authenticationAdherent);

        // Assert
        assertNotNull(response);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
