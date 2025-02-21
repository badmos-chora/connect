package org.backend.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.backend.user.enums.FollowRequestStatus;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.Instant;

@Entity(name = "follow_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    @NotNull
    private User senderUser;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    @NotNull
    private User receiverUser;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private FollowRequestStatus status = FollowRequestStatus.PENDING;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt = Instant.now();

}
