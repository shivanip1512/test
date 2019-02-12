package com.cannontech.web.dataImport;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.scheduledFileImport.DataImportWarning;

public class DataImportHelper {

    /**
     * This method converts the import result object to dataimport warning.
     */
    public static List<DataImportWarning> getDataImportWarning(Integer jobGroupId, String jobName , String importType, List<String> errorFiles, int successFileCount) {
        List<DataImportWarning> warning = new ArrayList<>();
        DataImportWarning dataImportWarning = new DataImportWarning();
        dataImportWarning.setJobGroupId(jobGroupId);
        dataImportWarning.setJobName(jobName);
        dataImportWarning.setImportType(importType);
        dataImportWarning.setFilesWithError(String.join(",", errorFiles));
        dataImportWarning.setSuccessFileCount(successFileCount);
        warning.add(dataImportWarning);
        return warning;
    
    }
}