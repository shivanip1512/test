package com.cannontech.core.authentication.service;

import java.security.GeneralSecurityException;

public interface CryptoService {
    
    public String encrypt(String input) throws GeneralSecurityException;
    
    public String decrypt(String input) throws GeneralSecurityException;
    
}
