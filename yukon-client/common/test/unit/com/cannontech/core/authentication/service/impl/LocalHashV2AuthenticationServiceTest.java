package com.cannontech.core.authentication.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public class LocalHashV2AuthenticationServiceTest {
    private final static String INITIAL_PASSWORD = "test$$";
    private final static String INITIAL_PASSWORD_ENCRYPTED =
            "QEcYGLFCgeYqT7AlpppFab5xiKsKjgP21zpLtiXpJ69+ETmAOFJXXKo5YIMj+r+L";
    private LocalHashV2AuthenticationService service;
    private MockYukonUserPasswordDao singleUserPasswordDao;
    private LiteYukonUser someUser;

    @Before
    public void setUp() throws Exception {
        service = new LocalHashV2AuthenticationService();
        UsersEventLogService usersEventLogService = createNiceMock(UsersEventLogService.class);
        singleUserPasswordDao = new MockYukonUserPasswordDao(INITIAL_PASSWORD_ENCRYPTED);
        ReflectionTestUtils.setField(service, "yukonUserPasswordDao", singleUserPasswordDao);
        ReflectionTestUtils.setField(service, "usersEventLogService", usersEventLogService);
        someUser = new LiteYukonUser(100, "testuser", LoginStatusEnum.ENABLED);
    }

    @Test
    public void testLogin() {
        assertTrue("Login failed when it should have succeeded", service.login(someUser, INITIAL_PASSWORD));
        assertFalse("Login succeeded when it should have failed", service.login(someUser, "not the right password"));

        // change password
        service.setPassword(someUser, "new pass");
        assertFalse("Login succeeded when it should have failed", service.login(someUser, "not the right password"));
        assertTrue("Login failed when it should have succeeded", service.login(someUser, "new pass"));
    }

    @Test
    public void testSetPassword() {
        // attempt change with correct password
        service.setPassword(someUser, "deadman");

        assertFalse("Login succeeded when it should have failed", service.login(someUser, "wrong password"));
        assertTrue("Login failed when it should have succeeded", service.login(someUser, "deadman"));

        service.setPassword(someUser, "Algebra Geometry Banana");

        assertFalse("Login succeeded when it should have failed", service.login(someUser, "deadman"));
        assertTrue("Login failed when it should have succeeded", service.login(someUser, "Algebra Geometry Banana"));
    }
}
