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
        if (response.getStatus().equals(Status.OK)) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().equals(Status.BAD_REQUEST)) {
            return ResponseEntity.badRequest().body(response.getMessage());
        } else {
            return ResponseEntity.internalServerError().body(response.getMessage());
        }
    }


    @GetMapping("/hi")
    public String hello() {
        return "Hello World!";
    }
}
