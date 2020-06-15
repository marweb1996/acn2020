package com.acn2020.chatAppAndroid.helper;

import com.acn2020.chatAppAndroid.model.ClientConnection;

public class ConnectionKeeper {
    private ClientConnection connection;
    private static ConnectionKeeper single_instance = null;

    private ConnectionKeeper() {
        this.connection = null;
    }

    public static ConnectionKeeper getInstance() {
        if (single_instance == null)
            single_instance = new ConnectionKeeper();

        return single_instance;
    }

    public void setConnection(ClientConnection clientConnection) {
        this.connection = clientConnection;
    }

    public ClientConnection getConnection() {
        return connection;
    }
}
