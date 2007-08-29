package com.cannontech.web.logging;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.util.ServletUtil;

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
public class LogDownloadController extends LogController {
    
    //logger for this class
    Logger logger = YukonLogManager.getLogger(LogDownloadController.class);
    
    /**
    * Gets the requested file and returns it back thru the response object.
    * @Override of AbstractController method
    * @return null
    */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request), AdministratorRole.ROLEID);
        response.setContentType("text/plain");
        
        String root = ServletRequestUtils.getStringParameter(request, "root", "/");

        File logFile = getLogFile(request, root);
        if(logFile != null){
            //set response header to the log filename
            response.setHeader("Content-Disposition", "attachment; filename=" + logFile.getName());

            //Download the file thru the response object
            FileCopyUtils.copy(logFile.toURL().openStream(), response.getOutputStream());
        }
        
        return null;
    }
}    