package com.budiyev.android.libdemoapp.model;

public class ClientConnection {
    private String clientId;
    private String aesKey;
    private String ipAddress;

    public ClientConnection() {
    }

    public ClientConnection(String clientId, String aesKey, String ipAddress) {
        this.clientId = clientId;
        this.aesKey = aesKey;
        this.ipAddress = ipAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
