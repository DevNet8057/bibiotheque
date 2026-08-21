package com.ibizabroker.bibliotheque.service;

import com.ibizabroker.bibliotheque.dao.ReservationRepository;
import com.ibizabroker.bibliotheque.entity.Reservation;
import com.ibizabroker.bibliotheque.entity.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ReservationScheduler {

    @Autowired
    private ReservationRepository reservationRepository;

    private static final List<ReservationStatus> STATUTS_EXPIRABLES = Arrays.asList(
            ReservationStatus.EN_ATTENTE, ReservationStatus.DISPONIBLE);

    /**
     * Vérifie toutes les 60 secondes les réservations dont la date d'expiration est dépassée
     * et passe leur statut à EXPIREE automatiquement.
     */
    @Scheduled(fixedRate = 60000)
    public void expirerReservations() {
        List<Reservation> expirees = reservationRepository
                .findByStatutInAndDateExpirationBefore(STATUTS_EXPIRABLES, new Date());

        for (Reservation reservation : expirees) {
            reservation.setStatut(ReservationStatus.EXPIREE);
            reservationRepository.save(reservation);
        }
    }
}
