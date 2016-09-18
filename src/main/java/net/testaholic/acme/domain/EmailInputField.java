package net.testaholic.acme.domain;

import org.hibernate.validator.constraints.Email;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A EmailInputField.
 */
@Entity
@Table(name = "email_input_field")
@Document(indexName = "emailinputfield")
public class EmailInputField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Email
    @Size(min = 6, max = 30)
    @Column(name = "email_field", length = 30, nullable = false)
    private String emailField;

    @ManyToOne
    private User login;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailField() {
        return emailField;
    }

    public void setEmailField(String emailField) {
        this.emailField = emailField;
    }

    public User getLogin() {
        return login;
    }

    public void setLogin(User user) {
        this.login = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailInputField emailInputField = (EmailInputField) o;
        if(emailInputField.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, emailInputField.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EmailInputField{" +
            "id=" + id +
            ", emailField='" + emailField + "'" +
            '}';
    }
}
