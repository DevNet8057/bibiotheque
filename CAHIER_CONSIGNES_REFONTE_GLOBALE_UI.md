# CAHIER DE CONSIGNES COMPLET — REFONTE GLOBALE DE L’APPLICATION BIBLIOTHÈQUE

## 1. Mission générale

La mission consiste à **refaire le design de l’ensemble du frontend de l’application Bibliothèque**, et pas uniquement la page Réservations.

Le travail doit être réalisé **écran par écran**, dans le vrai projet Angular existant, sans créer un prototype séparé.

L’objectif est de :

- reprendre toutes les pages existantes ;
- recoder proprement leur interface ;
- respecter les maquettes Figma existantes ;
- conserver toutes les fonctionnalités existantes ;
- respecter les exigences du sujet officiel ;
- améliorer l’expérience utilisateur ;
- gérer correctement les chargements, états vides, succès et erreurs ;
- rendre tout le site responsive ;
- conserver une architecture Angular propre ;
- connecter toutes les interfaces au vrai backend Spring Boot ;
- documenter précisément la progression dans un fichier Markdown.

---

# 2. PÉRIMÈTRE : REFONTE DE TOUT LE SITE

La refonte concerne **tout le site**, pas seulement le module Réservations.

L’IA doit reprendre progressivement tous les écrans existants, notamment lorsqu’ils sont présents dans le projet :

- Tableau de bord ;
- Livres ;
- Adhérents ;
- Réservations ;
- Emprunts ;
- Retours ;
- Rapports ;
- Paramètres ;
- Connexion / authentification ;
- pages d’accès refusé ;
- pages d’erreur ;
- navigation ;
- sidebar ;
- header ;
- formulaires ;
- tableaux ;
- cartes ;
- modales ;
- notifications ;
- loaders ;
- états vides ;
- composants partagés.

## Règle importante

**Ne pas refaire uniquement la page Réservations en laissant les autres pages dans l’ancien design.**

Tout le site doit finir avec une direction artistique cohérente.

---

# 3. DIRECTION ARTISTIQUE GLOBALE

Le rendu attendu doit s’inspirer du design de référence fourni.

Le style général attendu comprend notamment :

- fond bleu nuit / indigo très sombre ;
- interface moderne et premium ;
- sidebar sombre fixe sur desktop ;
- cartes avec grands coins arrondis ;
- bordures discrètes ;
- relief léger ;
- accents violets lumineux ;
- couleurs secondaires vertes, orange, jaunes, bleues selon les informations ;
- icônes modernes ;
- typographie très lisible ;
- titres forts ;
- cartes statistiques ;
- illustrations cartoon / 3D cohérentes avec l’univers de la bibliothèque ;
- espaces généreux ;
- bonne hiérarchie visuelle ;
- navigation claire ;
- éléments interactifs visibles ;
- design légèrement gamifié mais professionnel.

Le rendu doit ressembler à **un seul produit cohérent**, et non à un assemblage de pages indépendantes.

---

# 4. FIGMA EST OBLIGATOIRE POUR CHAQUE ÉCRAN

Pour **chaque écran** à refaire, l’IA doit consulter le Figma correspondant **avant de coder**.

## Procédure obligatoire

Avant de modifier un écran :

1. identifier l’écran à refaire ;
2. se connecter à Figma avec les outils/connecteurs disponibles ;
3. rechercher la frame correspondante ;
4. ouvrir la maquette ;
5. analyser :
   - structure ;
   - couleurs ;
   - typographies ;
   - tailles ;
   - espacements ;
   - boutons ;
   - champs ;
   - tableaux ;
   - cartes ;
   - illustrations ;
   - icônes ;
   - navigation ;
   - responsive si présent ;
   - états visuels si présents ;
6. seulement ensuite commencer le code.

## Si Figma est inaccessible

L’IA ne doit **jamais inventer silencieusement le design**.

Si elle ne peut pas :

- accéder au fichier ;
- trouver la frame ;
- identifier la bonne version ;
- ouvrir le design ;
- obtenir les permissions nécessaires ;

