package com.ibizabroker.bibliotheque.controller;

import com.ibizabroker.bibliotheque.dao.RoleRepository;
import com.ibizabroker.bibliotheque.dao.ReservationRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserManagementSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUtil jwtUtil;

    private Users administrateur;
    private Users adherent;

    @BeforeEach
    void preparerLesComptes() {
        reservationRepository.deleteAll();
        usersRepository.deleteAll();
        administrateur = creerUtilisateur("ADMIN_TEST", "ADMINISTRATEUR");
        adherent = creerUtilisateur("ADHERENT_TEST", "ADHERENT");
    }

    @Test
    void doitRefuserLaCreationDeCompteSansJeton() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nouveauCompteJson("COMPTE_SANS_JETON", "ADHERENT")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void doitRefuserLaCreationDeCompteParUnAdherent() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(adherent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nouveauCompteJson("COMPTE_INTERDIT", "ADHERENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void administrateurCreeBibliothecaireEncodeEtAuthentifieSonMotDePasse() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, bearer(administrateur))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nouveauCompteJson("TEST_LIBRARIAN_01", "BIBLIOTHECAIRE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("TEST_LIBRARIAN_01"))
                .andExpect(jsonPath("$.role[0].roleName").value("BIBLIOTHECAIRE"))
                .andExpect(jsonPath("$.password").doesNotExist());

        Users bibliothecaire = usersRepository.findByUsername("TEST_LIBRARIAN_01").orElseThrow();
        assertNotEquals("TestPwd123!", bibliothecaire.getPassword());
        assertTrue(passwordEncoder.matches("TestPwd123!", bibliothecaire.getPassword()));

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"TEST_LIBRARIAN_01\",\"password\":\"TestPwd123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty())
                .andExpect(jsonPath("$.user.role[0].roleName").value("BIBLIOTHECAIRE"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    private Users creerUtilisateur(String username, String roleName) {
        Role role = roleRepository.findFirstByRoleName(roleName).orElseThrow();
        Users user = new Users();
        user.setName(username);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("TestPwd123!"));
        user.setRole(Collections.singleton(role));
        return usersRepository.save(user);
    }

    private String nouveauCompteJson(String username, String role) {
        return "{\"name\":\"Compte de test\",\"username\":\"" + username
                + "\",\"password\":\"TestPwd123!\",\"role\":[{\"roleName\":\"" + role + "\"}]}";
    }

    private String bearer(Users utilisateur) {
        return "Bearer " + jwtUtil.generateToken(jwtService.loadUserByUsername(utilisateur.getUsername()));
    }
}
