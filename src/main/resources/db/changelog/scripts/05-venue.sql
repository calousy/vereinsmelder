CREATE TABLE venue (
  id char(36) NOT NULL DEFAULT UUID(),
   venue_name VARCHAR(255) NOT NULL,
   toponym VARCHAR(255) NULL,
   street VARCHAR(255) NULL,
   street_number int NULL,
   addition VARCHAR(10) NULL,
   zip int NULL,
   CONSTRAINT pk_venue PRIMARY KEY (id)
);

ALTER table competition
ADD venue_id char(36) NULL;

ALTER table competition
ADD CONSTRAINT FK_COMPETITION_ON_LOCATION FOREIGN KEY (venue_id) REFERENCES venue (id);


