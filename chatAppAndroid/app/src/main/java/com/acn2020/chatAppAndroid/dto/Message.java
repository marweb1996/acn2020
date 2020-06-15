package com.acn2020.chatAppAndroid.dto;

import java.util.Date;

public class Message {
    String clientId;
    String message;
    Date timestamp;

    public Message() {
    }

    public Message(String clientId, String message, Date timestamp) {
        this.clientId = clientId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
