package tech.marcosmartinelli.springsecurity.modules.album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.marcosmartinelli.springsecurity.modules.users.User;

import java.util.List;
import java.util.UUID;


@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {
    List<Album> findAllByUser(User user);
}
