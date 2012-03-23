package com.cannontech.selenium.solvents.common.userLogin.model;

public class Login {
    private String username;
    private String password1;
    private String password2;

    /** 
     * This is a helper method for creating basic logins.  This will use the supplied password
     * for password1 and password2
     */
    public Login(String username, String password) {
        this.username = username;
        this.password1 = password;
        this.password2 = password;
    }
    
    public Login(String username, String password1, String password2) {
        this.username = username;
        this.password1 = password1;
        this.password2 = password2;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

}