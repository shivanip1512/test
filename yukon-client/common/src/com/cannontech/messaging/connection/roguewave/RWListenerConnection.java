package com.cannontech.messaging.connection.roguewave;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.cannontech.event.Event;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.ConnectionException;
import com.cannontech.messaging.connection.ListenerConnection;
import com.cannontech.messaging.connection.event.ConnectionEventHandler;
import com.cannontech.messaging.connection.event.InboundConnectionEvent;
import com.cannontech.messaging.connection.event.InboundConnectionEventHandler;
import com.cannontech.messaging.connection.transport.socket.ServerSocketTransport;
import com.cannontech.messaging.message.BaseMessage;

public class RWListenerConnection extends RWConnectionBase<ServerSocketTransport> implements ListenerConnection,
    ConnectionEventHandler {

    private int backlog = 50; // The serverSocket listen queue depth
    protected final InboundConnectionEvent serverConnectionEvent;
    private final List<RWConnection> serverConnectionList;

    public RWListenerConnection(int port) {
        super(port);
        serverConnectionEvent = new InboundConnectionEvent();
        serverConnectionList = new LinkedList<>();
    }

    @Override
    protected void connect() throws ConnectionException {
        ServerSocketTransport transport = new ServerSocketTransport();
        setTransport(transport);
        transport.setPort(getPort());
        transport.setBacklog(getBacklog());

        transport.start();

        Thread listener = new Thread() {
            @Override
            public void run() {
                try {
                    executeListener();
                }
                catch (Throwable e) {
                    safeDisconnect(e);
                }
            }
        };
        listener.setDaemon(true);
        listener.start();
        setReader(listener);

        setState(ConnectionState.Connected);
    }

    private void executeListener() {
        try {
            ServerSocketTransport transport = getTransport();
            while (true) {

                Socket socket = transport.accept();

                RWConnection newConnection = new RWConnection(socket);

                synchronized (serverConnectionList) {
                    if (isDisconnectRequested()) {
                        // We can NOT accept incoming connection request as this server is being
                        // closed;
                        return;
                    }
                    newConnection.getConnectionEvent().registerHandler(this);
                    serverConnectionList.add(newConnection);
                }

                newConnection.setMappings(getMappings());
                serverConnectionEvent.fire(this, newConnection);
                newConnection.start();
            }
        }
        catch (Exception e) {
            safeDisconnect(e);
        }
    }

    @Override
    public void sendMessage(BaseMessage message) {}

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    @Override
    public Event<InboundConnectionEventHandler> getInboundConnectionEvent() {
        return serverConnectionEvent;
    }

    @Override
    public void onConnectionEvent(Connection source, ConnectionState state) {
        if (state == ConnectionState.Closed || state == ConnectionState.Error) {
            synchronized (serverConnectionList) {
                source.getConnectionEvent().unregisterHandler(this);
                serverConnectionList.remove(source);
            }
        }
    }
}
