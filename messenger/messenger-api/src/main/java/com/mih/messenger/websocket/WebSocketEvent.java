package com.mih.messenger.websocket;

import com.mih.messenger.domain.Message;
import com.mih.messenger.domain.User;

import java.util.HashMap;
import java.util.Map;

public class WebSocketEvent {

    private Map<String, Object> attributes;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public static WebSocketEvent message(Message message) {
        WebSocketEvent event = new WebSocketEvent();
        event.attributes = new HashMap<>();
        event.attributes.put("id", message.getId());
        event.attributes.put("conversationId", message.getConversation().getId());
        event.attributes.put("dateDelivered", message.getDateDelivered());
        event.attributes.put("dateViewed", message.getDateViewed());
        event.attributes.put("content", message.getContent());
        event.attributes.put("avatar", message.getAvatar());
        event.attributes.put("conversationId", message.getConversation().getId());
        return event;
    }

    public static WebSocketEvent disconnected(User user) {
        WebSocketEvent event = new WebSocketEvent();
        event.attributes = new HashMap<>();
        event.attributes.put("type", "disconnect");
        event.attributes.put("userId", user.getId());
        return event;
    }

    @Override
    public String toString() {
        return "WebSocketEvent{" +
                "attributes=" + attributes +
                '}';
    }
}
