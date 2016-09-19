package net.testaholic.acme.repository;

import net.testaholic.acme.domain.RadioCheckboxField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the RadioCheckboxField entity.
 */
public interface RadioCheckboxFieldRepository extends JpaRepository<RadioCheckboxField,Long> {

    @Query("select radioCheckboxField from RadioCheckboxField radioCheckboxField where radioCheckboxField.user.login = ?#{principal.username}")
    List<RadioCheckboxField> findByUserIsCurrentUser();

    @Query("select radioCheckboxField from RadioCheckboxField radioCheckboxField where radioCheckboxField.user.login = ?#{principal.username}")
    Page<RadioCheckboxField> findByUserIsCurrentUser(Pageable pageable);

}
