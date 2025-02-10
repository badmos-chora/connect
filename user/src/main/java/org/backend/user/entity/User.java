package org.backend.user.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.LinkedHashSet;
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
    private Set<Permission> permissions = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "user_follower",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "follower_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","follower_id"}),
            indexes = {
                    @Index(name = "idx_user_id",columnList = "user_id", unique = true),
                    @Index(name = "idx_follower_id",columnList = "follower_id", unique = true)}
    )
    private Set<User> followers = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<User> following = new LinkedHashSet<>();


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
