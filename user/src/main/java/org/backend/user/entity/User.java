package org.backend.user.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity(name = "users")
@Table(indexes = {
        @Index(name = "idx_user_user_name_unq", columnList = "user_name", unique = true)
})
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true ,name = "user_name")
    private String userName;


    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"))
    @Fetch(FetchMode.JOIN)
    private Set<Role> roles = new LinkedHashSet<>();

    @NotNull
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @NotNull
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Fetch(FetchMode.JOIN)
    private Set<Permission> permissions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "senderUser", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FollowRequest> sentFollowRequests = new ArrayList<>();

    @OneToMany(mappedBy = "receiverUser", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FollowRequest> receivedFollowRequests = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "follower", orphanRemoval = true)
    private List<UserConnection> followingList = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserConnection> followerList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
