package com.cannontech.web.common.captcha.model;

public class Captcha {

    private String remoteAddr; // The user's IP address (which is passed to the reCAPTCHA servers)
    private String response; // The user's entered solution to solve the CAPTCHA

    public Captcha(String remoteAddr,String response) {
        this.remoteAddr = remoteAddr;
        this.response = response;
    }

    public Captcha(String response) {
        this.response = response;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getResponse() {
        return response;
    }
}