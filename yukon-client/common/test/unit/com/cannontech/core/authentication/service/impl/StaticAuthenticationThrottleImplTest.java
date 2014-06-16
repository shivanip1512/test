package com.cannontech.core.authentication.service.impl;

import static com.cannontech.core.roleproperties.YukonRole.*;
import static com.cannontech.core.roleproperties.YukonRoleProperty.*;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.authentication.model.AuthenticationThrottleDto;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.YukonUserDaoImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.UnitTestUtil;

public class StaticAuthenticationThrottleImplTest {

    private PasswordPolicyServiceImpl passwordPolicyService;
    private StaticAuthenticationThrottleServiceImpl staticAuthenticationThrottleService;

    private static final Duration TEST_DURATION_BUFFER = Duration.standardSeconds(3);
    private static final Duration FIVE_SECONDS = Duration.standardSeconds(5);

    private static final LiteYukonUser USER_ONE = new LiteYukonUser(1, "userOne");
    private static final LiteYukonUser USER_TWO = new LiteYukonUser(2, "userTwo");
    private static final LiteYukonUser USER_NO_POLICY = new LiteYukonUser(3, "userNoPolicy");
    private static final LiteYukonUser USER_SYSTEM_POLICY = new LiteYukonUser(4, "userSystemPolicy");
    private static final LiteYukonUser USER_SHORT_LOCKOUT = new LiteYukonUser(5, "userShortLockout");

    private static final LiteYukonUser[] USERS_IN_TEST_ARRAY = { USER_ONE, USER_TWO, USER_NO_POLICY,
        USER_SYSTEM_POLICY, USER_SHORT_LOCKOUT };

