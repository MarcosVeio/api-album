package tech.marcosmartinelli.springsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.marcosmartinelli.springsecurity.domain.Album.Album;

import java.util.UUID;


@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {
}
