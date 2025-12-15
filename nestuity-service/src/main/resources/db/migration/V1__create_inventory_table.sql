CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    supply_name VARCHAR(255) NOT NULL,
    total_single_quantity DOUBLE PRECISION NOT NULL,
    total_unit_quantity DOUBLE PRECISION NOT NULL,
    unit_conversion DOUBLE PRECISION NOT NULL,
    preferred_supply_min INTEGER NOT NULL DEFAULT 14,
    CONSTRAINT fk_inventory_user FOREIGN KEY (user_id) REFERENCES nestuity_user (id)
);

ALTER TABLE nestuity_user DROP COLUMN IF EXISTS remaining_diapers;
ALTER TABLE baby DROP COLUMN IF EXISTS diapers_per_box;
