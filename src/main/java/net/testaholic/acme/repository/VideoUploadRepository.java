package net.testaholic.acme.repository;

import net.testaholic.acme.domain.VideoUpload;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the VideoUpload entity.
 */
public interface VideoUploadRepository extends JpaRepository<VideoUpload,Long> {

    @Query("select videoUpload from VideoUpload videoUpload where videoUpload.user.login = ?#{principal.username}")
    List<VideoUpload> findByUserIsCurrentUser();


    @Query("select videoUpload from VideoUpload videoUpload where videoUpload.user.login = ?#{principal.username}")
    Page<VideoUpload> findByUserIsCurrentUser(Pageable pageable);

}
