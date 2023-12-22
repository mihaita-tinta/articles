package com.mih.playground.driver;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.ZonedDateTime;
import java.util.UUID;

@Table
public class Driver {
    @PrimaryKey
    private UUID id;
    private final Long startTimestamp;
    private final String nickname;

    public Driver(UUID id, Long startTimestamp, String nickname) {
        this.id = id;
        this.startTimestamp = startTimestamp;
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public String getNickname() {
        return nickname;
    }
}