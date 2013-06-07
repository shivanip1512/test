package com.cannontech.messaging.connection.roguewave;

import java.util.List;

import com.cannontech.messaging.connection.ConnectionBase;
import com.cannontech.messaging.connection.transport.Transport;
import com.roguewave.vsj.DefineCollectable;

public abstract class RWConnectionBase<T extends Transport> extends ConnectionBase<T> {
    private int port;
    private Thread reader;
    private List<DefineCollectable> mappings;

    protected RWConnectionBase() {
        this(1515); // Default port
    }

    protected RWConnectionBase(int port) {
        this.port = port;
    }

    public List<DefineCollectable> getMappings() {
        return mappings;
    }

    public void setMappings(List<DefineCollectable> mappings) {
        this.mappings = mappings;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    protected Thread getReader() {
        return reader;
    }

    protected void setReader(Thread reader) {
        this.reader = reader;
    }
}
