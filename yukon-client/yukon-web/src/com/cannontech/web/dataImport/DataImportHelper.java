package com.cannontech.web.dataImport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileImport.DataImportWarning;

public class DataImportHelper {

    private static final Logger log = YukonLogManager.getLogger(DataImportHelper.class);
    /**
     * This method converts the import result object to dataimport warning.
     */
    public static List<DataImportWarning> getDataImportWarning(Integer jobGroupId, String jobName , String importType, List<String> errorFiles, int successFileCount) {
        List<DataImportWarning> warning = new ArrayList<>();
        DataImportWarning dataImportWarning = new DataImportWarning();
        dataImportWarning.setJobGroupId(jobGroupId);
        dataImportWarning.setJobName(jobName);
        dataImportWarning.setImportType(importType);
        String fileWithError = String.join(",", errorFiles);
        if (StringUtils.isNotBlank(fileWithError) && fileWithError.length() > 500) {
            fileWithError = reduceFileWithError(fileWithError);
        }
        dataImportWarning.setFilesWithError(fileWithError);
        dataImportWarning.setSuccessFileCount(successFileCount);
        warning.add(dataImportWarning);
        return warning;
    
    }

    /**
     * This method is used to reduce the fileWithError string in case we have
     * large number of files. For fileWithError with more than 500 char
     * we will reduce it to small size to fit into database column and 
     * print the additional info inside log file.
     */
    private static String reduceFileWithError(String fileWithError) {
        String limitedString = fileWithError.substring(0, 469);
        int lastIndex = limitedString.lastIndexOf(",");
        String reducedFileWithError = fileWithError.substring(0, lastIndex);
        String additionalFileWithError = fileWithError.substring(lastIndex + 1);
        List<String> errorFiles = new ArrayList<>(Arrays.asList(additionalFileWithError.split(",")));
        if (!errorFiles.isEmpty()) {
            reducedFileWithError = reducedFileWithError.concat(" And " + errorFiles.size() + " additional file.");
            log.debug("Additional files failed with error " + errorFiles);
        }
        return reducedFileWithError;
    }

}