package org.connect.media.service.implementations;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.connect.media.service.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final S3Template s3Template;
    private final S3Presigner presigner;

    @Value("${s3.bucket}")
    private String bucket;

    @Override
    public void uploadFile(String key, MultipartFile file) throws IOException {
       s3Template.upload(bucket, key, file.getInputStream());
    }

    @Override
    public URL getFile(String key, Long linkDuration) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket).key(key).build();

        GetObjectPresignRequest psReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(linkDuration))
                .getObjectRequest(getReq)
                .build();

        return presigner.presignGetObject(psReq).url();
    }

    @Override
    public URL uploadPresignedUrl(String key, Map<String,String> metadata, Long duration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .contentType(MimeTypeUtils.IMAGE_JPEG_VALUE)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(duration))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        return presignedRequest.url();
    }
}
