package com.cannontech.web.api.token;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ForgotPasswordRequest {

    private String forgottenPasswordField;
    private String gRecaptchaResponse;

    public String getForgottenPasswordField() {
        return forgottenPasswordField;
    }

    public void setForgottenPasswordField(String forgottenPasswordField) {
        this.forgottenPasswordField = forgottenPasswordField;
    }

    public String getgRecaptchaResponse() {
        return gRecaptchaResponse;
    }

    public void setgRecaptchaResponse(String gRecaptchaResponse) {
        this.gRecaptchaResponse = gRecaptchaResponse;
    }

    @JsonCreator
    ForgotPasswordRequest() {
    }

}
