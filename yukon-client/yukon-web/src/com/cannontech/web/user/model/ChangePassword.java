package com.cannontech.web.user.model;


/**
 * Four fields of actual user inputs.
 * RetrySeconds may or may not be set by validator.
 */
public class ChangePassword {

    Integer userId;
    String oldPassword;
    String newPassword;
    String confirmPassword;

    /**
     * This field may be set by the validator
     */
    Long retrySeconds;


    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    public String getPassword1() {
        return newPassword;
    }
    public void setPassword1(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getPassword2() {
        return confirmPassword;
    }
    public void setPassword2(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Long getRetrySeconds() {
        return retrySeconds;
    }
    public void setRetrySeconds(Long retrySeconds) {
        this.retrySeconds = retrySeconds;
    }
}
