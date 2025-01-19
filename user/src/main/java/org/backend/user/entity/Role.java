package org.backend.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;


    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new LinkedHashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Role role = (Role) obj;
        return id.equals(role.id);
    }
}