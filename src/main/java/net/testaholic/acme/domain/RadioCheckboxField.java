package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A RadioCheckboxField.
 */
@Entity
@Table(name = "radio_checkbox_field")
@Document(indexName = "radiocheckboxfield")
public class RadioCheckboxField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "radio_checkbox", nullable = false)
    private Boolean radioCheckbox;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isRadioCheckbox() {
        return radioCheckbox;
    }

    public void setRadioCheckbox(Boolean radioCheckbox) {
        this.radioCheckbox = radioCheckbox;
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
        RadioCheckboxField radioCheckboxField = (RadioCheckboxField) o;
        if(radioCheckboxField.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, radioCheckboxField.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RadioCheckboxField{" +
            "id=" + id +
            ", radioCheckbox='" + radioCheckbox + "'" +
            '}';
    }
}
