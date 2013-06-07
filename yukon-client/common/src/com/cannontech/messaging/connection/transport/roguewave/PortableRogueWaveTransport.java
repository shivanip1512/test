package com.cannontech.messaging.connection.transport.roguewave;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.connection.transport.socket.SocketTransport;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.PortableInputStream;
import com.roguewave.vsj.PortableOutputStream;

public class PortableRogueWaveTransport extends SocketTransport {

    private PortableInputStream pIn;
    private PortableOutputStream pOut;
    private CollectableStreamer streamer;
    private List<DefineCollectable> mappings;

    public PortableRogueWaveTransport() {
        setMappings(null);
    }

    public PortableRogueWaveTransport(Socket socket) {
        super(socket);
        setMappings(null);
    }

    public PortableRogueWaveTransport(String host, int port) {
        super(host, port);
        setMappings(null);
    }

    @Override
    public void start() throws TransportException {
        super.start();
        pIn = new PortableInputStream(getInputStream());
        pOut = new PortableOutputStream(getOutputStream());

        streamer = new CollectableStreamer();

        for (DefineCollectable collectable : mappings) {
            streamer.register(collectable);
        }
    }

    @Override
    public void close() {
        pIn = null;
        pOut = null;
        streamer = null;

        super.close();
    }

    public Object read() {
        try {
            return pIn.restoreObject(streamer);
        }
        catch (IOException e) {
            throw new TransportException("Error while reading on the roguewave streamer", e);
        }
    }

    public void write(Object message) {
        try {
            pOut.saveObject(message, streamer);
        }
        catch (IOException e) {
            throw new TransportException("Error while writing on the roguewave streamer", e);
        }
    }

    public List<DefineCollectable> getMappings() {
        return mappings;
    }

    public void setMappings(List<DefineCollectable> mappings) {
        if (mappings == null) {
            mappings = new ArrayList<DefineCollectable>();
        }

        this.mappings = mappings;
    }
}
