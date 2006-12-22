package com.cannontech.web.logging;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;

/**
 * LogController acts as an abstract base
 * class for other logXxxControllers
 * Contains some common directories for locating
 * the local and remote log files.
 * @see internal getLogFile() method
 * @author dharrington
 */
public abstract class LogController extends AbstractController {
    
    //create a logger for this class
    private Logger logger = YukonLogManager.getLogger(LogController.class);
    //get the local log directory
    private File localDir = new File(CtiUtilities.getYukonBase(), "Server/Log");
    //get the remote log directory
    private File remoteDir = new File(CtiUtilities.getYukonBase(), "Server/Log/Remote");
    
    /**
    * handleRequestInternal is inherited from AbstractController and remains abstract here.
    * It will be overridden in LogXxxControllers
    */
    abstract protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    /**
     * getLogFile returns a log file requested or null if not found.
     * @param request
     * @param response
     * @return The log file requested
     */
    protected File getLogFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
        //get requested url, convert to a file, get the filename
        String urlString = request.getRequestURL().toString();
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            logger.error("Exception creating url for requested log file", e);
            return null;
        }
        
        File urlFile = new File(url.toString());
        String logFileName = urlFile.getName();
  
        // get the file by passing directory and filename
        File localLogFile = new File(getLocalDir(), logFileName);
        File remoteLogFile = new File(getRemoteDir(), logFileName);
    
        //make sure the file exists before trying to return it
        if (localLogFile.canRead()) {
            return localLogFile;        
        } else if (remoteLogFile.canRead()) {
            return remoteLogFile;
        } else {
            logger.error("The following log file does not exist: " + logFileName);
            throw new IOException();
        }
    }

    public File getLocalDir() {
        return localDir;
    }

    public void setLocalDir(File localDir) {
        this.localDir = localDir;
    }

    public File getRemoteDir() {
        return remoteDir;
    }

    public void setRemoteDir(File remoteDir) {
        this.remoteDir = remoteDir;
    }
}
