package com.cannontech.messaging.message;

public class TraceMessage extends BaseMessage {
    private boolean end;
    private int attributes;
    private String trace;

    public TraceMessage() {}

    public TraceMessage(String trace) {
        this.trace = trace;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getAttributes() {
        return attributes;
    }

    public void setAttributes(int attributes) {
        this.attributes = attributes;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    @Override
    public String toString() {
        return this.trace;

    }
}
