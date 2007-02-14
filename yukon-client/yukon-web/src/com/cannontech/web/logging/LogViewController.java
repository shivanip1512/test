package com.cannontech.web.logging;

import java.io.File;
import java.io.FileReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.util.ServletUtil;

/**
 * LogViewController handles the retrieving of
 * a specific log file in the local or remote
 * directory and passes that file, as a string, 
 * to the ModelAndView.
 * @see view for this controller is logView.jsp
 * @author dharrington
 */
public class LogViewController extends LogController {
    
    //logger for this class
    Logger logger = YukonLogManager.getLogger(LogViewController.class);
    
    /**
    * Extracts a log file/filename from the local or remote 
    * directory and stores them in the ModelAndView object.
    * @Override
    * @return ModelAndView mav
    */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request), AdministratorRole.ROLEID);
        
        ModelAndView mav = new ModelAndView("logView");
        File logFile = getLogFile(request);
        FileReader fr = null;
        fr = new FileReader(logFile);   
            
        if (fr != null) {
            //copy the contents of the log file into a string
            String logContents = FileCopyUtils.copyToString(fr);
            
            //put contents of file in mav and return contents
            mav.addObject("logContents", logContents);
        
            //put file name into mav
            mav.addObject("logFileName", logFile.getName());
        } 
        return mav;
    }   
}       