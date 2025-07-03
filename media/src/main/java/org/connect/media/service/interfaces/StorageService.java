package org.connect.media.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface StorageService {
    void uploadFile(String key, MultipartFile file) throws IOException;

    URL getFile(String fileName);
}
