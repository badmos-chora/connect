package org.connect.media.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.UUID;

@Table("media")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Media {
    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String path;

    @Column
    private String url;

    @Column
    private String description;

    @Column
    private String size;

    @CreatedDate
    @Column(value = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Column(value = "updated_at")
    private Date updatedAt;

    @Column(value = "related_user_id")
    private Long relatedUserId;
}