package com.cannontech.core.authentication.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
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
        assertTrue(service.login(someUser, INITIAL_PASSWORD), "Login failed when it should have succeeded");
        assertFalse(service.login(someUser, "not the right password"), "Login succeeded when it should have failed");

        // change password
        service.setPassword(someUser, "new pass", someUser);
        assertFalse(service.login(someUser, "not the right password"), "Login succeeded when it should have failed");
        assertTrue(service.login(someUser, "new pass"), "Login failed when it should have succeeded");
    }

    @Test
    public void testSetPassword() {
        // attempt change with correct password
        service.setPassword(someUser, "deadman", someUser);

        assertFalse(service.login(someUser, "wrong password"), "Login succeeded when it should have failed");
        assertTrue(service.login(someUser, "deadman"), "Login failed when it should have succeeded");

        service.setPassword(someUser, "Algebra Geometry Banana", someUser);

        assertFalse(service.login(someUser, "deadman"), "Login succeeded when it should have failed");
        assertTrue(service.login(someUser, "Algebra Geometry Banana"), "Login failed when it should have succeeded");
    }
}
