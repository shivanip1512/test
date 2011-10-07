package com.cannontech.web.support.logging;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FileUtil;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;


/**
 * LogTailController handles the retrieving of
 * a specific log file in the local or remote
 * directory and passes that file, as a string, 
 * to the ModelAndView.
 * @see view for this controller is logTail.jsp
 * @author dharrington
 */
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogTailController extends LogController {
    
    //logger for this class
    private Logger logger = YukonLogManager.getLogger(LogTailController.class);
    private DateFormattingService dateFormattingService = null;
    
    /**
     * Extracts a log file/filename from the local or remote 
     * directory and stores them in the ModelAndView object.
     * 
     * @param request
     * @param userContext
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/logging/tail", method = RequestMethod.GET)
    public String tail(HttpServletRequest request,
                       YukonUserContext userContext,
                       ModelMap map) throws Exception {
    	
        /*This section sets up default values incase none are submitted
         * through the parameters
         */
        int numLines = ServletRequestUtils.getIntParameter(request, "numLines", 50);
        long offSet = ServletRequestUtils.getLongParameter(request, "offSet", 0);

        // Gets the correct log file from the request
        File logFile = getLogFile(request);
        Validate.isTrue(logFile.isFile());
        
        long lastModL = logFile.lastModified();
        long fileLengthL = logFile.length();
       
        // Checks to see if the logFile exists and has the ability to be read
        if((logFile != null) && (logFile.canRead())){
            String lastMod = dateFormattingService.format(new Date(lastModL), DateFormattingService.DateFormatEnum.BOTH, userContext);
            String fileLength = String.valueOf(fileLengthL/1024);
        	List<String> logLines = FileUtil.readLines(logFile, numLines, offSet);
            
       		// Setting value to the mav object
       		map.addAttribute("logContents", logLines);
       		map.addAttribute("logFileName", logFile.getName());
       		String rootlessDirFileString = getRootlessFilePath(logFile.getParentFile());
       		map.addAttribute("rootlessDirFileString", rootlessDirFileString);
            map.addAttribute("fileDateMod", lastMod);
            map.addAttribute("fileLength", fileLength);
        } else {
            logger.warn("Could not read log file: " + logFile);
        }

        String fileName = ServletRequestUtils.getRequiredStringParameter(request, "file");
        map.addAttribute("file", HtmlUtils.htmlEscape(getFileNameParameter(request)));
        map.addAttribute("logFilePath", fileName);
        map.addAttribute("numLines", numLines);
        
        return "logging/logTail.jsp";
    }   

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}       