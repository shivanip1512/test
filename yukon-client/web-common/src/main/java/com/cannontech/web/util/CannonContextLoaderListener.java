/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cannontech.web.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoader;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;

/**
 * Bootstrap listener to start up Spring's root WebApplicationContext.
 * Simply delegates to ContextLoader.
 *
 * <p>This listener should be registered after Log4jConfigListener in web.xml,
 * if the latter is used.
 *
 * This class has been modified for "cannon" by integrating it with the YukonSpringHook
 * so that things are shut down properly.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 * @see ContextLoader
 * @see ContextLoaderServlet
 * @see org.springframework.web.util.Log4jConfigListener
 */
public class CannonContextLoaderListener implements ServletContextListener {

    private CustomContextLoader contextLoader;


    /**
     * Initialize the root web application context.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        String appName = event.getServletContext().getInitParameter(CtiUtilities.CTI_APP_NAME_PROPERTY);
        System.setProperty(CtiUtilities.CTI_APP_NAME_PROPERTY, appName);
        
        try {
            String contextName = event.getServletContext().getInitParameter("parentContextKey");
            YukonSpringHook.setDefaultContext(contextName);
            this.contextLoader = createContextLoader();
            this.contextLoader.initWebApplicationContext(event.getServletContext());
        } catch (Throwable t) {
            // We have to use standard error because logging hasn't been initialized yet.
            System.err.println("Unable to initialize Spring Context. Setting error flag.");
            t.printStackTrace(System.err);
            ServletContext servletContext = event.getServletContext();
            ErrorHelperFilter.recordStartupError(servletContext, t);
        }
    }

    /**
     * Create the CustomContextLoader to use. Can be overridden in subclasses.
     * @return the new CustomContextLoader
     */
    protected CustomContextLoader createContextLoader() {
        return new CustomContextLoader();
    }

    /**
     * Return the ContextLoader used by this listener.
     */
    public CustomContextLoader getContextLoader() {
        return contextLoader;
    }


    /**
     * Close the root web application context.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (this.contextLoader != null) {
            this.contextLoader.closeWebApplicationContext(event.getServletContext());
        }
        
        // now shutdown the main context
        YukonSpringHook.shutdownContext();
    }

}
