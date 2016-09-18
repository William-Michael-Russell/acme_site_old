package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A AlpaNumericInputField.
 */
@Entity
@Table(name = "alpa_numeric_input_field")
@Document(indexName = "alpanumericinputfield")
public class AlpaNumericInputField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "alpha_numeric_field", length = 20, nullable = false)
    private String alphaNumericField;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlphaNumericField() {
        return alphaNumericField;
    }

    public void setAlphaNumericField(String alphaNumericField) {
        this.alphaNumericField = alphaNumericField;
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
        AlpaNumericInputField alpaNumericInputField = (AlpaNumericInputField) o;
        if(alpaNumericInputField.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, alpaNumericInputField.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlpaNumericInputField{" +
            "id=" + id +
            ", alphaNumericField='" + alphaNumericField + "'" +
            '}';
    }
}
