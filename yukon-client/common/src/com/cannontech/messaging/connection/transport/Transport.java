package com.cannontech.messaging.connection.transport;

public interface Transport {

    void start() throws TransportException;

    void close() throws TransportException;
}
