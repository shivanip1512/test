package com.cannontech.messaging.message.dispatch;

import java.util.Vector;

import com.cannontech.messaging.message.BaseMessage;

public class MultiMessage<E extends BaseMessage> extends BaseMessage {

    private Vector<E> vector;

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
