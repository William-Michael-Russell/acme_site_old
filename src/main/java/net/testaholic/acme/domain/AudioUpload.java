package net.testaholic.acme.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A AudioUpload.
 */
@Entity
@Table(name = "audio_upload")
@Document(indexName = "audioupload")
public class AudioUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 5000000)
    @Lob
    @Column(name = "audio_field", nullable = false)
    private byte[] audioField;

    @Column(name = "audio_field_content_type", nullable = false)    
    private String audioFieldContentType;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getAudioField() {
        return audioField;
    }

    public void setAudioField(byte[] audioField) {
        this.audioField = audioField;
    }

    public String getAudioFieldContentType() {
        return audioFieldContentType;
    }

    public void setAudioFieldContentType(String audioFieldContentType) {
        this.audioFieldContentType = audioFieldContentType;
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
        AudioUpload audioUpload = (AudioUpload) o;
        if(audioUpload.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, audioUpload.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AudioUpload{" +
            "id=" + id +
            ", audioField='" + audioField + "'" +
            ", audioFieldContentType='" + audioFieldContentType + "'" +
            '}';
    }
}
