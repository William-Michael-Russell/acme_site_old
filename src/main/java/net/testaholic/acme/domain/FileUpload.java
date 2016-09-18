package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A FileUpload.
 */
@Entity
@Table(name = "file_upload")
@Document(indexName = "fileupload")
public class FileUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 5000000)
    @Lob
    @Column(name = "field_field", nullable = false)
    private byte[] fieldField;

    @Column(name = "field_field_content_type", nullable = false)    
    private String fieldFieldContentType;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFieldField() {
        return fieldField;
    }

    public void setFieldField(byte[] fieldField) {
        this.fieldField = fieldField;
    }

    public String getFieldFieldContentType() {
        return fieldFieldContentType;
    }

    public void setFieldFieldContentType(String fieldFieldContentType) {
        this.fieldFieldContentType = fieldFieldContentType;
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
        FileUpload fileUpload = (FileUpload) o;
        if(fileUpload.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, fileUpload.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FileUpload{" +
            "id=" + id +
            ", fieldField='" + fieldField + "'" +
            ", fieldFieldContentType='" + fieldFieldContentType + "'" +
            '}';
    }
}
