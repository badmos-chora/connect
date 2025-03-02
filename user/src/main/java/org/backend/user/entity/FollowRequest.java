package org.backend.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.backend.user.enums.FollowRequestStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.Instant;

@Entity(name = "follow_request")
@Table(name = "follow_request")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_user_id",nullable = false)
    @NotNull
    private User senderUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_user_id",nullable = false)
    @NotNull
    private User receiverUser;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FollowRequestStatus status = FollowRequestStatus.PENDING;

    @NotNull
    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private Instant sentAt = Instant.now();

}
