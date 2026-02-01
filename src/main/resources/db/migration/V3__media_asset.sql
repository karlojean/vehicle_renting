CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE media_asset
(
    id                 UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    storage_path       VARCHAR(500) NOT NULL,
    bucket_name        VARCHAR(255) NOT NULL,
    original_filename  VARCHAR(255) NOT NULL,
    content_type       VARCHAR(100) NOT NULL,
    extension          VARCHAR(20),
    file_size_in_bytes BIGINT       NOT NULL,
    access_level       VARCHAR(50)  NOT NULL CHECK (access_level IN ('PUBLIC', 'PRIVATE')),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);


CREATE TABLE vehicle_media
(
    media_id   UUID PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,

    CONSTRAINT fk_vehicle_media_asset FOREIGN KEY (media_id) REFERENCES media_asset (id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_media_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle (id) ON DELETE CASCADE
);


CREATE TABLE inspection_media
(
    media_id      UUID PRIMARY KEY,
    inspection_id BIGINT NOT NULL,

    CONSTRAINT fk_inspection_media_asset FOREIGN KEY (media_id) REFERENCES media_asset (id) ON DELETE CASCADE,
    CONSTRAINT fk_inspection_media_inspection FOREIGN KEY (inspection_id) REFERENCES inspection (id) ON DELETE CASCADE
);