package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A CheckboxField.
 */
@Entity
@Table(name = "checkbox_field")
@Document(indexName = "checkboxfield")
public class CheckboxField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "checkbox_option", nullable = false)
    private String checkboxOption;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckboxOption() {
        return checkboxOption;
    }

    public void setCheckboxOption(String checkboxOption) {
        this.checkboxOption = checkboxOption;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CheckboxField checkboxField = (CheckboxField) o;
        if(checkboxField.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, checkboxField.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CheckboxField{" +
            "id=" + id +
            ", checkboxOption='" + checkboxOption + "'" +
            '}';
    }
}
