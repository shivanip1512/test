package com.cannontech.dr.eatonCloud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EatonCloudMessageListener {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private PxMWCommunicationServiceV1 pxMWCommunicationService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DateFormattingService dateFormattingService;
    
    //device id, group id, event id
    private final Cache<Pair<Integer, Integer>, Integer> devicesToEvents = CacheBuilder.newBuilder().build();
    
    public enum CommandParam {
        FLAGS("flags"),
        VRELAY("vrelay"),
        CYCLE_PERCENT("cycle percent"),
        CYCLE_PERIOD("cycle period"),
        CYCLE_COUNT("cycle count"),
        START_TIME("start time"),
        CONTROL_FLAGS("control flags"),
        STOP_TIME("stop time"),
        STOP_FLAGS("stop flags"), 
        CRITICALITY("criticality"),
        EVENT_ID("event id");
        
        private String paramName;

        private CommandParam(String paramName) {
            this.paramName = paramName;
        }

        public String getParamName() {
            return paramName;
        } 
    }
    
    /**
     * Format date for logging
     */
    private String formatDate(Instant date) {
        try {
            return dateFormattingService.format(date, DateFormatEnum.FULL, YukonUserContext.system);
        } catch (Exception e) {
            return "none";
        }
    }

    public void handleCyclingControlMessage(int groupId, Instant startTime, Instant endTime, int dutyCyclePercent) {
        Duration controlDuration = new Duration(startTime, endTime);
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();

        Set<Integer> devices = findInventoryAndRemoveOptOut(groupId);
        if(devices.isEmpty()) {
            log.info("No devices found for groupId:{}", groupId);
            return;  
        }
        Map<Integer, String> guids = deviceDao.getGuids(devices);   

        log.info("Sending LM Eaton Cloud Shed Command - Group Id:{} Devices:{} startTime:{} endTime:{} Duty Cycle Percent:{}",
                groupId,
                devices.size(), formatDate(startTime), formatDate(endTime), dutyCyclePercent);
   
        
        guids.forEach((deviceId, guid) -> {
            try {
                pxMWCommunicationService.sendCommand(guid,
                        new PxMWCommandRequestV1("LCR_Control",
                                getShedParams(groupId, deviceId, startTime, endTime, dutyCyclePercent)));
            } catch (Exception e) {
                log.error("Error sending shed command group id:" + groupId, e);
            }
        });

        controlHistoryService.sendControlHistoryShedMessage(groupId, 
                                                            startTime, 
                                                            ControlType.EATON_CLOUD, 
                                                            null,
                                                            controlDurationSeconds, 
                                                            dutyCyclePercent);
    }

    private Map<String, Object> getShedParams(int groupId, int deviceId, Instant startTime, Instant endTime, int dutyCyclePercent) {
        Integer eventId = nextValueHelper.getNextValue("PxMWEventIdIncrementor");
        Pair<Integer, Integer> cacheKey = Pair.of(deviceId, groupId);
        devicesToEvents.put(cacheKey, eventId);
        Map<String, Object> params = new HashMap<>();
        if (startTime != null) {
            params.put(CommandParam.START_TIME.getParamName(), startTime.getMillis() / 1000);
        } else {
            params.put(CommandParam.START_TIME.getParamName(), System.currentTimeMillis() / 1000);
        }
        
        if (endTime != null) {
            params.put(CommandParam.STOP_TIME.getParamName(), endTime.getMillis() / 1000);
        } else {
            params.put(CommandParam.STOP_TIME.getParamName(), 0);
        }
        
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.CYCLE_PERCENT.getParamName(), dutyCyclePercent);
        params.put(CommandParam.CYCLE_COUNT.getParamName(), 1);
        params.put(CommandParam.CRITICALITY.getParamName(), 255);
        params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
        return params;
    }

    public void handleRestoreMessage(int groupId, Instant restoreTime) {
        LiteYukonPAObject group = dbCache.getAllLMGroups().stream()
                .filter(g -> g.getLiteID() == groupId).findAny().orElse(null);
        if (group == null) {
            log.error("Group with id {} is not found", groupId);
            return;
        }

        Set<Integer> devices = findInventoryAndRemoveOptOut(groupId);
        if (devices.isEmpty()) {
            log.info("No devices found for groupId: {}", groupId);
            return;
        }
        log.info("Sedning LM Eaton Cloud Restore Command - Group Id:{} Devices:{} Restore Time:{}", groupId, devices.size(),
                formatDate(restoreTime));
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        guids.forEach((deviceId, guid) -> {
            try {
                pxMWCommunicationService.sendCommand(guid,
                        new PxMWCommandRequestV1("LCR_Control", getRestoreParams(groupId, deviceId, restoreTime)));
            } catch (Exception e) {
                log.error("Error sending shed command group id:" + groupId, e);
            }
        });
        controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
    }

    private Map<String, Object> getRestoreParams(int groupId, Integer deviceId, Instant restoreTime) {
        Pair<Integer, Integer> cacheKey = Pair.of(deviceId, groupId);
        Map<String, Object> params = new LinkedHashMap<>();
        Integer eventId = devicesToEvents.getIfPresent(cacheKey);
        if(eventId != null) {
            params.put(CommandParam.EVENT_ID.getParamName(), eventId);
            devicesToEvents.invalidate(cacheKey);
        } else {
            throw new BadConfigurationException(
                    "Event Id is not in cache unable to send restore to device:"
                            + deviceId + " group id:" + groupId + " restoreTime:" + formatDate(restoreTime));

        }
        params.put(CommandParam.STOP_FLAGS.getParamName(), 0);
        params.put(CommandParam.STOP_TIME.getParamName(), restoreTime.getMillis() / 1000);
        return params;
    }
    
    /**
     * Returns valid devices, removes opt outs
     */
    private Set<Integer> findInventoryAndRemoveOptOut(Integer groupId) {
        Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(Arrays.asList(groupId));
        Set<Integer> inventory = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(Arrays.asList(groupId));
        inventory.removeAll(optOutInventory);
        return inventory;
    }

}
