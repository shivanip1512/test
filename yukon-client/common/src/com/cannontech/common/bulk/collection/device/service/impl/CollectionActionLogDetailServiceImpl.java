package com.cannontech.common.bulk.collection.device.service.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.CONFIG_NAME;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.DEVICE_TYPE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.LAST_VALUE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry.POINT_DATA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionOptionalLogEntry;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class CollectionActionLogDetailServiceImpl implements CollectionActionLogDetailService {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private CollectionActionDao collectionActionDao;
    
    private static final String header="yukon.web.modules.tools.collectionActions.log.header.";
    
    private final Logger log = YukonLogManager.getLogger(CollectionActionLogDetailServiceImpl.class);
    
    private static volatile DateTime cleanupTime;

    
    /**
     * Log details are cached until execution/action is done (Completed, Canceled etc) to prevent duplicate
     * entries in the log file for the same devices.
     */
    private Cache<Integer, Set<CollectionActionLogDetail>> cache =
        CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();

    /**
     * Point id -> Point name
     */
    private Cache<Integer, String> pointNames =
        CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();
        
    @Override
    public void loadPointNames(CollectionActionResult result) {
        if (result.getAction().contains(CollectionActionOptionalLogEntry.POINT_DATA)) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    List<SimpleDevice> devices = result.getInputs().getCollection().getDeviceList();
                    log.debug("Point names load started for for cacheKey=" + result.getCacheKey() + " devices=" + devices.size());
                    List<LitePoint> points = pointDao.getLitePointsByDeviceIds(
                        devices.stream().map(d -> d.getDeviceId()).collect(Collectors.toList()));
                    pointNames.putAll(points.stream().collect(Collectors.toMap(p -> p.getLiteID(), p -> p.getPointName())));
                    log.debug("Point names load completed for for cacheKey=" + result.getCacheKey() + " loaded point names=" + points.size());
                }
            });
        }
    }

    @Override
    public List<CollectionActionLogDetail> buildLogDetails(List<? extends YukonPao> paos,
            CollectionActionDetail detail) {
        return paos.stream()
                .map(pao -> new CollectionActionLogDetail(pao, detail))
                .collect(Collectors.toList());
    }
       
    @Override
    public void appendToLog(CollectionActionResult result, CollectionActionLogDetail detail) {
        if(detail != null) {
            appendToLog(result, Lists.newArrayList(detail));
        }
    }

    @Override
    public void appendToLog(CollectionActionResult result, List<CollectionActionLogDetail> details) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(result.getContext());
        // Delete old Collection Action logs. Cleanup should not be done more than once a day.
        if (cleanupTime == null || cleanupTime.isBefore(new DateTime().minusDays(1))) {
            cleanupTime = new DateTime();
            deleteOldCollectionActionLogs();
        }
        if (!hasLog(result.getCacheKey())) {
            cache.put(result.getCacheKey(), new HashSet<>());
            addHeader(accessor, result);
        }
        if (CollectionUtils.isNotEmpty(details)) {
            List<String> data = new ArrayList<>();
            for (CollectionActionLogDetail detail : details) {
                if (cache.getIfPresent(result.getCacheKey()).contains(detail)) {
                    continue;
                }
                cache.getIfPresent(result.getCacheKey()).add(detail);
                List<String> fields = new ArrayList<>();

                if (detail.getDevice() != null) {
                    LiteYukonPAObject pao = dbCache.getAllPaosMap().get(detail.getDevice().getPaoIdentifier().getPaoId());
                    if (pao != null) {
                        fields.add(pao.getPaoName());
                    } else {
                        fields.add("");
                    }
                } else {
                    fields.add("");
                }
                fields.add(dateFormattingService.format(new Instant(), DateFormatEnum.BOTH, result.getContext()));
                fields.add(detail.getDetail() != null ? accessor.getMessage(detail.getDetail()) : "");
                fields.add(StringUtils.defaultIfEmpty(detail.getDeviceErrorText(), ""));
                if (result.getAction().contains(CONFIG_NAME)) {
                    fields.add(StringUtils.defaultIfEmpty(detail.getConfigName(), ""));
                }
                if (result.getAction().contains(DEVICE_TYPE)) {
                    fields.add(detail.getDevice() != null
                            ? accessor.getMessage(detail.getDevice().getDeviceType().getFormatKey()) : "");                }
                String pointName = "";
                String value = "";
                if (result.getAction().contains(POINT_DATA)) {
                    if (detail.getValue() != null) {
                        int pointId = detail.getValue().getId();
                        if (pointId > 0) {
                            pointName = pointNames.getIfPresent(pointId);
                            if (pointName == null) {
                                try {
                                    pointName = pointDao.getLitePoint(pointId).getPointName();
                                } catch (NotFoundException e) {
                                    pointName = "";
                                    log.error(e);
                                }
                                log.debug("Unable to find point with id=" + pointId
                                    + "in cache. Attempted the load from DB point name=" + pointName);
                            }
                        }
                        try {
                            value = "\"" + pointFormattingService.getValueString(detail.getValue(), Format.FULL,
                                result.getContext()) + "\"";

                        } catch (NotFoundException e) {
                            log.error(e);
                        }
                    }
                    fields.add(pointName);
                    fields.add(value);
                }
                if (result.getAction().contains(LAST_VALUE)) {
                    if (StringUtils.isNotEmpty(detail.getLastValue())) {
                        value = detail.getLastValue().replaceAll("/", "");
                        value = value.replaceAll("\n", "");
                    }
                    fields.add(value);
                }
                fields.add(StringUtils.defaultString(detail.getExecutionExceptionText()));
                data.add(String.join(",", fields));
            }
            writeToFile(data, result.getCacheKey());
        }
    }
    
    /**
     * Adds header to a file
     */
    private void addHeader(MessageSourceAccessor accessor, CollectionActionResult result) {
        List<String> list = new ArrayList<>();
        // required entries
        list.add(accessor.getMessage(header + "DeviceName"));
        list.add(accessor.getMessage(header + "ResponseDateTime"));
        list.add(accessor.getMessage(header + "Status"));
        list.add(accessor.getMessage(header + "Error"));
        // optional entries
        if(result.getAction().getOptionalLogEntries() != null) {
            result.getAction().getOptionalLogEntries().forEach(entry -> list.add(""));
        }
        // ExecutionExceptionText - no porter connection
        list.add("");
        writeToFile(Lists.newArrayList(String.join(",", list)), result.getCacheKey());
    }
    
    private void writeToFile(List<String> data, int cacheKey) {
        try {
            FileUtils.writeLines(new File(CtiUtilities.getCollectionActionDirPath(), String.valueOf(cacheKey) + ".csv"),
                data, true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public File getLog(int cacheKey) throws FileNotFoundException {
        String fileName = String.valueOf(cacheKey) + ".csv";
        File file = new File(CtiUtilities.getCollectionActionDirPath(), fileName);
        if (!file.exists()) {
            String error = fileName + " not found in " + CtiUtilities.getCollectionActionDirPath();
            throw new FileNotFoundException(error);
        }
        return file;
    }

    @Override
    public boolean hasLog(int cacheKey) {
        File file = new File(CtiUtilities.getCollectionActionDirPath(), String.valueOf(cacheKey) + ".csv");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private void deleteOldCollectionActionLogs() {
        int daysToKeep = globalSettingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_DAYS_TO_KEEP);
        
        if (daysToKeep > 0) {
            DateTime retentionDate = new DateTime().minusDays(daysToKeep);
            log.info("Collection action log file cleanup started. Deleting files older than " + retentionDate.toDate().toString() + ".");
            // Get Collection Action logs which are older than retentionDate (based on HISTORY_CLEANUP_DAYS_TO_KEEP)
            List<String> filesToDelete = collectionActionDao.getAllOldCollectionActionIds(retentionDate)
                                                            .stream()
                                                            .map(collectionId -> String.valueOf(collectionId) + ".csv")
                                                            .collect(Collectors.toList());
            int filesDeleted = 0;
            if (!filesToDelete.isEmpty()) {
                File currentDirectory = new File(CtiUtilities.getCollectionActionDirPath());
                File[] files = currentDirectory.listFiles(file -> filesToDelete.contains(file.getName()));
                for (File file : files) {
                    if (file.delete()) {
                        filesDeleted++;
                        log.info("Deleted collection action log file: " + file.getPath());
                    }
                }
            }
            log.info("Collection action log file cleanup is complete. " + filesDeleted + " log files were deleted.");
        } else {
            log.info("Collection action log file cleanup is disabled. No files were deleted.");
        }
    }
}
