package org.backend.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.backend.user.embeddable.UserConnectionId;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity(name = "user_connections")
@Table(name = "user_connections", indexes = {
        @Index(name = "idx_user_connections_following_id", columnList = "following_id"),
        @Index(name = "idx_user_connections_follower_id", columnList = "follower_id")

})
public class UserConnection {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "followerId", column = @Column(name = "follower_id")),
            @AttributeOverride(name = "followingId", column = @Column(name = "following_id"))
    })
    private UserConnectionId userConnectionId;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "follower_id", nullable = false, insertable = false, updatable = false)
    private User follower;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "following_id", nullable = false, insertable = false, updatable = false)
    private User following;

    private Boolean isCloseFriend = false;

    private Instant addedDate = Instant.now();

}
