package com.cannontech.web.logging;

import java.io.File;
import java.io.FileReader;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
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
    private DateFormattingService dateFormattingService = null;
    
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    /**
    * Extracts a log file/filename from the local or remote 
    * directory and stores them in the ModelAndView object.
    * @Override
    * @return ModelAndView mav
    */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request), AdministratorRole.ROLEID);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        ModelAndView mav = new ModelAndView("logView.jsp");
        
        String root = ServletRequestUtils.getStringParameter(request, "root", "/");

        // Gets the correct log file from the request
        File logFile = getLogFile(request, root);
        FileReader fr = null;
        fr = new FileReader(logFile);   
            
        if (fr != null) {
            //copy the contents of the log file into a string
            String logContents = FileCopyUtils.copyToString(fr);

            // Sets up the last modified and file length to be shown
            long lastModL = logFile.lastModified();
            String lastMod = dateFormattingService.formatDate(new Date(lastModL), DateFormattingService.DateFormatEnum.BOTH, userContext);
            long fileLengthL = logFile.length();
            String fileLength = String.valueOf(fileLengthL/1024);
            
            //put contents of file in mav and return contents
            mav.addObject("fileLength", fileLength);
            mav.addObject("fileDateMod", lastMod);
            mav.addObject("logFileName", logFile.getName());
            mav.addObject("logFilePath", root);
            mav.addObject("logContents", logContents);
        } 
        return mav;
    }   
}       