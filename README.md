# Bruitage

Appli Android native (Kotlin + Jetpack Compose) de bruitage pour caisse de manège forain.
Fonctionne 100% hors-ligne, distribution en APK sideloadé (pas de Google Play).

## État actuel du projet

Squelette v1 fonctionnel généré :

- Grille de 16 carrés tactiles (4x4)
- Appui simple = joue le son associé (retrigger si déjà en cours pour un son non-boucle ;
  ré-appui = stop si le son est en boucle)
- Appui long = configuration du carré : choix du son (parmi les fichiers embarqués dans
  `app/src/main/assets/sounds/`), nom, lecture en boucle, fondu à l'entrée, volume, couleur
- Bouton flottant "Tout arrêter"
- Configuration persistée localement sur la tablette (DataStore), donc conservée après
  redémarrage de l'appli
- Aucune permission réseau demandée (l'appli n'a pas besoin d'internet)

Non fait pour l'instant (volontairement, cf. échanges avec Claude) :

- Protection anti-partage de l'APK (code d'activation / device ID) — à ajouter dans un
  second temps une fois l'appli fonctionnelle validée
- Icône de lancement définitive (un placeholder simple est en place, à remplacer par un
  vrai visuel de marque plus tard)

## Où mettre les sons

Deux dossiers "audio" existent dans ce repo, ils n'ont pas le même rôle :

- `Fichiers audio/` (à la racine) : dossier de travail pour stocker/trier tes fichiers
  sources bruts, non lu par l'appli.
- `app/src/main/assets/sounds/` : dossier réellement embarqué dans l'APK. C'est ici qu'il
  faut copier les `.mp3` / `.wav` définitifs avant de compiler, pour qu'ils apparaissent
  dans le sélecteur de son (appui long sur un carré).

## Compiler l'APK sans rien installer sur ce PC (build cloud)

Le dépôt contient un workflow GitHub Actions (`.github/workflows/build-apk.yml`) qui
compile l'APK automatiquement dans le cloud à chaque `push` sur la branche `main`. Rien à
installer localement à part Git (déjà présent via Laragon).

Étapes (une seule fois) :

1. Créer un compte GitHub si besoin : https://github.com/signup (gratuit)
2. Créer un nouveau dépôt **privé** vide sur GitHub (ne pas cocher "Add a README") :
   https://github.com/new
3. Lier ce dossier local au dépôt distant et pousser le code (remplacer l'URL par celle de
   ton dépôt) :

```bash
git remote add origin https://github.com/<ton-compte>/<ton-repo>.git
git branch -M main
git push -u origin main
```

4. Aller dans l'onglet **Actions** du dépôt GitHub : le build se lance automatiquement et
   prend 2-3 minutes.
5. Une fois terminé, ouvrir le run et télécharger l'artifact `bruitage-debug-apk` (fichier
   zip contenant l'APK).
6. Transférer cet APK sur chaque tablette (câble USB, ou stockage externe) et l'installer en
   autorisant "Sources inconnues" dans les paramètres Android.

Pour relancer un build après modification (ex: nouveaux sons ajoutés) :

```bash
git add -A
git commit -m "Ajout des sons"
git push
```

Le workflow se redéclenche automatiquement et un nouvel APK est généré.

## Prochaines étapes suggérées

- Valider le nombre de carrés par défaut (16 actuellement, modifiable via `GRID_SIZE` dans
  `SoundBoardViewModel.kt`)
- Ajouter la protection anti-partage (code d'activation par tablette, saisi au premier
  lancement)
- Remplacer l'icône de lancement placeholder par un vrai visuel
- Ajouter un vrai jeu de sons de test dans `app/src/main/assets/sounds/` pour valider le
  fonctionnement de bout en bout
