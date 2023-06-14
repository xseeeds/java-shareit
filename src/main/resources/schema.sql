
drop table if exists bookings, comments, items, users cascade;

    create table bookings (
        id int8 generated by default as identity,
        booker_id int8,
        end_date timestamp,
        item_id int8,
        start_date timestamp,
        status varchar(255),
        primary key (id)
    );

    create table comments (
        id int8 generated by default as identity,
        author_id int8,
        created_date timestamp,
        item_id int8,
        text varchar(255),
        primary key (id)
    );

    create table items (
        id int8 generated by default as identity,
        available boolean,
        description varchar(255),
        name varchar(255),
        owner_id int8,
        request_id int8,
        primary key (id)
    );

    create table users (
        id int8 generated by default as identity,
        email varchar(255),
        name varchar(255),
        primary key (id)
    );

    alter table if exists users
       add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table if exists bokings
       add constraint FKrbue62imlg1u5r6rtmiifnuhw
       foreign key (booker_id)
       references users
       on delete cascade;

    alter table if exists bokings
       add constraint FKj806i9gf1tmgkrrgj23g0aqc0
       foreign key (item_id)
       references items
       on delete cascade;

    alter table if exists comments
       add constraint FKn2na60ukhs76ibtpt9burkm27
       foreign key (author_id)
       references users
       on delete cascade;

    alter table if exists comments
       add constraint FKkbkydvf8j8tfuego2iqxntwv2
       foreign key (item_id)
       references items
       on delete cascade;

    alter table if exists items
       add constraint FKe37yi0i6rmaqcqickvb1vty22
       foreign key (owner_id)
       references users
       on delete cascade;


/*
DROP TABLE IF EXISTS bookings, items, comments, users CASCADE;

CREATE TABLE IF NOT EXISTS users(
   id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   email varchar(100) NOT NULL UNIQUE,
   name varchar(100));


CREATE TABLE IF NOT EXISTS items(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100),
    description varchar(100) NOT NULL,
    available boolean,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS bookings(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_id BIGINT REFERENCES items(item_id) ON DELETE CASCADE,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    booker_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    status varchar(10));

CREATE TABLE IF NOT EXISTS comments(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text varchar(100) NOT NULL,
    item_id BIGINT REFERENCES items(item_id) ON DELETE CASCADE,
    author_name varchar NOT NULL,
    created timestamp);

*/