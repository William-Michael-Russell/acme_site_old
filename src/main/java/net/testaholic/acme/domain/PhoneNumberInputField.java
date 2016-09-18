package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PhoneNumberInputField.
 */
@Entity
@Table(name = "phone_number_input_field")
@Document(indexName = "phonenumberinputfield")
public class PhoneNumberInputField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^[(]{0,1}[0-9]{3}[)]{0,1}[-\\s\\.]{0,1}[0-9]{3}[-\\s\\.]{0,1}[0-9]{4}$")
    @Column(name = "phone_number_field", length = 20, nullable = false)
    private String phoneNumberField;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumberField() {
        return phoneNumberField;
    }

    public void setPhoneNumberField(String phoneNumberField) {
        this.phoneNumberField = phoneNumberField;
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
        PhoneNumberInputField phoneNumberInputField = (PhoneNumberInputField) o;
        if(phoneNumberInputField.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, phoneNumberInputField.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PhoneNumberInputField{" +
            "id=" + id +
            ", phoneNumberField='" + phoneNumberField + "'" +
            '}';
    }
}
