package com.mih.messenger.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
public class Conversation {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    private List<User> participants;

    @NotNull
    private Date dateStarted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public static Conversation between(User... participants) {
        if (participants == null || participants.length < 2) {
            throw new IllegalArgumentException("There should be at least 2 participants to the conversation");
        }

        Conversation conversation = new Conversation();
        conversation.setParticipants(Arrays.asList(participants));
        conversation.setDateStarted(new Date());
        return conversation;
    }
}
