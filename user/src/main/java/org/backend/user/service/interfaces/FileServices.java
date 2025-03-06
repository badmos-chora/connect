package org.backend.user.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileServices {
    String saveNewFile(MultipartFile file, Long loggedInUserId, String path, String type) throws IOException;
}
