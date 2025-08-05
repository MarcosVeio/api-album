ALTER TABLE tb_album
ADD CONSTRAINT fk_album_cover_image 
FOREIGN KEY (cover_image_id) REFERENCES tb_image(image_id) ON DELETE SET NULL;

CREATE INDEX idx_albums_cover_image_id ON tb_album(cover_image_id);