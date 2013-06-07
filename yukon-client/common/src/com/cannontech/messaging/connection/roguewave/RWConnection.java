package com.cannontech.messaging.connection.roguewave;

import java.net.Socket;

import com.cannontech.messaging.connection.ConnectionException;
import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.connection.transport.roguewave.PortableRogueWaveTransport;
import com.cannontech.messaging.message.BaseMessage;

public class RWConnection extends RWConnectionBase<PortableRogueWaveTransport> {
    private String host;
    private Socket socket;

    public RWConnection(String host, int port) {
        super(port);
        setHost(host);
    }

    public RWConnection(Socket socket) {
        super();
        setSocket(socket);
    }

    @Override
    protected void connect() throws ConnectionException {
        PortableRogueWaveTransport transport;
        if (getSocket() != null) {
            transport = new PortableRogueWaveTransport(getSocket());
        }
        else {
            transport = new PortableRogueWaveTransport(getHost(), getPort());
        }
        setTransport(transport);
        transport.setMappings(getMappings());
        transport.start();

        Thread reader = new Thread() {
            @Override
            public void run() {
                try {
                    executeReader();
                }
                catch (Throwable e) {
                    safeDisconnect(e);
                }
            }
        };

        setReader(reader);
        reader.setDaemon(true);
        reader.start();

        setState(ConnectionState.Connected);
    }

    private void executeReader() {
        try {
            while (true) {
                Object obj = getTransport().read();

                if (obj == null) {
                    // End of stream
                    break;
                }

                if (obj instanceof BaseMessage) {
                    messageEvent.fire(this, (BaseMessage) obj);
                }
                else {
                    // TODO should we log the fact that this is not a message?
                }
            }
        }
        catch (TransportException e) {
            safeDisconnect(e);
            return;
        }

        safeDisconnect();
    }

    @Override
    public void sendMessage(BaseMessage message) {
        PortableRogueWaveTransport transport = getTransport();
        if (transport == null) {
            throw new ConnectionException("Impossible to send messge because transport is not initialized");
        }
        transport.write(message);
    }

    @Override
    protected void disconnect() {
        super.disconnect();

        if (getReader() != null) {
            getReader().interrupt();
            setReader(null);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
