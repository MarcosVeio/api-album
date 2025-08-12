package tech.marcosmartinelli.springsecurity.modules.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.marcosmartinelli.springsecurity.modules.album.Album;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findAllByAlbum(Album album);
}
