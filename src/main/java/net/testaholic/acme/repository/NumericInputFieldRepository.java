package net.testaholic.acme.repository;

import net.testaholic.acme.domain.NumericInputField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the NumericInputField entity.
 */
public interface NumericInputFieldRepository extends JpaRepository<NumericInputField,Long> {

    @Query("select numericInputField from NumericInputField numericInputField where numericInputField.user.login = ?#{principal.username}")
    List<NumericInputField> findByUserIsCurrentUser();


    @Query("select numericInputField from NumericInputField numericInputField where numericInputField.user.login = ?#{principal.username}")
    Page<NumericInputField> findByUserIsCurrentUser(Pageable pageable);

}
