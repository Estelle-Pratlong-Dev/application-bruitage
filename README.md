# Application Bruitage — Soundboard Android

Application Android native de gestion de bruitages conçue pour être utilisée sur la caisse d'un manège forain.

> 🎵 Projet personnel — application métier

## Le projet

Cette application a été conçue pour disposer d'un **soundboard simple, rapide et entièrement hors ligne** sur tablette Android.

L'objectif est de pouvoir déclencher facilement différents bruitages depuis la caisse d'un manège, sans dépendre d'une connexion Internet ni d'un service externe.

L'interface repose sur une grille de boutons tactiles entièrement configurables afin de pouvoir adapter les sons et leur comportement directement depuis la tablette.

## Fonctionnalités

* Grille de 16 boutons tactiles
* Lecture instantanée des bruitages
* Arrêt et redémarrage d'un son déjà en cours
* Lecture en boucle configurable
* Bouton permettant d'arrêter tous les sons
* Configuration individuelle de chaque bouton
* Choix du fichier audio associé
* Personnalisation du nom
* Personnalisation de la couleur
* Réglage du volume
* Fondu à l'entrée configurable
* Persistance de la configuration après redémarrage de l'application

La configuration d'un bouton est accessible directement depuis l'interface par un appui long.

## Fonctionnement hors ligne

L'application est conçue pour fonctionner **entièrement hors ligne**.

Les fichiers audio sont directement embarqués dans l'APK et aucune connexion Internet n'est nécessaire pour utiliser l'application.

Aucune permission réseau n'est demandée.

Ce choix permet notamment de garantir son fonctionnement sur un manège, y compris lorsque la connexion mobile ou Wi-Fi est inexistante ou instable.

## Stockage de la configuration

Les paramètres personnalisés des différents boutons sont enregistrés localement sur la tablette avec **DataStore**.

La configuration est ainsi conservée après la fermeture ou le redémarrage de l'application.

## Stack technique

* Kotlin
* Android natif
* Jetpack Compose
* Android DataStore
* Gradle
* Git
* GitHub Actions

## Génération de l'APK

Le dépôt utilise **GitHub Actions** pour automatiser la compilation de l'application.

À chaque `push` sur la branche principale, un workflow génère automatiquement un APK pouvant ensuite être installé manuellement sur les tablettes Android.

Cette solution permet de produire les nouvelles versions de l'application sans dépendre d'une publication sur le Google Play Store.

## Distribution

L'application est destinée à un usage privé et n'est pas distribuée sur le Google Play Store.

L'APK est installé directement sur les tablettes utilisées avec le manège.

## Développement assisté par IA

Cette application a été développée avec l'assistance d'outils d'intelligence artificielle.

L'IA a été utilisée comme outil d'aide à la conception et à l'implémentation, notamment parce que le développement Android natif avec Kotlin et Jetpack Compose ne fait pas partie de mes technologies principales.

Le besoin, le fonctionnement attendu et les choix fonctionnels de l'application proviennent d'un **besoin concret rencontré sur le terrain**.

Ce projet m'a permis d'expérimenter le développement Android et la génération automatisée d'APK tout en réalisant un outil destiné à une utilisation réelle.

## Évolutions envisagées

* Validation du fonctionnement avec un ensemble complet de bruitages
* Amélioration de l'identité visuelle de l'application
* Ajout éventuel d'un système d'activation par tablette
* Ajustement de l'interface en fonction des retours lors de l'utilisation réelle

## Statut

🚧 **Projet fonctionnel — en cours d'amélioration**

La première version fonctionnelle de l'application est disponible et les prochaines évolutions seront principalement guidées par son utilisation en conditions réelles.