elle doit **s’arrêter pour cet écran et me demander** :

- le lien Figma ;
- le nom de la frame ;
- une capture d’écran ;
- ou l’accès nécessaire.

## Priorité

- **Figma** = référence visuelle.
- **Sujet officiel** = référence fonctionnelle.
- **Backend réel** = référence pour les données et règles métier.
- **Projet Angular existant** = référence architecturale.

Si une fonctionnalité obligatoire n’est pas représentée dans Figma, elle doit quand même être créée, mais dans le même langage visuel.

---

# 5. NE PAS INVENTER UNE NOUVELLE APPLICATION

Le but est de **refaire et recoder le design existant**, pas de remplacer toute l’identité visuelle par une idée arbitraire.

L’IA peut améliorer :

- alignements ;
- responsive ;
- transitions ;
- micro-animations ;
- lisibilité ;
- feedback ;
- cohérence ;
- états manquants ;
- accessibilité.

Mais elle doit rester fidèle au Figma et à la référence visuelle globale.

---

# 6. RESPONSIVE OBLIGATOIRE SUR TOUT LE SITE

Toute l’application doit fonctionner correctement sur :

- 1440 px ;
- 1024 px ;
- 768 px ;
- 390 px.

Chaque page doit être testée à ces largeurs.

## Desktop

- sidebar visible ;
- contenu bien aligné ;
- cartes correctement réparties ;
- tableaux lisibles ;
- espaces cohérents.

## Tablette

- sidebar compacte ou adaptée ;
- cartes réorganisées ;
- tableaux utilisables ;
- aucune zone coupée.

## Mobile

- sidebar transformée en drawer/menu hamburger ou comportement équivalent ;
- cartes en pleine largeur lorsque nécessaire ;
- formulaires utilisables ;
- boutons facilement cliquables ;
- textes lisibles ;
- modales adaptées ;
- messages d’erreur entièrement visibles ;
- filtres utilisables ;
- aucun élément hors écran ;
- aucune barre horizontale incontrôlée.

Pour les grands tableaux :

- utiliser la variante Figma si elle existe ;
- sinon choisir soit un scroll horizontal maîtrisé, soit une transformation en cartes mobiles.

---

# 7. ÉTATS D’INTERFACE OBLIGATOIRES

Tous les écrans qui chargent des données doivent gérer leurs états correctement.

## 7.1 Chargement

Pendant un appel API, afficher un état de chargement clair.

Exemples acceptés :

- skeleton loader ;
- loader animé ;
- animation de livre ;
- petite animation cohérente avec l’univers graphique.

L’utilisateur doit comprendre immédiatement que les données sont en cours de chargement.

### Interdit

- écran figé ;
- tableau vide sans explication ;
- chargement infini ;
- page blanche.

---

## 7.2 Données disponibles

Lorsque l’appel réussit, afficher les vraies données du backend.

---

## 7.3 État vide

Ne jamais afficher simplement une grande zone vide.

Exemples :

> Aucune réservation pour le moment.

> Aucun livre trouvé.

> Aucun adhérent enregistré.

Ajouter si possible une action ou une indication utile.

Exemple :

> Créez votre première réservation pour la voir apparaître ici.

---

## 7.4 État d’erreur

Une erreur doit être expliquée clairement.

Exemple :

> Nous n’arrivons pas à charger les réservations pour le moment. Réessayez dans quelques instants.

Prévoir un bouton :

**Réessayer**

lorsque c’est pertinent.

---

## 7.5 État de succès

Après une action réussie, afficher un feedback clair.

Exemples :

> Réservation créée avec succès.

> Modification enregistrée.

> Réservation annulée avec succès.

Le succès peut être présenté avec un toast ou une notification animée cohérente avec Figma.

---

# 8. MESSAGES D’ERREUR COMPRÉHENSIBLES PAR UN NON-INFORMATICIEN

Cette règle est **obligatoire sur tout le site**.

Les messages destinés aux utilisateurs doivent être compréhensibles par une personne qui ne connaît rien à :

