package com.mih.messenger.rest.model;

import java.util.List;

public class ConversationRequest {
    private List<UserDto> participants;

    public List<UserDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDto> participants) {
        this.participants = participants;
    }
}
