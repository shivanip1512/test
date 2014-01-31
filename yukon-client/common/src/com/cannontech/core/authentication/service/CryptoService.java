package com.cannontech.core.authentication.service;

import java.security.GeneralSecurityException;

@Deprecated
public interface CryptoService {
    /**
     * @deprecated Use {@link AESPasswordBasedCrypto()}
     */
    @Deprecated
    public String encrypt(String input) throws GeneralSecurityException;
    /**
     * @deprecated Use {@link AESPasswordBasedCrypto()}
     */
    @Deprecated
    public String decrypt(String input) throws GeneralSecurityException;
    
}