- Angular ;
- Spring Boot ;
- API ;
- HTTP ;
- JWT ;
- bases de données ;
- exceptions ;
- stack traces.

## Un bon message doit répondre à trois questions

1. Que s’est-il passé ?
2. Pourquoi l’action n’a-t-elle pas fonctionné ?
3. Que peut faire l’utilisateur maintenant ?

## Ne jamais afficher directement

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `409 Conflict`
- `500 Internal Server Error`
- `NullPointerException`
- `Failed to fetch`
- stack trace Java
- JSON technique
- chemin de fichier
- numéro de ligne
- nom de classe backend.

Les informations techniques doivent rester dans la console ou les logs développeur.

---

# 9. EXEMPLES DE MESSAGES UTILISATEUR

## 400

Au lieu de :

> Bad Request

Afficher :

> Sélectionnez un livre avant de continuer.

ou :

> Sélectionnez un adhérent avant de créer la réservation.

---

## 401

Au lieu de :

> Unauthorized

Afficher :

> Votre session a expiré. Veuillez vous reconnecter pour continuer.

---

## 403

Au lieu de :

> Forbidden

Afficher :

> Vous n’avez pas l’autorisation d’effectuer cette action.

---

## 404

Au lieu de :

> Not Found

Afficher :

> Ce livre est introuvable. Actualisez la liste puis réessayez.

ou :

> Cet adhérent n’est plus disponible dans le système. Actualisez la page puis réessayez.

---

## 409 — Livre disponible

Afficher :

> Ce livre est actuellement disponible. Vous pouvez l’emprunter directement, il n’est donc pas nécessaire de le réserver.

---

## 409 — Réservation existante

Afficher :

> Vous avez déjà une réservation en cours pour ce livre.

---

## 409 — Quota

Afficher :

> Vous avez atteint la limite de 3 réservations actives. Annulez ou terminez une réservation avant d’en créer une nouvelle.

---

## Serveur indisponible

Afficher :

> Nous n’arrivons pas à contacter le serveur pour le moment. Vérifiez votre connexion ou réessayez dans quelques instants.

Bouton :

**Réessayer**

---

## Erreur inconnue

Seulement lorsqu’aucun cas plus précis n’est disponible :

> Nous n’avons pas pu terminer cette action. Réessayez dans quelques instants.

---

# 10. ANIMATIONS DES MESSAGES ET FEEDBACKS

Les notifications doivent être agréables et cohérentes avec le design.

On peut utiliser :

- fade ;
- slide ;
- scale ;
- bounce léger ;
- apparition progressive ;
- glow discret.

Les erreurs importantes peuvent être accompagnées d’une icône ou illustration.

Mais :

- l’animation ne doit jamais remplacer le texte ;
- le message doit rester lisible ;
- la notification doit fonctionner sur mobile ;
- aucune animation ne doit bloquer l’utilisateur.

---

# 11. ARCHITECTURE ANGULAR OBLIGATOIRE

Tous les appels API doivent suivre :

`Composant → Service Angular → HttpClient → Backend Spring Boot`

## Obligatoire

Les services doivent utiliser Angular `HttpClient`.

Exemples :

- `ReservationService`
- `BooksService`
- `UsersService`

Réutiliser les services déjà présents si leur responsabilité correspond.

## Interdit dans les composants

Ne jamais faire directement :

- `HttpClient.get(...)`
- `HttpClient.post(...)`
- `fetch(...)`
- `axios(...)`

Les composants doivent appeler les services.

---

# 12. RÉUTILISER L’ARCHITECTURE EXISTANTE

Avant d’ajouter quelque chose, inspecter :

- services ;
- composants ;
- modèles ;
- interfaces ;
- routes ;
- guards ;
- interceptors ;
- styles globaux ;
- composants partagés.

Éviter la duplication.

Ne pas réécrire inutilement des composants qui fonctionnent déjà.

---

# 13. AUTHENTIFICATION ET JWT

Si le projet possède déjà :

- JWT ;
- interceptor ;
- guards ;
- gestion des rôles ;
- stockage du token ;

