package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.model.Zone;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DynamicDataDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfLine;
import com.cannontech.web.capcontrol.ivvc.models.VfLineSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.ivvc.service.ZoneService;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.SubBus;
import com.google.common.collect.Lists;

public class VoltageFlatnessGraphServiceImpl implements VoltageFlatnessGraphService {
    
    private AttributeService attributeService;
    private PaoDao paoDao;
    private DynamicDataSource dynamicDataSource;
    private StrategyDao strategyDao;
    private DynamicDataDao dynamicDataDao;
    private PointDao pointDao;
    private DateFormattingService dateFormattingService;
    private ZoneService zoneService;
    private FilterCacheFactory filterCacheFactory;
    
    //Change these to alter the color on the chart.
    private final String zoneLineColor = "#047D08";
    private final String zoneTransitionColor = "#E32222";
    
    private final int zoneTransitionId = -3;
    
    private final String bulletRoundOutlined = "round_outlined";
    
    //Every Graph must have a unique id
    private int graphId = 0;
    
    private int getGraphIdAndIncrement() {
    	return graphId++;
    }
    
    @Override
    public VfGraph getSubBusGraph(YukonUserContext userContext, int subBusId) {
        LiteYukonUser yukonUser = userContext.getYukonUser();
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
        
        return buildSubBusGraph(userContext, subBusId,cache);
    }

    @Override
    public VfGraph getZoneGraph(YukonUserContext userContext, int zoneId) {
        LiteYukonUser yukonUser = userContext.getYukonUser();
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(yukonUser);
        
        return buildZoneGraph(userContext, zoneId,cache);
    }

    private VfGraph buildSubBusGraph(YukonUserContext userContext, int subBusId, CapControlCache cache) {
        List<VfLine> lines = Lists.newArrayList();
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        
        int xPosition = 0;
        
        int numZones = zones.size();
        //Build A line for each zone on the bus.
        for (Zone zone : zones) {
            VfLine line = buildLineDataForZone(userContext, cache, zone.getId(),xPosition);
            //subtract 1 so the next zone's first point is on the same vertical axis as the last point of the previous zone
            xPosition += line.getPoints().size()-1;
            lines.add(line);
            numZones--;
            
            //Vertical Zone Border line
        	if (numZones > 0) {
        		VfLine zoneLine = new VfLine();
        		List<VfPoint> points = Lists.newArrayList();
        		VfPoint zoneTransitionPoint = new VfPoint(xPosition, 1000);
        		points.add(zoneTransitionPoint);
        		zoneLine.setId(zoneTransitionId);
        		zoneLine.setZoneName("Zone Transition");
        		zoneLine.setPoints(points);
        		VfLineSettings lineSetting = new VfLineSettings(zoneTransitionColor, 0, null, false, false, true);
        		zoneLine.setSettings(lineSetting);
        		lines.add(zoneLine);
        	}
        }

        VfGraph graph = new VfGraph();
        graph.setLines(lines);
        
        VfGraphSettings settings = buildSubBusGraphSettings(subBusId, cache);
        graph.setSettings(settings);
        
        return graph;
    }
    
    private VfGraph buildZoneGraph(YukonUserContext userContext, int zoneId, CapControlCache cache) {
        List<VfLine> lines = Lists.newArrayList();
        
        int xPosition = 0;
        VfLine line = buildLineDataForZone(userContext, cache, zoneId, xPosition);
        lines.add(line);
        
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
        String graphTitle = subBus.getCcName() + " Voltage Profile"; 
        double yUpper = getUpperGraphBoundary(cache, subBusId);
        double yLower = getLowerGraphBoundary(cache, subBusId);
        
        graphSettings.setYUpperBound(yUpper);
        graphSettings.setYLowerBound(yLower);
        graphSettings.setYAxisLabel("Volts");
        graphSettings.setXAxisLabel("Distance / Order");
        graphSettings.setGraphTitle(graphTitle);
        
        return graphSettings;
    }
	
	private VfGraphSettings buildZoneGraphSettings(int zoneId, CapControlCache cache) {
        Zone zone = zoneService.getZoneById(zoneId);
        VfGraphSettings graphSettings = new VfGraphSettings();
        
        String graphTitle = zone.getName() + " Voltage Profile"; 
        
        double yUpper = getUpperGraphBoundary(cache, zone.getSubstationBusId());
        double yLower = getLowerGraphBoundary(cache, zone.getSubstationBusId());
        
        graphSettings.setYUpperBound(yUpper);
        graphSettings.setYLowerBound(yLower);
        graphSettings.setYAxisLabel("Volts");
        graphSettings.setXAxisLabel("Distance / Order");
        graphSettings.setGraphTitle(graphTitle);
        
        return graphSettings;
    }
	
	private PointToZoneMapping getNextOrderedPointId(CapBankToZoneMapping bankToZone, List<PointToZoneMapping> pointsToZone) {
		
		double bankOrder = bankToZone.getZoneOrder();
		
		for (PointToZoneMapping pointToZone : pointsToZone) {
			double pointOrder = pointToZone.getZoneOrder();
			if (pointOrder < bankOrder) {
				pointsToZone.remove(pointToZone);
				return pointToZone; 
			}
		}
		
		return null;
	}

