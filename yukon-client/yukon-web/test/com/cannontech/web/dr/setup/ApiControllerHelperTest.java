package com.cannontech.web.dr.setup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

import com.cannontech.web.api.validation.ApiControllerHelper;

public class ApiControllerHelperTest {
    private HttpServletRequest req;
    private ApiControllerHelper helper;

    @Before
    public void setUp() throws Exception {
        req = createMockRequest();
        helper = new ApiControllerHelper();
    }

    @After
    public void tearDown() {
        req = null;
    }

    @Test
    public void testgetValidApiURL() {
        String pathURL = "/setup/loadGroup/save";
        String url = helper.getApiURL(req, pathURL);
        assertTrue("The url is valid", url.equals("http://localhost:8080/api/dr/setup/loadGroup/save"));
    }

    @Test
    public void testgetInvalidApiURL() {
        String pathURL = "/setup/loadGroup/save";
        String url = helper.getApiURL(req, pathURL);
        assertFalse("The url is invalid", url.equals("http://localhost:8080/dr/setup/loadGroup/save"));
    }

    private HttpServletRequest createMockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
        request.setPathInfo("/setup/loadGroup/save");
        request.setServerName("localhost:8080/dr");
        request.setRequestURI("/setup/loadGroup/save");
        request.setServletPath("/dr");
        return request;
    }
}
