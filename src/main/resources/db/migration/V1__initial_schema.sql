CREATE TABLE image
(
    id          BIGSERIAL PRIMARY KEY,
    contentType VARCHAR(50) NOT NULL,
    image_bytes BYTEA       NOT NULL
);


CREATE TABLE account
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) UNIQUE  NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    phone_number    VARCHAR(15),
    password        VARCHAR(255)        NOT NULL,
    role            VARCHAR(20),
    profile_picture BIGINT,
    CONSTRAINT fk_profile_picture FOREIGN KEY (profile_picture) REFERENCES image (id)
);



CREATE TABLE vehicle
(
    id        BIGSERIAL PRIMARY KEY,
    brand     VARCHAR(50) NOT NULL,
    type      VARCHAR(50) NOT NULL,
    model     VARCHAR(50) NOT NULL,
    fuel_type VARCHAR(50) NOT NULL
);


CREATE TABLE vehicle_image
(
    vehicle_id BIGINT NOT NULL,
    image_id   BIGINT NOT NULL UNIQUE,
    PRIMARY KEY (vehicle_id, image_id),

    CONSTRAINT fk_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle (id),
    CONSTRAINT fk_image FOREIGN KEY (image_id) REFERENCES image (id)
);



CREATE TABLE vehicle_listing
(
    id                 BIGSERIAL PRIMARY KEY,
    vehicle_no         VARCHAR(20) UNIQUE NOT NULL,
    price_per_day      DECIMAL(10, 2)     NOT NULL,
    seating            VARCHAR(20),
    renting_partner_id BIGINT             NOT NULL,
    vehicle_id         BIGINT             NOT NULL,

    CONSTRAINT fk_renting_partner FOREIGN KEY (renting_partner_id) REFERENCES account (id),
    CONSTRAINT fk_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle (id)
);

CREATE TABLE location
(
    id                    BIGSERIAL PRIMARY KEY,
    address_line          TEXT         NOT NULL,
    address_line_optional TEXT,
    area                  VARCHAR(100) NOT NULL,
    city                  VARCHAR(50)  NOT NULL,
    state                 VARCHAR(50)  NOT NULL,
    pin_code              VARCHAR(10)  NOT NULL,
    country               VARCHAR(50)  NOT NULL,
    phone_number          VARCHAR(15)
);

CREATE TABLE inspection
(
    id       BIGSERIAL PRIMARY KEY,
    comments TEXT
);

CREATE TABLE inspection_image
(
    inspection_id BIGINT NOT NULL,
    image_id      BIGINT NOT NULL UNIQUE,
    PRIMARY KEY (inspection_id, image_id),

    CONSTRAINT fk_inspection FOREIGN KEY (inspection_id) REFERENCES inspection (id),
    CONSTRAINT fk_image FOREIGN KEY (image_id) REFERENCES image (id)
);

CREATE TABLE booking
(
    id                   BIGSERIAL PRIMARY KEY,
    status               VARCHAR(20)    NOT NULL,
    total_payable_amount DECIMAL(10, 2) NOT NULL,
    renting_type         VARCHAR(20)    NOT NULL,
    duration             INT            NOT NULL,
    vehicle_id           BIGINT         NOT NULL,
    account_id           BIGINT         NOT NULL,

    CONSTRAINT fk_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle_listing (id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE pick_up
(
    id            BIGSERIAL PRIMARY KEY,
    date          TIMESTAMP NOT NULL,
    location      BIGINT    NOT NULL,
    inspection_id BIGINT    NOT NULL,

    CONSTRAINT fk_location FOREIGN KEY (location) REFERENCES location (id),
    CONSTRAINT fk_inspection FOREIGN KEY (inspection_id) REFERENCES inspection (id)
);

CREATE TABLE drop_off
(
    id            BIGSERIAL PRIMARY KEY,
    date          TIMESTAMP   NOT NULL,
    location      BIGINT      NOT NULL,
    inspection_id BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL,
    booking_id    BIGINT      NOT NULL UNIQUE,

    CONSTRAINT fk_location FOREIGN KEY (location) REFERENCES location (id),
    CONSTRAINT fk_inspection FOREIGN KEY (inspection_id) REFERENCES inspection (id),
    CONSTRAINT fk_booking_drop FOREIGN KEY (booking_id) REFERENCES booking(id)
);

CREATE TABLE review
(
    id                 BIGSERIAL PRIMARY KEY,
    rating             FLOAT CHECK (rating >= 0 AND rating <= 5),
    review             TEXT,
    account_id         BIGINT NOT NULL,
    vehicle_listing_id BIGINT NOT NULL,

    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id),
    CONSTRAINT fk_vehicle_listing FOREIGN KEY (vehicle_listing_id) REFERENCES vehicle_listing (id)
);