package com.cannontech.web.bulkimporter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

public class BulkImporterRefreshController implements Controller  {
    
    private DateFormattingService dateFormattingService = null;
    private BulkImportDataDao bulkImportDataDao = null;
    
  
    /**
     * Return a raw JSON string that includes updated info about the status of importing
     * and lists of all the failed, pending communications, and fail communication meters
     * 
     * @param request
     * @param response
     * @return
     * @throws ExceptionY
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // JSON obj, user
        JSONObject jsonUpdates = new JSONObject();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // import times
        Date lastImportTime = bulkImportDataDao.getLastImportTime(userContext);
        String lastImportTimeStr = dateFormattingService.formatDate(lastImportTime,
                                                                    DateFormattingService.DateFormatEnum.BOTH,
                                                                    userContext);

        Date nextImportTime = bulkImportDataDao.getNextImportTime(userContext);
        String nextImportTimeStr = dateFormattingService.formatDate(nextImportTime,
                                                                    DateFormattingService.DateFormatEnum.BOTH,
                                                                    userContext);
        
        jsonUpdates.put("lastImportAttempt", lastImportTimeStr);
        jsonUpdates.put("nextImportAttempt", nextImportTimeStr);
        
        // get raw data
        List<ImportFail> failuresList = bulkImportDataDao.getAllDataFailures();
        List<ImportPendingComm> pendingCommsList = bulkImportDataDao.getAllPending();
        List<ImportFail> failedCommsList = bulkImportDataDao.getAllCommunicationFailures();
        
        // add counts to JSON
        jsonUpdates.put("failureCount", failuresList.size());
        jsonUpdates.put("pendingCommsCount", pendingCommsList.size());
        jsonUpdates.put("failedCommsCount", failedCommsList.size());
        
        // convert raw data into simple lists of maps for results table
        List<Map<String, String>> failures = new ArrayList<Map<String, String>>();
        List<Map<String, String>> pendingComms = new ArrayList<Map<String, String>>();
        List<Map<String, String>> failedComms = new ArrayList<Map<String, String>>();
        
        for (ImportFail failure : failuresList) {
        	Map<String, String> item = new HashMap<String, String>();
            item.put("failName", failure.getName());
            item.put("errorString", failure.getErrorMsg());
            item.put("failTime", dateFormattingService.formatDate(failure.getDateTime(), DateFormattingService.DateFormatEnum.BOTH, userContext));
            failures.add(item);
        }
        
        for (ImportPendingComm pendingComm : pendingCommsList) {
            Map<String, String> item = new HashMap<String, String>();
            item.put("pendingName", pendingComm.getName());
            item.put("routeName", pendingComm.getRouteName());
            item.put("substationName", pendingComm.getSubstationName());
            pendingComms.add(item);
        }
        
        for (ImportFail failedComm : failedCommsList) {
            Map<String, String> item = new HashMap<String, String>();
            item.put("failName", failedComm.getName());
            item.put("routeName", failedComm.getRouteName());
            item.put("substationName", failedComm.getSubstationName());
            item.put("errorString", failedComm.getErrorMsg());
            item.put("failTime", dateFormattingService.formatDate(failedComm.getDateTime(), DateFormattingService.DateFormatEnum.BOTH, userContext));
            failedComms.add(item);
        }
        
        // add results to JSON
        jsonUpdates.put("failures", failures);
        jsonUpdates.put("pendingComms", pendingComms);
        jsonUpdates.put("failedComms", failedComms);
        
        // write JSON to response
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonUpdates.toString();
        writer.write(responseJsonStr);
        
        return null;
    }
    
 

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Required
    public void setBulkImportDataDao(BulkImportDataDao bulkImportDataDao) {
        this.bulkImportDataDao = bulkImportDataDao;
    }
    
}