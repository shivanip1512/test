package com.cannontech.dr.ecobee.message;

public enum Selector {
    IDENTIFIER("identifier");

    private String type;

    Selector(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
