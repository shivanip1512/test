package com.cannontech.services.assetAvailability.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.impl.AssetAvailabilityServiceImpl;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.simulators.message.request.AssetAvailArchiveSimulatorRequest;
import com.cannontech.yukon.IDatabaseCache;


public class AssetAvailabilityArchivingService {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityArchivingService.class);
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private AssetAvailabilityServiceImpl assetAvailabilityService;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource dataSource;
   
    
    public void startSimulation(AssetAvailArchiveSimulatorRequest request) {
        log.info("Simulation started");
        archive();
    }
    
    @PostConstruct
    public void init() {
        Long elevenPm = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay().minusHours(1), ChronoUnit.MINUTES);
        // https://www.rocknets.com/calculator/time-and-date/minutes-from-now
        // enter minutes to see what time it will actually run
        log.info("Scheduling Asset Availability Archiving to run at in {} minutes", elevenPm);
        executor.schedule(() -> {
            executor.scheduleAtFixedRate(() -> {
                archive();
            }, 0, 1, TimeUnit.DAYS);
        }, elevenPm, TimeUnit.MINUTES);
    }
    
    private List<PointData> archive(LiteYukonPAObject pao, Date now) {
        List<PointData> pointData = new ArrayList<>();
        if (!attributeService.isAttributeSupported(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_TOTAL_DEVICES)) {
            // device doesn't support asset availability archiving
            return pointData;
        }
        AssetAvailabilitySummary summary = assetAvailabilityService.getAssetAvailabilityFromDrGroup(pao.getPaoIdentifier());
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_UNAVAILABLE_DEVICES,
                (double) summary.getUnavailableSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_INACTIVE_DEVICES,
                (double) summary.getInactiveSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_OPTED_OUT_DEVICES,
                (double) summary.getOptedOutSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_ACTIVE_DEVICES,
                (double) summary.getActiveSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_TOTAL_DEVICES,
                (double) summary.getTotalSize(), now));
        return pointData;
    }
    
    private void archive() {
        log.info("Started archiving asset availability point data");
        List<PointData> pointData = new ArrayList<>();
        Date now = new Date();
        dbCache.getAllLMGroups().forEach(pao -> pointData.addAll(archive(pao, now)));
        dbCache.getAllLMScenarios().forEach(pao -> pointData.addAll(archive(pao, now)));
        dbCache.getAllLMControlAreas().forEach(pao -> pointData.addAll(archive(pao, now)));
        dbCache.getAllLMPrograms().forEach(pao -> pointData.addAll(archive(pao, now)));
        
        dataSource.putValues(pointData);
        log.info("Finished archiving asset availability point data, {} point data created", pointData.size());
    }
    
    
    private PointData generatePointData(LiteYukonPAObject device, BuiltInAttribute attribute, double value, Date now) {
        PointData pointData = new PointData();
        pointData.setTime(now);
        boolean pointCreated = attributeService.createPointForAttribute(device, attribute);
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
        if (pointCreated) {
            log.info(
                    "Device Id:{} Name:{} Attribute:{} Point Id:{} Point {} created.", device.getLiteID(),
                    device.getPaoName(), attribute, point.getLiteID(), point.getPointName());
        }
        
        pointData.setId(point.getLiteID());
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setValue(value);
        pointData.setType(point.getPointType());
        pointData.setTagsPointMustArchive(true);
  
        log.debug(
                "Device Id:{} Name:{} Attribute:{} Point Id:{} Point Name:{} point data created with value:{} using point multiplier:{} data:{}.",
                device.getLiteID(),
                device.getPaoName(),
                attribute,
                point.getLiteID(),
                point.getPointName(),
                pointData.getValue(),
                point.getMultiplier(),
                pointData.getTimeStamp());
        
        return pointData;
    }
}
