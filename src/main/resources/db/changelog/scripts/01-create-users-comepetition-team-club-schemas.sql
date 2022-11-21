CREATE TABLE club (
  id char(36) NOT NULL DEFAULT UUID(),
   name VARCHAR(255) NULL,
   CONSTRAINT pk_club PRIMARY KEY (id)
);

CREATE TABLE team (
  id char(36) NOT NULL DEFAULT UUID(),
   name VARCHAR(255) NULL,
   club_id char(36) NULL,
   competition_id char(36) NULL,
   registered datetime NULL,
   CONSTRAINT pk_team PRIMARY KEY (id)
);

CREATE TABLE competition (
  id char(36) NOT NULL DEFAULT UUID(),
   name VARCHAR(255) NULL,
   category VARCHAR(255) NULL,
   date date NULL,
   registration_end datetime NULL,
   CONSTRAINT pk_competition PRIMARY KEY (id)
);

CREATE TABLE application_user (
  id char(36) NOT NULL DEFAULT UUID(),
   username VARCHAR(255) NULL,
   firstname VARCHAR(255) NULL,
   lastname VARCHAR(255) NULL,
   email VARCHAR(255) NULL,
   hashed_password VARCHAR(255) NULL,
   profile_picture BLOB NULL,
   manager_of_id char(36) NULL,
   CONSTRAINT pk_application_user PRIMARY KEY (id)
);

CREATE TABLE user_roles (
    user_id char(36) NOT NULL,
    roles varchar(20) NOT NULL
);

ALTER TABLE application_user ADD CONSTRAINT FK_APPLICATION_USER_ON_MANAGEROF FOREIGN KEY (manager_of_id) REFERENCES club (id);

ALTER TABLE team ADD CONSTRAINT FK_TEAM_ON_CLUB FOREIGN KEY (club_id) REFERENCES club (id);

ALTER TABLE team ADD CONSTRAINT FK_TEAM_ON_COMPETITION FOREIGN KEY (competition_id) REFERENCES competition (id);

