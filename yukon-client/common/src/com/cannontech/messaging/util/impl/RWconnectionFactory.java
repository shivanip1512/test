package com.cannontech.messaging.util.impl;

import java.util.List;

import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.roguewave.RWConnection;
import com.cannontech.messaging.util.ConnectionFactory;
import com.roguewave.vsj.DefineCollectable;

public class RWconnectionFactory implements ConnectionFactory {

    List<DefineCollectable> collectableMapping;

    public RWconnectionFactory(List<DefineCollectable> collectableMapping) {
        this.collectableMapping = collectableMapping;
    }
    
    @Override
    public Connection createConnection(String host, int port) {
        RWConnection conn = new RWConnection(host, port);

        conn.setMappings(collectableMapping);

        return conn;
    }
}
