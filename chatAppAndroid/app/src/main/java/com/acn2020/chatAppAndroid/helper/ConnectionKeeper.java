package com.acn2020.chatAppAndroid.helper;

import com.acn2020.chatAppAndroid.model.ClientConnection;

import java.util.ArrayList;
import java.util.List;

public class ConnectionKeeper {
    private List<ClientConnection> connections;
    private static ConnectionKeeper single_instance = null;

    private ConnectionKeeper() {
        this.connections = new ArrayList<>();
    }

    public static ConnectionKeeper getInstance() {
        if (single_instance == null)
            single_instance = new ConnectionKeeper();

        return single_instance;
    }

    public void addConnection(ClientConnection clientConnection) {
        if(!connections.contains(clientConnection)) {
            connections.add(clientConnection);
        }
    }

    public void removeConnection(ClientConnection clientConnection) {
        connections.remove(clientConnection);
    }

    public ClientConnection getLastConnection() {
        return connections.get(connections.size() - 1);
    }
}
