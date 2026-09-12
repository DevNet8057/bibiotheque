package com.ibizabroker.bibliotheque.security;

import java.util.Locale;
import java.util.Set;

public final class RoleNames {

    public static final String ADMINISTRATEUR = "ADMINISTRATEUR";
    public static final String BIBLIOTHECAIRE = "BIBLIOTHECAIRE";
    public static final String ADHERENT = "ADHERENT";
    public static final Set<String> ALL = Set.of(ADMINISTRATEUR, BIBLIOTHECAIRE, ADHERENT);

    private RoleNames() {
    }

    public static String canonicalize(String roleName) {
        if (roleName == null) {
            return null;
        }
        String normalized = roleName.trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(normalized) || ADMINISTRATEUR.equals(normalized)) {
            return ADMINISTRATEUR;
        }
        if ("USER".equals(normalized) || "UTILISATEUR".equals(normalized) || ADHERENT.equals(normalized)) {
            return ADHERENT;
        }
        if ("BIBLIOTHECAIRE".equals(normalized)) {
            return BIBLIOTHECAIRE;
        }
        return normalized;
    }
}
