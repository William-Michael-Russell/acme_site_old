package net.testaholic.acme.repository;

import net.testaholic.acme.domain.AlphaNumericInputField;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AlphaNumericInputField entity.
 */
public interface AlphaNumericInputFieldRepository extends JpaRepository<AlphaNumericInputField,Long> {

    @Query("select alphaNumericInputField from AlphaNumericInputField alphaNumericInputField where alphaNumericInputField.user.login = ?#{principal.username}")
    List<AlphaNumericInputField> findByUserIsCurrentUser();

}
