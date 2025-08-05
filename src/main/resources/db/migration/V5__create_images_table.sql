CREATE TABLE tb_image (
    image_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    album_id UUID NOT NULL,
    img_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    date_file TIMESTAMP,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (album_id) REFERENCES tb_album(album_id) ON DELETE CASCADE
);

CREATE INDEX idx_images_album_id ON tb_image(album_id);
CREATE INDEX idx_images_file_name ON tb_image(file_name);
CREATE INDEX idx_images_file_type ON tb_image(file_type);
CREATE INDEX idx_images_created_at ON tb_image(created_at); 