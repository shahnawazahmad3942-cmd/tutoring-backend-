CREATE TABLE transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    package_id BIGINT NOT NULL REFERENCES packages(id),
    amount_snapshot NUMERIC(10, 2) NOT NULL,
    credits_snapshot INTEGER NOT null,
    razorpay_order_id VARCHAR(255) NOT NULL UNIQUE,
    razorpay_payment_id VARCHAR(255),
    status VARCHAR(50) NOT NULL CHECK(status IN('PENDING', 'SUCCESS', 'FAILED')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);