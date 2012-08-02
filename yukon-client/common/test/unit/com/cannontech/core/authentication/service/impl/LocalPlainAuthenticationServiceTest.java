package com.cannontech.core.authentication.service.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalPlainAuthenticationServiceTest {
    static final String INITIAL_PASSWORD = "test$$";
    private LocalPlainAuthenticationService service;
    private LiteYukonUser someUser;
    private MockYukonUserPasswordDao singleUserPasswordDao;

    @Before
    public void setUp() throws Exception {
        service = new LocalPlainAuthenticationService();
        singleUserPasswordDao = new MockYukonUserPasswordDao(INITIAL_PASSWORD);
        service.setYukonUserPasswordDao(singleUserPasswordDao);
        someUser = new LiteYukonUser(100, "testuser", LoginStatusEnum.ENABLED);
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
    public void testSetPassword() {
        service.setPassword(someUser, "bobbob");

        // check that password actually changed.
        assertTrue("Password didn't actually change", service.login(someUser, "bobbob"));
    }
}
