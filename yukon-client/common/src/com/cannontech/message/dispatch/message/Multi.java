package com.cannontech.message.dispatch.message;

import java.util.Vector;

import com.cannontech.message.util.Message;

public final class Multi<E extends Message> extends Message {	
    private Vector<E> vector;

    public Multi() {
    
    }
    
    public Vector<E> getVector() {
        if (vector == null) {
            vector = new Vector<E>();
        }
        return vector;
    }

    public void setVector(Vector<E> newVector) {
        vector = newVector;
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("com.cannontech.message.dispatch.message.Multi:\n  ");

        Vector<E> v = getVector();
        if (v != null) {
            for (final E e : v) {
                sb.append(e + "\n");
            }
            return sb.toString();
        } 

        sb.append("Empty multi");
        return sb.toString();
    }
}
