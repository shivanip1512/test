package com.cannontech.core.authentication.service.impl;

public interface PasswordHasher {
    public String hashPassword(String password);
}
