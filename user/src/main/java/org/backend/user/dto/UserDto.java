package org.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * DTO for {@link org.backend.user.entity.User}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(Long id, @NotBlank String userName, String firstName, String lastName,
                      @Email @NotBlank String email, @NotBlank String password) implements Serializable {
}