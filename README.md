## CASSE TETE DE TRI DE COULEURS - JavaFX ##

## JavaFX - WATERSORT COLOR PUZZLE ##

<hr />

### Jeu de réflexion, casse-tête ###

- le but est de rassembler chaque couleur dans un seul tube (verser chaque couleur dans un tube contenant une couleur
  identique),
- on jongle avec les tubes vides et les capacités maximales de chaque tube,
- et on ne doit rien renverser à côté !!

1. utilise JavaFX
2. work in progress (some functionalities are still lacking)!

### Installation pour développer ###

* s'assurer d'avoir installé Java 21 et JavaFX 21 sur son ordinateur
* dans un terminal, clôner ce dépôt chez soi :

```bash 
git clone https://github.com/esHome33/Water-Color-Puzzle.git
``` 

* changer de répertoire :

```bash
cd ./Water-Color-Puzzle
```

* et lancer la commande Gradle suivante :

```bash
./gradlew :run
```

### Génération des packages pour Windows et Debian Linux ###

Sur une machine Linux, pour produire un package Debian, exécuter la commande suivante :

```bash
./gradlew clean jpackage
```

Cette commande générera un fichier `*.deb` dans le répertoire `build/jpackage` qui est directement installable avec la
commande :

```bash
sudo apt install ./build/jpackage/*.deb
```

Une notification survient à l'issue de cette exécution et n'est pas importante (<code>Le téléchargement est effectué en
dehors du bac à sable en tant que superutilisateur</code>)

Pour créer le package zip pour Windows, utiliser conveyor :

```bash
conveyor make windows-zip
```

Si vous développez sous windows, attention, il va falloir reprendre le script `build.gradle.kts` car il est actuellement
optimisé pour Linux.

<i>Ce jeu est développé par ESHome33 en Java 21, JavaFX 21, Gradle avec l'IDE de JetBrains IntelliJ IDEA.</i>

### Installation pour jouer ###

Aller
sur [https://github.com/esHome33/Water-Color-Puzzle/releases](https://github.com/esHome33/Water-Color-Puzzle/releases)
et télécharger l'archive zip ou deb qui convient à votre système d'exploitation.

Cette archive contient le programme exécutable, ainsi que le runtime Java et JavaFX : l'exécution du programme
fonctionnera même si vous n'avez pas installé Java et JavaFX sur votre ordinateur.

<hr />

### English version ###

* The goal is to group each color into a single tube (pouring each color into a tube that already contains the same
  color).
* You juggle empty tubes and the maximum capacity of each tube.
* And you must not spill anything outside!


* choose the relevant release and enjoy!
* or clone this repository to start developing a new feature!

<i>developed in France by ESHome33 on JetBrains IntelliJ IDEA, Java 21, JavaFX 21 and Gradle.</i>

