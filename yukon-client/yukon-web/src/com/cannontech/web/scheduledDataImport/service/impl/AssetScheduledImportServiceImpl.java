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
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.util.BulkFileUpload;
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
    private final String failureColumnName = "Failure Reason";

    private static volatile DateTime cleanupTime;

    @Override
    public ScheduledDataImportResult initiateImport(String scheduleName, String importPath,
            String errorFileOutputPath) {

        if (cleanupTime == null || cleanupTime.isBefore(new DateTime().minusDays(1))) {
            cleanupTime = new DateTime();
            dataImportHelper.deleteArchiveFile();
        }
        return initiateAssetImport(importPath, errorFileOutputPath);

    }
     /**
      * Start asset import based on import path and create error file if there is any import error in file.
      * Also create file in archive directory and delete from import directory.
      */
    private ScheduledDataImportResult initiateAssetImport(String importPath, String errorFileOutputPath) {
        ScheduledDataImportResult dataImportResult = new ScheduledDataImportResult();
        try (Stream<Path> paths = Files.walk(Paths.get(importPath))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                   
                    String[] columnHeaders = getcolumnHeaders(path.toFile());
                    if (ArrayUtils.isNotEmpty(columnHeaders)) {
                        AccountImportResult result = new AccountImportResult();
                        String action = columnHeaders[result.COL_CUST_ACTION] != null ? columnHeaders[result.COL_CUST_ACTION] : columnHeaders[result.COL_HW_ACTION];

                        if (action != null) {
                            BulkFileUpload bulkFileUpload = new BulkFileUpload();
                            bulkFileUpload.setFile(path.toFile());

                            Instant startTime = Instant.now();
                            File archiveFile = dataImportHelper.getArchiveFile(startTime.toDate(), path.toFile(), ".csv");
                            String errorFileName = null;
                            boolean isValidActionColHeader = true;

                            if (action.equalsIgnoreCase(result.CUST_COLUMNS[result.COL_CUST_ACTION])) {
                                result.setAccountFileUpload(bulkFileUpload);
                                result.setCustArchiveDir(archiveFile);
                            } else if (action.equalsIgnoreCase(result.HW_COLUMNS[result.COL_HW_ACTION])) {
                                result.setHardwareFileUpload(bulkFileUpload);
                                result.setHwArchiveDir(archiveFile);
                            } else {
                                dataImportResult.getErrorFiles().add(path.toFile().getName());
                                log.warn("Column Name HW_ACTION/CUST_ACTION is not correct in File " + path.toFile().getName());
                                isValidActionColHeader = false;
                                moveFiletoErrorFileOutputPath(path.toFile(), errorFileOutputPath);
                            }

                            if (isValidActionColHeader) {
                                processAssetImport(path.toFile(), startTime, result);
                                if (result.hasErrors()) {
                                    errorFileName = archiveErrorsToCsvFile(startTime, result, columnHeaders, path.toFile(), errorFileOutputPath);
                                    dataImportResult.getErrorFiles().add(path.toFile().getName());
                                } else {
                                    dataImportResult.getSuccessFiles().add(path.toFile().getName());
                                }
                            }
                            ScheduledFileImportResult fileImportResult =
                                buildScheduledFileImportResult(result, startTime, path.toFile().getName(), archiveFile.getName(), errorFileOutputPath, errorFileName);
                            dataImportResult.getImportResults().add(fileImportResult);

                        } else {
                            dataImportResult.getErrorFiles().add(path.toFile().getName());
                            log.warn("HW_ACTION/CUST_ACTION is not present in File " + path.toFile().getName());
                            moveFiletoErrorFileOutputPath(path.toFile(), errorFileOutputPath);
                        }

                    }

                } catch (Exception e) {
                    dataImportResult.getErrorFiles().add(path.toFile().getName());
                    moveFiletoErrorFileOutputPath(path.toFile(), errorFileOutputPath);
                    log.error("Error occured while processing file" + path.toFile().getName() + e);
                }
            });
        } catch (IOException e) {
            log.error("Error occured while processing files due to I/O errors:" + e);
        }

        return dataImportResult;
    }

    /**
     *  Process account import based on file and account import inputs.
     */
    private void processAssetImport(File filetoProcess, Instant startTime, AccountImportResult result) throws Exception {

        result.setCurrentUser(YukonUserContext.system.getYukonUser());
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(YukonUserContext.system.getYukonUser());
        result.setEnergyCompany(energyCompany);
        result.setPrescan(true);
        result.setScheduled(true);
        result.setOutputLogDir(dataImportHelper.getArchiveFile(startTime.toDate(), filetoProcess, ".log"));
        importService.processAccountImport(result, YukonUserContext.system, startTime.toDate());

    }

    /**
     * Move import file to error output path.
     */
    private void moveFiletoErrorFileOutputPath(File filetoProcess, String errorFileOutputPath) {

        try {
            FileUtils.moveFile(filetoProcess, new File(errorFileOutputPath, filetoProcess.getName()));
        } catch (IOException e) {
            log.error("Unable to move file to Error path directory due to I/O issue" + e);
        }
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
