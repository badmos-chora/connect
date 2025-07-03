package org.connect.media.service.implementations;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.connect.media.service.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

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
    public URL getFile(String key) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket).key(key).build();

        GetObjectPresignRequest psReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getReq)
                .build();

        return presigner.presignGetObject(psReq).url();
    }
}
