package org.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.backend.user.enums.FollowRequestStatus;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link org.backend.user.entity.FollowRequest}
 */
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FollowRequestDto implements Serializable {
    @NotNull
    Long id;
    Long senderUserId;
    String senderUserName;
    String senderFullName;
    Long receiverUserId;
    String receiverUserName;
    String receiverFullName;
    @NotNull
    FollowRequestStatus status;
    String sentAt;
}