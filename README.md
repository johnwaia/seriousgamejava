# Quiz Game Application

## Description
This project implements a quiz game application in Java. It allows users to register, login, answer quiz questions based on their skill level, and challenge other players.

## Features
- **User Registration and Authentication**: Users can register with a username and password. Passwords are securely hashed using SHA-256 before storing in the database.
- **Quiz Questions**: Questions are categorized into different levels (easy, medium, difficult) and subjects (Networking, Web Development, Java OOP). Users answer questions based on their skill level.
- **Player Avatar and Skill Level**: Player avatars are determined based on their quiz scores, and the quiz questions' difficulty adapts accordingly.
- **Challenges**: Logged-in users can challenge other players to answer quiz questions and compete for high scores.

## Technologies Used
- Java
- SQLite (for database operations)
- SHA-256 (for password hashing)

## Prerequisites
- Java Development Kit (JDK) installed
- IDE (e.g., IntelliJ IDEA, Eclipse) for Java development

## Getting Started
1. Open the project in your preferred IDE.
2. Compile and run the `Main.java` file to start the application.

## Usage
1. **Registration**: 
   - Run the application and choose option 1 to register.
   - Enter a username and password when prompted.
   - Follow instructions to enter quiz scores for Networking, Web Development, and Java OOP.

2. **Login**:
   - Choose option 2 to log in with your registered username and password.

3. **Answering Quiz Questions**:
   - After login, answer quiz questions based on your selected skill level.
   - Questions are fetched from the database and displayed with options.
   - Enter your answer choice (A, B, or C) as prompted.

4. **Challenges**:
   - Once logged in, choose option 1 in the connected menu to challenge another player.
   - Enter the player's name to start the challenge.

5. **Updating Scores**:
   - Choose option 4 in the connected menu to update your quiz scores.
   - Enter new scores for Networking, Web Development, and Java OOP to update your avatar and skill levels.

## Classes Included
### Joueur.java
Represents a player in the quiz game, storing information such as username, password hash, quiz scores, and avatar.

### GestionBaseDeDonnees.java
Handles database operations for storing/retrieving player information, quiz questions, and challenge records.

### Database Structure

#### Création de la table 'defi'
```sql
CREATE TABLE defi (
    id_defi INT AUTO_INCREMENT PRIMARY KEY,
    id_emetteur INT,
    id_recepteur INT,
    date_creation TIMESTAMP,
    matiere VARCHAR(255),
    niveau VARCHAR(255),
    accepte TINYINT
);
```
#### Création de la table 'defi_questions'
```sql
CREATE TABLE defi_questions (
    id_defi INT,
    id_question INT,
    PRIMARY KEY (id_defi, id_question),
    FOREIGN KEY (id_defi) REFERENCES defi(id_defi),
    FOREIGN KEY (id_question) REFERENCES questions(id)
);
```
#### Création de la table 'joueurs'
```sql
CREATE TABLE joueurs (
    id_joueur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255),
    avatar VARCHAR(255),
    note_reseaux INT,
    note_web INT,
    note_java INT,
    mot_de_passe VARCHAR(64)
);
```
#### Création de la table 'joueur_question'
```sql
CREATE TABLE joueur_question (
    id INT AUTO_INCREMENT PRIMARY KEY,
    joueur_id INT,
    question_id INT,
    FOREIGN KEY (joueur_id) REFERENCES joueurs(id_joueur),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
```
#### Création de la table 'questions'
```sql
CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    texte TEXT,
    reponse TEXT,
    option1 TEXT,
    option2 TEXT,
    option3 TEXT,
    matiere VARCHAR(60),
    niveau VARCHAR(25)
);
```

### Question.java
Represents a quiz question with an ID, text, options, correct answer, and difficulty level (easy, medium, difficult).

### MenuPrincipal.java
Main menu interface for the quiz application, allowing users to register, login, answer questions, challenge other players, and update scores.

## Contributors
- [John waia](https://github.com/johnwaia)

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Ce README fournit une introduction claire au projet, détaille les fonctionnalités clés, fournit des instructions pour l'installation et l'utilisation, et comprend des sections pour les prérequis, les technologies utilisées, et les contributeurs. Assurez-vous d'ajuster les liens GitHub et les détails de licence selon les besoins de votre projet réel.
