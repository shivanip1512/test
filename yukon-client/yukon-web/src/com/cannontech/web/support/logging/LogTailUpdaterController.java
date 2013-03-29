package com.cannontech.web.support.logging;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FileUtil;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogTailUpdaterController extends LogController {
	
    private Logger logger = YukonLogManager.getLogger(LogTailController.class);
    private DateFormattingService dateFormattingService = null;
      
    @RequestMapping(value = "/logging/tail/update", method = RequestMethod.POST)
    public String tailUpdate(HttpServletRequest request,
                             HttpServletResponse response,
                             YukonUserContext userContext,
                             ModelMap map) throws Exception {

        // get JSON data
        StringWriter jsonDataWriter = new StringWriter();
        FileCopyUtils.copy(request.getReader(), jsonDataWriter);
        String jsonStr = jsonDataWriter.toString();
        JSONObject data = JSONObject.fromObject(jsonStr);
        final long oldFileLength = data.getLong("fileLength");
        final int linesPerUpdate = data.getInt("numLines");
        
        // takes the data and checks to see if the log file has changed        
        JSONObject jsonUpdates = new JSONObject();
        JSONArray jsonLogLines = new JSONArray();
        jsonUpdates.put("numLines", linesPerUpdate);
        
        File logFile = getLogFile(request);
        Validate.isTrue(logFile.isFile());
        
        if (logFile!= null && logFile.canRead()) {
            // Setting up the last modified variable for the JSON
        	final long lastModL = logFile.lastModified();
            Date lastMod = new Date(lastModL);
            String lastModStr = dateFormattingService.format(lastMod, DateFormattingService.DateFormatEnum.BOTH, userContext);
            jsonUpdates.put("fileDateMod", lastModStr);
            
            // Setting up the file length variable for the JSON
            final long fileLengthL = logFile.length();
            jsonUpdates.put("fileLength", String.valueOf(fileLengthL));
            
            // These sets of lines gathering all the new lines from the file and sets up the logLines variable that contains them.
        	List<String> logLines = FileUtil.readLines(logFile, linesPerUpdate, oldFileLength);
        	if (logLines != null && fileLengthL != oldFileLength) {
        		for (String logLine : logLines) {
        			if (logLine != null) {
                        jsonLogLines.add(logLine);
        			}
        		}
        	}
        } else {
            logger.warn("Could not read log file: " + logFile);
        }
        
        jsonUpdates.put("logContent", jsonLogLines);
        
        // The writing of the response and sending the response
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonUpdates.toString();
        writer.write(responseJsonStr);
        
        return null;
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}
