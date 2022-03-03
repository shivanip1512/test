package com.cannontech.web.dr.setup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.web.api.validation.ApiControllerHelper;

public class ApiControllerHelperTest {
    private HttpServletRequest req;
    private ApiControllerHelper helper;
    private SimpleYukonUserContext userContext;

    @BeforeEach
    public void setUp() throws Exception {
        req = createMockRequest();
        helper = new ApiControllerHelper();
        userContext = new SimpleYukonUserContext();
        userContext.setLocale(Locale.getDefault());
        userContext.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
    }

    @AfterEach
    public void tearDown() {
        req = null;
    }

    @Test
    public void testgetValidApiUrl() {
        String pathUrl = "/dr/setup/loadGroup/save";
        ReflectionTestUtils.invokeMethod(helper, "setWebServerUrl", req.getServerName());
        String url = helper.findWebServerUrl(req, userContext, pathUrl);
        assertTrue(url.equals("http://localhost:8080/api/dr/setup/loadGroup/save"), "The url is valid");
    }

    @Test
    public void testgetInvalidApiUrl() {
        String pathUrl = "/dr/setup/loadGroup/save";
        ReflectionTestUtils.invokeMethod(helper, "setWebServerUrl", req.getServerName());
        String url = helper.findWebServerUrl(req, userContext, pathUrl);
        assertFalse(url.equals("http://localhost:8080/yukon/dr/setup/loadGroup/save"), "The url is invalid");
    }

    private HttpServletRequest createMockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
        request.setServerName("http://localhost:8080");
        return request;
    }
}
