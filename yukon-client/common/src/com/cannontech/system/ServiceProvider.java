package com.cannontech.system;

public enum ServiceProvider {

    HONEYWELL("Honeywell");

    private String certificateSubject;

    private ServiceProvider(String certificateSubject) {
        this.certificateSubject = certificateSubject;
    }

    public String getCertificateSubject() {
        return certificateSubject;
    }

}
