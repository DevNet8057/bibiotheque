package com.ibizabroker.bibliotheque.controller;

import com.ibizabroker.bibliotheque.entity.ReservationRequest;
import com.ibizabroker.bibliotheque.entity.ReservationResponse;
import com.ibizabroker.bibliotheque.entity.ReservationStatus;
import com.ibizabroker.bibliotheque.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200/")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * POST /api/reservations — Créer une réservation
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> creerReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.creerReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/reservations — Lister les réservations, filtrable par statut et par adhérent
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> listerReservations(
            @RequestParam(required = false) ReservationStatus statut,
            @RequestParam(required = false) Integer adherentId) {
        List<ReservationResponse> responses = reservationService.listerReservations(statut, adherentId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/reservations/{id} — Consulter une réservation
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> consulterReservation(@PathVariable Integer id) {
        ReservationResponse response = reservationService.consulterReservation(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/reservations/{id}/annuler — Annuler une réservation
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<ReservationResponse> annulerReservation(@PathVariable Integer id) {
        ReservationResponse response = reservationService.annulerReservation(id);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/reservations/{id} — Supprimer une réservation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerReservation(@PathVariable Integer id) {
        reservationService.supprimerReservation(id);
        return ResponseEntity.noContent().build();
    }
}
