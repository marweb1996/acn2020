package com.budiyev.android.libdemoapp.dto;

public class Message {
    String clientId;
    String message;

    public Message() {
    }

    public Message(String clientId, String message) {
        this.clientId = clientId;
        this.message = message;
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
}
