package com.ibizabroker.bibliotheque.controller;

import com.ibizabroker.bibliotheque.dao.BooksRepository;
import com.ibizabroker.bibliotheque.dao.ReservationRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
import com.ibizabroker.bibliotheque.entity.Books;
import com.ibizabroker.bibliotheque.entity.Reservation;
import com.ibizabroker.bibliotheque.entity.ReservationStatus;
import com.ibizabroker.bibliotheque.entity.Role;
import com.ibizabroker.bibliotheque.entity.Users;
import com.ibizabroker.bibliotheque.service.JwtService;
import com.ibizabroker.bibliotheque.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtil jwtUtil;

    private Users adherentA;
    private Users adherentB;
    private Users bibliothecaire;
    private Books livreIndisponible;
    private Reservation reservationAdherentB;

    @BeforeEach
    void preparerDonneesDeDemonstration() {
        reservationRepository.deleteAll();
        booksRepository.deleteAll();
        usersRepository.deleteAll();

        adherentA = creerUtilisateur("ADHERENT_A", "ADHERENT");
        adherentB = creerUtilisateur("ADHERENT_B", "ADHERENT");
        bibliothecaire = creerUtilisateur("BIBLIOTHECAIRE", "BIBLIOTHECAIRE");

        livreIndisponible = new Books();
        livreIndisponible.setBookName("Livre indisponible");
        livreIndisponible.setBookAuthor("Auteur test");
        livreIndisponible.setBookGenre("Test");
        livreIndisponible.setNoOfCopies(0);
        livreIndisponible = booksRepository.save(livreIndisponible);

        creerReservation(adherentA);
        reservationAdherentB = creerReservation(adherentB);
    }

    @Test
    void doitRenvoyer401QuandListeDesReservationsEstAppeleeSansToken() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void doitRenvoyer401QuandListeDesReservationsEstAppeleeAvecUnTokenInvalide() throws Exception {
        mockMvc.perform(get("/api/reservations").header(HttpHeaders.AUTHORIZATION, "Bearer invalide"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void doitRenvoyerUniquementLesReservationsDeLAdherentAuthentifie() throws Exception {
        mockMvc.perform(get("/api/reservations").header(HttpHeaders.AUTHORIZATION, bearer(adherentA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].adherentId").value(adherentA.getUserId()));
    }

    @Test
    void doitRenvoyer403QuandAdherentAccedeReservationDUnAutreAdherent() throws Exception {
        mockMvc.perform(get("/api/reservations/{id}", reservationAdherentB.getReservationId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adherentA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitRenvoyer403QuandAdherentTenteDeSupprimerReservation() throws Exception {
        mockMvc.perform(delete("/api/reservations/{id}", reservationAdherentB.getReservationId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adherentA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitRenvoyer403QuandAdherentTenteDAnnulerReservationDUnAutreAdherent() throws Exception {
        mockMvc.perform(patch("/api/reservations/{id}/annuler", reservationAdherentB.getReservationId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(adherentA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void doitCreerReservationAuNomDeLAdherentAuthentifieMalgreUnAdherentIdUsurpe() throws Exception {
        Books autreLivreIndisponible = new Books();
        autreLivreIndisponible.setBookName("Autre livre indisponible");
        autreLivreIndisponible.setBookAuthor("Auteur test");
        autreLivreIndisponible.setBookGenre("Test");
        autreLivreIndisponible.setNoOfCopies(0);
        autreLivreIndisponible = booksRepository.save(autreLivreIndisponible);

        String body = String.format("{\"livreId\":%d,\"adherentId\":%d}",
                autreLivreIndisponible.getBookId(), adherentB.getUserId());

        mockMvc.perform(post("/api/reservations")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adherentA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adherentId").value(adherentA.getUserId()));
    }

    @Test
    void doitAutoriserBibliothecaireAListerToutesLesReservations() throws Exception {
        mockMvc.perform(get("/api/reservations").header(HttpHeaders.AUTHORIZATION, bearer(bibliothecaire)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void doitAutoriserBibliothecaireASupprimerReservation() throws Exception {
        mockMvc.perform(delete("/api/reservations/{id}", reservationAdherentB.getReservationId())
                        .header(HttpHeaders.AUTHORIZATION, bearer(bibliothecaire)))
                .andExpect(status().isNoContent());
    }

    private Users creerUtilisateur(String username, String roleName) {
        Role role = new Role();
        role.setRoleName(roleName);

        Users utilisateur = new Users();
        utilisateur.setUsername(username);
        utilisateur.setName(username);
        utilisateur.setPassword("mot-de-passe-de-test");
        utilisateur.setRole(Collections.singleton(role));
        return usersRepository.save(utilisateur);
    }

    private Reservation creerReservation(Users adherent) {
        Reservation reservation = new Reservation();
        reservation.setLivre(livreIndisponible);
        reservation.setAdherent(adherent);
        reservation.setDateReservation(new Date());
        reservation.setDateExpiration(new Date());
        reservation.setStatut(ReservationStatus.EN_ATTENTE);
        return reservationRepository.save(reservation);
    }

    private String bearer(Users utilisateur) {
        return "Bearer " + jwtUtil.generateToken(jwtService.loadUserByUsername(utilisateur.getUsername()));
    }
}
