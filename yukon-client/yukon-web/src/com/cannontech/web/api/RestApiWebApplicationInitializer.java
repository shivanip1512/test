package com.cannontech.web.api;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.core.Logger;
import org.springframework.web.WebApplicationInitializer;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.spring.ConfigurableXmlWebApplicationContext;
import com.cannontech.web.spring.RestApiDispatcherServlet;

/**
 * This initializer is bootstrapped automatically by Tomcat & Spring on startup. 
 * See WebApplicationInitializer and SpringServletContainerInitializer documentation for more info on how this works.
 * 
 * The REST API servlet is the only servlet initialized here. All other servlets are defined in /WEB-INF/web.xml.
 * 
 * There are also filters defined in web.xml that reference this servlet.
 * 
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html">WebApplicationInitializer</a>
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/SpringServletContainerInitializer.html">SpringServletContainerInitializer</a>
 */
public class RestApiWebApplicationInitializer implements WebApplicationInitializer {
    private static final Logger log = YukonLogManager.getLogger(RestApiWebApplicationInitializer.class);
    
    private static final String[] contextPaths = {
            "/WEB-INF/contexts/api/api-context-ecobee.xml",
            "/WEB-INF/contexts/api/api-context-aggregateIntervalDataReport.xml",
            "/WEB-INF/contexts/api/api-context-derEdge.xml"
    };
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        
        log.info("REST API servlet initializing.");
        if (log.isDebugEnabled()) {
            String paths = String.join(",\n", contextPaths);
            log.info("Context paths: \n{}", paths);
        }
        
        /*
         * This context is composed from:
         *   /WEB-INF/contexts/sharedWebContext.xml
         *   /WEB-INF/contexts/api-servlet.xml
         *   (all files in "contextPaths" array, defined above)
         */
        ConfigurableXmlWebApplicationContext apiContext = 
                new ConfigurableXmlWebApplicationContext(true, true, contextPaths);
        ServletRegistration.Dynamic servlet =
                servletContext.addServlet("api", new RestApiDispatcherServlet(apiContext));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/api/*");
        
        log.info("REST API servlet initialized.");
        
        /*
         * When the APIs are migrated to a new service, we may want to handle filters here instead of in web.xml
         *   loginFilter, jwtFilter, hiddenHttpMethodFilter, errorhelper, timerfilter, generalSecurityFilter,
         *   getMethodConvertingFilter
         */
    }

}
