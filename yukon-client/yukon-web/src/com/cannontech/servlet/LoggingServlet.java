package com.cannontech.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;

import org.apache.logging.log4j.Logger;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;

/**
 * Returns an xml logging file from the server to
 * the client to configure log4j logging
 * @author dharrington
 */
public class LoggingServlet extends HttpServlet {

    private static final String REMOTE_LOGGING_XML = "remoteLogging.xml";
    Logger logger = YukonLogManager.getLogger(LoggingServlet.class);
    /**
     *  typically c:\Yukon, but installation dependent
     */
    private static String yukonBase = CtiUtilities.getYukonBase();
    
    /**
     *  The path to the log file
     */
    private final static String path = yukonBase + "/Server/Config/" + REMOTE_LOGGING_XML;
    
    /**
     * default constructor
     */
    public LoggingServlet() {
        super();
    }
    
    /**
     * finds the logging xml config file and returns it as a response object
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        URL classpathUrl = CTILogger.class.getClassLoader().getResource(REMOTE_LOGGING_XML);
        // get xml config file for logging
        File file = new File(path);
        if (file.canRead()) {
            URL fileUrl = file.toURL();
            FileCopyUtils.copy(fileUrl.openStream(), response.getOutputStream());
        } else if (classpathUrl != null) {
            FileCopyUtils.copy(classpathUrl.openStream(), response.getOutputStream());
        } else {
            logger.error("cannot read xml config file for logging, client is only logging to console");
        }
    } 
}

