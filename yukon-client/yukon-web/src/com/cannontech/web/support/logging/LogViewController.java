package com.cannontech.web.support.logging;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

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
import com.cannontech.web.taglib.Writable;

/**
 * LogViewController handles the retrieving of
 * a specific log file in the local or remote
 * directory and passes that file, as a string, 
 * to the ModelAndView.
 * @see view for this controller is logView.jsp
 * @author dharrington
 */
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogViewController extends LogController {
    
    //logger for this class
    Logger logger = YukonLogManager.getLogger(LogViewController.class);
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
    @RequestMapping(value = "/logging/view", method = RequestMethod.GET)
     public String view(HttpServletRequest request,
                        YukonUserContext userContext,
                        ModelMap map) throws Exception {
        
        // Gets the correct log file from the request
        File logFile = getLogFile(request);
        Validate.isTrue(logFile.isFile());
        
        final FileReader fr = new FileReader(logFile);   
            
        if (fr != null) {
            // Sets up the last modified and file length to be shown
            long lastModL = logFile.lastModified();
            String lastMod = dateFormattingService.format(new Date(lastModL), DateFormattingService.DateFormatEnum.BOTH, userContext);
            long fileLengthL = logFile.length();
            String fileLength = String.valueOf(fileLengthL/1024);
            
            //put contents of file in map and return contents
            map.addAttribute("fileLength", fileLength);
            map.addAttribute("fileDateMod", lastMod);
            map.addAttribute("logFileName", logFile.getName());
            String fileName = ServletRequestUtils.getRequiredStringParameter(request, "file");
            map.addAttribute("logFilePath", fileName);
            map.addAttribute("file", HtmlUtils.htmlEscape(getFileNameParameter(request)));
            map.addAttribute("logContents", new Writable() {
                public void write(Writer out) throws IOException {
                    FileUtil.copyNoFlush(fr, out);
                }
            });
        } 
        return "logging/logView.jsp";
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}       