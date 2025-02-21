package org.backend.user.projections;

/**
 * Projection for {@link org.backend.user.entity.User}
 */
public interface UserInfoProjection {
    Long getId();

    String getUserName();

    String getFirstName();

    String getLastName();

    String getEmail();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}