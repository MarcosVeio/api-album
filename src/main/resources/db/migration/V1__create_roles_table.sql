CREATE TABLE tb_roles (
    role_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tb_roles (role_id, name) VALUES
    (1, 'BASIC'),
    (2, 'ADMIN');

CREATE INDEX idx_roles_name ON tb_roles(name);