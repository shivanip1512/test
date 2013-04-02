package com.cannontech.core.authentication.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthenticationCategoryTest {
    @Test
    public void testPasswordEncrypter() {
        for (AuthType authType : AuthType.values()) {
        }
        for (AuthenticationCategory authenticationCategory : AuthenticationCategory.values()) {
            if (authenticationCategory == AuthenticationCategory.ENCRYPTED) {
                
            }
            authenticationCategory.getSupportingAuthType();
        }
        fail("Not yet implemented");
    }
}
