package com.cannontech.web.logging;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FileUtil;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

public class LogTailUpdaterController extends LogController {
	
    private Logger logger = YukonLogManager.getLogger(LogTailController.class);
    private DateFormattingService dateFormattingService = null;
      
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get JSON data
        StringWriter jsonDataWriter = new StringWriter();
        FileCopyUtils.copy(request.getReader(), jsonDataWriter);
        String jsonStr = jsonDataWriter.toString();
        JSONObject data = JSONObject.fromString(jsonStr);
        long oldFileLength = data.getLong("fileLength");

        int linesPerUpdate = 50;
        linesPerUpdate = data.getInt("numLines");
        
        // takes the data and checks to see if the log file has changed        
        JSONObject jsonUpdates = new JSONObject();
        JSONArray jsonLogLines = new JSONArray();
        jsonUpdates.put("logContent",jsonLogLines);
        jsonUpdates.put("numLines", linesPerUpdate);
        
        File logFile = getLogFile(request, data.getString("filePath"));
        
        if((logFile!= null) && (logFile.canRead())){
            // Setting up the last modified variable for the JSON
        	long lastModL = logFile.lastModified();
            Date lastMod = new Date(lastModL);
            String lastModStr = dateFormattingService.formatDate(lastMod, DateFormattingService.DateFormatEnum.BOTH, userContext);
            jsonUpdates.put("fileDateMod", lastModStr);
            
            // Setting up the file length variable for the JSON
            long fileLengthL = logFile.length();
            jsonUpdates.put("fileLength", String.valueOf(fileLengthL));
            
            /* These sets of lines gathering all the new lines from the file and sets up 
             * the logLines variable that contains them.
             */
        	List<String> logLines = FileUtil.readLines(logFile, linesPerUpdate, oldFileLength);
        	if ((logLines != null) && 
        		(fileLengthL != oldFileLength)) {
                
                for(int i = 0; i < logLines.size();i++){
        			if(logLines.get(i) != null){
                        jsonLogLines.put(logLines.get(i));
        			}
        		}
        	}
        } else {
            logger.warn("Could not read log file: " + logFile);
        }
        
        // The writing of the response and sending the response
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonUpdates.toString();
        writer.write(responseJsonStr);
        
        return null;
    }

    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}
