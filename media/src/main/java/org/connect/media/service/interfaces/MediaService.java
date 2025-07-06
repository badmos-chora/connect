package org.connect.media.service.interfaces;

import org.connect.media.utils.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    ServiceResponse<?> uploadFile(MultipartFile file);

    ServiceResponse<?> getFile(String fileName);

    ServiceResponse<?> getUploadLink(String fileName);
}
