package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A AlertPrompt.
 */
@Entity
@Table(name = "alert_prompt")
@Document(indexName = "alertprompt")
public class AlertPrompt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "alert_name", nullable = false)
    private String alertName;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
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
        AlertPrompt alertPrompt = (AlertPrompt) o;
        if(alertPrompt.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, alertPrompt.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AlertPrompt{" +
            "id=" + id +
            ", alertName='" + alertName + "'" +
            '}';
    }
}
