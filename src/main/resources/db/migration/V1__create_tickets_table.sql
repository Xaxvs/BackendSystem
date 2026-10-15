CREATE TABLE tickets (
                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         title VARCHAR(200) NOT NULL,
                         description TEXT NOT NULL,
                         customer_email VARCHAR(254) NOT NULL,
                         status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT chk_tickets_status
                             CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'))
);