CREATE TABLE tb_album (
    album_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_users(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_albums_user_id ON tb_album(user_id);
CREATE INDEX idx_albums_title ON tb_album(title);
CREATE INDEX idx_albums_created_at ON tb_album(created_at); 