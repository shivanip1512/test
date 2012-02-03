package com.cannontech.yukon.api.amr.endpoint;

public enum ErrorCode {
    UNSUPPORTED_DEVICE("unsupportedDevice"),
    ;

    private final String xmlValue;

    private ErrorCode(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    public String getXmlValue() {
        return xmlValue;
    }
}
