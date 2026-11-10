# Jeu Java (`jeu-java`)

Projet de jeu développé dans le cadre du module de programmation Java.

---

##  Configuration et Prérequis

* **Langage / JDK** : Java **JDK 17** (ou supérieur)
* **Système d'exploitation** : Windows, macOS, Linux
* **Dépendances** : Aucune bibliothèque externe (bibliothèque Java standard uniquement)

---

##  Compilation et Exécution (Ligne de commande)

### 1. Clonage du dépôt

bash
git clone https://github.com/sinodriss2/jeu-java.git
cd jeu-java


### 2. Compilation
Exécutez la commande suivante à la racine du projet pour compiler le code source dans le dossier `bin` :

bash
javac -d bin -sourcepath src src/fr/ul/acl/start/Main.java

### 3. Exécution
Lancez le jeu avec la commande suivante :

bash
java -cp bin fr.ul.acl.start.Main


---

##  Exécution via un IDE

* **VS Code** : Ouvrez le dossier (`code .`), ouvrez `Main.java` et appuyez sur **Run** (F5).
* **Eclipse / IntelliJ** : Importez le dossier `jeu-java` comme projet Java existant et exécutez la classe `Main.java`.

---

##  Organisation du dépôt et Suivi Agile (Sprints)

Toute la documentation relative aux livrables Agile (Sprint Backlog, diagrammes UML, Sprint Review, Sprint Retrospective) est disponible dans le dossier `/docs/` :

text
jeu-java/
├── docs/
│   └── sprint-1/
│       ├── sprint-backlog.md          # Tâches et fonctionnalités du sprint
│       ├── conception/                # Diagrammes UML (classe, séquence)
│       ├── sprint-review.md           # Bilan des fonctionnalités validées
│       └── sprint-retrospective.md   # Bilan d'équipe et répartition des contributions
├── src/                               # Code source Java (.java)
│   └── fr/ul/acl/start/Main.java
└── README.md                          # Documentation principale

---

## 🏷️ Versions et Tags

Chaque fin de Sprint fait l'objet d'un tag de version sur la branche `main` :
* **v1** : Version livrée à la fin du Sprint 1.