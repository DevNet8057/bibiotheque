package com.ibizabroker.bibliotheque.service;

import com.ibizabroker.bibliotheque.dao.RoleRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
import com.ibizabroker.bibliotheque.entity.Role;
import com.ibizabroker.bibliotheque.entity.UserResponse;
import com.ibizabroker.bibliotheque.entity.Users;
import com.ibizabroker.bibliotheque.exceptions.ConflictException;
import com.ibizabroker.bibliotheque.exceptions.NotFoundException;
import com.ibizabroker.bibliotheque.security.RoleNames;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UsersRepository usersRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(Users request) {
        validateCreateRequest(request);
        String username = request.getUsername().trim();
        if (usersRepository.existsByUsername(username)) {
            throw new ConflictException("Cet identifiant est déjà utilisé. Choisissez-en un autre.");
        }

        Users user = new Users();
        user.setName(request.getName().trim());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Collections.singleton(resolveRequestedRole(request)));
        return toResponse(usersRepository.save(user));
    }

    public List<UserResponse> list() {
        return usersRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getById(Integer id) {
        return toResponse(find(id));
    }

    public UserResponse update(Integer id, Users request) {
        if (request == null || isBlank(request.getName()) || isBlank(request.getUsername())) {
            throw new IllegalArgumentException("Le nom et l'identifiant sont obligatoires.");
        }
        Users user = find(id);
        String username = request.getUsername().trim();
        if (!username.equals(user.getUsername()) && usersRepository.existsByUsername(username)) {
            throw new ConflictException("Cet identifiant est déjà utilisé. Choisissez-en un autre.");
        }
        user.setName(request.getName().trim());
        user.setUsername(username);
        user.setRole(Collections.singleton(resolveRequestedRole(request)));
        return toResponse(usersRepository.save(user));
    }

    private void validateCreateRequest(Users request) {
        if (request == null || isBlank(request.getName()) || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Le nom, l'identifiant, le mot de passe et le rôle sont obligatoires.");
        }
        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        resolveRequestedRole(request);
    }

    private Role resolveRequestedRole(Users request) {
        if (request.getRole() == null || request.getRole().size() != 1) {
            throw new IllegalArgumentException("Sélectionnez un seul rôle valide.");
        }
        String roleName = RoleNames.canonicalize(request.getRole().iterator().next().getRoleName());
        if (!RoleNames.ALL.contains(roleName)) {
            throw new IllegalArgumentException("Le rôle demandé n'existe pas.");
        }
        return roleRepository.findFirstByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Le rôle demandé n'existe pas."));
    }

    private Users find(Integer id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur avec l'id " + id + " introuvable."));
    }

    private UserResponse toResponse(Users user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
