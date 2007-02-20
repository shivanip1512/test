package com.cannontech.core.authentication.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalPlainAuthenticationServiceTest {
    static final String INITIAL_PASSWORD = "test$$";
    private LocalPlainAuthenticationService service;
    private LiteYukonUser someUser;
    private SingleUserPasswordDao singleUserPasswordDao;

    @Before
    public void setUp() throws Exception {
        service = new LocalPlainAuthenticationService();
        singleUserPasswordDao = new SingleUserPasswordDao(INITIAL_PASSWORD);
        service.setYukonUserPasswordDao(singleUserPasswordDao);
        someUser = new LiteYukonUser(100, "testuser", "Enabled");
    }

    @Test
    public void testGetPassword() {
        String password = service.getPassword(someUser);
        assertEquals("Retrieved password doesn't match", INITIAL_PASSWORD, password);
        
        // change password
        service.setPassword(someUser, "new pass");
        password = service.getPassword(someUser);
        assertEquals("Retrieved password doesn't match", "new pass", password);
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
        
        // check that password actually changed (this works because this is "PLAIN"
        String currentPassword = singleUserPasswordDao.getCurrentPassword();
        assertEquals("Password didn't actually change", "qwerty", currentPassword);
    }

    @Test
    public void testSetPassword() {
        service.setPassword(someUser, "bobbob");
        
        // check that password actually changed (this works because this is "PLAIN"
        String currentPassword = singleUserPasswordDao.getCurrentPassword();
        assertEquals("Password didn't actually change", "bobbob", currentPassword);
    }

}
