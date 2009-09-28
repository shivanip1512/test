package com.cannontech.web.support.logging;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
* LogDownloadController handles the downloading of
* a requested log file contained in the local or remote
* directory. This controller has no corresponding view, so
* handleRequestInternal() returns null instead
* of a ModelAndView. It then returns the file  
* as a stream thru the response object.
* @see LogController base class and AbstractController
* @author dharrington
*/
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogDownloadController extends LogController {
    
    //logger for this class
    Logger logger = YukonLogManager.getLogger(LogDownloadController.class);
    
    /**
    * Gets the requested file and returns it back thru the response object.
    * @Override of AbstractController method
    * @return null
    */
    @RequestMapping(value = "/logging/download", method = RequestMethod.GET)
    public String download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/plain");
        
        File logFile = getLogFile(request);
        Validate.isTrue(logFile.isFile());
        
        if(logFile != null){
            //set response header to the log filename
            response.setHeader("Content-Disposition", "attachment; filename=" + logFile.getName());
            response.setHeader("Content-Length", Long.toString(logFile.length()));

            //Download the file thru the response object
            FileCopyUtils.copy(logFile.toURI().toURL().openStream(), response.getOutputStream());
        }
        
        return null;
    }
}    