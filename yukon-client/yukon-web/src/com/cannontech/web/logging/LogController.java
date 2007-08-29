package com.cannontech.web.logging;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

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

    //get the local log directory
    protected File localDir = new File(CtiUtilities.getYukonBase(), "Server/Log");
    
    /**
     * Gets the correct file obj from the request and returns it.
     * 
     * @param request
     * @param root
     * @return
     * @throws IOException
     */
    protected File getLogFile(HttpServletRequest request, String root) throws IOException {
        
        File currentDir = null;
        if(!root.equals("/")){
            currentDir = new File(localDir, root);
        }else{
            currentDir = new File(localDir, "");
        }
        String fileName = this.getFileName(request);
        File logFile = new File(currentDir, fileName);
        
        return logFile;
    }

    /**
     * Gets the given file name from the request object
     * 
     * @param request
     * @return
     * @throws IOException
     */
    protected String getFileName(HttpServletRequest request) throws IOException {

        //get requested url, convert to a file, get the filename
        UrlPathHelper helper = new UrlPathHelper();
        helper.setUrlDecode(true);
        
        String fileNameAndParams = helper.getPathWithinServletMapping(request);
        String fileName = StringUtils.substringAfterLast(fileNameAndParams, "/");
        
        return fileName;
    }
    

    public void setLocalDir(File localDir) {
        this.localDir = localDir;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
