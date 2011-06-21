package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.GraphIntervalRounding;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
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
import com.cannontech.enums.Phase;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.ZoneDtoHelper;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfLineSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.SubBus;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class VoltageFlatnessGraphServiceImpl implements VoltageFlatnessGraphService {
    
    private AttributeService attributeService;
    private PaoDao paoDao;
    private DynamicDataSource dynamicDataSource;
    private StrategyDao strategyDao;
    private PointDao pointDao;
    private DateFormattingService dateFormattingService;
    private ZoneService zoneService;
    private FilterCacheFactory filterCacheFactory;
    private PointFormattingService pointFormattingService;
    private PaoLoadingService paoLoadingService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static final Logger log = YukonLogManager.getLogger(VoltageFlatnessGraphService.class);
    private ConfigurationSource configurationSource;
    private ZoneDtoHelper zoneDtoHelper;
    
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
        List<VfLine> lines = Lists.newArrayList();
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        VfGraph graph = new VfGraph();
        VfGraphSettings settings = buildSubBusGraphSettings(subBusId, cache, userContext);
        graph.setSettings(settings);
        
        //Build A line for each zone on the bus.
        for (Zone zone : zones) {
            List<VfLine> zoneLines = buildLineDataForZone(graph.getSettings(), userContext, cache, 
                                                          zone);
            lines.addAll(zoneLines);
        }
        
        graph.setLines(lines);
        setSubBusGraphLineLegendVisibility(graph);
        
        if (settings.isShowZoneTransitionTextBusGraph()) {
            addZoneTransitionText(graph);
        }
        
        transformToLineGraph(graph);
        return graph;
    }
    
    private void addZoneTransitionText(VfGraph graph) {
        Map<Double, VfPoint> zoneTransitionPoints = Maps.newHashMap(); //so we don't add multiple transition points for a "no phase" point (which actually has all 3 phase points)
        for (VfLine line : graph.getLines()) {
            for (VfPoint point : line.getPoints()) {
                if (point.isRegulator()) {
                    if (zoneTransitionPoints.containsKey(point.getX())) {
                        VfPoint transitionPoint = zoneTransitionPoints.get(point.getX());
                        if (transitionPoint.getY() < point.getY()) {
                            zoneTransitionPoints.put(point.getX(), point);
                        }
                    } else {
                        zoneTransitionPoints.put(point.getX(), point);
                    }
                }
            }
        }

        List<VfLine> linesWithZoneTransitions = Lists.newArrayList(graph.getLines());
        for (Entry<Double, VfPoint> entry : zoneTransitionPoints.entrySet()) {
            VfPoint point = entry.getValue();
            VfPoint transitionPoint = new VfPoint(point.getZoneName(), null, null, false, point.getX(), point.getY());
            List<VfPoint> points = Lists.newArrayListWithCapacity(1);
            points.add(transitionPoint);
            VfLineSettings lineSettings = getZoneTransitionLineSetting(graph.getSettings());
            VfLine zoneLine = new VfLine(graphId.getAndIncrement(), null, point.getZoneName(), null, lineSettings, points);
            linesWithZoneTransitions.add(zoneLine);
        }
        graph.setLines(linesWithZoneTransitions);
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
        List<VfLine> lines = Lists.newArrayList();
        Zone zone = zoneService.getZoneById(zoneId);
        VfGraph graph = new VfGraph();
        VfGraphSettings settings = buildZoneGraphSettings(zoneId, cache, userContext);
        graph.setSettings(settings);
        
        List<VfLine> zoneLines = buildLineDataForZone(graph.getSettings(), userContext, cache, zone);
        lines.addAll(zoneLines);
        graph.setLines(lines);

        if (settings.isShowZoneTransitionTextZoneGraph()) {
            addZoneTransitionText(graph);
        }

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
        Map<Phase, String> bulletTypePhaseMap = Maps.newHashMapWithExpectedSize(3);
        for (Phase phase : Phase.getRealPhases()) {
            String phaseString = messageSourceAccessor.getMessage("yukon.common.phase");
            phaseString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phase" + phase);
            phaseStringMap.put(phase, phaseString);
            String zoneLineColorPhase = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhase" + phase);
            zoneLineColorPhaseMap.put(phase, zoneLineColorPhase);
            String phaseBulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseBulletType" + phase);
            bulletTypePhaseMap.put(phase, phaseBulletType);
        }

        String zoneTransitionDataLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneTransitionDataLabel");
        String zoneLineColorNoPhase = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorNoPhase");
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
                                bulletTypePhaseMap,
                                showZoneTransitionTextBusGraph,
                                showZoneTransitionTextZoneGraph,
                                zoneTransitionDataLabel,
                                balloonDistanceText);
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
        Set<? extends PointValueQualityHolder> pointValues = dynamicDataSource.getPointValue(allGraphPoints);

        for (PointValueQualityHolder point : pointValues) {
            long pointTime = point.getPointDataTimeStamp().getTime();
            if (largestTime < pointTime) {
                largestTime = pointTime;
            }
        }
        return largestTime;
    }
    
    private LitePoint getRegulatorVoltagePoint(int regulatorId) {
        YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
        BuiltInAttribute regulatorVoltageAttribute = BuiltInAttribute.VOLTAGE_Y;
        boolean pointExists = attributeService.pointExistsForAttribute(regulatorPao, regulatorVoltageAttribute);
        if (!pointExists) {
            return null;
        }
        LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, regulatorVoltageAttribute);
        return regulatorPoint;
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
    public boolean allZonesHaveRequiredAttributes(int subBusId, LiteYukonUser user) {
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        for (Zone zone : zones) {
            boolean hasRequiredAttribute = zoneHasRequiredAttribute(zone.getId(), 
                                                                    BuiltInAttribute.VOLTAGE_Y,
                                                                    user);
            if (!hasRequiredAttribute) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean zoneHasRequiredAttribute(int zoneId, BuiltInAttribute attribute, LiteYukonUser user) {
        AbstractZone abstractZone = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);
        for (RegulatorToZoneMapping regulatorToZone : abstractZone.getRegulatorsList()) {
            YukonPao yukonPao = paoDao.getYukonPao(regulatorToZone.getRegulatorId());
            boolean hasAttribute = attributeService.pointExistsForAttribute(yukonPao, attribute);
            if (!hasAttribute) {
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

	private List<VfLine> buildLineDataForZone(VfGraphSettings settings, YukonUserContext userContext, 
	                                          CapControlCache cache, Zone zone) {
        List<VfLine> lines = Lists.newArrayList();
        List<VfPoint> points = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
        List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
        double graphStartPosition = getGraphStartPositionForZone(zone);

        for (RegulatorToZoneMapping regulatorToZone: zone.getRegulators()) {
            //Add the regulator (three for a threePhaseZone)
            VfPoint regulatorGraphPoint = getRegulatorVfPoint(settings, userContext, regulatorToZone, 
                                                              zone.getName(), graphStartPosition);
            points.add(regulatorGraphPoint);
        }

        //Add the cap banks
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            Map<Integer, Phase> bankPoints = zoneService.getMonitorPointsForBankAndPhase(bankToZone.getDeviceId());
            for (Entry<Integer, Phase> entrySet : bankPoints.entrySet()) {
        		VfPoint graphPoint = getCapBankToZoneVfPoint(settings, userContext, cache, bankToZone, 
        		                                             entrySet.getKey(), entrySet.getValue(), 
        		                                             zone.getName(), graphStartPosition);
                points.add(graphPoint);
            }
        }

        //Add the additional points
        for (PointToZoneMapping pointToZone : pointsToZone) {
            VfPoint graphPoint = getPointToZoneVfPoint(settings, userContext, pointToZone, zone.getName(), 
                                                       graphStartPosition);
            points.add(graphPoint);
        }
        
        Map<Phase, List<VfPoint>> phasePointsMap = Maps.newHashMap();
        for (Phase phase : Phase.getRealPhases()) {
            phasePointsMap.put(phase, new ArrayList<VfPoint>());
        }
        
        for (VfPoint vfPoint: points) {
            Phase pointPhase = vfPoint.getPhase();
            if(pointPhase == null) {
                // Point isn't on a phase? Add it to all phases
                for (Phase enumPhase : Phase.getRealPhases()) {
                    phasePointsMap.get(enumPhase).add(vfPoint);
                }
            } else {
                phasePointsMap.get(pointPhase).add(vfPoint);
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
                VfLine phaseLine = new VfLine(graphId.getAndIncrement(), phaseString, zone.getName(), 
                                               phase, lineSettings, phasePointList);
                lines.add(phaseLine);
            }
        }
        
        List<VfPoint> phaseAPoints = phasePointsMap.get(Phase.A);
        if (haveShownLine == false && !phaseAPoints.isEmpty()) {
            //Using phase A points here since all three phases contain our "no phase" points
            Collections.sort(phaseAPoints, positionOrderer);
            VfLineSettings noPhaseLineSettings = getNoPhaseLineSetting(settings);
            VfLine noPhaseLine = new VfLine(graphId.getAndIncrement(), null, zone.getName(), null, 
                                            noPhaseLineSettings, phaseAPoints);
            lines.add(noPhaseLine);
        }
        
        return lines;
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
	                                    zoneName, graphStartPosition, true);
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
        VfPoint graphPoint = getVfPoint(settings, userContext, paoId, pointId, distance, phase, null,
                                        zoneName, xPosition, false);
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
		VfPoint graphPoint = getVfPoint(settings, userContext, paoId, pointId, distance, phase, 
		                                pointName, zoneName, xPosition, false);
		return graphPoint;
	}

    private VfPoint getVfPoint(VfGraphSettings settings, YukonUserContext userContext,
                               int paoId, int pointId, double distance, Phase phase, String pointName, 
                               String zoneName, double xPosition, boolean isRegulator) {
        YukonPao regulatorPao = paoDao.getYukonPao(paoId);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(regulatorPao);
        PointValueQualityHolder pointValue;
        if (isRegulator) {
            LitePoint regulatorPoint = getRegulatorVoltagePoint(paoId);
            if (regulatorPoint == null) {
                throw new IllegalUseOfAttribute("Voltage point not found on regulator: " + paoId);
            }
            pointValue = dynamicDataSource.getPointValue(regulatorPoint.getLiteID());
        } else {
            pointValue = dynamicDataSource.getPointValue(pointId);
        }
        String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
        String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), 
                                                        DateFormatEnum.BOTH, userContext);
        String nameString = displayablePao.getName();
        String distanceString = (distance != 0) ? String.valueOf(distance) : null;
        String phaseString = settings.getPhaseString(phase);
        String description = getBalloonText(settings, userContext, pointValueString, phaseString, 
                                            pointName, nameString, timestamp, zoneName, distanceString);
        VfPoint graphPoint = new VfPoint(description, zoneName, phase, isRegulator, xPosition, 
                                         pointValue.getValue());
        return graphPoint;
    }

    private String getBalloonText(VfGraphSettings settings, YukonUserContext userContext, String value, 
                                  String phase, String pointName, String paoName, String timeStamp, 
                                  String zone, String distance) {
        value = StringUtils.defaultIfEmpty(value, "");
        phase = StringUtils.defaultIfEmpty(phase, "");
        pointName = StringUtils.defaultIfEmpty(pointName, "");
        paoName = StringUtils.defaultIfEmpty(paoName, "");
        timeStamp = StringUtils.defaultIfEmpty(timeStamp, "");
        zone = StringUtils.defaultIfEmpty(zone, "");
        distance = StringUtils.defaultIfEmpty(distance, "");
        
        if (!distance.isEmpty()) {
            String balloonDistanceText = settings.getBalloonDistanceText();
            distance = balloonDistanceText + distance;
        }
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String balloonText = messageSourceAccessor.
            getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText",
                       value, phase, pointName, paoName, timeStamp, zone, distance);
        
        balloonText = cleanUpBalloonText(balloonText);
        return balloonText;
    }
    
    private String cleanUpBalloonText(String value) {
        value = StringEscapeUtils.unescapeJava(value);
        value = removeEmptyLines(value);
        value = StringUtils.trimToEmpty(value);
        value = StringUtils.replace(value, "\n", "<br>");
        value = StringEscapeUtils.escapeHtml(value);
        return value;
    }
    
    private String removeEmptyLines(String value) {
        if (StringUtils.contains(value, "\n\n")) {
            value = StringUtils.replace(value, "\n\n", "\n");
            value = removeEmptyLines(value);
        }
        return value;
    }
    
    private VfLineSettings getZoneTransitionLineSetting(VfGraphSettings graphSettings) {
        String zoneTransitionDataLabel = graphSettings.getZoneTransitionDataLabel();
        VfLineSettings lineSetting = new VfLineSettings(null, null, null, 
                                                        false, false, false, false, false, 
                                                        false, zoneTransitionDataLabel);
        return lineSetting;
    }
    
    private VfLineSettings getLineSettingsForPhase(VfGraphSettings settings, Phase phase) {
        String phaseZoneLineColor = settings.getPhaseZoneLineColor(phase);
        String phaseBulletType = settings.getPhaseBulletType(phase);
        VfLineSettings lineSettings = new VfLineSettings(phaseZoneLineColor, 
                                                        phaseBulletType, 
                                                        true, true, true, true, true);
        return lineSettings;
    }
    
    private VfLineSettings getNoPhaseLineSetting(VfGraphSettings settings) {
        String zoneLineColorNoPhase = settings.getZoneLineColorNoPhase();
        String phaseABulletType = settings.getPhaseBulletType(Phase.A); // Just use the same as phase A, since this will almost never happen
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorNoPhase, 
                                                        phaseABulletType, 
                                                        true, true, true, false, true);
        return lineSetting;
    }

	public double getUpperVoltLimitForSubBus(CapControlCache cache, int subBusId) {
    	CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        double yUpper = Double.parseDouble(TargetSettingType.UPPER_VOLT_LIMIT.getDefaultValue());
        for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
            if (setting.getType() == TargetSettingType.UPPER_VOLT_LIMIT) {
                yUpper = Double.parseDouble(setting.getPeakValue());
            }
        }
        return yUpper;
    }
    
    public double getLowerVoltLimitForSubBus(CapControlCache cache, int subBusId) {
    	CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        double yLower = Double.parseDouble(TargetSettingType.LOWER_VOLT_LIMIT.getDefaultValue());
        for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
            if (setting.getType() == TargetSettingType.LOWER_VOLT_LIMIT) {
                yLower = Double.parseDouble(setting.getPeakValue());
            }
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

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }

    @Autowired
    public void setZoneService(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @Autowired
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
    
    @Autowired
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
		this.pointFormattingService = pointFormattingService;
	}
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
		this.paoLoadingService = paoLoadingService;
	}
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setZoneDtoHelper(ZoneDtoHelper zoneDtoHelper) {
        this.zoneDtoHelper = zoneDtoHelper;
    }
}
