package com.cannontech.messaging.message;

public class BooleanData {
    private boolean value;

    public BooleanData() {
        this(false);
    }

    public BooleanData(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
