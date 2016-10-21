package net.testaholic.acme.repository;

import net.testaholic.acme.domain.DropDownField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DropDownField entity.
 */
public interface DropDownFieldRepository extends JpaRepository<DropDownField,Long> {

    @Query("select dropDownField from DropDownField dropDownField where dropDownField.user.login = ?#{principal.username}")
    List<DropDownField> findByUserIsCurrentUser();


    @Query("select dropDownField from DropDownField dropDownField where dropDownField.user.login = ?#{principal.username}")
    Page<DropDownField> findByUserIsCurrentUser(Pageable pageable);

}
