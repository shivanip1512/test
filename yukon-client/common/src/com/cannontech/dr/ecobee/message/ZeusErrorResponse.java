package com.cannontech.dr.ecobee.message;

public class ZeusErrorResponse {
    private String error;
    private String description;

    public ZeusErrorResponse() {
    }

    public ZeusErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}