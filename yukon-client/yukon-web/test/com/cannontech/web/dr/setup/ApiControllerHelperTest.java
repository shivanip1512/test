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
        String pathURL = "/dr/setup/loadGroup/save";
        String url = helper.getApiURL(req.getServerName(), pathURL);
        assertTrue("The url is valid", url.equals("http://localhost:8080/api/dr/setup/loadGroup/save"));
    }

    @Test
    public void testgetInvalidApiURL() {
        String pathURL = "/dr/setup/loadGroup/save";
        String url = helper.getApiURL(req.getServerName(), pathURL);
        assertFalse("The url is invalid", url.equals("http://localhost:8080/dr/setup/loadGroup/save"));
    }

    private HttpServletRequest createMockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest(new MockServletContext());
        request.setServerName("http://localhost:8080");
        return request;
    }
}
