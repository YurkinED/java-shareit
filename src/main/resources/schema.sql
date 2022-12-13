
CREATE TABLE IF NOT EXISTS users (
   id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   user_name varchar(50),
   email varchar(50),
   CONSTRAINT pk_users PRIMARY KEY (id),
   CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(50),
    description varchar(50),
    available boolean,
    user_id BIGINT REFERENCES users(id),
    CONSTRAINT pk_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT REFERENCES items(id),
    booker_id BIGINT REFERENCES users(id),
    status int,
    CONSTRAINT pk_bookings PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    comment_text varchar NOT NULL,
    item_id BIGINT REFERENCES items(id),
    author_id BIGINT REFERENCES users(id),
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT comments_pk PRIMARY KEY (id)
);