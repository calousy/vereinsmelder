ALTER table team
ADD updated TIMESTAMP;

ALTER table team
ADD enabled boolean DEFAULT True;
