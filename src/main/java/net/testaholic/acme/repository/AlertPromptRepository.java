package net.testaholic.acme.repository;

import net.testaholic.acme.domain.AlertPrompt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AlertPrompt entity.
 */
public interface AlertPromptRepository extends JpaRepository<AlertPrompt,Long> {

    @Query("select alertPrompt from AlertPrompt alertPrompt where alertPrompt.user.login = ?#{principal.username}")
    List<AlertPrompt> findByUserIsCurrentUser();

    @Query("select alertPrompt from AlertPrompt alertPrompt where alertPrompt.user.login = ?#{principal.username}")
    Page<AlertPrompt> findByUserIsCurrentUser(Pageable pageable);

}
