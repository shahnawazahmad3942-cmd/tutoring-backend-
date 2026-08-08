CREATE TABLE bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    slot_id BIGINT NOT NULL UNIQUE REFERENCES slots(id),
    type VARCHAR(50) NOT NULL CHECK (type IN('DEMO', 'ACTUAL')),
    status VARCHAR(50) NOT NULL CHECK (status IN('CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_bookings_user_id ON bookings(user_id);
