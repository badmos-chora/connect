package org.backend.user.repository.interfaces;

import org.backend.user.embeddable.UserConnectionId;
import org.backend.user.entity.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserConnectionRepository extends JpaRepository<UserConnection, UserConnectionId>, JpaSpecificationExecutor<UserConnection> {
}