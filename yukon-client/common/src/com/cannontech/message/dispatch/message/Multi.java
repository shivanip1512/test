package com.cannontech.message.dispatch.message;

import java.util.Vector;

import com.cannontech.message.util.Message;

public final class Multi<E extends Message> extends Message {
    
    private Vector<E> vector;

    public Vector<E> getVector() {
        if (vector == null) {
            vector = new Vector<E>();
        }
        return vector;
    }

    public void setVector(Vector<E> vector) {
        this.vector = vector;
    }

    @Override
    public String toString() {
        return String.format("Multi [vector=%s]", vector);
    }

}