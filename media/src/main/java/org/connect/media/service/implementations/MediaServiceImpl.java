package org.connect.media.service.implementations;

import lombok.RequiredArgsConstructor;
import org.connect.media.enums.Status;
import org.connect.media.service.interfaces.MediaService;
import org.connect.media.utils.ServiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private String folderName = "media";
    @Override
    public ServiceResponse<?> uploadFile(MultipartFile file) {
        try {
            Path basePath = Paths.get(folderName);
            if (!Files.exists(basePath))
                Files.createDirectories(basePath);
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String newFileName = dateFormat.format(new Date()) + "_" + file.getOriginalFilename();
            Path uploadPath = basePath.resolve(newFileName);
            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            return ServiceResponse.builder()
                    .status(Status.OK)
                    .message("File uploaded successfully")
                    .data(uploadPath.toString())
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .status(Status.BAD_REQUEST)
                    .message("File upload failed: " + e.getMessage())
                    .build();
        }
    }
}
