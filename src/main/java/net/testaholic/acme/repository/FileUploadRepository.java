package net.testaholic.acme.repository;

import net.testaholic.acme.domain.FileUpload;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the FileUpload entity.
 */
public interface FileUploadRepository extends JpaRepository<FileUpload,Long> {

    @Query("select fileUpload from FileUpload fileUpload where fileUpload.user.login = ?#{principal.username}")
    List<FileUpload> findByUserIsCurrentUser();

}
