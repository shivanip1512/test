package com.cannontech.services.assetAvailability.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.simulators.message.request.AssetAvailArchiveSimulatorRequest;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Multimap;


public class AssetAvailabilityArchivingService {
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityArchivingService.class);
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private AssetAvailabilityServiceImpl assetAvailabilityService;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource dataSource;
    @Autowired private ProgramDao programDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private ScenarioDao scenarioDao;
    
    public void startSimulation(AssetAvailArchiveSimulatorRequest request) {
        log.info("Simulation started");
        archive();
    }
    
    @PostConstruct
    public void init() {
        Long minutesToElevenPm = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay().minusHours(1), ChronoUnit.MINUTES);
        // https://www.rocknets.com/calculator/time-and-date/minutes-from-now
        // enter minutes to see what time it will actually run
        log.info("Scheduling Asset Availability Archiving to run in {} minutes", minutesToElevenPm);
        Long minutesInADay = 1440L;
        executor.scheduleAtFixedRate(() -> {
            archive();
        }, minutesToElevenPm, minutesInADay, TimeUnit.MINUTES);

    }
    
    private List<PointData> getPointDataForSummary(LiteYukonPAObject pao, AssetAvailabilitySummary summary, Date now) {
        List<PointData> pointData = new ArrayList<>();
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_UNAVAILABLE_DEVICES,
                summary.getUnavailableSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_INACTIVE_DEVICES,
                summary.getInactiveSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_OPTED_OUT_DEVICES,
                summary.getOptedOutSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_ACTIVE_DEVICES,
                summary.getActiveSize(), now));
        pointData.add(generatePointData(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_TOTAL_DEVICES,
                summary.getTotalSize(), now));
        return pointData;
    }
    
    private void archive() {
        if(dbCache.getAllLMGroups().isEmpty()) {
            return;
        }
        
        log.info("Started archiving asset availability point data");
        List<PointData> pointData = new ArrayList<>();

        Map<Integer, AssetAvailabilitySummary> groupIdToSummary = new HashMap<>();

        Date now = new Date();
        dbCache.getAllLMGroups().stream()
                .filter(pao -> attributeService.isAttributeSupported(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_TOTAL_DEVICES))
                .forEach(pao -> {
                    AssetAvailabilitySummary summary = assetAvailabilityService
                            .getAssetAvailabilityFromDrGroup(pao.getPaoIdentifier());
                    groupIdToSummary.put(pao.getLiteID(), summary);
                    pointData.addAll(getPointDataForSummary(pao, summary, now));
                });

        if (!dbCache.getAllLMPrograms().isEmpty()) {
            Multimap<Integer, Integer> programIdsToGroupIds = programDao
                    .getGroupIdsByProgramIds(getIds(dbCache.getAllLMPrograms()));
            archive(dbCache.getAllLMPrograms(), pointData, groupIdToSummary, now, programIdsToGroupIds);
        }
        if (!dbCache.getAllLMControlAreas().isEmpty()) {
            Multimap<Integer, Integer> controlAreaIdsToGroupIds = controlAreaDao
                    .getGroupIdsByControlAreaId(getIds(dbCache.getAllLMControlAreas()));
            archive(dbCache.getAllLMControlAreas(), pointData, groupIdToSummary, now, controlAreaIdsToGroupIds);
        }
        if (!dbCache.getAllLMScenarios().isEmpty()) {
            Multimap<Integer, Integer> scenarioIdsToGroupIds = scenarioDao
                    .getGroupIdsByScenarioId(getIds(dbCache.getAllLMScenarios()));
            archive(dbCache.getAllLMScenarios(), pointData, groupIdToSummary, now, scenarioIdsToGroupIds);
        }

        dataSource.putValues(pointData);
        log.info("Finished archiving asset availability point data, {} point data created", pointData.size());
    }

    private Set<Integer> getIds(List<LiteYukonPAObject> paos) {
        Set<Integer> programIds = paos.stream()
                .filter(pao -> attributeService.isAttributeSupported(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_TOTAL_DEVICES))
                .map(pao -> pao.getLiteID())
                .collect(Collectors.toSet());
        return programIds;
    }

    private void archive(List<LiteYukonPAObject> paos, List<PointData> pointData,
            Map<Integer, AssetAvailabilitySummary> groupIdToSummary, Date now,
            Multimap<Integer, Integer> idsToGroups) {
        paos.stream()
                .filter(pao -> attributeService.isAttributeSupported(pao, BuiltInAttribute.LM_ASSET_AVAILABILITY_TOTAL_DEVICES))
                .forEach(pao -> {
                    Collection<Integer> groupIds = idsToGroups.get(pao.getLiteID());
                    List<AssetAvailabilitySummary> groupSummary = groupIdToSummary.entrySet().stream()
                            .filter(entry -> groupIds.contains(entry.getKey()))
                            .map(entry -> entry.getValue())
                            .collect(Collectors.toList());

                    AssetAvailabilitySummary summary = new AssetAvailabilitySummary();
                    summary.setActiveSize(groupSummary.stream().mapToInt(s -> s.getActiveSize()).sum());
                    summary.setUnavailableSize(groupSummary.stream().mapToInt(s -> s.getUnavailableSize()).sum());
                    summary.setInactiveSize(groupSummary.stream().mapToInt(s -> s.getInactiveSize()).sum());
                    summary.setTotalSize(groupSummary.stream().mapToInt(s -> s.getTotalSize()).sum());
                    summary.setOptedOutSize(groupSummary.stream().mapToInt(s -> s.getOptedOutSize()).sum());
                    pointData.addAll(getPointDataForSummary(pao, summary, now));
                });
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
