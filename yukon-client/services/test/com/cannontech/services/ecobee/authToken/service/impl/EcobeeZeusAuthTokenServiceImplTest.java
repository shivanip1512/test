package com.cannontech.services.ecobee.authToken.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class EcobeeZeusAuthTokenServiceImplTest {
    RestTemplate restTemplateMock;
    EcobeeZeusAuthTokenServiceImpl impl;

    @Test
    public void test_shouldCancelScheduler() throws Exception {
        restTemplateMock = new RestTemplate();
        impl = new EcobeeZeusAuthTokenServiceImpl(restTemplateMock);
        Class<EcobeeZeusAuthTokenServiceImpl> implClass = EcobeeZeusAuthTokenServiceImpl.class;
        Method method = implClass.getDeclaredMethod("isExpiredAuthToken", String.class);
        method.setAccessible(true);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();
        DateTime beforeTime = DateTime.now(DateTimeZone.UTC).minusHours(1);
        DateTime afterTime = DateTime.now(DateTimeZone.UTC).plusHours(1);
        assertTrue("Must be true", (boolean) method.invoke(impl, formatter.print(beforeTime)));
        assertFalse("Must be false", (boolean) method.invoke(impl, formatter.print(afterTime)));
    }

}
