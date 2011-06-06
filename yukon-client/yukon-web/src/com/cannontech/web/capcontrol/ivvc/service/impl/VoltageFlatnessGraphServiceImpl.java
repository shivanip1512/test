package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneRegulator;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigIntegerKeysEnum;
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
    
    private double upperLimit;
    private double lowerLimit;
    
    private String graphLabel;
    private String yAxisLabel;
    private String xAxisLabel;
    
    private String phaseAString;
    private String phaseBString;
    private String phaseCString;

    private String zoneLineColorPhaseA;
    private String zoneLineColorPhaseB;
    private String zoneLineColorPhaseC;
    private String zoneLineColorNoPhase;
     
    private String phaseABulletType;
    private String phaseBBulletType;
    private String phaseCBulletType;
    
    private boolean showZoneTransitionText;
    private String zoneTransitionDataLabel;
    
    private String balloonDistanceText;
    
    private int bucketResolution;

    //Every Graph must have a unique id
    private AtomicInteger graphId = new AtomicInteger();
    
    private Ordering<VfPoint> positionOrderer = Ordering.natural().onResultOf(new Function<VfPoint, Double>() {
        @Override
        public Double apply(VfPoint from) {
            return from.getX();
        }
    });

    public void initialize(YukonUserContext userContext, int subBusId, CapControlCache cache) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        graphLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.graphLabel");
        yAxisLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.yAxisLabel");
        String xLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.xAxisLabel");
        if (xLabel != StringUtils.EMPTY) {
            xAxisLabel = xLabel;
        } else {
            xAxisLabel = null;
        }
        
        balloonDistanceText = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.distance");
        
        try {
            bucketResolution = configurationSource.getInteger(MasterConfigIntegerKeysEnum.CAP_CONTROL_IVVC_GRAPH_RESOLUTION.name(), 200);
        } catch (NumberFormatException e) {
            bucketResolution = 200;
            log.error("Error getting numeric value from master config value: " + MasterConfigIntegerKeysEnum.CAP_CONTROL_IVVC_GRAPH_RESOLUTION.name() + 
                      ". Using default of " + bucketResolution + ". ", e);
        }
        
        upperLimit = getUpperVoltLimitForSubBus(cache, subBusId);
        lowerLimit = getLowerVoltLimitForSubBus(cache, subBusId);

        phaseAString = messageSourceAccessor.getMessage("yukon.common.phase");
        phaseAString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phaseA");
        phaseBString = messageSourceAccessor.getMessage("yukon.common.phase");
        phaseBString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phaseB");
        phaseCString = messageSourceAccessor.getMessage("yukon.common.phase");
        phaseCString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phaseC");

        zoneTransitionDataLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneTransitionDataLabel");

        zoneLineColorPhaseA = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhaseA");
        zoneLineColorPhaseB = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhaseB");
        zoneLineColorPhaseC = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhaseC");
        zoneLineColorNoPhase = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorNoPhase");

        phaseABulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseABulletType");
        phaseBBulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseBBulletType");
        phaseCBulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseCBulletType");

        showZoneTransitionText = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.showZoneTransitionText"));
    }
    
    @Override
    public VfGraph getSubBusGraph(YukonUserContext userContext, int subBusId) throws IllegalUseOfAttribute {
        LiteYukonUser yukonUser = userContext.getYukonUser();
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
		initialize(userContext, subBusId, cache);
		
        VfGraph graph = buildSubBusGraph(userContext, subBusId,cache);
        return graph;
    }
    
    @Override
    public VfGraph getZoneGraph(YukonUserContext userContext, int zoneId) throws IllegalUseOfAttribute {
        LiteYukonUser yukonUser = userContext.getYukonUser();
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
		Zone zone = zoneService.getZoneById(zoneId);
		int subBusId = zone.getSubstationBusId();
		initialize(userContext, subBusId, cache);
        
		VfGraph graph = buildZoneGraph(userContext, zoneId,cache);
		return graph;
    }

    private VfGraph buildSubBusGraph(YukonUserContext userContext, int subBusId, CapControlCache cache) 
            throws IllegalUseOfAttribute {
        List<VfLine> lines = Lists.newArrayList();
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        
        //Build A line for each zone on the bus.
        for (Zone zone : zones) {
            List<VfLine> zoneLines = buildLineDataForZone(userContext, cache, zone);
            lines.addAll(zoneLines);
        }
        
        VfGraph graph = new VfGraph();
        graph.setLines(lines);
        setSubBusGraphLineLegendVisibility(graph);
        
        if (showZoneTransitionText) {
            addZoneTransitionText(graph);
        }
        
        transformToLineGraph(graph);
        
        VfGraphSettings settings = buildSubBusGraphSettings(subBusId, cache);
        graph.setSettings(settings);
        
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
            VfLineSettings lineSettings = getZoneTransitionLineSetting();
            VfLine zoneLine = new VfLine(graphId.getAndIncrement(), null, point.getZoneName(), null, lineSettings, points);
            linesWithZoneTransitions.add(zoneLine);
        }
        graph.setLines(linesWithZoneTransitions);
    }
    
    private void setSubBusGraphLineLegendVisibility(VfGraph graph) {
        List<VfLine> lines = graph.getLines();
        boolean phaseAShown = false;
        boolean phaseBShown = false;
        boolean phaseCShown = false;
        
        for (VfLine line : lines) {
            boolean isVisibleInLegend = line.getSettings().isVisibleInLegend();
            Phase phase = line.getPhase();
            if (phase == Phase.A && isVisibleInLegend){
                if (phaseAShown) {
                    line.getSettings().setVisibleInLegend(false);
                } else {
                    phaseAShown = true;
                }
            }
            else if (phase == Phase.B && isVisibleInLegend){
                if (phaseBShown) {
                    line.getSettings().setVisibleInLegend(false);
                } else {
                    phaseBShown = true;
                }
            }
            else if (phase == Phase.C && isVisibleInLegend){
                if (phaseCShown) {
                    line.getSettings().setVisibleInLegend(false);
                } else {
                    phaseCShown = true;
                }
            }
        }
        graph.setLines(lines);
    }
    
	private VfGraph buildZoneGraph(YukonUserContext userContext, int zoneId, CapControlCache cache) 
	        throws IllegalUseOfAttribute {
        List<VfLine> lines = Lists.newArrayList();
        Zone zone = zoneService.getZoneById(zoneId);
        
        List<VfLine> zoneLines = buildLineDataForZone(userContext, cache, zone);
        lines.addAll(zoneLines);
        
        //Make graph object and add the lines.
        VfGraph graph = new VfGraph();
        graph.setLines(lines);
        transformToLineGraph(graph);
        
        VfGraphSettings settings = buildZoneGraphSettings(zoneId, cache);
        graph.setSettings(settings);
        
        return graph;
    }
    
	private VfGraphSettings buildSubBusGraphSettings(int subBusId, CapControlCache cache) {
        VfGraphSettings graphSettings = new VfGraphSettings();
        
        SubBus subBus = cache.getSubBus(subBusId);
        String title = subBus.getCcName() + " " + graphLabel; 
        
        graphSettings.setYUpperBound(upperLimit);
        graphSettings.setYLowerBound(lowerLimit);
        graphSettings.setYAxisLabel(yAxisLabel);
        graphSettings.setXAxisLabel(xAxisLabel);
        graphSettings.setGraphTitle(title);
        
        return graphSettings;
    }
	
	private VfGraphSettings buildZoneGraphSettings(int zoneId, CapControlCache cache) {
        Zone zone = zoneService.getZoneById(zoneId);
        VfGraphSettings graphSettings = new VfGraphSettings();
        
        String title = zone.getName() + " " + graphLabel;
        
        graphSettings.setYUpperBound(upperLimit);
        graphSettings.setYLowerBound(lowerLimit);
        graphSettings.setYAxisLabel(yAxisLabel);
        graphSettings.setXAxisLabel(xAxisLabel);
        graphSettings.setGraphTitle(title);
        
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
            for (ZoneRegulator zoneRegulator: zone.getRegulators()) {
                //Regulator point
                int regulatorId = zoneRegulator.getRegulatorId();
                YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
                try {
                    LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE_Y);
                    allGraphPoints.add(regulatorPoint.getPointID());
                } catch(IllegalUseOfAttribute e) {
                    log.error("No voltage attribute exists on regulator with ID: " + regulatorId, e);
                    return null;
                }
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
         long time = 0;
         if (allGraphPoints != null) {
             time = getLargestPointTime(allGraphPoints);
         }
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
    
    private LitePoint getRegulatorVoltagePoint(int regulatorId) throws IllegalUseOfAttribute {
        try {
            YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
            LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE_Y);
            return regulatorPoint;
        } catch(Exception e) {
            log.error("No voltage attribute exists on regulator with ID: " + regulatorId, e);
            throw new IllegalUseOfAttribute(e.getMessage());
        }
    }

    private Set<Integer> getAllPointsInZoneGraph(int zoneId) {
        Set<Integer> allGraphPoints = Sets.newHashSet();

        //Regulator point
        Zone zone = zoneService.getZoneById(zoneId);
        for (ZoneRegulator zoneRegulator : zone.getRegulators()) {
            int regulatorId = zoneRegulator.getRegulatorId(); //FIX LATER
            try {
                LitePoint regulatorPoint = getRegulatorVoltagePoint(regulatorId);
                allGraphPoints.add(regulatorPoint.getPointID());
            } catch(IllegalUseOfAttribute e) {
                //silently ignoring for now
            }
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
    public long getLargestPointTimeForZoneGraph(int zoneId) {
        Set<Integer> allGraphPoints = getAllPointsInZoneGraph(zoneId);
        long time = getLargestPointTime(allGraphPoints);
        return time;
    }

	private List<VfLine> buildLineDataForZone(YukonUserContext userContext, CapControlCache cache, Zone zone)
	        throws IllegalUseOfAttribute {
        List<VfLine> lines = Lists.newArrayList();
        List<VfPoint> points = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
        List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
        double graphStartPosition = getGraphStartPositionForZone(zone);

        for (ZoneRegulator zoneRegulator: zone.getRegulators()) {
            //Add the regulator (three for a threePhaseZone)
            VfPoint regulatorGraphPoint = getRegulatorVfPoint(userContext, zoneRegulator, 
                                                              zone.getName(), graphStartPosition);
            points.add(regulatorGraphPoint);
        }

        //Add the cap banks
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            Map<Integer, Phase> bankPoints = zoneService.getMonitorPointsForBankAndPhase(bankToZone.getDeviceId());
            for (Entry<Integer, Phase> entrySet : bankPoints.entrySet()) {
        		VfPoint graphPoint = getCapBankToZoneVfPoint(userContext, cache, bankToZone, 
        		                                             entrySet.getKey(), entrySet.getValue(), 
        		                                             zone, graphStartPosition);
                points.add(graphPoint);
            }
        }

        //Add the additional points
        for (PointToZoneMapping pointToZone : pointsToZone) {
            VfPoint graphPoint = getPointToZoneVfPoint(userContext, pointToZone, zone, graphStartPosition);
            points.add(graphPoint);
        }
        
        List<VfPoint> phaseAPoints = Lists.newArrayList();
        List<VfPoint> phaseBPoints = Lists.newArrayList();
        List<VfPoint> phaseCPoints = Lists.newArrayList();
        
        for (VfPoint vfPoint: points) {
            if (vfPoint.getPhase() == Phase.A) {
                phaseAPoints.add(vfPoint);
            } else if (vfPoint.getPhase() == Phase.B) {
                phaseBPoints.add(vfPoint);
            } else if (vfPoint.getPhase() == Phase.C) {
                phaseCPoints.add(vfPoint);
            } else {
                phaseAPoints.add(vfPoint);
                phaseBPoints.add(vfPoint);
                phaseCPoints.add(vfPoint);
            }
        }

        boolean haveShownLine = false;
        if (!phaseAPoints.isEmpty() && pointsContainPhase(phaseAPoints, Phase.A)) {
            haveShownLine = true;
            Collections.sort(phaseAPoints, positionOrderer);
            VfLineSettings phaseALineSettings = getPhaseALineSetting();
            VfLine phaseALine = new VfLine(graphId.getAndIncrement(), phaseAString, zone.getName(), Phase.A, phaseALineSettings, phaseAPoints);
            lines.add(phaseALine);
        }

        if (!phaseBPoints.isEmpty() && pointsContainPhase(phaseBPoints, Phase.B)) {
            haveShownLine = true;
            Collections.sort(phaseBPoints, positionOrderer);
            VfLineSettings phaseBLineSettings = getPhaseBLineSetting();
            VfLine phaseBLine = new VfLine(graphId.getAndIncrement(), phaseBString, zone.getName(), Phase.B, phaseBLineSettings, phaseBPoints);
            lines.add(phaseBLine);
        }

        if (!phaseCPoints.isEmpty() && pointsContainPhase(phaseCPoints, Phase.C)) {
            haveShownLine = true;
            Collections.sort(phaseCPoints, positionOrderer);
            VfLineSettings phaseCLineSettings = getPhaseCLineSetting();
            VfLine phaseCLine = new VfLine(graphId.getAndIncrement(), phaseCString, zone.getName(), Phase.C, phaseCLineSettings, phaseCPoints);
            lines.add(phaseCLine);
        }
        
        if (!phaseAPoints.isEmpty() && haveShownLine == false) {
            //Using phase A points here since all three phases contain our "no phase" points
            Collections.sort(phaseAPoints, positionOrderer);
            VfLineSettings noPhaseLineSettings = getNoPhaseLineSetting();
            VfLine noPhaseLine = new VfLine(graphId.getAndIncrement(), null, zone.getName(), null, noPhaseLineSettings, phaseAPoints);
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
	
	private VfPoint getRegulatorVfPoint(YukonUserContext userContext, ZoneRegulator zoneRegulator, String zoneName, double graphStartPosition) 
	        throws IllegalUseOfAttribute {
	    int regulatorId = zoneRegulator.getRegulatorId();
	    Phase phase = zoneRegulator.getPhase();
        YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(regulatorPao);
        LitePoint regulatorPoint = getRegulatorVoltagePoint(regulatorId);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(regulatorPoint.getLiteID());
        String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
        String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
        String nameString = displayablePao.getName();
        String phaseString = getPhaseString(phase);
        String description = getBalloonText(userContext, pointValueString, phaseString, null, 
                                            nameString, timestamp, zoneName, null);
        VfPoint regulatorGraphPoint = new VfPoint(description, zoneName, phase, true, 
                                                  graphStartPosition, pointValue.getValue());
        return regulatorGraphPoint;
	}
	
	private VfPoint getCapBankToZoneVfPoint(YukonUserContext userContext, CapControlCache cache, 
	                                        CapBankToZoneMapping bankToZone, Integer pointId, 
	                                        Phase phase, Zone zone, double graphStartPosition) {
	    CapBankDevice bank = cache.getCapBankDevice(bankToZone.getDeviceId());
	    YukonPao yukonPao = paoDao.getYukonPao(bank.getControlDeviceID());
	    DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
	    PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
	    String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
	    String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
	    double distance = bankToZone.getDistance();
	    String distanceString = (distance != 0) ? String.valueOf(distance) : null;
        String phaseString = getPhaseString(phase);
        String balloonText = getBalloonText(userContext, pointValueString, phaseString, null, 
                                            displayablePao.getName(), timestamp, zone.getName(), 
                                            distanceString);
	    double xPosition = graphStartPosition + bankToZone.getGraphPositionOffset();
	    VfPoint graphPoint = new VfPoint(balloonText, zone.getName(), phase, false, xPosition, 
	                                     pointValue.getValue());
	    return graphPoint;
	}

	private VfPoint getPointToZoneVfPoint(YukonUserContext userContext, PointToZoneMapping pointToZone, Zone zone, double graphStartPosition) {
		int pointId = pointToZone.getPointId();
		LitePoint litePoint = pointDao.getLitePoint(pointId);
		YukonPao yukonPao = paoDao.getYukonPao(litePoint.getPaobjectID());
		DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
		String litePointString = litePoint.getPointName();
		Phase phase = pointToZone.getPhase();
        String phaseString = getPhaseString(phase);
		String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
		String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
        double distance = pointToZone.getDistance();
        String distanceString = (distance != 0) ? String.valueOf(distance) : null;
        String balloonText = getBalloonText(userContext, pointValueString, phaseString, litePointString, 
		                       displayablePao.getName(), timestamp, zone.getName(), distanceString);
		double xPosition = graphStartPosition + pointToZone.getGraphPositionOffset();
		VfPoint graphPoint = new VfPoint(balloonText, zone.getName(), phase, false, xPosition, 
		                                 pointValue.getValue());
		return graphPoint;
	}

    private String getBalloonText(YukonUserContext userContext, String value, String phase, 
                                  String pointName, String paoName, String timeStamp, String zone, 
                                  String distance) {
        value = StringUtils.defaultIfEmpty(value, "");
        phase = StringUtils.defaultIfEmpty(phase, "");
        pointName = StringUtils.defaultIfEmpty(pointName, "");
        paoName = StringUtils.defaultIfEmpty(paoName, "");
        timeStamp = StringUtils.defaultIfEmpty(timeStamp, "");
        zone = StringUtils.defaultIfEmpty(zone, "");
        distance = StringUtils.defaultIfEmpty(distance, "");
        
        if (!distance.isEmpty()) {
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
        value = removeEmptyLines(value);
        value = StringUtils.trimToEmpty(value);
        value = StringUtils.replace(value, "\\n", "<br>");
        value = StringEscapeUtils.escapeHtml(value);
        return value;
    }
    
    private String removeEmptyLines(String value) {
        if (StringUtils.contains(value, "\\n\\n")) {
            value = StringUtils.replace(value, "\\n\\n", "\\n");
            value = removeEmptyLines(value);
        }
        return value;
    }
    
    private String getPhaseString(Phase phase) {
        if (phase == Phase.A) {
            return phaseAString;
        } else if (phase == Phase.B) {
            return phaseBString;
        } else if (phase == Phase.C) {
            return phaseCString;
        }
        return null;
    }
    
    private VfLineSettings getZoneTransitionLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(null, null, null, 
                                                        false, false, false, false, false, 
                                                        false, zoneTransitionDataLabel);
        return lineSetting;
    }
    
    private VfLineSettings getPhaseALineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorPhaseA, phaseABulletType, 
                                                        true, true, true, true, true);
        return lineSetting;
    }
    
    private VfLineSettings getPhaseBLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorPhaseB, phaseBBulletType,
                                                        true, true, true, true, true);
        return lineSetting;
    }
    
    private VfLineSettings getPhaseCLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorPhaseC, phaseCBulletType, 
                                                        true, true, true, true, true);
        return lineSetting;
    }
    
    private VfLineSettings getNoPhaseLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorNoPhase, phaseABulletType, 
                                                        true, true, true, false, true);
        return lineSetting;
    }

	public double getUpperVoltLimitForSubBus(CapControlCache cache, int subBusId) {
    	CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        //Default Upper value
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
        //Default lower value
        double yLower = Double.parseDouble(TargetSettingType.LOWER_VOLT_LIMIT.getDefaultValue());;
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
        double bucketSizeAsDouble = bucketSize.doubleValue();
        List<Double> bucketXValues = Lists.newArrayList();
        Integer iterationNum = 0;
        Double currentVal = 0.0;
        int precision = bucketSize.scale();
        DecimalFormat f = new DecimalFormat();
        f.setMaximumFractionDigits(precision);
        f.setGroupingUsed(false);
        
        //this "while" conditional statement below makes the graph have the same amount of
        //leading space between the last point and the right y-axis as exists between the left y-axis and the first point
        while(currentVal <= (max + min)) {
            bucketXValues.add(currentVal);
            iterationNum++;
            currentVal = Double.valueOf(f.format(iterationNum * bucketSizeAsDouble));
        }
        graph.setSeriesValues(bucketXValues);

        List<VfPoint> seriesPoints = getBaseSeriesPoints(bucketXValues);
        for (VfLine line : lines) {
            for (VfPoint point : line.getPoints()) {
                Integer seriesId = getSeriesIdForPoint(seriesPoints, point, bucketSizeAsDouble);
                point.setSeriesId(seriesId);
            }
        }
    }
    
    private Integer getSeriesIdForPoint(List<VfPoint> seriesPoints, VfPoint pointToPlace, double bucketSize) {
        double pointX = pointToPlace.getX();
        for (VfPoint seriesPoint : seriesPoints) {
            double seriesX = seriesPoint.getX();
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
}
