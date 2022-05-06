package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.GraphIntervalRounding;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.ZoneDtoHelper;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfLineSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class VoltageFlatnessGraphServiceImpl implements VoltageFlatnessGraphService {
    
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private PaoDao paoDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private StrategyDao strategyDao;
    @Autowired private PointDao pointDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private ZoneService zoneService;
    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ZoneDtoHelper zoneDtoHelper;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;

    private static final Logger log = YukonLogManager.getLogger(VoltageFlatnessGraphService.class);
    
    private int bucketResolution;

    //Every Graph must have a unique id
    private AtomicInteger graphId = new AtomicInteger();
    
    private Ordering<VfPoint> positionOrderer = Ordering.natural().onResultOf(new Function<VfPoint, Double>() {
        @Override
        public Double apply(VfPoint from) {
            return from.getX();
        }
    });

    @PostConstruct
    public void initialize() {
        try {
            bucketResolution = configurationSource.getInteger("CAP_CONTROL_IVVC_GRAPH_RESOLUTION", 200);
        } catch (NumberFormatException e) {
            bucketResolution = 200;
            log.error("Error getting numeric value from master config value: CAP_CONTROL_IVVC_GRAPH_RESOLUTION" + 
                      ". Using default of " + bucketResolution + ". ", e);
        }
    }
    
    @Override
    public VfGraph getSubBusGraph(YukonUserContext userContext, int subBusId) {
        VfGraph graph = buildSubBusGraph(userContext, subBusId);
        return graph;
    }
    
    @Override
    public VfGraph getZoneGraph(YukonUserContext userContext, int zoneId) {
		VfGraph graph = buildZoneGraph(userContext, zoneId);
		return graph;
    }

    private VfGraph buildSubBusGraph(YukonUserContext userContext, int subBusId) {
        LiteYukonUser yukonUser = userContext.getYukonUser();
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        VfGraph graph = new VfGraph();
        VfGraphSettings settings = buildSubBusGraphSettings(subBusId, cache, userContext);
        graph.setSettings(settings);
        
        //Build A line for each feeder on the bus.
        List<Feeder> feeders = cache.getFeedersBySubBus(subBusId);
        List<VfLine> feederLines = buildLineDataForFeeders(graph.getSettings(), userContext, cache, zones, feeders);
        graph.setLines(feederLines);
        setSubBusGraphLineLegendVisibility(graph);
        addYAxisLimitsToSettings(graph, userContext);
        
        transformToLineGraph(graph);
        return graph;
    }
    
    private void addYAxisLimitsToSettings(VfGraph graph, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Double yLowest = getMinYValue(graph.getLines());
        Double yHighest = getMaxYValue(graph.getLines());
        double yMinLimit = (yLowest != null && yLowest < graph.getSettings().getYLowerBound()) ? yLowest : graph.getSettings().getYLowerBound(); 
        double yMaxLimit = (yHighest != null && yHighest > graph.getSettings().getYUpperBound()) ? yHighest : graph.getSettings().getYUpperBound();

        Double yMin = yMinLimit + 
                Double.valueOf(messageSourceAccessor.getMessage(
                    "yukon.web.modules.capcontrol.ivvc.voltProfileGraph.values.yLeft.min"));
        Double yMax = yMaxLimit + 
                Double.valueOf(messageSourceAccessor.getMessage(
                    "yukon.web.modules.capcontrol.ivvc.voltProfileGraph.values.yLeft.max"));
        graph.getSettings().setyMin(yMin);
        graph.getSettings().setyMax(yMax);
        //adjust points so they display at top or bottom if they are off the chart
        graph.getLines().forEach(line -> {
            line.getPoints().forEach(point -> {
                if (point.isIgnore() && (point.getY() < yMin)) {
                    point.setY(yMin);
                }
                if (point.isIgnore() && (point.getY() > yMax)) {
                    point.setY(yMax);
                }
            });
        });
    }
    
    private void setSubBusGraphLineLegendVisibility(VfGraph graph) {
        List<VfLine> lines = graph.getLines();
        Map<Phase, Boolean> phaseShownMap = Maps.newHashMapWithExpectedSize(3);
        for (Phase phase : Phase.values()) {
            phaseShownMap.put(phase, false);
        }
        for (VfLine line : lines) {
            boolean isVisibleInLegend = line.getSettings().isVisibleInLegend();
            Phase phase = line.getPhase();
            if (isVisibleInLegend){
                if (phaseShownMap.get(phase)) {
                    line.getSettings().setVisibleInLegend(false);
                } else {
                    phaseShownMap.put(phase, true);
                }
            }
        }
        graph.setLines(lines);
    }
    
	private VfGraph buildZoneGraph(YukonUserContext userContext, int zoneId) {
        LiteYukonUser yukonUser = userContext.getYukonUser();
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
        Zone zone = zoneService.getZoneById(zoneId);
        VfGraph graph = new VfGraph();
        VfGraphSettings settings = buildZoneGraphSettings(zoneId, cache, userContext);
        graph.setSettings(settings);
        
        List<Feeder> feeders = cache.getFeedersBySubBus(zone.getSubstationBusId());
        List<VfLine> feederLines = buildLineDataForFeeders(graph.getSettings(), userContext, cache, Arrays.asList(zone), feeders);
        graph.setLines(feederLines);

        addYAxisLimitsToSettings(graph, userContext);

        transformToLineGraph(graph);
        return graph;
    }
    
	private VfGraphSettings buildSubBusGraphSettings(int subBusId, CapControlCache cache, 
	                                                 YukonUserContext userContext) {
        SubBus subBus = cache.getSubBus(subBusId);
        String subBusName = subBus.getCcName();
        VfGraphSettings graphSettings = getGraphSettings(subBusId, subBusName, cache, userContext);
        return graphSettings;
    }
	
	private VfGraphSettings buildZoneGraphSettings(int zoneId, CapControlCache cache, 
	                                               YukonUserContext userContext) {
        Zone zone = zoneService.getZoneById(zoneId);
        VfGraphSettings graphSettings = getGraphSettings(zone.getSubstationBusId(), zone.getName(), 
                                                         cache, userContext);
        return graphSettings;
    }

    private VfGraphSettings getGraphSettings(int subBusId, String name, CapControlCache cache, 
                                             YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String graphWidgetLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.graphWidgetLabel");
        String yAxisLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.yAxisLabel");
        String xLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.xAxisLabel");
        String xAxisLabel;
        if (xLabel != StringUtils.EMPTY) {
            xAxisLabel = xLabel;
        } else {
            xAxisLabel = null;
        }

        double upperLimit = getUpperVoltLimitForSubBus(cache, subBusId);
        double lowerLimit = getLowerVoltLimitForSubBus(cache, subBusId);

        String balloonDistanceText = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.distance");
        
        Map<Phase, String> phaseStringMap = Maps.newHashMapWithExpectedSize(3);
        Map<Phase, String> zoneLineColorPhaseMap = Maps.newHashMapWithExpectedSize(3);
        for (Phase phase : Phase.getRealPhases()) {
            String phaseString = messageSourceAccessor.getMessage("yukon.common.phase");
            phaseString += " " + messageSourceAccessor.getMessage(phase);
            phaseStringMap.put(phase, phaseString);
            String zoneLineColorPhase = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhase" + phase);
            zoneLineColorPhaseMap.put(phase, zoneLineColorPhase);
        }

        String zoneTransitionDataLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneTransitionDataLabel");
        String zoneLineColorNoPhase = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorNoPhase");
        String zonePointColorIgnoredPoints = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zonePointColorIgnoredPoints");
        String zonePointColorNoFeederPoints = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zonePointColorNoFeederPoints");
        boolean showZoneTransitionTextBusGraph = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.showZoneTransitionText.busGraph"));
        boolean showZoneTransitionTextZoneGraph = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.showZoneTransitionText.zoneGraph"));
        String graphTitle = name + " " + graphWidgetLabel;

        VfGraphSettings graphSettings =
            new VfGraphSettings(lowerLimit,
                                upperLimit,
                                yAxisLabel,
                                xAxisLabel,
                                graphTitle,
                                graphWidgetLabel,
                                phaseStringMap,
                                zoneLineColorPhaseMap,
                                zoneLineColorNoPhase,
                                showZoneTransitionTextBusGraph,
                                showZoneTransitionTextZoneGraph,
                                zoneTransitionDataLabel,
                                balloonDistanceText,
                                zonePointColorIgnoredPoints,
                                zonePointColorNoFeederPoints);
        return graphSettings;
    }

	private double getGraphStartPositionForZone(Zone zone) {
		double startingPos = 0;
		startingPos = zone.getGraphStartPosition();
		
		if (zone.getParentId() != null) {
			Zone parent = zoneService.getZoneById(zone.getParentId());
			startingPos += getGraphStartPositionForZone(parent);
		}
		
		return startingPos;
	}

    private Set<Integer> getAllPointsInSubBusGraph(int subBusId) {
        Set<Integer> allGraphPoints = Sets.newHashSet();
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        for (Zone zone : zones) {
            for (RegulatorToZoneMapping regulatorToZone: zone.getRegulators()) {
                //Regulator point
                int regulatorId = regulatorToZone.getRegulatorId();
                LitePoint regulatorPoint = getRegulatorVoltagePoint(regulatorId);
                if (regulatorPoint == null) {
                    return null;
                }
                allGraphPoints.add(regulatorPoint.getPointID());
            }

            // Cap bank points
            List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
            for (CapBankToZoneMapping bankToZone : banksToZone) {
                List<Integer> bankPoints = zoneService.getMonitorPointsForBank(bankToZone.getDeviceId());
                allGraphPoints.addAll(bankPoints);
            }

            // Additional voltage points
            List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
            for (PointToZoneMapping pointToZone : pointsToZone) {
                allGraphPoints.add(pointToZone.getPointId());
            }
        }
        return allGraphPoints;
    }

    @Override
    public long getLargestPointTimeForSubBusGraph(int subBusId) {
        Set<Integer> allGraphPoints = getAllPointsInSubBusGraph(subBusId);
        if (allGraphPoints == null) {
            return 0;
        }
        long time = getLargestPointTime(allGraphPoints);
        return time;
    }

    private long getLargestPointTime(Set<Integer> allGraphPoints) {
        long largestTime = 0;
        Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getPointValues(allGraphPoints);

        for (PointValueQualityHolder point : pointValues) {
            long pointTime = point.getPointDataTimeStamp().getTime();
            if (largestTime < pointTime) {
                largestTime = pointTime;
            }
        }
        return largestTime;
    }
    
    private Double getMinYValue(List<VfLine> lines) {
        Double yMin = null;
        for (VfLine line : lines) {
            for (VfPoint point : line.getPoints()) {
                if (!point.isIgnore()) {
                    if (yMin == null || point.getY() < yMin) {
                        yMin = point.getY();
                    }
                }
            }
        }
        return yMin;
    }
    
    private Double getMaxYValue(List<VfLine> lines) {
        Double yMax = null;
        for (VfLine line : lines) {
            for (VfPoint point : line.getPoints()) {
                if (!point.isIgnore()) {
                    if (yMax == null || point.getY() > yMax) {
                        yMax = point.getY();
                    }
                }
            }
        }
        return yMax;
    }
    
    private LitePoint getRegulatorVoltagePoint(int regulatorId) {
        YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
        RegulatorPointMapping regulatorVoltageMapping = RegulatorPointMapping.VOLTAGE;
        try {
            return extraPaoPointAssignmentDao.getLitePoint(regulatorPao, regulatorVoltageMapping);
        } catch (NotFoundException e) {
            log.debug("Regulator with id(" + regulatorId + ") does not have VOLTAGE regulator point mapping.");
            return null;
        }
    }

    private Set<Integer> getAllPointsInZoneGraph(int zoneId) {
        Set<Integer> allGraphPoints = Sets.newHashSet();
        //Regulator point
        Zone zone = zoneService.getZoneById(zoneId);
        for (RegulatorToZoneMapping regulatorToZone : zone.getRegulators()) {
            int regulatorId = regulatorToZone.getRegulatorId();
            LitePoint regulatorPoint = getRegulatorVoltagePoint(regulatorId);
            if (regulatorPoint == null) {
                return null;
            }
            allGraphPoints.add(regulatorPoint.getPointID());
        }

        // Cap bank points
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zoneId);
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            List<Integer> bankPoints = zoneService.getMonitorPointsForBank(bankToZone.getDeviceId());
            allGraphPoints.addAll(bankPoints);
        }

        // Additional voltage points
        List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zoneId);
        for (PointToZoneMapping pointToZone : pointsToZone) {
            allGraphPoints.add(pointToZone.getPointId());
        }
        return allGraphPoints;
    }
    
    @Override
    public boolean allZonesHaveRequiredRegulatorPointMapping(int subBusId, LiteYukonUser user) {
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        for (Zone zone : zones) {
            boolean hasRequiredAttribute = zoneHasRequiredRegulatorPointMapping(zone.getId(), 
                                                                    RegulatorPointMapping.VOLTAGE,
                                                                    user);
            if (!hasRequiredAttribute) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean zoneHasRequiredRegulatorPointMapping(int zoneId, RegulatorPointMapping regulatorPointMapping, LiteYukonUser user) {
        AbstractZone abstractZone = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);
        for (RegulatorToZoneMapping regulatorToZone : abstractZone.getRegulatorsList()) {
            YukonPao yukonPao = paoDao.getYukonPao(regulatorToZone.getRegulatorId());
            try {
                extraPaoPointAssignmentDao.getPointId(yukonPao, regulatorPointMapping);
            } catch (NotFoundException e) {
                log.debug("Zone with id(" + zoneId + ") does not have required regulator point mapping: " + regulatorPointMapping);
                return false;
            }
        }
        return true;
    }

    @Override
    public long getLargestPointTimeForZoneGraph(int zoneId) {
        Set<Integer> allGraphPoints = getAllPointsInZoneGraph(zoneId);
        if (allGraphPoints == null) {
            return 0;
        }
        long time = getLargestPointTime(allGraphPoints);
        return time;
    }
    
    private List<VfLine> buildLineDataForFeeders(VfGraphSettings settings, YukonUserContext userContext, CapControlCache cache, 
                                                List<Zone> zones, List<Feeder> feeders) {
        List<VfLine> lines = Lists.newArrayList();
        List<VfPoint> ignoredPoints = Lists.newArrayList();
        List<VfPoint> noFeederPoints = Lists.newArrayList();
        
        for (Feeder feeder : feeders) {
            List<VfPoint> points = Lists.newArrayList();
            for (Zone zone : zones) {
                List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
                List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
                List<VoltageLimitedDeviceInfo> infos = ccMonitorBankListDao.getDeviceInfoByZoneId(zone.getId());
                double graphStartPosition = getGraphStartPositionForZone(zone);

                for (RegulatorToZoneMapping regulatorToZone: zone.getRegulators()) {
                    VfPoint regulatorGraphPoint = getRegulatorVfPoint(settings, userContext, regulatorToZone, 
                                                                      zone.getName(), graphStartPosition);
                    checkForStrategyOverride(regulatorGraphPoint, infos, userContext);
                    regulatorGraphPoint.setFeederId(feeder.getCcId());
                    //Add the regulator (three for a threePhaseZone)
                    //if the zone is the parent zone add the regulator points to all feeders or if the regulator point is assigned to the current feeder add the point
                    if (zone.getParentId() == null || regulatorToZone.getFeederId() == feeder.getCcId()) {
                        points.add(regulatorGraphPoint);
                    } else {
                        if (regulatorToZone.getFeederId() == null) {
                            if (!noFeederPoints.contains(regulatorGraphPoint)) {
                                noFeederPoints.add(regulatorGraphPoint);
                            }
                        }
                    }
                }

                //Add the cap banks
                for (CapBankToZoneMapping bankToZone : banksToZone) {
                    //check if capbank is associated with feeder
                    CapBankDevice capBank = cache.getCapBankDevice(bankToZone.getDeviceId());
                    if (capBank.getParentID() == feeder.getCcId()) {
                        Map<Integer, Phase> bankPoints = zoneService.getMonitorPointsForBankAndPhase(bankToZone.getDeviceId());
                        for (Entry<Integer, Phase> entrySet : bankPoints.entrySet()) {
                            VfPoint graphPoint = getCapBankToZoneVfPoint(settings, userContext, cache, bankToZone, 
                                                                         entrySet.getKey(), entrySet.getValue(), 
                                                                         zone.getName(), graphStartPosition);
                            checkForStrategyOverride(graphPoint, infos, userContext);
                            graphPoint.setFeederId(feeder.getCcId());
                            points.add(graphPoint);
                        }
                    }
                }

                //Add the additional points
                for (PointToZoneMapping pointToZone : pointsToZone) {
                    VfPoint graphPoint = getPointToZoneVfPoint(settings, userContext, pointToZone, zone.getName(), 
                                                               graphStartPosition);
                    checkForStrategyOverride(graphPoint, infos, userContext);
                    if (pointToZone.getFeederId() == null) {
                        if (!noFeederPoints.contains(graphPoint)) {
                            noFeederPoints.add(graphPoint);
                        }
                    } else {
                        if (pointToZone.getFeederId() == feeder.getCcId()) {
                            graphPoint.setFeederId(feeder.getCcId());
                            points.add(graphPoint);
                        }
                    }
                }
            }
            

            Map<Phase, List<VfPoint>> phasePointsMap = Maps.newHashMap();
            for (Phase phase : Phase.values()) {
                phasePointsMap.put(phase, new ArrayList<VfPoint>());
            }
            
            for (VfPoint vfPoint: points) {
                if (vfPoint.isIgnore()){
                    if (!ignoredPoints.contains(vfPoint)) {
                        ignoredPoints.add(vfPoint);
                    }
                } else {
                    Phase pointPhase = vfPoint.getPhase();
                    if(pointPhase == Phase.ALL) {
                        // Point isn't on a phase? Add it to all phases
                        for (Phase enumPhase : Phase.getRealPhases()) {
                            phasePointsMap.get(enumPhase).add(vfPoint);
                        }
                    } else {
                        phasePointsMap.get(pointPhase).add(vfPoint);
                    }
                }
            }

            boolean haveShownLine = false;
            for (Phase phase: Phase.getRealPhases()) {
                List<VfPoint> phasePointList = phasePointsMap.get(phase);
                if (!phasePointList.isEmpty() && pointsContainPhase(phasePointList, phase)) {
                    haveShownLine = true;
                    Collections.sort(phasePointList, positionOrderer);
                    VfLineSettings lineSettings = getLineSettingsForPhase(settings, phase);
                    String phaseString = settings.getPhaseString(phase);
                    VfLine phaseLine = new VfLine(graphId.getAndIncrement(), phaseString, feeder.getCcName(), 
                                                   phase, lineSettings, phasePointList);
                    lines.add(phaseLine);
                }
            }
            
            List<VfPoint> phaseAPoints = phasePointsMap.get(Phase.A);
            if (haveShownLine == false && !phaseAPoints.isEmpty()) {
                //Using phase A points here since all three phases contain our "no phase" points
                Collections.sort(phaseAPoints, positionOrderer);
                VfLineSettings noPhaseLineSettings = getNoPhaseLineSetting(settings);
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String allPhases = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneDetail.phase.allPhases");
                VfLine noPhaseLine = new VfLine(graphId.getAndIncrement(), allPhases, feeder.getCcName(), Phase.ALL, 
                                                noPhaseLineSettings, phaseAPoints);
                lines.add(noPhaseLine);
            }
        }

        
        //Add Ignored Points separately so we don't get a line
        for (VfPoint ignoredPoint : ignoredPoints) {
            VfLineSettings ignoredPointLineSettings = getIgnoredPointsLineSetting(settings);
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String ignoredLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneDetail.ignoredPoints");
            VfLine noPhaseLine = new VfLine(graphId.getAndIncrement(), ignoredLabel, "", null, 
                                            ignoredPointLineSettings, Arrays.asList(ignoredPoint));
            lines.add(noPhaseLine);
        }
        
        //Add points not associated with a feeder separately so we don't get a line
        for (VfPoint noFeederPoint : noFeederPoints) {
            VfLineSettings noFeederPointLineSettings = getNoFeederPointsLineSetting(settings);
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String noFeederLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.zoneDetail.noFeeder");
            VfLine noPhaseLine = new VfLine(graphId.getAndIncrement(), noFeederLabel, "", null, 
                                            noFeederPointLineSettings, Arrays.asList(noFeederPoint));
            lines.add(noPhaseLine);
        }
        
        return lines;
    }
    
    private void checkForStrategyOverride(VfPoint vfPoint, List<VoltageLimitedDeviceInfo> infos, YukonUserContext userContext) {

        Optional<VoltageLimitedDeviceInfo> voltageInfo = infos.stream()
                .filter(info -> info.getPointId() == vfPoint.getPointId()).findFirst();
        if (voltageInfo.isPresent()) {
            VoltageLimitedDeviceInfo info = voltageInfo.get();
            if (info.isOverrideStrategy()) {
                vfPoint.setOverrideStrategy(info.isOverrideStrategy());
                vfPoint.setLowerLimit(info.getLowerLimit());
                vfPoint.setUpperLimit(info.getUpperLimit());
                //add tooltip text
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String overrideStategyText = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.strategyOverrideText", info.getLowerLimit(), info.getUpperLimit());
                String description = vfPoint.getDescription() + overrideStategyText;
                vfPoint.setDescription(description);
            }
        }
    }
	
	private boolean pointsContainPhase(List<VfPoint> points, Phase phase) {
	    for (VfPoint point : points) {
	        if (point.getPhase() == phase) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private VfPoint getRegulatorVfPoint(VfGraphSettings settings, YukonUserContext userContext, 
	                                    RegulatorToZoneMapping regulatorToZone, String zoneName, 
	                                    double graphStartPosition) {
	    int regulatorId = regulatorToZone.getRegulatorId();
	    Phase phase = regulatorToZone.getPhase();
	    VfPoint graphPoint = getVfPoint(settings, userContext, regulatorId, 0, 0, phase, null, 
	                                    zoneName, graphStartPosition, true, false);
        return graphPoint;
	}
	
	private VfPoint getCapBankToZoneVfPoint(VfGraphSettings settings, YukonUserContext userContext, 
	                                        CapControlCache cache, CapBankToZoneMapping bankToZone, 
	                                        Integer pointId, Phase phase, String zoneName, 
	                                        double graphStartPosition) {
	    CapBankDevice bank = cache.getCapBankDevice(bankToZone.getDeviceId());
	    int paoId = bank.getControlDeviceID();
	    double distance = bankToZone.getDistance();
        double xPosition = graphStartPosition + bankToZone.getGraphPositionOffset();
        //Display point as ignored if cap bank is disabled
        VfPoint graphPoint = getVfPoint(settings, userContext, paoId, pointId, distance, phase, null,
                                        zoneName, xPosition, false, bank.getCcDisableFlag());
	    return graphPoint;
	}

	private VfPoint getPointToZoneVfPoint(VfGraphSettings settings, YukonUserContext userContext, 
	                                      PointToZoneMapping pointToZone, String zoneName, 
	                                      double graphStartPosition) {
		int pointId = pointToZone.getPointId();
		LitePoint litePoint = pointDao.getLitePoint(pointId);
		String pointName = litePoint.getPointName();
		int paoId = litePoint.getPaobjectID();
		Phase phase = pointToZone.getPhase();
		double distance = pointToZone.getDistance();
		double xPosition = graphStartPosition + pointToZone.getGraphPositionOffset();
		boolean ignore = pointToZone.isIgnore();
		VfPoint graphPoint = getVfPoint(settings, userContext, paoId, pointId, distance, phase, 
		                                pointName, zoneName, xPosition, false, ignore);
		return graphPoint;
	}

    private VfPoint getVfPoint(VfGraphSettings settings, YukonUserContext userContext,
                               int paoId, int pointId, double distance, Phase phase, String pointName, 
                               String zoneName, double xPosition, boolean isRegulator, boolean ignore) {
        YukonPao regulatorPao = paoDao.getYukonPao(paoId);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(regulatorPao);
        PointValueQualityHolder pointValue;

        if (isRegulator) {
            LitePoint regulatorPoint = getRegulatorVoltagePoint(paoId);
            if (regulatorPoint == null) {
                throw new IllegalUseOfAttribute("Voltage point not found on regulator: " + paoId);
            }
            pointValue = asyncDynamicDataSource.getPointValue(regulatorPoint.getLiteID());
            pointId = regulatorPoint.getLiteID();
        } else {
            pointValue = asyncDynamicDataSource.getPointValue(pointId);
        }
        //Display Bad Data Quality points as Ignored
        PointQuality quality = pointValue.getPointQuality();
        if (!quality.equals(PointQuality.Manual) && !quality.equals(PointQuality.Normal)) {
            ignore = true;
        }
                
        String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
        String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), 
                                                        DateFormatEnum.BOTH, userContext);
        String nameString = displayablePao.getName();
        String distanceString = (distance != 0) ? String.valueOf(distance) : null;
        String phaseString = settings.getPhaseString(phase);
        String description = getBalloonText(settings, userContext, pointValueString, phaseString, 
                                            pointName, nameString, timestamp, zoneName, distanceString, BooleanUtils.toStringTrueFalse(ignore));
        VfPoint graphPoint = new VfPoint(pointId, description, zoneName, phase, isRegulator, xPosition, 
                                         pointValue.getValue(), ignore);
        return graphPoint;
    }

    private String getBalloonText(VfGraphSettings settings, YukonUserContext userContext, String value, 
                                  String phase, String pointName, String paoName, String timeStamp, 
                                  String zone, String distance, String ignore) {
        value = StringUtils.defaultIfEmpty(value, "");
        phase = StringUtils.defaultIfEmpty(phase, "");
        pointName = StringUtils.defaultIfEmpty(pointName, "");
        paoName = StringUtils.defaultIfEmpty(paoName, "");
        timeStamp = StringUtils.defaultIfEmpty(timeStamp, "");
        zone = StringUtils.defaultIfEmpty(zone, "");
        distance = StringUtils.defaultIfEmpty(distance, "");
        ignore = StringUtils.defaultIfEmpty(ignore,  "");

        String escapedZone = StringEscapeUtils.escapeXml11(zone);
        String escapedPaoName = StringEscapeUtils.escapeXml11(paoName);
        String escapedPointName = StringEscapeUtils.escapeXml11(pointName);

        if (!distance.isEmpty()) {
            String balloonDistanceText = settings.getBalloonDistanceText();
            distance = balloonDistanceText + distance;
        }

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String balloonText = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText",
                value, phase, escapedPointName, escapedPaoName, timeStamp, escapedZone, distance, ignore);
        return balloonText;
    }
    
    private VfLineSettings getLineSettingsForPhase(VfGraphSettings settings, Phase phase) {
        String phaseZoneLineColor = settings.getPhaseZoneLineColor(phase);
        VfLineSettings lineSettings = new VfLineSettings(phaseZoneLineColor, 
                                                        true, true, true, true, true);
        return lineSettings;
    }
    
    private VfLineSettings getNoPhaseLineSetting(VfGraphSettings settings) {
        String zoneLineColorNoPhase = settings.getZoneLineColorNoPhase();
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorNoPhase,
                                                        true, true, true, false, true);
        return lineSetting;
    }
    
    private VfLineSettings getIgnoredPointsLineSetting(VfGraphSettings settings) {
        String zonePointColorIgnoredPoints = settings.getZonePointColorIgnoredPoints();
        VfLineSettings lineSetting = new VfLineSettings(zonePointColorIgnoredPoints,
                                                        true, true, true, false, true);
        return lineSetting;
    }
    
    private VfLineSettings getNoFeederPointsLineSetting(VfGraphSettings settings) {
        String zonePointColorNoFeederPoints = settings.getZonePointColorNoFeederPoints();
        VfLineSettings lineSetting = new VfLineSettings(zonePointColorNoFeederPoints,
                                                        true, true, true, false, true);
        return lineSetting;
    }
    
    public double getUpperVoltLimitForSubBus(CapControlCache cache, int subBusId) {
        CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        double yUpper = TargetSettingType.UPPER_VOLT_LIMIT.getDefaultValue();
        
        PeakTargetSetting upperLimit = strategy.getTargetSettings().get(TargetSettingType.UPPER_VOLT_LIMIT);
        if (upperLimit != null) {
            yUpper = upperLimit.getPeakValue();
        }
        
        return yUpper;
    }
    
    public double getLowerVoltLimitForSubBus(CapControlCache cache, int subBusId) {
        CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        double yLower = TargetSettingType.LOWER_VOLT_LIMIT.getDefaultValue();
        
        PeakTargetSetting lowerLimit = strategy.getTargetSettings().get(TargetSettingType.LOWER_VOLT_LIMIT);
        if (lowerLimit != null) {
            yLower = lowerLimit.getPeakValue();
        }
        
        return yLower;
    }

    private void transformToLineGraph(VfGraph graph) {
        List<VfLine> lines = graph.getLines();
        if (lines.isEmpty()) {
            return;
        }

        //sort the points based on x value
        for (VfLine line : lines) {
            List<VfPoint> points = line.getPoints();
            Collections.sort(points, positionOrderer);
        }

        List<Double> xValues = Lists.newArrayList();

        for (VfLine line : lines) {
            List<VfPoint> points = line.getPoints();
            for (VfPoint point : points) {
                double xValue = point.getX();
                if (!xValues.contains(xValue)) {
                    xValues.add(xValue);
                }
            }
        }
        
        double min = Collections.min(xValues);
        double max = Collections.max(xValues);
        BigDecimal bucketSize = getGraphBucketSize(max);
        double bucketEndValue = min + max;
        List<Double> bucketXValues = getBucketValues(bucketSize, bucketEndValue);
        graph.setSeriesValues(bucketXValues);

        List<VfPoint> seriesPoints = getBaseSeriesPoints(bucketXValues);
        for (VfLine line : lines) {
            for (VfPoint point : line.getPoints()) {
                Integer seriesId = getSeriesIdForPoint(seriesPoints, point, bucketSize.doubleValue());
                point.setSeriesId(seriesId);
            }
        }
    }

    public static List<Double> getBucketValues(BigDecimal bucketSize, double bucketEndValue) {
        List<Double> bucketValues = Lists.newArrayList();
        double bucketSizeAsDouble = bucketSize.doubleValue();
        if (bucketSizeAsDouble > 0) {
            Integer iterationNum = 0;
            Double currentVal = 0.0;
            int precision = bucketSize.scale();
            DecimalFormat f = new DecimalFormat();
            f.setMaximumFractionDigits(precision);
            f.setGroupingUsed(false);

            //this "while" conditional statement below makes the graph have the same amount of
            //leading space between the last point and the right y-axis as exists between the left y-axis and the first point
            while(currentVal <= bucketEndValue) {
                bucketValues.add(currentVal);
                iterationNum++;
                currentVal = Double.valueOf(f.format(iterationNum * bucketSizeAsDouble));
            }
        } else {
            bucketValues.add(0.0);
        }
        return bucketValues;
    }
    
    private Integer getSeriesIdForPoint(List<VfPoint> seriesPoints, VfPoint pointToPlace, double bucketSize) {
        double pointX = pointToPlace.getX();
        for (VfPoint seriesPoint : seriesPoints) {
            double seriesX = seriesPoint.getX();
            if (seriesX == pointX && bucketSize == 0 && pointX == 0) {
                return seriesPoint.getSeriesId();
            }
            if (seriesX <= pointX && pointX < (seriesX + bucketSize)) {
                return seriesPoint.getSeriesId();
            }
        }
        return null;
    }
    
    private List<VfPoint> getBaseSeriesPoints(List<Double> seriesValues) {
        List<VfPoint> basePoints = Lists.newArrayListWithCapacity(seriesValues.size());
        for (int i = 0; i < seriesValues.size(); i++) {
            Double value = seriesValues.get(i);
            VfPoint point = new VfPoint(value, null, i);
            basePoints.add(point);
        }
        return basePoints;
    }

    private BigDecimal getGraphBucketSize(double max) {
        Double iterationSize = max / bucketResolution;
        BigDecimal iterationSizeRoundedUp = GraphIntervalRounding.roundUp(iterationSize, 5);
        return iterationSizeRoundedUp;
    }
}
