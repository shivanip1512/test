package com.cannontech.web.logging;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FileUtil;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.util.ServletUtil;


/**
 * LogTailController handles the retrieving of
 * a specific log file in the local or remote
 * directory and passes that file, as a string, 
 * to the ModelAndView.
 * @see view for this controller is logTail.jsp
 * @author dharrington
 */
public class LogTailController extends LogController {
    
    //logger for this class
    private Logger logger = YukonLogManager.getLogger(LogTailController.class);
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
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        
        ModelAndView mav = new ModelAndView("logTail.jsp");
        
        /*This section sets up default values incase none are submitted
         * through the parameters
         */
        int numLines = ServletRequestUtils.getIntParameter(request, "numLines", 50);
        long offSet = ServletRequestUtils.getLongParameter(request, "offSet", 0);
        String root = ServletRequestUtils.getStringParameter(request, "root", "/");

        // Gets the correct log file from the request
        File logFile = getLogFile(request, root);
        long lastModL = logFile.lastModified();
        long fileLengthL = logFile.length();
       
        // Checks to see if the logFile exists and has the ability to be read
        if((logFile != null) && (logFile.canRead())){
            String lastMod = dateFormattingService.formatDate(new Date(lastModL), DateFormattingService.DateFormatEnum.BOTH, yukonUser);
            String fileLength = String.valueOf(fileLengthL/1024);
        	List<String> logLines = FileUtil.readLines(logFile, numLines, offSet);
            
       		// Setting value to the mav object
       		mav.addObject("logContents", logLines);
       		mav.addObject("logFileName", logFile.getName());
            mav.addObject("fileDateMod", lastMod);
            mav.addObject("fileLength", fileLength);
        } else {
            logger.warn("Could not read log file: " + logFile);
        }

        mav.addObject("logFilePath", root);
        mav.addObject("numLines", numLines);
        
        return mav;
    }   
}       