USE supermarket_db;

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,

    user_id INT NULL,

    entity_type VARCHAR(50),
    entity_id INT,

    action_type VARCHAR(50) NOT NULL,
    description VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id)
        REFERENCES app_users(user_id)
) ENGINE=InnoDB;