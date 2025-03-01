package org.backend.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.enums.UserConnectionType;

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

    @NotNull
    @Column(name = "is_close_friend", nullable = false)
    @Builder.Default
    private Boolean isCloseFriend = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "connection_type", nullable = false)
    @Builder.Default
    private UserConnectionType connectionType = UserConnectionType.FOLLOWING;

    @Builder.Default
    private Instant addedDate = Instant.now();

}
