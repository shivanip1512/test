package com.cannontech.web.common.captcha.model;

public class Captcha {
    
    private String remoteAddr; // The user's IP address (which is passed to the reCAPTCHA servers)
    private String challenge; // Describes the CAPTCHA which the user is solving
    private String response; // The user's entered solution to solve the CAPTCHA
    
    public Captcha(String remoteAddr, String challenge, String response) {
        this.remoteAddr = remoteAddr;
        this.challenge = challenge;
        this.response = response;
    }
    
    public String getRemoteAddr() {
        return remoteAddr;
    }
    
    public String getChallenge() {
        return challenge;
    }
    
    public String getResponse() {
        return response;
    }
}