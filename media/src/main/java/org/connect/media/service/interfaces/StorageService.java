package org.connect.media.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public interface StorageService {
    void uploadFile(String key, MultipartFile file) throws IOException;

    URL getFile(String fileName, Long linkDuration);

    URL uploadPresignedUrl(String key, Map<String,String> metadata, Long duration);
}
