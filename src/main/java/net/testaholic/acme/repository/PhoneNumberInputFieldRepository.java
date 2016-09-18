package net.testaholic.acme.repository;

import net.testaholic.acme.domain.PhoneNumberInputField;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PhoneNumberInputField entity.
 */
public interface PhoneNumberInputFieldRepository extends JpaRepository<PhoneNumberInputField,Long> {

    @Query("select phoneNumberInputField from PhoneNumberInputField phoneNumberInputField where phoneNumberInputField.user.login = ?#{principal.username}")
    List<PhoneNumberInputField> findByUserIsCurrentUser();


    @Query("select phoneNumberInputField from PhoneNumberInputField phoneNumberInputField where phoneNumberInputField.user.login = ?#{principal.username}")
    Page<PhoneNumberInputField> findByUserIsCurrentUser(Pageable pageable);

}
