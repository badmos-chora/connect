package org.connect.media.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;
import org.connect.media.enums.Status;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "response")
@Getter
@Builder
public class ServiceResponse<T> {
    private Status status;
    private String message;
    private T data;
    private Map<String, List<T>> map;

    @JsonIgnore
    public ResponseEntity<ServiceResponse<T>> getResponseEntityWithBody() {
        return switch (status) {
            case OK -> ResponseEntity.ok(this);
            case BAD_REQUEST -> ResponseEntity.badRequest().body(this);
            default -> ResponseEntity.internalServerError().body(this);
        };
    }
}
