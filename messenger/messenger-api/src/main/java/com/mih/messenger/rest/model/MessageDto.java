package com.mih.messenger.rest.model;

import com.mih.messenger.domain.User;

import java.util.Date;

public class MessageDto {
    private Long id;
    private String content;
    private Date dateSent;
    private Date dateDelivered;
    private Date dateViewed;
    private String from;
    private Long conversationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDateDelivered() {
        return dateDelivered;
    }

    public void setDateDelivered(Date dateDelivered) {
        this.dateDelivered = dateDelivered;
    }

    public Date getDateViewed() {
        return dateViewed;
    }

    public void setDateViewed(Date dateViewed) {
        this.dateViewed = dateViewed;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", conversationId=" + conversationId +
                '}';
    }
}
