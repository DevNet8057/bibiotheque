package com.ibizabroker.bibliotheque.dao;

import com.ibizabroker.bibliotheque.entity.Reservation;
import com.ibizabroker.bibliotheque.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByStatut(ReservationStatus statut);

    List<Reservation> findByAdherent_UserId(Integer userId);

    List<Reservation> findByAdherent_UserIdAndStatutIn(Integer userId, List<ReservationStatus> statuts);

    List<Reservation> findByLivre_BookIdAndAdherent_UserIdAndStatutIn(Integer bookId, Integer userId, List<ReservationStatus> statuts);

    long countByAdherent_UserIdAndStatutIn(Integer userId, List<ReservationStatus> statuts);

    List<Reservation> findByStatutInAndDateExpirationBefore(List<ReservationStatus> statuts, Date date);
}
