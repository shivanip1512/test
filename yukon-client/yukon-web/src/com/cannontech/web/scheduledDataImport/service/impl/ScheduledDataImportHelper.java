package com.cannontech.web.scheduledDataImport.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.scheduledDataImport.dao.ScheduledDataImportDao;
import com.google.common.collect.Multimap;
import com.opencsv.CSVWriter;

public class ScheduledDataImportHelper {
    
    private static final Logger log = YukonLogManager.getLogger(ScheduledDataImportHelper.class);
    
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ScheduledDataImportDao scheduledDataImportDao;

    /**
     * clean archive directory based on HISTORY_CLEANUP_DAYS_TO_KEEP property.
     * and also update database that file has been deleted.
     */
    public void deleteArchiveFile() {
        boolean deleted = false;
        int daysToKeep = globalSettingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_DAYS_TO_KEEP);
        Instant cutoff = Instant.now().minus(Duration.standardDays(daysToKeep));
        int filesDeleted = 0;
        if (daysToKeep > 0) {
            Multimap<Instant, ScheduleImportHistoryEntry> entriesMap = scheduledDataImportDao.getEntriesWithArchiveByDate();

            for (Instant entryInstant : entriesMap.keySet()) {
                if (entryInstant.isBefore(cutoff)) {
                    Collection<ScheduleImportHistoryEntry> entries = entriesMap.get(entryInstant);
                    for (ScheduleImportHistoryEntry entry : entries) {
                        log.debug("Attempting to delete file \"" + entry.getArchiveFileName() + "\" with creation date"
                            + entryInstant);

                        File archiveFile = new File(CtiUtilities.getImportArchiveDirPath(), entry.getArchiveFileName());
                        if (archiveFile.exists()) {
                            deleted = archiveFile.delete();

                        } else {
                            deleted = true;
                            log.warn("Attempted to delete non-existent file. Archive file \"" + entry.getArchiveFileName()
                                + "\" not found for entry with id " + entry.getEntryId());

                        }
                        if (deleted) {
                            scheduledDataImportDao.markArchiveDeleted(entry.getEntryId());
                            filesDeleted++;
                        }
                    }
                }
            }
            if (filesDeleted > 0) {
                log.info("Schedule Import History cleanup complete. " + filesDeleted + " archived files were deleted.");
            }
        }
    }

    /**
     * Write errors into csv file in error path directory
     */
    public String archiveErrors(List<String[]> errorList, Instant startTime, String errorFileOutputPath, File file) {
        String errorFileName = null;
        if (CollectionUtils.isNotEmpty(errorList)) {
            FileUtil.verifyDirectory(errorFileOutputPath);
            errorFileName = getErrorFileName(startTime.toDate(), file, "ErrorResults", ".csv");
            File errorFile = new File(errorFileOutputPath, errorFileName);
            try (FileWriter writer = new FileWriter(errorFile);
                 CSVWriter csvWriter = new CSVWriter(writer);) {
                csvWriter.writeAll(errorList);

            } catch (IOException e) {
                log.error("Unable to generate Error file" + errorFileName + "due to I/O errors: ", e);
            }
        }
        return errorFileName;

    }

    /**
     * Get error file name (File_ErrorResults_YYYYMMDD_HHMM or
     * File_ErrorResults_IN_Header_YYYYMMddHHmm)based on startTime, file and ext.
     */
    public String getErrorFileName(Date startTime, File file, String fileNameExtraInfo, String ext) {
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        String fileName = file.getName().substring(0, file.getName().length() - 4);
        String errorFileName = fileName + "_" + fileNameExtraInfo + "_" + now + ".csv";
        return errorFileName;

    }

    /**
     * create file name (File_YYYYMMDD_HHMM) based on start time and File.
     */
    public File getArchiveFile(Date startTime, File fileToProcess, String ext) {
        String archiveDirPath = CtiUtilities.getImportArchiveDirPath();

        if (fileToProcess != null) {
            String fileName = fileToProcess.getName();
            String archiveFileName = getFileName(startTime, fileName, ext);
            return new File(archiveDirPath, archiveFileName);
        }
        return null;
    }

    /**
     *  Get File name based on startTime, FileName and extn
     */
    private String getFileName(Date startTime, String fileName, String ext) {
        String archiveFileName = fileName.substring(0, fileName.length() - 4) + "-"
            + StarsUtils.starsDateFormat.format(startTime) + "_" + StarsUtils.starsTimeFormat.format(startTime) + ext;
        return archiveFileName;

    }

}
