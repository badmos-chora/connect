package org.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.backend.user.projections.UserConnectionProjection;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.backend.user.entity.User}
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDTO implements Serializable {
    @NotBlank
    private final String userName;
    private final String fullName;
    @NotNull
    private final Boolean isPrivate;
    private final Long followingCount;
    private final Long followerCount;
    private final List<UserConnectionProjection> followingList;
    private final List<UserConnectionProjection> followerList;
}