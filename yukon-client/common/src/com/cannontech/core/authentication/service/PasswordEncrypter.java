package com.cannontech.core.authentication.service;

import com.cannontech.core.authentication.model.AuthenticationCategory;

/**
 * Exactly one class which implements {@link PasswordSetProvider} should also implement this class.
 * It should be the same class which handles the supporting authentication type for
 * {@link AuthenticationCategory#ENCRYPTED}. This is meant to be used for encrypting plain text
 * passwords and ONLY for encrypting plain text passwords.  See YUK-11346 and YUK-12045.
 */
public interface PasswordEncrypter {
    String encryptPassword(String password);
}
