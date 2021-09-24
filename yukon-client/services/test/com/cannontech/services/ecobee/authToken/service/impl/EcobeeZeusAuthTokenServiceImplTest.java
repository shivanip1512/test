package com.cannontech.services.ecobee.authToken.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationResponse;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenResponse;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class EcobeeZeusAuthTokenServiceImplTest {
    private RestTemplate restTemplateMock;
    private EcobeeZeusAuthTokenServiceImpl impl;
    private HttpHeaders header = new HttpHeaders();
    private ScheduledExecutor scheduledExecutor;
    private AsyncDynamicDataSource asyncDynamicDataSource;

    @BeforeEach
    public void setup() throws Exception {
        restTemplateMock = new RestTemplate();
        impl = new EcobeeZeusAuthTokenServiceImpl(restTemplateMock);
        header.setContentType(MediaType.APPLICATION_JSON);
        scheduledExecutor = EasyMock.createMock(ScheduledExecutor.class);
        asyncDynamicDataSource = EasyMock.createMock(AsyncDynamicDataSource.class);
    }

    @Test
    public void test_shouldCancelScheduler() throws Exception {
        Class<EcobeeZeusAuthTokenServiceImpl> implClass = EcobeeZeusAuthTokenServiceImpl.class;
        Method method = implClass.getDeclaredMethod("isExpiredRefreshToken", String.class);
        method.setAccessible(true);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();
        DateTime tokenGeneratedTime = DateTime.now(DateTimeZone.UTC).minusHours(24);
        ReflectionTestUtils.setField(impl, "tokenGeneratedTime", tokenGeneratedTime);
        DateTime beforeTime = DateTime.now(DateTimeZone.UTC).minusHours(1);
        DateTime afterTime = DateTime.now(DateTimeZone.UTC).plusHours(1);
        assertFalse((boolean) method.invoke(impl, formatter.print(beforeTime)), "Must be false");
        assertTrue((boolean) method.invoke(impl, formatter.print(afterTime)), "Must be true");
    }

    @Test
    public void test_authenticate_emptyConfigurations() throws Exception {
        ReflectionTestUtils.setField(impl, "ecobeePassword", "");
        ReflectionTestUtils.setField(impl, "ecobeeUsername", "");
        ReflectionTestUtils.setField(impl, "ecobeeServerURL", "");
        Assertions.assertThrows(InvalidCacheLoadException.class, () -> {
            impl.authenticate();
        });
    }

    @Test
    public void test_authenticate_success() throws Exception {
        setupRequiredFields();

        ZeusAuthenticationResponse mockResponse = new ZeusAuthenticationResponse("yUo111RE9wtoMmT", "JWiuhuihuih372896378",
                "2021-03-12T21:13:44Z");

        ResponseEntity<ZeusAuthenticationResponse> responseEntity = new ResponseEntity<>(mockResponse, header, HttpStatus.OK);

        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);
        setPostForEntity(restTemplateMock, responseEntity);
        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);

        ZeusEcobeeAuthTokenResponse response = impl.authenticate();

        assertTrue(response != null, "Response must not be null: ");
        assertTrue(response.getAuthToken().equals("yUo111RE9wtoMmT"), "Auth token should match: ");
        assertTrue(response.getRefreshToken().equals("JWiuhuihuih372896378"), "Refresh token should match: ");
        assertTrue(response.getExpiryTimestamp().equals("2021-03-12T21:13:44Z"), "Expiry timestamp should match: ");
    }

    @Test
    public void test_authenticate_unAuthorized() throws Exception {
        setupRequiredFields();

        ResponseEntity<ZeusAuthenticationResponse> responseEntity = new ResponseEntity<>(null, header, HttpStatus.UNAUTHORIZED);

        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);
        setPostForEntity(restTemplateMock, responseEntity);
        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);

        Assertions.assertThrows(InvalidCacheLoadException.class, () -> {
            impl.authenticate();
        });
    }

    @Test
    public void test_authenticate_badRequest() throws Exception {
        setupRequiredFields();

        ResponseEntity<ZeusAuthenticationResponse> responseEntity = new ResponseEntity<>(null, header, HttpStatus.BAD_REQUEST);

        RestTemplate restTemplateMock = EasyMock.createMock(RestTemplate.class);
        setPostForEntity(restTemplateMock, responseEntity);
        EasyMock.replay(restTemplateMock);
        ReflectionTestUtils.setField(impl, "restTemplate", restTemplateMock);

        Assertions.assertThrows(UncheckedExecutionException.class, () -> {
            impl.authenticate();
        });
    }

    private void setupRequiredFields() {
        GlobalSettingDao mockGlobalSettingDao = EasyMock.createMock(GlobalSettingDao.class);
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL)).andReturn("testUrl");
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.ECOBEE_USERNAME)).andReturn("testUsername");
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.ECOBEE_PASSWORD)).andReturn("testPassword");
        EasyMock.expect(mockGlobalSettingDao.getString(GlobalSettingType.HTTP_PROXY)).andReturn(StringUtils.EMPTY);

        EasyMock.expectLastCall();
        EasyMock.replay(mockGlobalSettingDao);
        ReflectionTestUtils.setField(impl, "globalSettingDao", mockGlobalSettingDao);
        ReflectionTestUtils.setField(impl, "scheduledExecutor", scheduledExecutor);
        ReflectionTestUtils.setField(impl, "asyncDynamicDataSource", asyncDynamicDataSource);
        ReflectionTestUtils.setField(impl, "ecobeePassword", "pwd");
        ReflectionTestUtils.setField(impl, "ecobeeUsername", "name");
        ReflectionTestUtils.setField(impl, "ecobeeServerURL", "url");

    }

    @SuppressWarnings("unchecked")
    private <T> void setPostForEntity(RestTemplate mock, ResponseEntity<T> responseEntity) throws Exception {
        EasyMock.expect(mock.postForEntity(EasyMock.anyObject(String.class), EasyMock.anyObject(Object.class),
                (Class<T>) EasyMock.anyObject(Object.class))).andReturn(responseEntity);
    }
}