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
 * This initializer is bootstrapped automatically by Tomcat. 
 * The REST API servlet is the only servlet initialized here. All other servlets are defined in WEB-INF/web.xml.
 * 
 * There are also filters defined in web.xml that reference this servlet.
 */
public class RestApiWebApplicationInitializer implements WebApplicationInitializer {
    private static final Logger log = YukonLogManager.getLogger(RestApiWebApplicationInitializer.class);
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // We don't need to manually set the config file location, because CannonDispatcherServlet looks in
        // /WEB-INF/contexts for a file named after the servlet.
        // see CannonDispatcherServlet.java and CannonXmlWebApplicationContext.java
        // 
        // To manually set the config file location, we'd have to do something like this:
        //
        // XmlWebApplicationContext apiContext = new XmlWebApplicationContext();
        // apiContext.setConfigLocation("/WEB-INF/contexts/api-servlet.xml");
        // ServletRegistration.Dynamic servlet =
        //        servletContext.addServlet("dispatcher", new DispatcherServlet(apiContext));
        
        log.info("REST API servlet initializing.");
        
        String[] contextPaths = {
                ""
        };
        
        ConfigurableXmlWebApplicationContext apiContext = 
                new ConfigurableXmlWebApplicationContext(true, true, contextPaths);
        ServletRegistration.Dynamic servlet =
                servletContext.addServlet("api", new RestApiDispatcherServlet(apiContext));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/api/*");
        
        log.info("REST API servlet initialized.");
        
//        log.info("REST API servlet initializing.");
//        ServletRegistration.Dynamic servlet =
//                servletContext.addServlet("api", new CannonDispatcherServlet());
//        servlet.setLoadOnStartup(1);
//        servlet.addMapping("/api/*");
//        log.info("REST API servlet initialized.");
        
        //When this is migrated to a new service, we may want to handle filters here instead of in web.xml
        /*
        loginFilter
        jwtFilter
        hiddenHttpMethodFilter
        errorhelper
        timerfilter
        generalSecurityFilter
        getMethodConvertingFilter
        */
        
        //remove api refs from web.xml, add comment
    }

}
