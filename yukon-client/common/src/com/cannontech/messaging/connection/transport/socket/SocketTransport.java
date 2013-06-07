package com.cannontech.messaging.connection.transport.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.cannontech.messaging.connection.transport.Transport;
import com.cannontech.messaging.connection.transport.TransportException;

public class SocketTransport implements Transport {

    private int port = 1515;
    private String host;
    private Socket socket = null;
    private InputStream inStream;
    private OutputStream outStream;

    public SocketTransport() {}

    public SocketTransport(Socket socket) {
        this.socket = socket;
    }

    public SocketTransport(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() throws TransportException {
        try {
            if (socket == null) {
                socket = new Socket(getHost(), getPort());
            }

            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        }
        catch (IOException e) {
            throw new TransportException("Failed to open socket", e);
        }
    }

    @Override
    public void close() throws TransportException {

        try {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e) {
            throw new TransportException("Failed to close socket", e);
        }
        finally {
            socket = null;
            inStream = null;
            outStream = null;
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InputStream getInputStream() {
        return inStream;
    }

    public OutputStream getOutputStream() {
        return outStream;
    }
}
