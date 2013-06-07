package com.cannontech.messaging.connection.transport.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.cannontech.messaging.connection.transport.Transport;
import com.cannontech.messaging.connection.transport.TransportException;

public class ServerSocketTransport implements Transport {

    private int port = 1515; // The port the web server listens on
    private int backlog = 50; // The serverSocket listen queue depth
    private ServerSocket serverSocket = null; // The servers listening socket

    @Override
    public void start() {
        try {
            if (serverSocket == null) {
                serverSocket = new ServerSocket(getPort(), getBacklog());
            }
        }
        catch (IOException e) {
            throw new TransportException("Failed to start server socket", e);
        }
    }

    @Override
    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                throw new TransportException("Failed to close server socket", e);
            }
            finally {
                serverSocket = null;
            }
        }
    }

    public Socket accept() throws TransportException {
        if (serverSocket == null) {
            throw new TransportException("Transport not started or already closed");
        }

        try {
            return serverSocket.accept();
        }
        catch (IOException e) {
            throw new TransportException("Failed to accept connection", e);
        }
    }

    public ServerSocket getSocket() {
        return serverSocket;
    }

    public void setSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }
}