les réutiliser.

Ne pas désactiver la sécurité pour faire fonctionner une page.

Si la session expire, afficher un message utilisateur clair et rediriger correctement vers la connexion lorsque nécessaire.

---

# 14. AUCUNE DONNÉE MÉTIER CODÉE EN DUR

Les données doivent venir du backend.

Cela concerne notamment :

- livres ;
- adhérents ;
- réservations ;
- emprunts ;
- statistiques ;
- informations métier.

Les données présentes dans Figma servent uniquement d’exemple visuel.

---

# 15. EXIGENCES PARTICULIÈRES — MODULE RÉSERVATIONS

L’écran Réservations doit contenir :

1. liste des réservations ;
2. formulaire de création ;
3. annulation.

---

# 16. LISTE DES RÉSERVATIONS

Afficher au minimum :

- titre du livre ;
- nom de l’adhérent ;
- statut ;
- date de réservation ;
- date d’expiration ;
- action.

## Filtres

Prévoir :

- Tous ;
- `EN_ATTENTE` ;
- `DISPONIBLE` ;
- `ANNULEE` ;
- `EXPIREE` ;
- `HONOREE`.

Les filtres doivent réellement agir sur les données.

---

# 17. FORMULAIRE DE CRÉATION

Le formulaire doit contenir :

- sélection Livre ;
- sélection Adhérent.

Ces données viennent des endpoints existants.

## Interdit

Ne pas demander de saisir :

- `livreId`
- `adherentId`

manuellement.

Le bouton reste désactivé tant que les deux champs ne sont pas renseignés.

## Après création réussie

1. afficher le succès ;
2. actualiser les données ;
3. faire apparaître la nouvelle réservation ;
4. réinitialiser le formulaire si nécessaire ;
5. ne pas recharger toute la page.

---

# 18. ANNULATION DE RÉSERVATION

Appel attendu :

`PATCH /api/reservations/{id}/annuler`

L’action d’annulation est disponible uniquement pour :

- `EN_ATTENTE`
- `DISPONIBLE`

Elle ne doit pas être proposée pour :

- `ANNULEE`
- `EXPIREE`
- `HONOREE`

Avant l’annulation :

- demander confirmation dans une modale cohérente avec le design ;
- ne pas utiliser `alert()`.

Après succès :

- mettre à jour l’interface ;
- afficher le succès ;
- ne pas recharger toute la page.

---

# 19. TEST SERVEUR INDISPONIBLE

Test obligatoire :

1. démarrer l’application ;
2. arrêter volontairement le backend ;
3. recharger la page ;
4. vérifier que le loader se termine ;
5. vérifier qu’un message compréhensible apparaît ;
6. vérifier le bouton Réessayer ;
7. redémarrer le backend ;
8. tester Réessayer.

---

# 20. PROCÉDURE DE TRAVAIL OBLIGATOIRE POUR CHAQUE ÉCRAN

## Étape A — Lire la progression

Avant toute nouvelle tâche :

1. ouvrir `PROGRESSION_REFONTE_UI.md` ;
2. lire ce qui est terminé ;
3. lire ce qui reste ;
4. vérifier les problèmes ouverts.

## Étape B — Examiner le projet

Identifier :

- fichiers concernés ;
- composants ;
- services ;
- routes ;
- modèles ;
- dépendances ;
- backend associé.

## Étape C — Consulter Figma

Ouvrir la frame de l’écran.

Si impossible : me demander le Figma ou une capture.

## Étape D — Faire un mini-plan

Avant le code, écrire :

- ce qui sera conservé ;
- ce qui sera refait ;
- fichiers concernés ;
- API utilisées ;
- composants à créer ou réutiliser ;
- comportement responsive prévu.

## Étape E — Implémenter

Coder progressivement.

## Étape F — Tester

Tester :

- fonctionnement ;
- API ;
- loading ;
- données ;
- vide ;
- erreurs ;
- succès ;
- responsive ;
- console navigateur ;
- compilation.

## Étape G — Comparer à Figma

