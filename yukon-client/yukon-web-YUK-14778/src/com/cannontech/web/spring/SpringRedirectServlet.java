package com.cannontech.web.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;

public class SpringRedirectServlet extends HttpServlet {

    private ConfigurationSource configurationSource;
    
    private static final Logger log = YukonLogManager.getLogger(SpringRedirectServlet.class);
    private static final String SPRING_LEADING_URL = "/spring";
    
    @Override
    public void init() {
        configurationSource = YukonSpringHook.getBean(ConfigurationSource.class);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean devMode = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false);
        if (devMode) {
            throw new UnavailableException("A request with url of "
                    + SPRING_LEADING_URL
                    + "/* has been accessed. Since you are a developer ("
                    + MasterConfigBoolean.DEVELOPMENT_MODE.name()
                    + " is TRUE), please remedy this by removing "
                    + SPRING_LEADING_URL + " from the accessed link.");
        }

        log.info("redirecting from " + SPRING_LEADING_URL + " leading url");
        String urlWithoutSpring = StringUtils.removeStart(ServletUtil.getFullURL(request), SPRING_LEADING_URL);
        response.sendRedirect(response.encodeRedirectURL(urlWithoutSpring));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /* If we get a Post, then simply pass this off to the doGet method.
         * Doing so will wipe out any request parameters.
         * The Controller handling the request ideally is robust enough to handle this 
        */
        doGet(request, response);
    }
}