    private VfLine buildLineDataForZone(YukonUserContext userContext, CapControlCache cache, int zoneId, int xPos) {
        VfLine line = new VfLine();
        List<VfPoint> points = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zoneId);
        List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zoneId);
        Zone zone = zoneService.getZoneById(zoneId);
        
        //Add the regulator as the first point
        int regulatorId = zone.getRegulatorId();
        LiteYukonPAObject regulatorPao = paoDao.getLiteYukonPAO(regulatorId);
        LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE);
        PointValueQualityHolder regulatorPointValue = dynamicDataSource.getPointValue(regulatorPoint.getLiteID());
        Date date = regulatorPointValue.getPointDataTimeStamp();
        String regTimestamp = dateFormattingService.format(date, DateFormatEnum.BOTH, userContext);
        String regDescription = regulatorPao.getPaoName() + "\n" + regTimestamp + "\n" + zone.getName();
        VfPoint regulatorGraphPoint = new VfPoint(regDescription, xPos++, regulatorPointValue.getValue());
        points.add(regulatorGraphPoint);
        
        //Add a point on the line for each bank in the zone
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            List<Integer> bankPoints = dynamicDataDao.getMonitorPointsForBank(bankToZone.getDeviceId());
            
            //Is there a point with an order less than this Bank? If so add that first
            PointToZoneMapping pointToZone = getNextOrderedPointId(bankToZone, pointsToZone);
            while (pointToZone != null ) {
            	int pointId = pointToZone.getPointId();
        		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        		String pointTimestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
        		LitePoint litePoint = pointDao.getLitePoint(pointId);
        		LiteYukonPAObject pao = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        		String pointDescription = litePoint.getPointName() + "\n" + pao.getPaoName() + "\n" + pointTimestamp + "\n" + zone.getName();
        		VfPoint graphPoint = new VfPoint(pointDescription, xPos, pointValue.getValue());
        		xPos++;
                points.add(graphPoint);
                pointToZone = getNextOrderedPointId(bankToZone, pointsToZone);
            }
            
            for (Integer pointId : bankPoints) {
            	LitePoint litePoint = pointDao.getLitePoint(pointId);
            	String pointName = litePoint.getPointName();
            	
            	if (pointName.equals("Voltage")) {
            		CapBankDevice bank = cache.getCapBankDevice(bankToZone.getDeviceId());
            		LiteYukonPAObject pao = paoDao.getLiteYukonPAO(bank.getControlDeviceID());
            		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
            		String pointTimestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
            		String pointDescription = pao.getPaoName() + "\n" + pointTimestamp + "\n" + zone.getName();
            		VfPoint graphPoint = new VfPoint(pointDescription, xPos, pointValue.getValue());
                    points.add(graphPoint);
            	}
            }
            xPos++;
        }
        
        //Add the rest of the points onto the graph if there are any left
        for (PointToZoneMapping pointToZone : pointsToZone) {
        	int pointId = pointToZone.getPointId();
        	PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
    		LitePoint litePoint = pointDao.getLitePoint(pointId);
    		LiteYukonPAObject pao = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
    		String pointTimestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
    		String pointDescription = litePoint.getPointName() + "\n" + pao.getPaoName() + "\n" + pointTimestamp + "\n" + zone.getName();
    		VfPoint graphPoint = new VfPoint(pointDescription, xPos, pointValue.getValue());
    		xPos++;
            points.add(graphPoint);
        }
        
        line.setZoneName(zone.getName());
        line.setPoints(points);
        line.setId(getGraphIdAndIncrement());
        
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColor, 0, bulletRoundOutlined, true, true, false);
        line.setSettings(lineSetting);
        
        return line;
    }
    
    public double getUpperGraphBoundary(CapControlCache cache, int subBusId) {
    	CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        //Default Upper value
        double yUpper = Double.parseDouble(TargetSettingType.UPPER_VOLT_LIMIT.getDefaultValue());
        for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
            if ( TargetSettingType.UPPER_VOLT_LIMIT.getDisplayName().equals((setting.getName())) ) {
                yUpper = Double.parseDouble(setting.getPeakValue());
            }
        }
        return yUpper;
    }
    
    public double getLowerGraphBoundary(CapControlCache cache, int subBusId) {
    	CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
    	
        //Default lower value
        double yLower = Double.parseDouble(TargetSettingType.LOWER_VOLT_LIMIT.getDefaultValue());;
        for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
            if ( TargetSettingType.LOWER_VOLT_LIMIT.getDisplayName().equals((setting.getName())) ) {
                yLower = Double.parseDouble(setting.getPeakValue());
            }
        }
        return yLower;
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
    public void setDynamicDataDao(DynamicDataDao dynamicDataDao) {
		this.dynamicDataDao = dynamicDataDao;
	}
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
}
