package com.mih.messenger.rest.model;

import java.util.Date;

public class ConversationDto extends ConversationRequest {
    private Long id;
    private Date dateStarted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }
}
