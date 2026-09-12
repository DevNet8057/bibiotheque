# Test fonctionnel complet — Bibliothèque

## Environnement

- Vérification le 12 septembre 2026 : PostgreSQL Docker (`5433`), Spring Boot/JWT (`8080`) et Angular 14 (`4200`) démarrés réellement.
- Figma non fourni : Benilde NJEUTCHOU a demandé de conserver la direction artistique existante. Les composants, classes CSS et structure visuelle déjà en place ont été réutilisés.
- Limite documentée : l’automatisation du navigateur échoue dans cet environnement avant l’ouverture d’un onglet. Les scénarios ci-dessous ont été exécutés contre les services démarrés, mais les clics, captures UI et vérifications responsive ne sont pas déclarés testés.

## Comptes utilisés

Les comptes temporaires sont conservés pour la démonstration.

| Compte | Rôle | Identifiant | Mot de passe | Usage |
|---|---|---|---|---|
| Administrateur de test | ADMINISTRATEUR | `TEST_ADMIN_01` | `TestPwd123!` | Amorçage et création des comptes |
| Bibliothécaire de test | BIBLIOTHECAIRE | `TEST_LIBRARIAN_01` | `TestPwd123!` | Livres, réservations, permissions |
| Adhérent A | ADHERENT | `TEST_USER_01` | `TestPwd123!` | Propriété et usurpation |
| Adhérent B | ADHERENT | `TEST_USER_02` | `TestPwd123!` | Ressource d’un autre adhérent |

## Modules testés

- [ ] Tableau de bord : non manipulé dans un navigateur, voir la limite d’automatisation.
- [x] Livres : création par BIBLIOTHECAIRE sur l’API réelle.
- [x] Utilisateurs : création par ADMINISTRATEUR, rôles et reconnexion réels.
- [x] Emprunts et retours : flux réel, contrôle de propriété et stock vérifiés.
- [x] Réservations : création, filtrage, usurpation et restrictions de rôle réels.

## Utilisateurs et rôles

- [x] Migration des rôles historiques `Admin`/`User` vers `ADMINISTRATEUR`/`ADHERENT`, avec catalogue `BIBLIOTHECAIRE`.
- [x] Écran Angular conservant la DA existante, avec un sélecteur Administrateur / Bibliothécaire / Adhérent.
- [x] `POST /admin/users` : ADMINISTRATEUR crée BIBLIOTHECAIRE et ADHERENT (`201`).
- [x] Connexion réelle des comptes créés : autorités retournées `ADMINISTRATEUR`, `BIBLIOTHECAIRE` et `ADHERENT`.
- [x] BIBLIOTHECAIRE ne peut pas créer de compte (`403`).
- [x] BCrypt et absence de mot de passe dans les réponses contrôlés par test d’intégration.

## Livres, emprunts et retours

- [x] BIBLIOTHECAIRE crée des livres de test sur l’API réelle.
- [x] ADHERENT A a tenté un emprunt avec l’identifiant de B dans le body : l’emprunt est créé pour A, jamais pour B.
- [x] A consulte ses emprunts (`200`) et est refusé sur ceux de B (`403`).
- [x] Retour réel validé (`200`) ; le nombre d’exemplaires est redevenu `1`.
- [x] Un double retour est refusé par conflit métier (`409`).

## Réservations et sécurité

- [x] RS-01 : `GET /api/reservations` sans jeton retourne `401` sur l’API démarrée.
- [x] RS-02 : actions interdites à un ADHERENT retournent `403`.
- [x] RS-03 : A ne consulte pas la réservation de B (`403`).
- [x] RS-04 : A envoie l’`adherentId` de B mais la réservation est créée pour A.
- [x] RS-05 : A ne voit que sa liste (`1` réservation dans ce scénario).
- [x] BIBLIOTHECAIRE liste les utilisateurs nécessaires aux réservations et supprime une réservation (`204`).
- [x] Prévol CORS `OPTIONS /admin/users` : `200`, avec `Access-Control-Allow-Origin: http://localhost:4200`.

## Responsive

- [ ] 1440 px
- [ ] 1024 px
- [ ] 768 px
- [ ] 390 px

Non testé : le navigateur n’était pas pilotable. Aucun résultat responsive n’est inventé.

## Tests automatisés

- [x] `bibliotheque-backend\\mvnw.cmd test` : **24 tests**, 0 échec, 0 erreur, 0 ignoré — `BUILD SUCCESS`.
- [x] `npm run build` : compilation Angular réussie.
- [!] Trois avertissements non bloquants de budget CSS restent sur `header`, `home` et `reservation-page`.

## Bugs rencontrés et corrections effectuées

- [x] Rôles historiques ambigus : centralisation et migration vers trois rôles canoniques.
- [x] `JwtRequest` ne reconnaissait pas les propriétés JSON `username`/`password` : setters corrigés.
- [x] `CascadeType.ALL` entre `Users` et `Role` pouvait réinsérer un rôle existant : cascade retiré.
- [x] Angular appelait `/api/borrow` alors que le backend déclarait `/borrow` : URL alignée et flux sécurisé.
- [x] Une erreur TypeScript stricte dans `SimpleChanges` a été détectée par le build et corrigée.
- [!] Intégration navigateur locale indisponible : tests UI et responsive restent à faire avec un navigateur pilotable.

## Tests de régression

- [x] Réservations : 21 tests existants conservés et réussis.
- [x] Gestion utilisateur : 3 tests d’intégration ajoutés et réussis.
- [x] Compilation Angular après corrections.

## Comptes temporaires créés

- [x] `TEST_ADMIN_01`, `TEST_LIBRARIAN_01`, `TEST_USER_01`, `TEST_USER_02`.

## Comptes supprimés

- [ ] Aucun : ils sont conservés pour la démonstration.

## Reste à faire

- [ ] Manipuler visuellement l’interface et vérifier le responsive sur 1440, 1024, 768 et 390 px dès qu’un navigateur pilotable est disponible.
- [ ] Vérifier visuellement le tableau de bord, ses états vides et ses statistiques.
