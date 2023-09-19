create table public.movies
(
    id               serial
        primary key,
    title            varchar(200) not null,
    release_date     date         not null,
    director         varchar(100) not null,
    duration_minutes integer,
    description      text
);


create table public.screenings
(
    id         serial
        primary key,
    movie_id   integer
        references public.movies,
    start_time timestamp     not null,
    price      numeric(8, 2) not null
);


create table public.users
(
    id       serial
        primary key,
    username varchar(50) not null,
    email    varchar(50) not null,
    password varchar(50) not null
);


create table public.genres
(
    id    serial
        primary key,
    genre varchar(30) not null
        unique
);


create table public.seat_class
(
    id         serial
        primary key,
    seat_class varchar(10) not null
);


create table public.seats
(
    id            serial
        primary key,
    seat_no       varchar(4) not null,
    seat_class_id integer
        references public.seat_class
);


create table public.tickets
(
    id            serial
        primary key,
    screening_id  integer
        references public.screenings,
    user_id       integer
        references public.users,
    seat_id       integer
        references public.seats,
    ticket_price  numeric(8, 2) not null,
    purchase_date timestamp     not null
);


create table public.movie_to_genre
(
    movie_id integer not null
        references public.movies,
    genre_id integer not null
        references public.genres,
    primary key (movie_id, genre_id)
);


