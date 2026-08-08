CREATE TABLE credit_ledger (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    delta INTEGER NOT NULL,
    entry_type VARCHAR(50) NOT NULL CHECK(entry_type IN('PURCHASE', 'HOLD', 'REFUND', 'CONSUME', 'EXPIRY')),
    transaction_id BIGINT REFERENCES transactions(id),
    booking_id BIGINT REFERENCES bookings(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CHECK (
        (transaction_id IS NOT NULL AND booking_id IS NULL) OR
        (transaction_id IS NULL AND booking_id IS NOT NULL)
    )
);

CREATE INDEX idx_credit_ledger_user_id ON credit_ledger(user_id);