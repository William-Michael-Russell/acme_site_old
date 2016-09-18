package net.testaholic.acme.repository;

import net.testaholic.acme.domain.ImageUpload;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ImageUpload entity.
 */
public interface ImageUploadRepository extends JpaRepository<ImageUpload,Long> {

    @Query("select imageUpload from ImageUpload imageUpload where imageUpload.user.login = ?#{principal.username}")
    List<ImageUpload> findByUserIsCurrentUser();

}
