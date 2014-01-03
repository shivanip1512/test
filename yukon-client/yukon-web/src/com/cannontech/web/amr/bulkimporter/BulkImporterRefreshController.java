package com.cannontech.web.amr.bulkimporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.importdata.dao.BulkImportDataDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;

@CheckRoleProperty(YukonRoleProperty.IMPORTER_ENABLED)
@Controller
public class BulkImporterRefreshController {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private BulkImportDataDao bulkImportDataDao;

    /**
     * Return a raw JSON string that includes updated info about the status of importing
     * and lists of all the failed, pending communications, and fail communication meters
     */
    @RequestMapping("/bulkimporter/refreshResults")
    @ResponseBody
    public Map<String, Object> refreshResults(YukonUserContext userContext) {
        
        // JSON obj, user
        Map<String, Object> jsonUpdates = Maps.newHashMapWithExpectedSize(9);
        
        // import times
        String lastImportTimeStr
            = bulkImportDataDao.getFormattedLastImportTime(userContext, DateFormattingService.DateFormatEnum.BOTH);
        String nextImportTimeStr
            = bulkImportDataDao.getFormattedNextImportTime(userContext, DateFormattingService.DateFormatEnum.BOTH);
        
        jsonUpdates.put("lastImportAttempt", lastImportTimeStr);
        jsonUpdates.put("nextImportAttempt", nextImportTimeStr);
        
        // get raw data
        int importDataCount = bulkImportDataDao.getImportDataCount();
        List<ImportFail> failuresList = bulkImportDataDao.getAllDataFailures();
        List<ImportPendingComm> pendingCommsList = bulkImportDataDao.getAllPending();
        List<ImportFail> failedCommsList = bulkImportDataDao.getAllCommunicationFailures();
        
        // add counts to JSON
        jsonUpdates.put("importDataCount", importDataCount);
        jsonUpdates.put("failureCount", failuresList.size());
        jsonUpdates.put("pendingCommsCount", pendingCommsList.size());
        jsonUpdates.put("failedCommsCount", failedCommsList.size());
        
        // convert raw data into simple lists of maps for results table
        List<Map<String, String>> failures = new ArrayList<>();
        List<Map<String, String>> pendingComms = new ArrayList<>();
        List<Map<String, String>> failedComms = new ArrayList<>();
        
        for (ImportFail failure : failuresList) {
        	Map<String, String> item = new HashMap<>();
            item.put("failName", failure.getName());
            item.put("errorString", failure.getErrorMsg());
            item.put("failTime", dateFormattingService.format(failure.getDateTime(),
                                                              DateFormattingService.DateFormatEnum.BOTH,
                                                              userContext));
            failures.add(item);
        }
        
        for (ImportPendingComm pendingComm : pendingCommsList) {
            Map<String, String> item = new HashMap<>();
            item.put("pendingName", pendingComm.getName());
            item.put("routeName", pendingComm.getRouteName());
            item.put("substationName", pendingComm.getSubstationName());
            pendingComms.add(item);
        }
        
        for (ImportFail failedComm : failedCommsList) {
            Map<String, String> item = new HashMap<>();
            item.put("failName", failedComm.getName());
            item.put("routeName", failedComm.getRouteName());
            item.put("substationName", failedComm.getSubstationName());
            item.put("errorString", failedComm.getErrorMsg());
            item.put("failTime", dateFormattingService.format(failedComm.getDateTime(),
                                                              DateFormattingService.DateFormatEnum.BOTH,
                                                              userContext));
            failedComms.add(item);
        }

        // add results to JSON
        jsonUpdates.put("failures", failures);
        jsonUpdates.put("pendingComms", pendingComms);
        jsonUpdates.put("failedComms", failedComms);

        return jsonUpdates;
    }
}