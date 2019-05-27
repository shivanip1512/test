package com.cannontech.web.scheduledDataImport.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ToolsEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.common.util.FileUtil;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.scheduledDataImport.ScheduledDataImportException;
import com.cannontech.web.scheduledDataImport.ScheduledDataImportResult;
import com.cannontech.web.scheduledDataImport.ScheduledFileImportResult;
import com.cannontech.web.scheduledDataImport.service.ScheduledImportService;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;
import com.cannontech.web.stars.dr.operator.importAccounts.service.AccountImportService;
import com.google.common.collect.Lists;

public class AssetScheduledImportServiceImpl implements ScheduledImportService {

    private static final Logger log = YukonLogManager.getLogger(AssetScheduledImportServiceImpl.class);

    @Autowired private AccountImportService importService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ScheduledDataImportHelper dataImportHelper;
    @Autowired private ToolsEventLogService logService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private final String failureColumnName = "Failure Reason";
    private static final String baseKey = "yukon.web.modules.operator.scheduledDataImportDetail.";
    private static final String typeKey = "yukon.web.modules.operator.scheduledData.importType.";
     
    
    Set<String> importPathSet = ConcurrentHashMap.newKeySet();

    private static volatile DateTime cleanupTime;

    @Override
    public ScheduledDataImportResult initiateImport(YukonUserContext userContext, String scheduleName, String importPath,
            String errorFileOutputPath) {

        if (cleanupTime == null || cleanupTime.isBefore(new DateTime().minusDays(1))) {
            cleanupTime = new DateTime();
            dataImportHelper.deleteArchiveFile();
        }
        return initiateAssetImport(userContext, scheduleName, importPath, errorFileOutputPath);

    }
     /**
      * Start asset import based on import path and create error file if there is any import error in file.
      * Also create file in archive directory and delete from import directory.
      */
    private ScheduledDataImportResult initiateAssetImport(YukonUserContext userContext, String scheduleName, String importPath, String errorFileOutputPath) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        boolean importPathCheck = importPathSet.add(importPath);
        ScheduledDataImportResult dataImportResult = new ScheduledDataImportResult();
        if (importPathCheck) {
            try (Stream<Path> paths = Files.walk(Paths.get(importPath))) {
                paths.filter(path -> (Files.isRegularFile(path) && FileUploadUtils.validateCsvFileContentType(path)
                    && path.toFile().length() > 4)).forEach(path -> {
                        // Check if a file is empty or not based on byte size (length > 4). (if remove the data from excel,
                        // it still give you empty string for row)
                    Instant startTime = Instant.now();
                    String errorFileName = null;
                    AccountImportResult result = new AccountImportResult();


                    log.info("Scheduled Data Import for type Asset Started at " + startTime.toDate() + " for  "
                        + path.toFile());
                    logService.importStarted(userContext.getYukonUser(), 
                                            accessor.getMessage(baseKey + "dataImportSchedule") + " - "
                                           + accessor.getMessage(typeKey + "ASSET_IMPORT"), path.getFileName().toString());
                    File archiveFile = dataImportHelper.getArchiveFile(startTime.toDate(), path.toFile(), ".csv");
                    try {
                        String[] columnHeaders = getcolumnHeaders(path.toFile());
                        // both Column COL_HW_ACTION/COL_CUST_ACTION at second position in file
                        if (ArrayUtils.isNotEmpty(columnHeaders) && columnHeaders.length > 2) {
                            String action = columnHeaders[result.COL_CUST_ACTION] != null ? columnHeaders[result.COL_CUST_ACTION] : columnHeaders[result.COL_HW_ACTION];
                            if (action != null) {
                                BulkFileUpload bulkFileUpload = new BulkFileUpload();
                                bulkFileUpload.setFile(path.toFile());
                                boolean isValidActionColHeader = true;
                                if (action.equalsIgnoreCase(result.CUST_COLUMNS[result.COL_CUST_ACTION])) {
                                    log.debug("Customer file type detected");
                                    result.setAccountFileUpload(bulkFileUpload);
                                    result.setCustArchiveDir(archiveFile);
                                } else if (action.equalsIgnoreCase(result.HW_COLUMNS[result.COL_HW_ACTION])) {
                                    log.debug("Hardware file type detected");
                                    result.setHardwareFileUpload(bulkFileUpload);
                                    result.setHwArchiveDir(archiveFile);
                                } else {
                                    dataImportResult.getErrorFiles().add(path.toFile().getName());
                                    isValidActionColHeader = false;
                                    errorFileName = moveFiletoErrorFileOutputPath(startTime, path.toFile(), errorFileOutputPath, archiveFile);
                                    log.warn("Column Name HW_ACTION/CUST_ACTION is not correct in File " + path.toFile().getName());
                                }

                                if (isValidActionColHeader) {
                                    processAssetImport(userContext, path.toFile(), startTime, result);
                                    if (result.hasErrors()) {
                                        errorFileName = archiveErrorsToCsvFile(startTime, result, columnHeaders, path.toFile(), errorFileOutputPath);
                                        dataImportResult.getErrorFiles().add(path.toFile().getName());
                                    } else {
                                        dataImportResult.getSuccessFiles().add(path.toFile().getName());
                                    }
                                }

                            } else {
                                dataImportResult.getErrorFiles().add(path.toFile().getName());
                                errorFileName = moveFiletoErrorFileOutputPath(startTime, path.toFile(), errorFileOutputPath, archiveFile);
                                log.warn("HW_ACTION/CUST_ACTION is not present in File " + path.toFile().getName());
                            }

                        }

                    } catch (ScheduledDataImportException | IOException e) {
                        dataImportResult.getErrorFiles().add(path.toFile().getName());
                        errorFileName = moveFiletoErrorFileOutputPath(startTime, path.toFile(), errorFileOutputPath, archiveFile);
                        log.error("Error occured while processing file " + path.toFile().getName() + e);
                    }

                    ScheduledFileImportResult fileImportResult = buildScheduledFileImportResult(result, startTime,
                        path.toFile().getName(), archiveFile.getName(), errorFileOutputPath, errorFileName);
                    dataImportResult.getImportResults().add(fileImportResult);

                    log.info("Scheduled Data Import for type Asset completed at " + Instant.now().toDate() + " for  " + path.toFile());
                    logService.importCompleted(accessor.getMessage(baseKey + "dataImportSchedule") + " - " + accessor.getMessage(typeKey + "ASSET_IMPORT"),
                        path.getFileName().toString(), fileImportResult.getSuccessCount(), fileImportResult.getFailureCount());
                });
            } catch (Exception e) {
                log.error("Error occured while processing files: " + e);
                logService.scheduleImportError(scheduleName, accessor.getMessage(typeKey + "ASSET_IMPORT"),
                    e.toString());
            }
            importPathSet.remove(importPath);
        } else {
            log.warn(scheduleName + " Schedule is not importing files as another schedule "
                + "is already running on directory " + importPath);
        }
        return dataImportResult;
    }

    /**
     *  Process account import based on file and account import inputs.
     */
    private void processAssetImport(YukonUserContext userContext, File filetoProcess, Instant startTime, AccountImportResult result) {
        LiteYukonUser user = userContext.getYukonUser();
        result.setCurrentUser(user);
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        result.setEnergyCompany(energyCompany);
        result.setPrescan(true);
        result.setScheduled(true);
        result.setOutputLogDir(dataImportHelper.getArchiveFile(startTime.toDate(), filetoProcess, ".log"));
        importService.processAccountImport(result, userContext, startTime.toDate());

    }

    /**
     * Move import file to error output path and archive directory and return error file Name.
     */
    private String moveFiletoErrorFileOutputPath(Instant startTime, File filetoProcess, String errorFileOutputPath, File archiveFile) {
        String fileName = null;
        try {
            FileUtil.verifyDirectory(errorFileOutputPath);
            fileName = dataImportHelper.getErrorFileName(startTime.toDate(), filetoProcess, "_ErrorResults_IN_Header_", ".csv");
            FileUtils.copyFile(filetoProcess, new File(errorFileOutputPath, fileName));
            // Move to archive directory
            if (!archiveFile.exists()) {
                FileUtils.moveFile(filetoProcess, archiveFile);
            } else {
                boolean isDeleted = filetoProcess.delete();
                if (isDeleted == false) {
                    log.error("* " + filetoProcess.getName() + " was not deleted *");
                }
            }

        } catch (IOException e) {
            log.error("Unable to move file to Error path directory due to I/O issue " + e);
        }

        return fileName;
    }

    /**
     * Read header columns from CSV file for deciding customer/hardware file.
     * Based on action column, set files in result for further process.
     */
    private String[] getcolumnHeaders(File file) throws IOException {
        String[] columnHeaders;
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file))) {
            CSVReader csvReader = new CSVReader(inputStreamReader);
            columnHeaders = csvReader.readNext();
        }
        return columnHeaders;
    }

    /**
     * Read errors from account import result and write into CSV error output file.
     */
    private String archiveErrorsToCsvFile(Instant startTime, AccountImportResult result, String[] columnHeaders,
            File file, String errorFileOutputPath) {

        List<String[]> errorList = getErrors(result, columnHeaders);
        String errorFileName = dataImportHelper.archiveErrors(errorList, startTime, errorFileOutputPath, file);
        return errorFileName;

    }

    /**
     * Build data errors in csv format from account import result and create addition column to show failure reason.
     */
    private List<String[]> getErrors(AccountImportResult result, String[] columnHeaders) {
        List<String[]> errors = Lists.newArrayList();
        String[] headers = Arrays.copyOf(columnHeaders, columnHeaders.length + 1);
        headers[columnHeaders.length] = failureColumnName;

        errors.add(headers);

        Pattern pattern = Pattern.compile("^\\[line:\\s+(\\d+)\\s+error:\\s+(.+)\\]$");
        Object[] array = result.getErrorList().toArray();

        for (int x = 0; x < result.getErrorList().size(); x++) {
            if (array[x] instanceof String[]) {
                String[] value = (String[]) array[x];
                if (value.length > 1 && value[1] != null) {
                    Matcher m = pattern.matcher(value[1]);
                    if (m.matches()) {
                        String[] rowData = StringEscapeUtils.escapeHtml4(value[0]).split(",");
                        String[] columnData = Arrays.copyOf(rowData, headers.length);
                        columnData[headers.length - 1] = m.group(2);
                        errors.add(columnData);
                    }
                }
            }
        }
        return errors;
    }

    /**
     * Create ScheduledFileImportResult for smart notification and save data in schedule data import
     * history
     */
    private ScheduledFileImportResult buildScheduledFileImportResult(AccountImportResult result, Instant startTime,
            String fileName, String archiveFileName, String errorFileOutputPath, String failedFileName) {

        int successCount = result.getCompleted() - result.getErrors().size();

        return new ScheduledFileImportResult(fileName, ScheduledImportType.ASSET_IMPORT, startTime, archiveFileName,
            true, failedFileName, errorFileOutputPath, successCount, result.getErrors().size());

    }

}
