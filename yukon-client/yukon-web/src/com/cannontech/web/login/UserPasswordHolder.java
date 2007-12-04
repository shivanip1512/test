package com.cannontech.web.login;

public class UserPasswordHolder {
    private final String username;
    private final String password;
    
    public UserPasswordHolder(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
}
