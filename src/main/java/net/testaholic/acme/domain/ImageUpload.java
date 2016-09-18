package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ImageUpload.
 */
@Entity
@Table(name = "image_upload")
@Document(indexName = "imageupload")
public class ImageUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 5000000)
    @Lob
    @Column(name = "image_field", nullable = false)
    private byte[] imageField;

    @Column(name = "image_field_content_type", nullable = false)    
    private String imageFieldContentType;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImageField() {
        return imageField;
    }

    public void setImageField(byte[] imageField) {
        this.imageField = imageField;
    }

    public String getImageFieldContentType() {
        return imageFieldContentType;
    }

    public void setImageFieldContentType(String imageFieldContentType) {
        this.imageFieldContentType = imageFieldContentType;
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
        ImageUpload imageUpload = (ImageUpload) o;
        if(imageUpload.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, imageUpload.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageUpload{" +
            "id=" + id +
            ", imageField='" + imageField + "'" +
            ", imageFieldContentType='" + imageFieldContentType + "'" +
            '}';
    }
}