    private static final MockRolePropertyDaoImpl rolePropertyDaoMock = new MockRolePropertyDaoImpl();
    {
        // @formatter:off
        rolePropertyDaoMock.setupRolesFor(USER_ONE)
        .withRoleProperty(LOCKOUT_DURATION, 6)
        .withRoleProperty(LOCKOUT_THRESHOLD, 3)
        .withRoleProperty(MAXIMUM_PASSWORD_AGE, 20)
        .withRoleProperty(MINIMUM_PASSWORD_AGE, 1)
        .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 6)
        .withRoleProperty(PASSWORD_HISTORY, 3)
        .withRoleProperty(POLICY_QUALITY_CHECK, 2)
        .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, true)
        .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS, false)
        .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, true)
        .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, true);

        rolePropertyDaoMock.setupRolesFor(USER_TWO)
        .withRoleProperty(LOCKOUT_DURATION, 120)
        .withRoleProperty(LOCKOUT_THRESHOLD, 8)
        .withRoleProperty(MAXIMUM_PASSWORD_AGE, 15)
        .withRoleProperty(MINIMUM_PASSWORD_AGE, 0)
        .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 9)
        .withRoleProperty(PASSWORD_HISTORY, 10)
        .withRoleProperty(POLICY_QUALITY_CHECK, 1)
        .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, false)
        .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS, true)
        .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, false)
        .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, false);

        rolePropertyDaoMock.setupRolesFor(USER_NO_POLICY)
        .withRoleProperty(LOCKOUT_DURATION, 0)
        .withRoleProperty(LOCKOUT_THRESHOLD, 0)
        .withRoleProperty(MAXIMUM_PASSWORD_AGE, 0)
        .withRoleProperty(MINIMUM_PASSWORD_AGE, 0)
        .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 0)
        .withRoleProperty(PASSWORD_HISTORY, 0)
        .withRoleProperty(POLICY_QUALITY_CHECK, 0)
        .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, true)
        .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS,true)
        .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, true)
        .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, true);

        rolePropertyDaoMock.setupRolesFor(USER_SHORT_LOCKOUT)
        .withRoleProperty(LOCKOUT_DURATION, 1)
        .withRoleProperty(LOCKOUT_THRESHOLD, 3)
        .withRoleProperty(MAXIMUM_PASSWORD_AGE, 0)
        .withRoleProperty(MINIMUM_PASSWORD_AGE, 0)
        .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 0)
        .withRoleProperty(PASSWORD_HISTORY, 0)
        .withRoleProperty(POLICY_QUALITY_CHECK, 0)
        .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, true)
        .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS, true)
        .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, true)
        .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, true);

        rolePropertyDaoMock.setupRolesFor(USER_SYSTEM_POLICY).withRole(PASSWORD_POLICY, false);
     // @formatter:on
    }

    private static final YukonUserDao yukonUserDaoMock = new YukonUserDaoImpl() {
        @Override
        public LiteYukonUser findUserByUsername(java.lang.String username) {
            for (LiteYukonUser userInTest : USERS_IN_TEST_ARRAY) {
                if (userInTest.getUsername().equals(username)) {
                    return userInTest;
                }
            }

            return null;
        }
    };

    @Before
    public void init() {
        passwordPolicyService = new PasswordPolicyServiceImpl();
        ReflectionTestUtils.setField(passwordPolicyService, "rolePropertyDao", rolePropertyDaoMock);

        staticAuthenticationThrottleService = new StaticAuthenticationThrottleServiceImpl();
        ReflectionTestUtils.setField(staticAuthenticationThrottleService, "passwordPolicyService",
            passwordPolicyService);
        ReflectionTestUtils.setField(staticAuthenticationThrottleService, "yukonUserDao", yukonUserDaoMock);
    }

    @Test
    public void testLoginLockouts() throws Exception {
        for (LiteYukonUser user : USERS_IN_TEST_ARRAY) {
            testLoginLockout(user);
        }

        // The username uses the static authentication throttle defaults
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(null);
        Assert.assertFalse(doLoginAttempsLockTheAccount("Made up username", passwordPolicy.getLockoutThreshold()));
        Assert.assertTrue(doLoginAttempsLockTheAccount("Made up username", 1));
    }

    @Test
    public void testLockoutDurations() throws Exception {
        for (LiteYukonUser user : USERS_IN_TEST_ARRAY) {
            testLockoutDuration(user);
        }

        // The username uses the static authentication throttle defaults
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(null);
        Assert.assertFalse(doLoginAttempsLockTheAccount("Made up username", passwordPolicy.getLockoutThreshold()));
        try {
            tryLogin("Made up username", 1);
        } catch (AuthenticationThrottleException e) {
            Duration throttleDuration = Duration.standardSeconds(e.getThrottleSeconds());
            Duration roughLockoutDuration = passwordPolicy.getLockoutDuration();

            Assert.assertTrue(
                "The lockout duration is longer than it should be. [Expected: "
                    + roughLockoutDuration.plus(TEST_DURATION_BUFFER) + ", Actual:" + throttleDuration + "]",
                throttleDuration.isShorterThan(roughLockoutDuration.plus(TEST_DURATION_BUFFER)));
            Assert.assertTrue(
                "The lockout duration is shorter than it should be.  [Expected: "
                    + roughLockoutDuration.minus(TEST_DURATION_BUFFER) + ", Actual:" + throttleDuration + "]",
                throttleDuration.isLongerThan(roughLockoutDuration.minus(TEST_DURATION_BUFFER)));
        }
    }

    @Test
    public void testLoginSucceeded() throws Exception {
        for (LiteYukonUser user : USERS_IN_TEST_ARRAY) {
            testLoginSucceded(user);
        }

        // The username uses the static authentication throttle defaults
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(null);
        try {
            tryLogin("Made up username", passwordPolicy.getLockoutThreshold());
        } catch (AuthenticationThrottleException e) {
            Assert.fail();
        }

        staticAuthenticationThrottleService.loginSucceeded("Made up username");

        try {
            tryLogin("Made up username", 1);
        } catch (AuthenticationThrottleException e) {
            Assert.fail();
        }
    }

    @Test
    public void testRemoveAuthenticationThrottles() throws Exception {
        for (LiteYukonUser user : USERS_IN_TEST_ARRAY) {
            testRemoveAuthenticationThrottle(user);
        }

        // The username uses the static authentication throttle defaults
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(null);
        try {
            tryLogin("Made up username", passwordPolicy.getLockoutThreshold());
        } catch (AuthenticationThrottleException e) {
            Assert.fail();
        }

        staticAuthenticationThrottleService.removeAuthenticationThrottle("Made up username");

        try {
            tryLogin("Made up username", 1);
        } catch (AuthenticationThrottleException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetAuthenticationThrottleData() throws Exception {
        // Check and make sure all of the logins are empty
        for (LiteYukonUser user : USERS_IN_TEST_ARRAY) {
            Assert.assertNull(staticAuthenticationThrottleService.getAuthenticationThrottleData(user.getUsername()));
        }
        Assert.assertNull(staticAuthenticationThrottleService.getAuthenticationThrottleData("Made up username"));

        // Run up to 10 login attempts on each user or get it's first lockout..
        doLoginAttempsLockTheAccount(USER_ONE, 10);
        testAuthenticationThrottleDtoData(USER_ONE.getUsername(), Duration.standardMinutes(6), 3);

        doLoginAttempsLockTheAccount(USER_TWO, 8);
        testAuthenticationThrottleDtoData(USER_TWO.getUsername(), Duration.standardMinutes(120), 8);

        doLoginAttempsLockTheAccount(USER_NO_POLICY, 10);
        testAuthenticationThrottleDtoData(USER_NO_POLICY.getUsername(), Duration.standardMinutes(0), 10);

        doLoginAttempsLockTheAccount(USER_SYSTEM_POLICY, 10);
        testAuthenticationThrottleDtoData(USER_SYSTEM_POLICY.getUsername(), Duration.standardMinutes(5), 5);

        // Run 10 login attempts on the random user name that doesn't have an account.
        for (int i = 0; i < 10; i++) {
            try {
                tryLogin("made up username", 1);
            } catch (AuthenticationThrottleException e) {}
        }
        testAuthenticationThrottleDtoData("made up username", Duration.standardMinutes(5), 5);
    }

    @Test
    public void testSelfUnlocking() throws Exception {
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(USER_SHORT_LOCKOUT);
        Assert.assertTrue(doLoginAttempsLockTheAccount(USER_SHORT_LOCKOUT, passwordPolicy.getLockoutThreshold() + 1));
    }

    /**
     * This method attempts a login the number of times supplied
     * 
     * @throws AuthenticationThrottleException
     */
    private void tryLogin(String username, int numberOfTries) throws AuthenticationThrottleException {
        for (int loginAttempts = 0; loginAttempts < numberOfTries; loginAttempts++) {
            staticAuthenticationThrottleService.loginAttempted(username);
        }
    }

    /**
     * This method tries to login an account multiple times. If the account becomes locked it will return
     * true. If not it will return false.
     */
    private boolean doLoginAttempsLockTheAccount(LiteYukonUser user, int numberOfTries) {
        return doLoginAttempsLockTheAccount(user.getUsername(), numberOfTries);
    }

    private boolean doLoginAttempsLockTheAccount(String username, int numberOfTries) {
        try {
            tryLogin(username, numberOfTries);
        } catch (AuthenticationThrottleException e) {
            return true;
        }

        return false;
    }

    /**
     * This method tests the login lockout ranges.
     */
    private void testLoginLockout(LiteYukonUser user) {
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);
        int lockoutThreshold = passwordPolicy.getLockoutThreshold();

        Assert.assertFalse(doLoginAttempsLockTheAccount(user, lockoutThreshold));
        if (passwordPolicy.getLockoutDuration().isLongerThan(Duration.ZERO)) {
            Assert.assertTrue(doLoginAttempsLockTheAccount(user, 1));
        } else {
            // The user does not have a lockout duration. So the user will not be locked out.
            Assert.assertFalse(doLoginAttempsLockTheAccount(user, 1));
        }
    }

    /**
     * This method tests the lockout duration for the supplied user.
     */
    private void testLockoutDuration(LiteYukonUser user) {
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);

        try {
            tryLogin(user.getUsername(), passwordPolicy.getLockoutThreshold() + 1);
        } catch (AuthenticationThrottleException e) {
            Duration throttleDuration = Duration.standardSeconds(e.getThrottleSeconds());
            Duration passwordPolicyLockoutDuration = passwordPolicy.getLockoutDuration();

            Assert.assertTrue("The lockout duration is longer than it should be.",
                throttleDuration.isShorterThan(passwordPolicyLockoutDuration.plus(TEST_DURATION_BUFFER)));
            Assert.assertTrue("The lockout duration is shorter than it should be.",
                throttleDuration.isLongerThan(passwordPolicyLockoutDuration.minus(TEST_DURATION_BUFFER)));
            return;
        }

        if (passwordPolicy.getLockoutDuration().isLongerThan(Duration.ZERO)) {
            Assert.fail();
        }
    }

    /**
     * This method tests that the login has been cleaned up when a login succeeded
     */
    private void testLoginSucceded(LiteYukonUser user) {
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);

        try {
            tryLogin(user.getUsername(), passwordPolicy.getLockoutThreshold() + 1);
        } catch (AuthenticationThrottleException e) {}

        staticAuthenticationThrottleService.loginSucceeded(user.getUsername());

        try {
            tryLogin(user.getUsername(), 1);
        } catch (AuthenticationThrottleException e) {
            Assert.fail();
        }
    }

    /**
     * This method tests that the login has been removed when the throttle has been manually removed
     */
    private void testRemoveAuthenticationThrottle(LiteYukonUser user) {
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(user);

        try {
            tryLogin(user.getUsername(), passwordPolicy.getLockoutThreshold() + 1);
        } catch (AuthenticationThrottleException e) {}

        staticAuthenticationThrottleService.removeAuthenticationThrottle(user.getUsername());

        try {
            tryLogin(user.getUsername(), 1);
        } catch (AuthenticationThrottleException e) {
            Assert.fail();
        }
    }

    private void testAuthenticationThrottleDtoData(String username, Duration expectedThrottleDuration,
            int expectedRetryCount) {
        UnitTestUtil.adjustSystemTimeBySeconds(5);
        Instant now = Instant.now();
        AuthenticationThrottleDto authenticationThrottleData =
            staticAuthenticationThrottleService.getAuthenticationThrottleData(username);

        Duration throttleDuration = Duration.standardSeconds(authenticationThrottleData.getThrottleDurationSeconds());
        Assert.assertEquals(expectedThrottleDuration, throttleDuration);
        Assert.assertEquals(expectedRetryCount, authenticationThrottleData.getRetryCount());

        // The user can't be locked out since there is no duration.
        if (throttleDuration.equals(Duration.ZERO)) {
            return;
        }

        // Testing last failed login time
        Instant lastFailedLoginTime = new Instant(authenticationThrottleData.getLastFailedLoginTime());
        Instant roughExpectedLastFailedLoginTime = now.minus(FIVE_SECONDS);

        Assert.assertTrue("The last failed login for " + username + " is earlier than expected. [Expected: "
            + lastFailedLoginTime + ", Actual:" + roughExpectedLastFailedLoginTime.minus(TEST_DURATION_BUFFER) + "]",
            lastFailedLoginTime.isAfter(roughExpectedLastFailedLoginTime.minus(TEST_DURATION_BUFFER)));
        Assert.assertTrue("The last failed login for " + username + " is later than expected. [Expected: "
            + lastFailedLoginTime + ", Actual:" + roughExpectedLastFailedLoginTime.plus(TEST_DURATION_BUFFER) + "]",
            lastFailedLoginTime.isBefore(roughExpectedLastFailedLoginTime.plus(TEST_DURATION_BUFFER)));

        // Testing the throttle endtime
        Instant throttleEndtime = new Instant(authenticationThrottleData.getThrottleEndtime());
        Instant roughExpectedThrottleEndtime = now.minus(FIVE_SECONDS).plus(throttleDuration);
        Assert.assertTrue("The remaining lockout for " + username + " is shorter than expected. [Expected: "
            + throttleEndtime + ", Actual:" + roughExpectedThrottleEndtime.minus(TEST_DURATION_BUFFER) + "]",
            throttleEndtime.isAfter(roughExpectedThrottleEndtime.minus(TEST_DURATION_BUFFER)));
        Assert.assertTrue("The remaining lockout for " + username + " is longer than expected. [Expected: "
            + throttleEndtime + ", Actual:" + roughExpectedThrottleEndtime.plus(TEST_DURATION_BUFFER) + "]",
            throttleEndtime.isBefore(roughExpectedThrottleEndtime.plus(TEST_DURATION_BUFFER)));

        // Testing actual throttle duration remaining
        Duration actualThrottleDuration = Duration.millis(authenticationThrottleData.getActualThrottleDuration());
        Duration roughExpectedActualThrottleDuration = throttleDuration;
        Assert.assertTrue("The remaining lockout for " + username + " is shorter than expected. [Expected: "
            + actualThrottleDuration + ", Actual:" + roughExpectedActualThrottleDuration.minus(TEST_DURATION_BUFFER)
            + "]", actualThrottleDuration.isLongerThan(roughExpectedActualThrottleDuration.minus(TEST_DURATION_BUFFER)));
        Assert.assertTrue("The remaining lockout for " + username + " is longer than expected. [Expected: "
            + actualThrottleDuration + ", Actual:" + roughExpectedActualThrottleDuration.plus(TEST_DURATION_BUFFER)
            + "]", actualThrottleDuration.isShorterThan(roughExpectedActualThrottleDuration.plus(TEST_DURATION_BUFFER)));

    }
}