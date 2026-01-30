package dev.jeankarlo.vehiclerenting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "media_assets")
public class MediaAsset {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(max = 500)
    @NotNull
    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Size(max = 255)
    @NotNull
    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Size(max = 255)
    @NotNull
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Size(max = 100)
    @NotNull
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Size(max = 20)
    @Column(name = "extension", length = 20)
    private String extension;

    @NotNull
    @Column(name = "file_size_in_bytes", nullable = false)
    private Long fileSizeInBytes;

    @Size(max = 50)
    @NotNull
    @Column(name = "access_level", nullable = false, length = 50)
    private String accessLevel;

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
}