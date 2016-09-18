package net.testaholic.acme.repository;

import net.testaholic.acme.domain.EmailInputField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the EmailInputField entity.
 */
public interface EmailInputFieldRepository extends JpaRepository<EmailInputField,Long> {

    @Query("select emailInputField from EmailInputField emailInputField where emailInputField.login.login = ?#{principal.username}")
    List<EmailInputField> findByLoginIsCurrentUser();


    @Query("select emailInputField from EmailInputField emailInputField where emailInputField.login.login = ?#{principal.username}")
    Page<EmailInputField> findByLoginIsCurrentUser(Pageable pageable);
}
