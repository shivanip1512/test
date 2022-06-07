package com.cannontech.messaging.serialization.thrift;

import java.nio.charset.Charset;

import org.apache.thrift.TByteArrayOutputStream;
import org.apache.thrift.TConfiguration;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Memory buffer-based implementation of the TTransport interface.
 */
public class YukonTMemoryBuffer extends TTransport {

    private TByteArrayOutputStream arrOutputStream;
    private TConfiguration configuration;
    private int position;

    /**
     * Create a YukonTMemoryBuffer with an initial buffer size of <i>size</i>. The
     * internal buffer will grow as necessary to accommodate the size of the data
     * being written to it.
     *
     */
    public YukonTMemoryBuffer(int size) {
        super();
        configuration = new TConfiguration();
        arrOutputStream = new TByteArrayOutputStream(size);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        byte[] src = arrOutputStream.get();
        int amtToRead = (len > arrOutputStream.len() - position ? arrOutputStream.len() - position : len);
        if (amtToRead > 0) {
            System.arraycopy(src, position, buf, off, amtToRead);
            position += amtToRead;
        }
        return amtToRead;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        arrOutputStream.write(buf, off, len);
    }

    @Override
    public TConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Output the contents of the memory buffer as a String, using the supplied encoding
     **/
    public String toString(Charset charset) {
        return arrOutputStream.toString(charset);
    }

    public String inspect() {
        StringBuilder buf = new StringBuilder();
        byte[] bytes = arrOutputStream.toByteArray();
        for (int i = 0; i < bytes.length; i++) {
            buf.append(position == i ? "==>" : "").append(Integer.toHexString(bytes[i] & 0xff)).append(" ");
        }
        return buf.toString();
    }

    public int length() {
        return arrOutputStream.size();
    }

    public byte[] getArray() {
        return arrOutputStream.get();
    }

    @Override
    public void open() throws TTransportException {
        /* Do nothing */
    }

    @Override
    public void close() {
        /* Do nothing */
    }

    @Override
    public void updateKnownMessageSize(long size) throws TTransportException {
        /* Do nothing */
    }

    @Override
    public void checkReadBytesAvailable(long numBytes) throws TTransportException {
        /* Do nothing */
    }

}
