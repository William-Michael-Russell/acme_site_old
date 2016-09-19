package net.testaholic.acme.repository;

import net.testaholic.acme.domain.AudioUpload;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AudioUpload entity.
 */
public interface AudioUploadRepository extends JpaRepository<AudioUpload,Long> {

    @Query("select audioUpload from AudioUpload audioUpload where audioUpload.user.login = ?#{principal.username}")
    List<AudioUpload> findByUserIsCurrentUser();

    @Query("select audioUpload from AudioUpload audioUpload where audioUpload.user.login = ?#{principal.username}")
    Page<AudioUpload> findByUserIsCurrentUser(Pageable pageable);

}
