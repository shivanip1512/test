package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
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
    
    private String graphLabel;
    private String yAxisLabel;
    private String xAxisLabel;
    
    private String noPhaseString;
    private String phaseAString;
    private String phaseBString;
    private String phaseCString;

    private String zoneLineColorNoPhase;   
    private String zoneLineColorPhaseA;
    private String zoneLineColorPhaseB;
    private String zoneLineColorPhaseC;
     
    private String noPhaseBulletType;
    private String phaseABulletType;
    private String phaseBBulletType;
    private String phaseCBulletType;
    
    private boolean noPhaseVisibleInLegend;
    private boolean phaseAVisibleInLegend;
    private boolean phaseBVisibleInLegend;
    private boolean phaseCVisibleInLegend;

    //Every Graph must have a unique id
    private AtomicInteger graphId = new AtomicInteger();
    
    private Ordering<VfPoint> positionOrderer = Ordering.natural().onResultOf(new Function<VfPoint, Double>() {
        @Override
        public Double apply(VfPoint from) {
            return from.getX();
        }
    });

    public void initialize(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        graphLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.graphLabel");
        yAxisLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.yAxisLabel");
        String xLabel = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.xAxisLabel");
        if (xLabel != StringUtils.EMPTY) {
            xAxisLabel = xLabel;
        } else {
            xAxisLabel = null;
        }

        phaseAString = messageSourceAccessor.getMessage("yukon.common.phase");
        phaseAString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phaseA");
        phaseBString = messageSourceAccessor.getMessage("yukon.common.phase");
        phaseBString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phaseB");
        phaseCString = messageSourceAccessor.getMessage("yukon.common.phase");
        phaseCString += " " + messageSourceAccessor.getMessage("yukon.common.phase.phaseC");
        noPhaseString = messageSourceAccessor.getMessage("yukon.common.noPhase");

        zoneLineColorNoPhase = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorNoPhase");   
        zoneLineColorPhaseA = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhaseA");
        zoneLineColorPhaseB = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhaseB");
        zoneLineColorPhaseC = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.zoneLineColorPhaseC");

        noPhaseBulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.noPhaseBulletType");
        phaseABulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseABulletType");
        phaseBBulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseBBulletType");
        phaseCBulletType = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseCBulletType");

        noPhaseVisibleInLegend = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.noPhaseVisibleInLegend"));
        phaseAVisibleInLegend = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseAVisibleInLegend"));
        phaseBVisibleInLegend = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseBVisibleInLegend"));
        phaseCVisibleInLegend = Boolean.valueOf(messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.phaseCVisibleInLegend"));
    }
    
    @Override
    public VfGraph getSubBusGraph(YukonUserContext userContext, int subBusId) throws IllegalUseOfAttribute {
        initialize(userContext);
        LiteYukonUser yukonUser = userContext.getYukonUser();
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
        
        VfGraph graph = buildSubBusGraph(userContext, subBusId,cache);
        return graph;
    }
    
    @Override
    public VfGraph getZoneGraph(YukonUserContext userContext, int zoneId) throws IllegalUseOfAttribute {
        initialize(userContext);
        LiteYukonUser yukonUser = userContext.getYukonUser();
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
        
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
        
            //Vertical Zone Border line - Colored Red (don't add this if the zone starts on the Y-Axis)
//    		double xRegPosition = getGraphStartPositionForZone(zone);
//    		if (xRegPosition != 0) {
//        		VfPoint pointHigh = new VfPoint(xRegPosition, upperLimit, true);
//        		VfPoint pointLow = new VfPoint(xRegPosition, lowerLimit, true);
//        		List<VfPoint> points = Lists.newArrayList();
//        		points.add(pointHigh);
//        		points.add(pointLow);
//        		VfLine zoneLine = new VfLine(zoneTransitionId, true, zoneTransitionLineSetting, points);
//        		lines.add(zoneLine);
//        	}
        }
        
        VfGraph graph = new VfGraph();
        setSubBusGraphLineLegendVisibility(lines);
        graph.setLines(lines);
        
        VfGraphSettings settings = buildSubBusGraphSettings(subBusId, cache);
        graph.setSettings(settings);
        
        return graph;
    }
    
    private void setSubBusGraphLineLegendVisibility(List<VfLine> lines) {
        boolean phaseAShown = false;
        boolean phaseBShown = false;
        boolean phaseCShown = false;
        boolean noPhaseShown = false;
        
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
            else if (phase == null && isVisibleInLegend){
                if (noPhaseShown) {
                    line.getSettings().setVisibleInLegend(false);
                } else {
                    noPhaseShown = true;
                }
            }
        }
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
        
        VfGraphSettings settings = buildZoneGraphSettings(zoneId, cache);
        graph.setSettings(settings);
        
        return graph;
    }
    
	private VfGraphSettings buildSubBusGraphSettings(int subBusId, CapControlCache cache) {
        VfGraphSettings graphSettings = new VfGraphSettings();
        
        SubBus subBus = cache.getSubBus(subBusId);
        String title = subBus.getCcName() + " " + graphLabel; 
        double yUpper = getUpperVoltLimitForSubBus(cache, subBusId);
        double yLower = getLowerVoltLimitForSubBus(cache, subBusId);
        
        graphSettings.setYUpperBound(yUpper);
        graphSettings.setYLowerBound(yLower);
        graphSettings.setYAxisLabel(yAxisLabel);
        graphSettings.setXAxisLabel(xAxisLabel);
        graphSettings.setGraphTitle(title);
        
        return graphSettings;
    }
	
	private VfGraphSettings buildZoneGraphSettings(int zoneId, CapControlCache cache) {
        Zone zone = zoneService.getZoneById(zoneId);
        VfGraphSettings graphSettings = new VfGraphSettings();
        
        String title = zone.getName() + " " + graphLabel;
        
        double yUpper = getUpperVoltLimitForSubBus(cache, zone.getSubstationBusId());
        double yLower = getLowerVoltLimitForSubBus(cache, zone.getSubstationBusId());
        
        graphSettings.setYUpperBound(yUpper);
        graphSettings.setYLowerBound(yLower);
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
        List<VfLine> lines = Lists.newArrayListWithCapacity(3);
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
        List<VfPoint> noPhasePoints = Lists.newArrayList();
        
        for (VfPoint vfPoint: points) {
            if (vfPoint.getPhase() == Phase.A) {
                phaseAPoints.add(vfPoint);
            } else if (vfPoint.getPhase() == Phase.B) {
                phaseBPoints.add(vfPoint);
            } else if (vfPoint.getPhase() == Phase.C) {
                phaseCPoints.add(vfPoint);
            } else {
                noPhasePoints.add(vfPoint);
            }
        }

        if (!phaseAPoints.isEmpty()) {
            Collections.sort(phaseAPoints, positionOrderer);
            VfLineSettings phaseALineSettings = getPhaseALineSetting();
            VfLine phaseALine = new VfLine(graphId.getAndIncrement(), phaseAString, false, Phase.A, phaseALineSettings, phaseAPoints);
            lines.add(phaseALine);
        }

        if (!phaseBPoints.isEmpty()) {
            Collections.sort(phaseBPoints, positionOrderer);
            VfLineSettings phaseBLineSettings = getPhaseBLineSetting();
            VfLine phaseBLine = new VfLine(graphId.getAndIncrement(), phaseBString, false, Phase.B, phaseBLineSettings, phaseBPoints);
            lines.add(phaseBLine);
        }

        if (!phaseCPoints.isEmpty()) {
            Collections.sort(phaseCPoints, positionOrderer);
            VfLineSettings phaseCLineSettings = getPhaseCLineSetting();
            VfLine phaseCLine = new VfLine(graphId.getAndIncrement(), phaseCString, false, Phase.C, phaseCLineSettings, phaseCPoints);
            lines.add(phaseCLine);
        }
        
        if (!noPhasePoints.isEmpty()) {
            Collections.sort(noPhasePoints, positionOrderer);
            VfLineSettings noPhaseLineSettings = getNoPhaseLineSetting();
            VfLine noPhaseLine = new VfLine(graphId.getAndIncrement(), noPhaseString, false, null, noPhaseLineSettings, noPhasePoints);
            lines.add(noPhaseLine);
        }
        
        return lines;
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
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String nameString = displayablePao.getName();
        String description = null;
        if (phase != null) {
            String phaseString = getPhaseString(phase);
            description = messageSourceAccessor.
                getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndPhaseAndNoDist",
                           pointValueString, phaseString, nameString, timestamp, zoneName);
        } else {
            description = messageSourceAccessor.
                getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndNoDist",
                           pointValueString, nameString, timestamp, zoneName);
        }
            
        VfPoint regulatorGraphPoint = new VfPoint(description, phase, true, graphStartPosition, pointValue.getValue(), null);
        return regulatorGraphPoint;
	}
	
	private VfPoint getCapBankToZoneVfPoint(YukonUserContext userContext, CapControlCache cache, 
	                                        CapBankToZoneMapping bankToZone, Integer pointId, 
	                                        Phase phase, Zone zone, double graphStartPosition) {
	    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
	    
	    CapBankDevice bank = cache.getCapBankDevice(bankToZone.getDeviceId());
	    YukonPao yukonPao = paoDao.getYukonPao(bank.getControlDeviceID());
	    DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
	    PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
	    String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
	    String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
	    double distance = bankToZone.getDistance();
	    String description = "";
	    if (phase != null) {
	        String phaseString = getPhaseString(phase);
	        if (distance != 0) {
	            description = messageSourceAccessor.
	                getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndDist",
	                           pointValueString, phaseString, displayablePao.getName(), timestamp,
	                           zone.getName(), distance);
	        } else {
	            description = messageSourceAccessor.
	                getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndNoDist",
	                           pointValueString, phaseString, displayablePao.getName(), timestamp,
	                           zone.getName());
	        }
	    } else {
	        if (distance != 0) {
	            description = messageSourceAccessor.
	                getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndDist",
	                           pointValueString, displayablePao.getName(), timestamp, 
	                           zone.getName(), distance);
	        } else {
	            description = messageSourceAccessor.
	                getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndNoDist",
	                           pointValueString, displayablePao.getName(), timestamp,
	                           zone.getName());
	        }
	    }
	    
	    
	    double xPosition = graphStartPosition + bankToZone.getGraphPositionOffset();
	    VfPoint graphPoint = new VfPoint(description, phase, false, xPosition, pointValue.getValue(), null);
	    return graphPoint;
	}
	
	private VfPoint getPointToZoneVfPoint(YukonUserContext userContext, PointToZoneMapping pointToZone, Zone zone, double graphStartPosition) {
	    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		int pointId = pointToZone.getPointId();
		LitePoint litePoint = pointDao.getLitePoint(pointId);
		YukonPao yukonPao = paoDao.getYukonPao(litePoint.getPaobjectID());
		DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
		String litePointString = litePoint.getPointName();
		Phase phase = pointToZone.getPhase();
		String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
		String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
		double distance = pointToZone.getDistance();
		String description = "";
		if (phase != null) {
		    String phaseString = getPhaseString(phase);
		    if (distance != 0) {
		        description = messageSourceAccessor.
		            getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.pointAndPhaseAndDist",
		                       pointValueString, phaseString, litePointString, 
		                       displayablePao.getName(), timestamp, zone.getName(), distance);
		    } else {
		        description = messageSourceAccessor.
		            getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.pointAndPhaseAndNoDist",
		                       pointValueString, phaseString, litePointString, 
		                       displayablePao.getName(), timestamp, zone.getName());
		    }
		} else {
		    if (distance != 0) {
		        description = messageSourceAccessor.
		            getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.pointAndDist",
		                       pointValueString, litePointString, displayablePao.getName(), 
		                       timestamp, zone.getName(), distance);
		    } else {
		        description = messageSourceAccessor.
		            getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.pointAndNoDist",
		                       pointValueString, litePointString, displayablePao.getName(),
		                       timestamp, zone.getName());
		    }
		}
		
		double xPosition = graphStartPosition + pointToZone.getGraphPositionOffset();
		VfPoint graphPoint = new VfPoint(description, phase, false, xPosition, pointValue.getValue(), null);
		return graphPoint;
	}

    private String getPhaseString(Phase phase) {
        if (phase == Phase.A) {
            return phaseAString;
        } else if (phase == Phase.B) {
            return phaseBString;
        } else if (phase == Phase.C) {
            return phaseCString;
        }
        return noPhaseString;
    }

    private VfLineSettings getNoPhaseLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorNoPhase, 0, noPhaseBulletType, true, true, true, noPhaseVisibleInLegend);
        return lineSetting;
    }
    
    private VfLineSettings getPhaseALineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorPhaseA, 0, phaseABulletType, true, true, true, phaseAVisibleInLegend);
        return lineSetting;
    }
    
    private VfLineSettings getPhaseBLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorPhaseB, 0, phaseBBulletType, true, true, true, phaseBVisibleInLegend);
        return lineSetting;
    }
    
    private VfLineSettings getPhaseCLineSetting() {
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColorPhaseC, 0, phaseCBulletType, true, true, true, phaseCVisibleInLegend);
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

    /**
     * Used for debugging graph (b/c you can plug in this output into the amchart website's
     * visual editor)
     */
    private void outputGraphXML(VfGraph graph) {
        System.out.println("Points:");
        System.out.println("");
        for (VfLine line : graph.getLines()) {
            for (VfPoint point : line.getPoints()) {
                System.out.println("x: " + point.getX());
                System.out.println("y: " + point.getY());
                System.out.println("desc: " + point.getDescription());
            }
        }
        System.out.println("");
        System.out.println("<graphs>");
        for (VfLine line : graph.getLines()) {
            if (line.isBoundary() == false) {
                System.out.println("<graph gid=\"" + line.getId() + ">");
                System.out.println("<title>" + line.getLineName() + "</title>");
                System.out.println("<bullet_size>10</bullet_size>");
                System.out.println("<balloon_text><![CDATA[{description}]]></balloon_text>");
                System.out.println("<bullet>" + line.getSettings().getBullet() + "</bullet>");
                System.out.println("</graph>");
            }
        }
        System.out.println("</graphs>");
        System.out.println("");

        System.out.println("<chart>");
        System.out.println("<series>");
        int index = 0;
        for (Double seriesValue : graph.getSeriesValues()) {
            System.out.println("<value xid=\"" + index + "\">" + seriesValue + "</value>");
            index++;
        }
        System.out.println("</series>");
        System.out.println("<graphs>");
        
        index = 1;
        for (VfLine line : graph.getLines()) {
            if (line.isBoundary() == false) {
                System.out.println("<graph gid=\"" + line.getId() + ">");
                for (VfPoint point : line.getPoints()) {
                    if (point.getY() != null) {
                        System.out.println("<value xid=\"" + point.getSeriesId() + "\" description=\"" + point.getDescription() + ">" + point.getY() + "</value>");
                    } else {
                        System.out.println("<value xid=\"" + point.getSeriesId() + "\"></value>");
                    }
                }
                System.out.println("</graph>");
            }
            index++;
        }
        
        System.out.println("</graphs>");
        System.out.println("</chart>");
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
}
