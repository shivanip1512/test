package com.cannontech.web.dataImport;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.scheduledFileImport.DataImportWarning;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;

public class DataImportHelper {

    /**
     * This method converts the import result object to dataimport warning.
     */
    public static List<DataImportWarning> getDataImportWarning(String taskName , String importType, AccountImportResult importResult) {
        List<DataImportWarning> warning = new ArrayList<>();
        DataImportWarning dataImportWarning = new DataImportWarning();
        dataImportWarning.setTaskName(taskName);
        dataImportWarning.setImportType(importType);
        dataImportWarning.setFilesWithError("ImportFile1.xls , ImportFile3.xls");//TODO It will be replaced with actual method call after YUK-19061 & YUK-19160 changes. 
        dataImportWarning.setSuccessFileCount(3);//TODO It will be replaced with actual method call after YUK-19061 & YUK-19160 changes.
        warning.add(dataImportWarning);
        return warning;
    
    }
}
