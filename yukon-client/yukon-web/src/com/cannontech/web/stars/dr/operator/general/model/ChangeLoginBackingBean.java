package com.cannontech.web.stars.dr.operator.general.model;

public class ChangeLoginBackingBean {
    private String customerLoginGroupName;
    private boolean loginEnabled;
    private String username;
    private String password1;
    private String password2;

    public String getCustomerLoginGroupName() {
        return customerLoginGroupName;
    }
    public void setCustomerLoginGroupName(String customerLoginGroupName) {
        this.customerLoginGroupName = customerLoginGroupName;
    }

    public boolean isLoginEnabled() {
        return loginEnabled;
    }
    public void setLoginEnabled(boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
    }
    public void setLoginEnabled(String userStatus) {
        if ("enabled".equalsIgnoreCase(userStatus)) {
            this.loginEnabled = true;
            return;
        }
            
        this.loginEnabled = false;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        if (username.equalsIgnoreCase("(none")) {
            this.username = null;
        } else {
            this.username = username;
        }
    }
    
    public String getPassword1() {
        return password1;
    }
    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}