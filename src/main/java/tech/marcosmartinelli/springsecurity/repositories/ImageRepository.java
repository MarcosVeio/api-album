package tech.marcosmartinelli.springsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.marcosmartinelli.springsecurity.domain.Image.Image;

import java.util.UUID;


@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
}
