package org.backend.user.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.user.service.interfaces.FileServices;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
@AllArgsConstructor
public class FileServicesImpl implements FileServices {

    @Override
    public String saveNewFile(MultipartFile file, Long loggedInUserId, String path, String extension)  throws IOException {
        Path basePath = Paths.get(path);
        if(!Files.exists(basePath))
            Files.createDirectories(basePath);
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
         String newFileName = loggedInUserId + "_" + dateFormat.format(new Date())+"."+extension;
         Path uploadPath = basePath.resolve(newFileName);
         Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
         return uploadPath.toString();
    }
}
