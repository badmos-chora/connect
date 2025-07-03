package org.connect.media.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.connect.media.enums.Status;
import org.connect.media.service.interfaces.MediaService;
import org.connect.media.service.interfaces.StorageService;
import org.connect.media.utils.ServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaServiceImpl implements MediaService {
    private final StorageService storageService;

    @Value("${s3.folder.profile}")
    private String profilePath;

    @Override
    public ServiceResponse<?> uploadFile(MultipartFile file) {
        ServiceResponse.ServiceResponseBuilder<String> builder = ServiceResponse.builder();
        try {
            String fileName = profilePath+"/"+file.getOriginalFilename();
            storageService.uploadFile(fileName,file);
            URL url = storageService.getFile(fileName);
            builder.data(url.toExternalForm());
            builder.status(Status.OK);
        }catch (Exception e){
            log.error("Exception occurred while uploading file {}",file.getOriginalFilename(),e);
            builder.status(Status.ERROR);
        }
        return builder.build();
    }
}
