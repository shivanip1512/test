package com.cannontech.common.rfn.message.gateway;

import java.io.Serializable;

public class Authentication implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private boolean defaultUser;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isDefaultUser() {
        return defaultUser;
    }
    
    public void setDefaultUser(boolean defaultUser) {
        this.defaultUser = defaultUser;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (defaultUser ? 1231 : 1237);
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Authentication other = (Authentication) obj;
        if (defaultUser != other.defaultUser)
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Authentication [username=%s, password=%s, defaultUser=%s]", username, password, defaultUser);
    }
    
}