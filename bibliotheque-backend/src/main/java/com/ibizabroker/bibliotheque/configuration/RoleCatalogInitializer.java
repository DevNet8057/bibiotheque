package com.ibizabroker.bibliotheque.configuration;

import com.ibizabroker.bibliotheque.dao.RoleRepository;
import com.ibizabroker.bibliotheque.dao.UsersRepository;
import com.ibizabroker.bibliotheque.entity.Role;
import com.ibizabroker.bibliotheque.entity.Users;
import com.ibizabroker.bibliotheque.security.RoleNames;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class RoleCatalogInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsersRepository usersRepository;

    public RoleCatalogInitializer(RoleRepository roleRepository, UsersRepository usersRepository) {
        this.roleRepository = roleRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role administrateur = role(RoleNames.ADMINISTRATEUR);
        Role bibliothecaire = role(RoleNames.BIBLIOTHECAIRE);
        Role adherent = role(RoleNames.ADHERENT);

        for (Users user : usersRepository.findAll()) {
            Set<Role> migratedRoles = new LinkedHashSet<>();
            if (user.getRole() != null) {
                for (Role legacyRole : user.getRole()) {
                    String canonicalRole = RoleNames.canonicalize(legacyRole.getRoleName());
                    if (RoleNames.ADMINISTRATEUR.equals(canonicalRole)) {
                        migratedRoles.add(administrateur);
                    } else if (RoleNames.BIBLIOTHECAIRE.equals(canonicalRole)) {
                        migratedRoles.add(bibliothecaire);
                    } else if (RoleNames.ADHERENT.equals(canonicalRole)) {
                        migratedRoles.add(adherent);
                    }
                }
            }
            if (migratedRoles.isEmpty()) {
                migratedRoles.add(adherent);
            }
            user.setRole(migratedRoles);
        }
    }

    private Role role(String roleName) {
        return roleRepository.findFirstByRoleName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setRoleName(roleName);
            return roleRepository.save(role);
        });
    }
}
