package com.transithub.document.s3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class S3StorageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;

    public S3StorageService(S3Client s3Client, S3Presigner s3Presigner,
                            @Value("${aws.s3.bucket:transithub-documents-dev}") String bucket) {
        this.s3Client = s3Client; this.s3Presigner = s3Presigner; this.bucket = bucket;
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(b -> b.bucket(bucket));
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(b -> b.bucket(bucket));
        }
    }

    public String uploadFile(String key, byte[] content, String contentType) {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket).key(key).contentType(contentType).build(),
            RequestBody.fromBytes(content));
        return key;
    }

    public String generatePresignedUrl(String key, Duration expiry) {
        var presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expiry)
            .getObjectRequest(b -> b.bucket(bucket).key(key))
            .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(b -> b.bucket(bucket).key(key));
    }

    public List<String> listFiles(String prefix) {
        return s3Client.listObjectsV2(b -> b.bucket(bucket).prefix(prefix))
            .contents().stream().map(S3Object::key).toList();
    }
}
