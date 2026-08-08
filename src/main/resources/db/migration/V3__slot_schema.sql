CREATE TABLE slots (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subject_id BIGINT NOT NULL REFERENCES subjects(id),
    start_time TIMESTAMP NOT NULL,                        
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('AVAILABLE', 'BOOKED', 'CANCELLED', 'COMPLETED')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_slots_start_time ON slots(start_time);
CREATE INDEX idx_slots_status ON slots(status);
/*
They create B-tree indexes on start_time and status — a lookup 
structure Postgres maintains alongside the table so it can jump 
straight to matching rows instead of scanning every row in 
slots.
*/
