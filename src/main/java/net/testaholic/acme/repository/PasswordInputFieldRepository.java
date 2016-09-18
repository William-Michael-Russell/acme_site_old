package net.testaholic.acme.repository;

import net.testaholic.acme.domain.PasswordInputField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PasswordInputField entity.
 */
public interface PasswordInputFieldRepository extends JpaRepository<PasswordInputField,Long> {

    @Query("select passwordInputField from PasswordInputField passwordInputField where passwordInputField.user.login = ?#{principal.username}")
    List<PasswordInputField> findByUserIsCurrentUser();

    @Query("select passwordInputField from PasswordInputField passwordInputField where passwordInputField.user.login = ?#{principal.username}")
    Page<PasswordInputField> findByUserIsCurrentUser(Pageable pageable);

}
