package com.mih.messenger.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Conversation conversation;

    @ManyToOne
    @NotNull
    private User from;

    @NotNull
    private String content;

    @NotNull
    private Date dateSent;
    private Date dateDelivered;
    private Date dateViewed;
    private Long avatar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
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

    public Long getAvatar() {
        return avatar;
    }

    public void setAvatar(Long avatar) {
        this.avatar = avatar;
    }

    public static Message from(User sender, Conversation conversation, String content, Long avatar) {
        Message message = new Message();
        message.setContent(content);
        message.setConversation(conversation);
        message.setFrom(sender);
        message.setDateSent(new Date());
        message.setAvatar(avatar);
        return message;
    }

    public Message with(Long id) {
        this.id = id;
        return this;
    }
}
