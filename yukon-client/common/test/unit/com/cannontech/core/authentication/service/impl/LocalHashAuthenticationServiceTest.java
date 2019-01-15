package com.cannontech.core.authentication.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Note: If this test ever fails, there is a good chance that something has changed
 * that will prevent customers from logging into their system.
 */
public class LocalHashAuthenticationServiceTest {
    private final static String INITIAL_PASSWORD = "test$$";
    private final static String INITIAL_PASSWORD_ENCRYPTED = "ce2fa859f3669918b9f5d836f394cc94722771a";
    private LocalHashAuthenticationService service;
    private LiteYukonUser someUser;
    private MockYukonUserPasswordDao singleUserPasswordDao;

    @Before
    public void setUp() throws Exception {
        service = new LocalHashAuthenticationService();
        UsersEventLogService usersEventLogService = createNiceMock(UsersEventLogService.class);
        singleUserPasswordDao = new MockYukonUserPasswordDao(INITIAL_PASSWORD_ENCRYPTED);
        ReflectionTestUtils.setField(service, "yukonUserPasswordDao", singleUserPasswordDao);
        ReflectionTestUtils.setField(service, "usersEventLogService", usersEventLogService);
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
        String before = singleUserPasswordDao.getDigest(null);
        // attempt change with correct password
        service.setPassword(someUser, "deadman");
        String after = singleUserPasswordDao.getDigest(null);
        assertNotSame("Password didn't change", before, after);

        // check that password isn't stored as plain text
        assertEquals("Password doesn't match computed hash", "34a8f998bb5b9b2f7b19465caafc4e98039447", after);
    }

    @Test
    public void testSHAPassword() throws NoSuchAlgorithmException {
        service.setPassword(someUser, "Algebra Geometry Banana");
        String hashed = singleUserPasswordDao.getDigest(null);
        Assert.assertEquals("Hash doesn't match precomputed value", "fe6335916832f16b198396e8b27f743bd81ff", hashed);
    }
}
