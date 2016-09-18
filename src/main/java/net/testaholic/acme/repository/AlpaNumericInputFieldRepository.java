package net.testaholic.acme.repository;

import net.testaholic.acme.domain.AlpaNumericInputField;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AlpaNumericInputField entity.
 */
public interface AlpaNumericInputFieldRepository extends JpaRepository<AlpaNumericInputField,Long> {

    @Query("select alpaNumericInputField from AlpaNumericInputField alpaNumericInputField where alpaNumericInputField.user.login = ?#{principal.username}")
    List<AlpaNumericInputField> findByUserIsCurrentUser();

}
