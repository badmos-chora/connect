package org.connect.media.repository;

import org.connect.media.entity.Media;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface MediaRepository extends Repository<Media, UUID> {
}
