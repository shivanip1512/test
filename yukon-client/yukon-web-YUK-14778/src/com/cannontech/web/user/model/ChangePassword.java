package com.cannontech.web.user.model;

/**
 * Default retry password seconds = 10
 */
public class ChangePassword {

    private int userId;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private long retrySeconds = 10;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
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
    public long getRetrySeconds() {
        return retrySeconds;
    }
    public void setRetrySeconds(long retrySeconds) {
        this.retrySeconds = retrySeconds;
    }
}
