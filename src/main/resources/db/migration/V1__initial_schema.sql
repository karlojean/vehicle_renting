CREATE TABLE account
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR(50) UNIQUE  NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(15),
    password     VARCHAR(255)        NOT NULL,
    role         VARCHAR(20)         NOT NULL
);

CREATE TABLE location
(
    id           BIGSERIAL PRIMARY KEY,
    address_line VARCHAR(255) NOT NULL,
    city         VARCHAR(50)  NOT NULL,
    state        VARCHAR(50)  NOT NULL,
    pin_code     VARCHAR(10)  NOT NULL,
    country      VARCHAR(50)  NOT NULL,
    latitude     DECIMAL(9, 6),
    longitude    DECIMAL(9, 6),
    owner_id          BIGINT         NOT NULL,

    CONSTRAINT fk_location_owner FOREIGN KEY (owner_id) REFERENCES account (id)
);

CREATE TABLE vehicle
(
    id                BIGSERIAL PRIMARY KEY,
    brand             VARCHAR(50)    NOT NULL,
    model             VARCHAR(50)    NOT NULL,
    fuel_type         VARCHAR(20)    NOT NULL CHECK (fuel_type IN ('PETROL', 'DIESEL', 'ETHANOL', 'ELECTRIC', 'HYBRID')),
    vehicle_type      VARCHAR(20)    NOT NULL CHECK (vehicle_type IN
                                                     ('HATCHBACK', 'SEDAN', 'SUV', 'PICKUP', 'VAN', 'MOTORCYCLE')),
    year_manufactured INT            NOT NULL,
    license_plate     VARCHAR(20)    NOT NULL UNIQUE,
    color             VARCHAR(30),
    price_per_day_cents    BIGINT NOT NULL,
    description       TEXT           NOT NULL,
    is_active         BOOLEAN                 DEFAULT TRUE,
    created_at        TIMESTAMPTZ    NOT NULL DEFAULT now(),
    owner_id          BIGINT         NOT NULL,
    location_id       BIGINT         NOT NULL,

    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES account (id),
    CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES location (id)
);

