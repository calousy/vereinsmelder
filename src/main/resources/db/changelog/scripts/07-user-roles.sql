CREATE TABLE IF NOT EXISTS user_roles (
  user_id char(36) NOT NULL,
   roles VARCHAR(20) NOT NULL
);

ALTER table user_roles
ADD CONSTRAINT PK_USER_ROLE PRIMARY KEY (user_id, roles);
