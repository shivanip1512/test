package com.cannontech.core.authentication.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalHashAuthenticationServiceTest {
    static final String INITIAL_PASSWORD = "test$$";
    private LocalHashAuthenticationService service;
    private LiteYukonUser someUser;
    private MockYukonUserPasswordDao singleUserPasswordDao;
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
        singleUserPasswordDao = new MockYukonUserPasswordDao(hasher.hashPassword(INITIAL_PASSWORD));
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
}
