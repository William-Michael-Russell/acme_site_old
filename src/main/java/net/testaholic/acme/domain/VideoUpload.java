package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A VideoUpload.
 */
@Entity
@Table(name = "video_upload")
@Document(indexName = "videoupload")
public class VideoUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 5000000)
    @Lob
    @Column(name = "video_field", nullable = false)
    private byte[] videoField;

    @Column(name = "video_field_content_type", nullable = false)    
    private String videoFieldContentType;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getVideoField() {
        return videoField;
    }

    public void setVideoField(byte[] videoField) {
        this.videoField = videoField;
    }

    public String getVideoFieldContentType() {
        return videoFieldContentType;
    }

    public void setVideoFieldContentType(String videoFieldContentType) {
        this.videoFieldContentType = videoFieldContentType;
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
        VideoUpload videoUpload = (VideoUpload) o;
        if(videoUpload.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, videoUpload.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "VideoUpload{" +
            "id=" + id +
            ", videoField='" + videoField + "'" +
            ", videoFieldContentType='" + videoFieldContentType + "'" +
            '}';
    }
}
