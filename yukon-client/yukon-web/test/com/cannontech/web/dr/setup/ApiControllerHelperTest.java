package com.cannontech.web.dr.setup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.web.api.validation.ApiControllerHelper;

public class ApiControllerHelperTest {
    private HttpServletRequest req;
    private ApiControllerHelper helper;
    private SimpleYukonUserContext userContext;

    @Before
    public void setUp() throws Exception {
        req = createMockRequest();
        helper = new ApiControllerHelper();
        userContext = new SimpleYukonUserContext();
        userContext.setLocale(Locale.getDefault());
        userContext.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
    }

    @After
    public void tearDown() {
        req = null;
    }

    @Test
    public void testgetValidApiUrl() {
        String pathUrl = "/dr/setup/loadGroup/save";
        ReflectionTestUtils.invokeMethod(helper, "setWebserverUrl", req.getServerName());
        String url = helper.findWebServerUrl(req, userContext, pathUrl);
        assertTrue("The url is valid", url.equals("http://localhost:8080/api/dr/setup/loadGroup/save"));
    }

    @Test
    public void testgetInvalidApiUrl() {
        String pathUrl = "/dr/setup/loadGroup/save";
        ReflectionTestUtils.invokeMethod(helper, "setWebserverUrl", req.getServerName());
        String url = helper.findWebServerUrl(req, userContext, pathUrl);
        assertFalse("The url is invalid", url.equals("http://localhost:8080/yukon/dr/setup/loadGroup/save"));
    }

    private HttpServletRequest createMockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
        request.setServerName("http://localhost:8080");
        return request;
    }
}
