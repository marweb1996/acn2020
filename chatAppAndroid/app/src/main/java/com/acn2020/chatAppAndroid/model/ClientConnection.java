package com.acn2020.chatAppAndroid.model;

public class ClientConnection {
    private String clientId;
    private String aesKey;
    private String ipAddress;
    private String hostName;

    public ClientConnection() {
    }

    public ClientConnection(String clientId, String aesKey, String ipAddress, String hostName) {
        this.clientId = clientId;
        this.aesKey = aesKey;
        this.ipAddress = ipAddress;
        this.hostName = hostName;
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

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
