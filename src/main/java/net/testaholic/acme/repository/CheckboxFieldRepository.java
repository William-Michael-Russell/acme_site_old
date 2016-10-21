package net.testaholic.acme.repository;

import net.testaholic.acme.domain.CheckboxField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the CheckboxField entity.
 */
public interface CheckboxFieldRepository extends JpaRepository<CheckboxField,Long> {

    @Query("select checkboxField from CheckboxField checkboxField where checkboxField.user.login = ?#{principal.username}")
    List<CheckboxField> findByUserIsCurrentUser();

    @Query("select checkboxField from CheckboxField checkboxField where checkboxField.user.login = ?#{principal.username}")
    Page<CheckboxField> findByUserIsCurrentUser(Pageable pageable);

}
