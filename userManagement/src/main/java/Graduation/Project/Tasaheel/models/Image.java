package Graduation.Project.Tasaheel.models;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob  // This annotation is important for handling large binary data
    @Column(nullable = false)
    private byte[] data;

    public imageType getType() {
        return type;
    }

    public void setType(imageType type) {
        this.type = type;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private imageType type;

    // This could be a foreign key linking to the user or any other entity
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;


    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    public Image() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
