package com.cannontech.web.api.token;

public class ForgotPasswordResponse {
    private String status;
    private String message;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