Vérifier :

- structure ;
- proportions ;
- couleurs ;
- espacements ;
- boutons ;
- responsive ;
- rendu final.

## Étape H — Mettre à jour le journal Markdown

Obligatoire avant de passer à l’étape suivante.

---

# 21. JOURNAL DE PROGRESSION OBLIGATOIRE

Créer à la racine du repository :

`PROGRESSION_REFONTE_UI.md`

Ce fichier est la mémoire du projet.

Il doit être mis à jour **après chaque étape importante et après chaque écran**.

Structure recommandée :

```md
# Progression — Refonte UI Bibliothèque

## Objectif général

## Écran actuellement traité

## Référence Figma
- écran :
- frame :
- lien :
- consulté : oui/non
- remarques :

## Ce qui existait avant

## Ce qui vient d’être fait
- [x]

## Tests réellement effectués
- [x]
- [ ]

## Responsive testé
- [ ] 1440
- [ ] 1024
- [ ] 768
- [ ] 390

## Fichiers créés

## Fichiers modifiés

## Services / endpoints utilisés

## Messages d’erreur testés

## Problèmes rencontrés

## Décisions prises

## Ce qui reste à faire
- [ ]

## Prochaine étape
```

## Règles

- ne jamais cocher une tâche qui n’a pas été testée ;
- noter les problèmes réellement rencontrés ;
- noter les fichiers réellement touchés ;
- mettre à jour le fichier avant de passer à une autre page ;
- relire le fichier au début de la prochaine session.

---

# 22. ORDRE CONSEILLÉ POUR LA REFONTE

Procéder progressivement :

1. shell général / layout ;
2. sidebar ;
3. header/navigation ;
4. composants partagés ;
5. Tableau de bord ;
6. Livres ;
7. Adhérents ;
8. Réservations ;
9. Emprunts ;
10. Retours ;
11. Rapports ;
12. Paramètres ;
13. Authentification et écrans système ;
14. responsive global ;
15. états loading/empty/error/success globaux ;
16. tests finaux.

Adapter l’ordre si l’architecture réelle du projet exige une autre séquence.

---

# 23. COMPOSANTS PARTAGÉS À PRIVILÉGIER

Si plusieurs écrans utilisent les mêmes éléments, créer ou réutiliser des composants communs :

- sidebar ;
- header ;
- page title ;
- card ;
- button ;
- badge ;
- table ;
- form field ;
- modal ;
- confirmation dialog ;
- loader ;
- skeleton ;
- empty state ;
- error state ;
- toast ;
- notification.

Ne pas copier-coller le même design dans plusieurs pages si un composant partagé peut être utilisé.

---

# 24. QUALITÉ DES MESSAGES UTILISATEUR

Avant de considérer un écran terminé, se poser cette question :

> Une personne qui ne connaît ni Angular, ni Java, ni HTTP comprend-elle immédiatement ce qui se passe ?

Pour chaque erreur :

- [ ] message en français naturel ;
- [ ] cause compréhensible ;
- [ ] prochaine action claire ;
- [ ] aucun jargon inutile ;
- [ ] aucun code HTTP comme seul message ;
- [ ] pas de stack trace ;
- [ ] message visible sur mobile ;
- [ ] couleur non utilisée comme seul indicateur ;
- [ ] bouton Réessayer si pertinent.

---

# 25. TESTS RESPONSIVE

Chaque écran doit être vérifié à :

## 1440 px
Desktop complet.

## 1024 px
Petit écran / tablette paysage.

## 768 px
Tablette.

## 390 px
Smartphone.

La validation responsive doit être notée dans `PROGRESSION_REFONTE_UI.md`.

---

# 26. TESTS DU MODULE RÉSERVATIONS

## Liste

- chargement ;
- données disponibles ;
- liste vide ;
- filtre ;
- backend indisponible ;
- Réessayer.

## Formulaire

- aucun champ ;
- livre seulement ;
- adhérent seulement ;
- création réussie ;
- 400 ;
- 404 ;
- 409 livre disponible ;
- 409 doublon ;
- 409 quota atteint.

