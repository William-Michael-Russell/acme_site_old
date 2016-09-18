package net.testaholic.acme.repository;

import net.testaholic.acme.domain.Text_inputs;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Text_inputs entity.
 */
public interface Text_inputsRepository extends JpaRepository<Text_inputs,Long> {

}
