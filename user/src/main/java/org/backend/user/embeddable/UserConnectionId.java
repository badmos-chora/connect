package org.backend.user.embeddable;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class UserConnectionId {
  private Long followerId;
    private Long followingId;
}