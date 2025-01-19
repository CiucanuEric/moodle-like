-- Create databases
CREATE DATABASE authdb;
CREATE DATABASE maindb;

-- Create user for both databases
CREATE USER 'app_user'@'%' IDENTIFIED BY 'app_password';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON authdb.* TO 'app_user'@'%';
GRANT ALL PRIVILEGES ON maindb.* TO 'app_user'@'%';

USE authdb;
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) DEFAULT 'user'
);

USE maindb;
CREATE TABLE students (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nume VARCHAR(255) NOT NULL,
                          prenume VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          grupa VARCHAR(50),
                          an_studiu INT
);

CREATE TABLE professors (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            nume VARCHAR(255) NOT NULL,
                            prenume VARCHAR(255) NOT NULL,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            grad_didactic VARCHAR(50),
                            afiliere VARCHAR(255)
);

CREATE TABLE lectures (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nume_disciplina VARCHAR(255) NOT NULL,
                          cod VARCHAR(50) NOT NULL UNIQUE,
                          an_studiu INT NOT NULL,
                          tip_disciplina VARCHAR(50),
                          categorie_disciplina VARCHAR(50),
                          tip_examinare VARCHAR(50),
                          professor_id INT,
                          FOREIGN KEY (professor_id) REFERENCES professors(id)
);

CREATE TABLE studenti_discipline (
                                     id_student INT,
                                     id_disciplina INT,
                                     PRIMARY KEY (id_student, id_disciplina),
                                     FOREIGN KEY (id_student) REFERENCES students(id),
                                     FOREIGN KEY (id_disciplina) REFERENCES lectures(id)
);