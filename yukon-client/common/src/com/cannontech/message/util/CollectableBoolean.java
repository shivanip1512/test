package com.cannontech.message.util;

public class CollectableBoolean {
    private boolean value;
    public CollectableBoolean() {
        value = false;
    }
    public CollectableBoolean(boolean value) {
        this.value = value;
    }
    public void setValue(boolean value) {
        this.value = value;
    }
    public boolean getValue() {
        return value;
    }
}
