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

     Boolean getIsEnabled();

    Boolean getIsLocked();

    default Boolean isActive() {
        return getIsEnabled() && !getIsLocked();
    }

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    Boolean getIsPrivate();

    String getProfilePicture();
}