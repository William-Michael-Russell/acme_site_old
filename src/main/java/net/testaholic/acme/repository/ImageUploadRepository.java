package net.testaholic.acme.repository;

import net.testaholic.acme.domain.ImageUpload;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ImageUpload entity.
 */
public interface ImageUploadRepository extends JpaRepository<ImageUpload,Long> {

    @Query("select imageUpload from ImageUpload imageUpload where imageUpload.user.login = ?#{principal.username}")
    List<ImageUpload> findByUserIsCurrentUser();

    @Query("select imageUpload from ImageUpload imageUpload where imageUpload.user.login = ?#{principal.username}")
    Page<ImageUpload> findByUserIsCurrentUser(Pageable pageable);

}
