package com.ibizabroker.bibliotheque.controller;

import com.ibizabroker.bibliotheque.entity.ReservationRequest;
import com.ibizabroker.bibliotheque.entity.ReservationResponse;
import com.ibizabroker.bibliotheque.entity.ReservationStatus;
import com.ibizabroker.bibliotheque.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200/")
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Réservation", description = "Module de gestion des réservations de livres")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * POST /api/reservations — Créer une réservation
     */
    @PostMapping
    @Operation(summary = "Créer une réservation", description = "Réserver un livre indisponible. Le client envoie livreId et adherentId uniquement.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Champ livreId ou adherentId manquant"),
            @ApiResponse(responseCode = "404", description = "Livre ou adhérent introuvable"),
            @ApiResponse(responseCode = "409", description = "Règle de gestion violée (RG-01, RG-02, RG-03)")
    })
    @PreAuthorize("hasAnyRole('ADHERENT', 'BIBLIOTHECAIRE')")
    public ResponseEntity<ReservationResponse> creerReservation(@RequestBody ReservationRequest request,
                                                                 Authentication authentication) {
        ReservationResponse response = reservationService.creerReservation(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/reservations — Lister les réservations, filtrable par statut et par adhérent
     */
    @GetMapping
    @Operation(summary = "Lister les réservations", description = "Filtrable par statut et/ou par adhérent")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @PreAuthorize("hasAnyRole('ADHERENT', 'BIBLIOTHECAIRE')")
    public ResponseEntity<List<ReservationResponse>> listerReservations(
            @Parameter(description = "Filtrer par statut") @RequestParam(required = false) ReservationStatus statut,
            @Parameter(description = "Filtrer par ID adhérent") @RequestParam(required = false) Integer adherentId,
            Authentication authentication) {
        List<ReservationResponse> responses = reservationService.listerReservations(statut, adherentId, authentication);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/reservations/{id} — Consulter une réservation
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consulter une réservation", description = "Récupérer les détails d'une réservation par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @PreAuthorize("hasAnyRole('ADHERENT', 'BIBLIOTHECAIRE')")
    public ResponseEntity<ReservationResponse> consulterReservation(@PathVariable Integer id,
                                                                      Authentication authentication) {
        ReservationResponse response = reservationService.consulterReservation(id, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/reservations/{id}/annuler — Annuler une réservation
     */
    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une réservation", description = "Annulation possible uniquement si le statut est EN_ATTENTE ou DISPONIBLE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation annulée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "Règle de gestion violée (RG-05, RG-06)")
    })
    @PreAuthorize("hasAnyRole('ADHERENT', 'BIBLIOTHECAIRE')")
    public ResponseEntity<ReservationResponse> annulerReservation(@PathVariable Integer id,
                                                                    Authentication authentication) {
        ReservationResponse response = reservationService.annulerReservation(id, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/reservations/{id} — Supprimer une réservation
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation", description = "Suppression définitive d'une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @PreAuthorize("hasRole('BIBLIOTHECAIRE')")
    public ResponseEntity<Void> supprimerReservation(@PathVariable Integer id, Authentication authentication) {
        reservationService.supprimerReservation(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
