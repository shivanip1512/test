package com.cannontech.core.authentication.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalHashAuthenticationServiceTest {
    static final String INITIAL_PASSWORD = "test$$";
    private LocalHashAuthenticationService service;
    private LiteYukonUser someUser;
    private SingleUserPasswordDao singleUserPasswordDao;
    private PasswordHasher hasher;

    @Before
    public void setUp() throws Exception {
        service = new LocalHashAuthenticationService();
        hasher = new PasswordHasher() {
                    public String hashPassword(String password) {
                        return password + "*";
                    }
                };
        service.setPasswordHasher(hasher);
        singleUserPasswordDao = new SingleUserPasswordDao(hasher.hashPassword(INITIAL_PASSWORD));
        service.setYukonUserPasswordDao(singleUserPasswordDao);
        someUser = new LiteYukonUser(100, "testuser", "Enabled");
    }

    @Test
    public void testLogin() {
        boolean b = service.login(someUser, INITIAL_PASSWORD);
        assertTrue("Login failed when it should have succeeded", b);

        b = service.login(someUser, "not the right password");
        assertFalse("Login succeeded when it should have failed", b);

        // change password
        service.setPassword(someUser, "new pass");
        b = service.login(someUser, "not the right password");
        assertFalse("Login succeeded when it should have failed", b);
        b = service.login(someUser, "new pass");
        assertTrue("Login failed when it should have succeeded", b);
    }

    @Test
    public void testChangePassword() {
        // attempt change with incorrect current
        try {
            service.changePassword(someUser, "whatever", "blah");
            fail("Change password should have thrown exception");
        } catch (BadAuthenticationException e) {
        }
        
        // attempt change with correct password
        try {
            service.changePassword(someUser, INITIAL_PASSWORD, "qwerty");
        } catch (BadAuthenticationException e) {
            fail("Change password should not have thrown exception");
        }
        
        // check that password isn't stored as plain text
        String currentPassword = singleUserPasswordDao.getCurrentPassword();
        assertNotSame("Password appears to be plain text", "qwerty", currentPassword);
        String currentPassHash = hasher.hashPassword("qwerty");
        assertEquals("Password doesn't match computed hash", currentPassHash, currentPassword);
     }

    @Test
    public void testSetPassword() {
        String before = singleUserPasswordDao.getCurrentPassword();
        // attempt change with correct password
        service.setPassword(someUser, "deadman");
        String after = singleUserPasswordDao.getCurrentPassword();
        assertNotSame("Password didn't change", before, after);
        
        // check that password isn't stored as plain text
        String currentPassHash = hasher.hashPassword("deadman");
        assertEquals("Password doesn't match computed hash", currentPassHash, after);
    }

}
