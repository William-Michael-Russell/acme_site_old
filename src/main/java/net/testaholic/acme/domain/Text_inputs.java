package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Text_inputs.
 */
@Entity
@Table(name = "text_inputs")
@Document(indexName = "text_inputs")
public class Text_inputs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Text_inputs text_inputs = (Text_inputs) o;
        if(text_inputs.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, text_inputs.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Text_inputs{" +
            "id=" + id +
            '}';
    }
}