## Annulation

- `EN_ATTENTE` ;
- `DISPONIBLE` ;
- confirmation ;
- succès ;
- erreur 409 ;
- absence de bouton pour les statuts non annulables.

---

# 27. GIT

Travailler sur la branche prévue par le sujet, par exemple :

`feature/reservation-ui-prenom-nom`

Avant chaque commit :

- `git status`
- `git diff`

Faire des commits compréhensibles et cohérents.

Ne pas inclure de fichiers générés inutiles.

---

# 28. PR ET LIVRABLE

Pour le module Réservations, préparer notamment les captures demandées :

1. chargement ;
2. liste remplie ;
3. liste vide ;
4. erreur métier `409` affichée clairement.

Le rendu responsive doit également être prêt à être montré.

---

# 29. CHECKLIST GLOBALE DE FIN

La refonte n’est terminée que si :

- [ ] tout le site concerné a été repris, pas seulement Réservations ;
- [ ] chaque écran a été comparé à son Figma ;
- [ ] l’IA a demandé le Figma/capture lorsqu’il était inaccessible ;
- [ ] la direction artistique est cohérente ;
- [ ] sidebar et navigation sont cohérentes ;
- [ ] chaque écran dynamique gère loading ;
- [ ] chaque écran dynamique gère données ;
- [ ] chaque écran dynamique gère état vide ;
- [ ] chaque écran dynamique gère erreurs ;
- [ ] succès et feedbacks sont présents ;
- [ ] messages compréhensibles pour un non-informaticien ;
- [ ] aucune erreur technique brute affichée ;
- [ ] responsive 1440 validé ;
- [ ] responsive 1024 validé ;
- [ ] responsive 768 validé ;
- [ ] responsive 390 validé ;
- [ ] vraies données backend utilisées ;
- [ ] services Angular via `HttpClient` ;
- [ ] aucun appel HTTP direct dans un composant ;
- [ ] JWT et sécurité existante respectés ;
- [ ] fonctionnalités Réservations fonctionnelles ;
- [ ] annulation fonctionnelle ;
- [ ] erreurs 400/404/409 correctement présentées ;
- [ ] test serveur indisponible effectué ;
- [ ] `PROGRESSION_REFONTE_UI.md` à jour ;
- [ ] aucun écran important laissé avec l’ancien design sans justification ;
- [ ] compilation sans erreur ;
- [ ] console navigateur vérifiée ;
- [ ] tests finaux effectués.

---

# 30. INSTRUCTION FINALE À L’IA DE DÉVELOPPEMENT

Tu dois agir comme un développeur qui reprend et modernise un vrai projet existant.

**Tu dois refaire le design de l’ensemble du site, écran par écran, et pas uniquement la page Réservations.**

Pour chaque écran :

1. lis `PROGRESSION_REFONTE_UI.md` ;
2. inspecte le code existant ;
3. récupère le Figma correspondant ;
4. si tu n’as pas accès au Figma, demande-moi le lien, la frame ou une capture ;
5. analyse le design ;
6. prépare un petit plan ;
7. recode l’écran dans le vrai projet ;
8. conserve ses fonctionnalités ;
9. utilise les vraies APIs ;
10. passe par les services Angular utilisant `HttpClient` ;
11. gère loading, données, vide, erreur et succès lorsque pertinent ;
12. transforme les erreurs techniques en messages compréhensibles par un utilisateur non informaticien ;
13. rends l’écran responsive ;
14. teste 1440, 1024, 768 et 390 px ;
15. compare le résultat au Figma ;
16. mets à jour `PROGRESSION_REFONTE_UI.md` ;
17. seulement ensuite passe à l’écran suivant.

Ne déclare jamais le travail terminé simplement parce que l’écran « s’affiche ».

Le travail est terminé lorsque l’interface est :
- fonctionnelle ;
- fidèle au Figma ;
- cohérente avec le reste du site ;
- connectée au backend ;
- responsive ;
- compréhensible par un utilisateur normal ;
- testée ;
- documentée.
