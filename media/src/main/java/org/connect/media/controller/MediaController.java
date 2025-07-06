package org.connect.media.controller;


import lombok.RequiredArgsConstructor;
import org.connect.media.enums.Status;
import org.connect.media.service.interfaces.MediaService;
import org.connect.media.utils.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/file")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) {
        ServiceResponse<?> response = mediaService.uploadFile(file);
        return response.getResponseEntityWithBody();
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam String fileName) {
        ServiceResponse<?> response = mediaService.getFile(fileName);
        return response.getResponseEntityWithBody();
    }

    @GetMapping("/upload")
    public ResponseEntity<?> getUploadLink(@RequestParam String fileName) {
        ServiceResponse<?> response = mediaService.getUploadLink(fileName);
        return response.getResponseEntityWithBody();
    }



    @GetMapping("/hi")
    public String hello() {
        return "Hello World!";
    }
}
