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

package com.cannontech.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderServlet;

import com.cannontech.clientutils.CTILogger;

/**
 * Bootstrap listener to start up Spring's root WebApplicationContext.
 * Simply delegates to ContextLoader.
 *
 * <p>This listener should be registered after Log4jConfigListener in web.xml,
 * if the latter is used.
 *
 * <p>For Servlet 2.2 containers and Servlet 2.3 ones that do not initalize
 * listeners before servlets, use ContextLoaderServlet. See the latter's Javadoc
 * for details.
 *
 * @author Juergen Hoeller
 * @since 17.02.2003
 * @see ContextLoader
 * @see ContextLoaderServlet
 * @see org.springframework.web.util.Log4jConfigListener
 */
public class CannonContextLoaderListener implements ServletContextListener {

    private ContextLoader contextLoader;


    /**
     * Initialize the root web application context.
     */
    public void contextInitialized(ServletContextEvent event) {
        try {
            this.contextLoader = createContextLoader();
            this.contextLoader.initWebApplicationContext(event.getServletContext());
        } catch (Throwable t) {
            CTILogger.error("Unable to initialize Spring Context. Setting error flag.", t);
            ServletContext servletContext = event.getServletContext();
            servletContext.setAttribute("com.cannontech.SERVLET_STARTUP_ERROR", t);
        }
    }

    /**
     * Create the ContextLoader to use. Can be overridden in subclasses.
     * @return the new ContextLoader
     */
    protected ContextLoader createContextLoader() {
        return new ContextLoader();
    }

    /**
     * Return the ContextLoader used by this listener.
     */
    public ContextLoader getContextLoader() {
        return contextLoader;
    }


    /**
     * Close the root web application context.
     */
    public void contextDestroyed(ServletContextEvent event) {
        if (this.contextLoader != null) {
            this.contextLoader.closeWebApplicationContext(event.getServletContext());
        }
    }

}
