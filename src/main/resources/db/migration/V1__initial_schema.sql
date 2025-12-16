CREATE TABLE location
(
    id                    BIGSERIAL PRIMARY KEY,
    address_line          VARCHAR(255)         NOT NULL,
    city                  VARCHAR(50)  NOT NULL,
    state                 VARCHAR(50)  NOT NULL,
    pin_code              VARCHAR(10)  NOT NULL,
    country               VARCHAR(50)  NOT NULL
);

CREATE TABLE account
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) UNIQUE  NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    phone_number    VARCHAR(15),
    password        VARCHAR(255)        NOT NULL,
    role            VARCHAR(20)         NOT NULL
);

CREATE TABLE vehicle
(
    id                BIGSERIAL PRIMARY KEY,
    brand             VARCHAR(50) NOT NULL,
    model             VARCHAR(50) NOT NULL,
    fuel_type         VARCHAR(20) NOT NULL CHECK (fuel_type IN ('PETROL', 'DIESEL', 'ETHANOL', 'ELECTRIC', 'HYBRID')),
    vehicle_type      VARCHAR(20) NOT NULL CHECK (vehicle_type IN
                                                  ('HATCHBACK', 'SEDAN', 'SUV', 'PICKUP', 'VAN', 'MOTORCYCLE')),
    owner_id          BIGINT      NOT NULL,
    year_manufactured INT         NOT NULL,
    license_plate     VARCHAR(20) NOT NULL UNIQUE,
    color             VARCHAR(30),
    is_active         BOOLEAN              DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES account (id)
);

CREATE TABLE listing
(
    id            BIGSERIAL PRIMARY KEY,
    price_per_day DECIMAL(10, 2) NOT NULL,
    description   TEXT           NOT NULL,
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    vehicle_id    BIGINT         NOT NULL,
    location_id   BIGINT         NOT NULL,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT fk_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle (id),
    CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES location (id)
);
