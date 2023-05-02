DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS event_locations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS participations CASCADE;

CREATE TABLE public.users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE public.categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE public.event_locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat FLOAT                                   NOT NULL,
    lon FLOAT                                   NOT NULL,
    CONSTRAINT pk_event_locations PRIMARY KEY (id)
);

CREATE TABLE public.events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title              VARCHAR(255)                            NOT NULL,
    annotation         VARCHAR(500)                            NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    created_on         TIMESTAMP                                       ,
    published_on       TIMESTAMP                                       ,
    description        VARCHAR(2000)                           NOT NULL,
    event_date         TIMESTAMP                               NOT NULL,
    user_id            BIGINT                                  NOT NULL,
    location_id        BIGINT                                  NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  INTEGER                                 NOT NULL,
    request_moderation BOOLEAN                                 NOT NULL,
    state              INTEGER                                 NOT NULL,
    views              BIGINT                                  NOT NULL,
    confirmed_requests BIGINT                                  NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id),
    FOREIGN KEY (category_id) REFERENCES public.categories(id)      ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES public.event_locations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)     REFERENCES public.users(id)           ON DELETE CASCADE
);

CREATE TABLE public.compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN                                 NOT NULL,
    title  VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (id)
);

CREATE TABLE public.compilations_events
(
    compilation_id BIGINT NOT NULL,
    event_id      BIGINT NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES public.compilations(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id)       REFERENCES public.events(id)       ON DELETE CASCADE
);

CREATE TABLE public.participations
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created  TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT                                  NOT NULL,
    user_id  BIGINT                                  NOT NULL,
    status   INTEGER                                 NOT NULL,
    CONSTRAINT pk_participations PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE
);
