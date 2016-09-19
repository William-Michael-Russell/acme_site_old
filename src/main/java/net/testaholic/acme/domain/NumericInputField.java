package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A NumericInputField.
 */
@Entity
@Table(name = "numeric_input_field")
@Document(indexName = "numericinputfield")
public class NumericInputField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 21)
    @Column(name = "numeric_field", nullable = false)
    private Integer numericField;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumericField() {
        return numericField;
    }

    public void setNumericField(Integer numericField) {
        this.numericField = numericField;
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
        NumericInputField numericInputField = (NumericInputField) o;
        if(numericInputField.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, numericInputField.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "NumericInputField{" +
            "id=" + id +
            ", numericField='" + numericField + "'" +
            '}';
    }
}
