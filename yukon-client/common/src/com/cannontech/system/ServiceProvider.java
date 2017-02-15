package com.cannontech.system;

public enum ServiceProvider {

    HONEYWELL("HONEYWELL");

    private String certificateSubject;

    private ServiceProvider(String certificateSubject) {
        this.certificateSubject = certificateSubject;
    }

    public String getCertificateSubject() {
        return certificateSubject;
    }

}
