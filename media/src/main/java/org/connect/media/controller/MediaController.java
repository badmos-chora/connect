package org.connect.media.controller;


import lombok.RequiredArgsConstructor;
import org.connect.media.enums.Status;
import org.connect.media.service.interfaces.MediaService;
import org.connect.media.utils.ServiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) {
        ServiceResponse<?> response = mediaService.uploadFile(file);
        return response.getResponseEntityWithBody();
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestPart MultipartFile file) {
        ServiceResponse<?> response = mediaService.uploadFile(file);
        return response.getResponseEntityWithBody();
    }


    @GetMapping("/hi")
    public String hello() {
        return "Hello World!";
    }
}
