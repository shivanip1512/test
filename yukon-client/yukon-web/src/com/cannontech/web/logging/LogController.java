package com.cannontech.web.logging;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AuthDao;

/**
 * LogController acts as an abstract base
 * class for other logXxxControllers
 * Contains some common directories for locating
 * the local and remote log files.
 * @see internal getLogFile() method
 * @author dharrington
 */
public abstract class LogController extends AbstractController {
    protected AuthDao authDao;
    //create a logger for this class
    private Logger logger = YukonLogManager.getLogger(LogController.class);
    //get the local log directory
    private File localDir = new File(CtiUtilities.getYukonBase(), "Server/Log");
    
    /**
     * getLogFile returns a log file requested or null if not found.
     * @param request
     * @param response
     * @return The log file requested
     */
    protected File getLogFile(HttpServletRequest request) throws IOException {
    
        //get requested url, convert to a file, get the filename
        UrlPathHelper helper = new UrlPathHelper();
        helper.setUrlDecode(true);
        String urlString = helper.getRequestUri(request);
        String logFileName = StringUtils.substringAfterLast(urlString, "/");
  
        // get the file by passing directory and filename
        File localLogFile = new File(getLocalDir(), logFileName);
    
        //make sure the file exists before trying to return it
        if (localLogFile.canRead()) {
            return localLogFile;        
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

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
