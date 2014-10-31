package com.cannontech.common.fileExportHistory.task;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.dao.FileExportHistoryDao;
import com.cannontech.common.fileExportHistory.service.FileExportHistoryService;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class RepeatingExportHistoryDeletionTask extends YukonTaskBase {
    private Logger log = YukonLogManager.getLogger(RepeatingExportHistoryDeletionTask.class);
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private FileExportHistoryDao fileExportHistoryDao;
    @Autowired private FileExportHistoryService fileExportHistoryService;
    
    @Override
    public void start(){
        //get global settings
        int daysToKeep = globalSettingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_DAYS_TO_KEEP);
        int filesToKeep = globalSettingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_FILES_TO_KEEP);
        log.info("Starting file export history cleanup. History cleanup Days To Keep: " + daysToKeep +
                 ". History cleanup Files To Keep: " + filesToKeep);
        
        int filesDeleted = 0;
        
        //delete by age
        if(daysToKeep > 0) {
            log.info("Deleting files older than " + daysToKeep + " days old.");
            filesDeleted += deleteByAge(daysToKeep);
        }
        
        //delete by quantity per Schedule
        if(filesToKeep > 0) {
            log.info("Deleting files for any schedule with more than " + filesToKeep + ".");
            filesDeleted += deleteByQuantity(filesToKeep);
        }
        log.info("File export history cleanup complete. " + filesDeleted + " archived files were deleted.");
    }
    
    private int deleteByAge(int daysToKeep) {
        Instant cutoff = Instant.now().minus(Duration.standardDays(daysToKeep));
        int filesDeleted = 0;
        //Only get entries that still have an archive file
        Multimap<Instant, ExportHistoryEntry> entriesMap = fileExportHistoryDao.getEntriesWithArchiveByDate();
        
        for(Instant entryInstant : entriesMap.keySet()) {
            //check if entry is too old
            if(entryInstant.isBefore(cutoff)) {
                Collection<ExportHistoryEntry> entries = entriesMap.get(entryInstant);
                for(ExportHistoryEntry entry : entries) {
                    //delete file
                    log.debug("Attempting to delete file \"" + entry.getFileName() +"\" with creation date" + entryInstant);
                    boolean deleted = fileExportHistoryService.deleteArchivedFile(entry.getId());
                    if(deleted) {
                        filesDeleted++;
                    }
                }
            }
        }
        return filesDeleted;
    }
    
    private int deleteByQuantity(int filesToKeep) {
        int filesDeleted = 0;
        //Only get entries that still have an archive file
        Multimap<String, ExportHistoryEntry> entriesMap = fileExportHistoryDao.getEntriesWithArchiveBySchedule();
        
        for(String jobName : entriesMap.keySet()) {
            //check if Schedule has too many archived files
            List<ExportHistoryEntry> entries = Lists.newArrayList(entriesMap.get(jobName));
            int numberOverMax = entries.size() - filesToKeep;
            if(numberOverMax > 0) {
                //keep deleting oldest file until no longer over the maximum
                Collections.sort(entries, ExportHistoryEntry.OLDEST_TO_NEWEST_COMPARATOR);
                for(int index = 0; numberOverMax > 0; index++, numberOverMax--) {
                    //delete file
                    ExportHistoryEntry entry = entries.get(index);
                    log.debug("Attempting to delete file \"" + entry.getFileName() + "\" for jobName \"" + jobName);
                    boolean deleted = fileExportHistoryService.deleteArchivedFile(entry.getId());
                    if(deleted) {
                        filesDeleted++;
                    }
                }
            }
        }
        return filesDeleted;
    }
}