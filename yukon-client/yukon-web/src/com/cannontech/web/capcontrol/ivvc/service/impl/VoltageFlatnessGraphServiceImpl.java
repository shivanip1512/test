package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DynamicDataDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
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
    private PointFormattingService pointFormattingService;
    private PaoLoadingService paoLoadingService;
    
    //Change these to alter the color on the chart.
    private final String zoneLineColor = "#047D08";
    private final String zoneTransitionColor = "#E32222";
    private final String grayAreaColor = "#000000";
    
    private final int grayAreaId = -2;
    private final int zoneTransitionId = -3;
    
    private final String graphLabel = "Voltage Profile";
    private final String yAxisLabel = "Volts";
    
    private double maxXPosition = 0;
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
        double upperLimit = getUpperVoltLimitForSubBus(cache, subBusId);
        double lowerLimit = getLowerVoltLimitForSubBus(cache, subBusId);

        double graphStartPosition = 0.0;
        int index = 0;
        //Build A line for each zone on the bus.
        for (Zone zone : zones) {
        	graphStartPosition += zone.getGraphStartPosition();
            VfLine line = buildLineDataForZone(userContext, cache, zone);
            //subtract 1 so the next zone's first point is on the same vertical axis as the last point of the previous zone
            lines.add(line);
            
            //Vertical Zone Border line
        	if (index < zones.size()-1) {
        		VfLine zoneLine = new VfLine();
        		List<VfPoint> points = Lists.newArrayList();
        		
        		//Y-Axis value of the zone transition point is the next zone's regulator point value
        		double xRegPosition = getGraphStartPositionForZone(zones.get(index+1));
        		VfPoint pointHigh = new VfPoint(xRegPosition, upperLimit);
        		VfPoint pointLow = new VfPoint(xRegPosition, lowerLimit);
        		points.add(pointHigh);
        		points.add(pointLow);
        		zoneLine.setId(zoneTransitionId);
        		zoneLine.setZoneName("Zone Transition");
        		zoneLine.setPoints(points);
        		VfLineSettings lineSetting = new VfLineSettings(zoneTransitionColor, 0, null, false, false, false, true);
        		zoneLine.setSettings(lineSetting);
        		lines.add(zoneLine);
        	}
        	index++;
        }
        
        VfLine lowerGray = getLowerGrayArea(subBusId, cache);
        VfLine upperGray = getUpperGrayArea(subBusId, cache);
        lines.add(lowerGray);
        lines.add(upperGray);

        VfGraph graph = new VfGraph();
        graph.setLines(lines);
        
        VfGraphSettings settings = buildSubBusGraphSettings(subBusId, cache);
        graph.setSettings(settings);
        
        return graph;
    }
    
	private VfGraph buildZoneGraph(YukonUserContext userContext, int zoneId, CapControlCache cache) {
        List<VfLine> lines = Lists.newArrayList();
        
        Zone zone = zoneService.getZoneById(zoneId);
        VfLine line = buildLineDataForZone(userContext, cache, zone);
        lines.add(line);
        
        VfLine lowerGray = getLowerGrayArea(zone.getSubstationBusId(), cache);
        VfLine upperGray = getUpperGrayArea(zone.getSubstationBusId(), cache);
        lines.add(lowerGray);
        lines.add(upperGray);
        
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
        graphSettings.setXAxisLabel(null);
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
        graphSettings.setXAxisLabel(null);
        graphSettings.setGraphTitle(title);
        
        return graphSettings;
    }
	
	private VfLine getUpperGrayArea(int subBusId, CapControlCache cache) {
    	double upperVoltLimit = getUpperVoltLimitForSubBus(cache, subBusId);
		
    	VfPoint point1 = new VfPoint(-10, upperVoltLimit);
		VfPoint point2 = new VfPoint(-10, 300);
		VfPoint point3 = new VfPoint(maxXPosition+100, 300);
		VfPoint point4 = new VfPoint(maxXPosition+100, upperVoltLimit);
		
		List<VfPoint> points = Lists.newArrayList();
		points.add(point1);
		points.add(point2);
		points.add(point3);
		points.add(point4);
		
		VfLine grayAreaLine = new VfLine();
		grayAreaLine.setId(grayAreaId);
		grayAreaLine.setPoints(points);
		
		VfLineSettings lineSettings = new VfLineSettings(grayAreaColor, 10, null, false, false, false, false);
		grayAreaLine.setSettings(lineSettings);
		
		return grayAreaLine;
	}

	private VfLine getLowerGrayArea(int subBusId, CapControlCache cache) {
    	
    	double lowerVoltLimit = getLowerVoltLimitForSubBus(cache, subBusId);
		
		VfPoint point1 = new VfPoint(-10, -10);
		VfPoint point2 = new VfPoint(-10, lowerVoltLimit);
		VfPoint point3 = new VfPoint(maxXPosition+100, lowerVoltLimit);
		VfPoint point4 = new VfPoint(maxXPosition+100, -10);
		
		List<VfPoint> points = Lists.newArrayList();
		points.add(point1);
		points.add(point2);
		points.add(point3);
		points.add(point4);
		
		VfLine grayAreaLine = new VfLine();
		grayAreaLine.setId(grayAreaId);
		grayAreaLine.setPoints(points);
		
		VfLineSettings lineSettings = new VfLineSettings(grayAreaColor, 10, null, false, false, false, false);
		grayAreaLine.setSettings(lineSettings);
		
		return grayAreaLine;
	}
	
	private double getGraphStartPositionForZone(Zone zone) {
		double startingPos = zone.getGraphStartPosition();
		Zone parent = null;
		if (zone.getParentId() != null) {
			parent = zoneService.getZoneById(zone.getParentId());
		}
		
		while(parent != null) {
			startingPos += parent.getGraphStartPosition();
			if (parent.getParentId() != null) {
				parent = zoneService.getZoneById(parent.getParentId());
			} else {
				parent = null;
			}
		}
		
		return startingPos;
	}
	
	private VfLine buildLineDataForZone(YukonUserContext userContext, CapControlCache cache, Zone zone) {
        VfLine line = new VfLine();
        List<VfPoint> points = Lists.newArrayList();
        List<CapBankToZoneMapping> banksToZone = zoneService.getCapBankToZoneMapping(zone.getId());
        List<PointToZoneMapping> pointsToZone = zoneService.getPointToZoneMapping(zone.getId());
        double graphStartPosition = getGraphStartPositionForZone(zone);
        
        //Add the regulator as the first point
        VfPoint regulatorGraphPoint = getRegulatorVfPoint(userContext, zone, graphStartPosition);
        points.add(regulatorGraphPoint);
        
        //Add a point on the line for each bank in the zone
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            List<Integer> bankPoints = dynamicDataDao.getMonitorPointsForBank(bankToZone.getDeviceId());
            
            //Is there a point with an order less than this Bank? If so add that first
            PointToZoneMapping pointToZone = getNextPointToZone(bankToZone, pointsToZone);
            while (pointToZone != null ) {
            	VfPoint graphPoint = getPointToZoneVfPoint(userContext, pointToZone, zone, graphStartPosition);
                points.add(graphPoint);
                pointToZone = getNextPointToZone(bankToZone, pointsToZone);
            }
            
            for (Integer pointId : bankPoints) {
            	LitePoint litePoint = pointDao.getLitePoint(pointId);
            	String pointName = litePoint.getPointName();
            	
            	if (pointName.equals("Voltage")) {
            		VfPoint graphPoint = getCapBankToZoneVfPoint(userContext, cache, bankToZone, pointId, zone, graphStartPosition);
                    points.add(graphPoint);
            	}
            }
        }
        
        //Add the rest of the points onto the graph if there are any left
        for (PointToZoneMapping pointToZone : pointsToZone) {
        	VfPoint graphPoint = getPointToZoneVfPoint(userContext, pointToZone, zone, graphStartPosition);
            points.add(graphPoint);
        }
        
        line.setZoneName(zone.getName());
        line.setPoints(points);
        line.setId(getGraphIdAndIncrement());
        
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColor, 0, bulletRoundOutlined, true, true, true, false);
        line.setSettings(lineSetting);
        
        return line;
    }
	
	private PointToZoneMapping getNextPointToZone(CapBankToZoneMapping bankToZone, List<PointToZoneMapping> pointsToZone) {
		
		double bankOrder = bankToZone.getPosition();
		
		for (PointToZoneMapping pointToZone : pointsToZone) {
			double pointOrder = pointToZone.getPosition();
			if (pointOrder < bankOrder) {
				pointsToZone.remove(pointToZone);
				return pointToZone; 
			}
		}
		
		return null;
	}
	
	private VfPoint getRegulatorVfPoint(YukonUserContext userContext, Zone zone, double graphStartPosition) {
		int regulatorId = zone.getRegulatorId();
        YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(regulatorPao);
        LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE);
        PointValueQualityHolder regulatorPointValue = dynamicDataSource.getPointValue(regulatorPoint.getLiteID());
        String regTimestamp = dateFormattingService.format(regulatorPointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
        String regDescription = pointFormattingService.getValueString(regulatorPointValue, Format.VALUE, userContext) + 
        						"\n" + displayablePao.getName() + "\n" + regTimestamp + "\n" + zone.getName();
        
        setMaxXPosition(graphStartPosition);
        double pointValue = regulatorPointValue.getValue();
        VfPoint regulatorGraphPoint = new VfPoint(regDescription, graphStartPosition, pointValue);
        return regulatorGraphPoint;
	}
	
	private VfPoint getPointToZoneVfPoint(YukonUserContext userContext, PointToZoneMapping pointToZone, Zone zone, double graphStartPosition) {
		int pointId = pointToZone.getPointId();
		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
		String pointTimestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
		LitePoint litePoint = pointDao.getLitePoint(pointId);
		YukonPao yukonPao = paoDao.getYukonPao(litePoint.getPaobjectID());
        DisplayablePao displayablePointPao = paoLoadingService.getDisplayablePao(yukonPao);
		String pointDescription = pointFormattingService.getValueString(pointValue, Format.VALUE, userContext) +
								  "\n" + litePoint.getPointName() + "\n" + displayablePointPao.getName() + "\n" + pointTimestamp + "\n" + zone.getName();
		
		if (pointToZone.getDistance() != 0) {
			pointDescription +=  "\nDist: " + pointToZone.getDistance(); 
		}
		
		double xPosition = graphStartPosition + pointToZone.getPosition();
		setMaxXPosition(xPosition);
		VfPoint graphPoint = new VfPoint(pointDescription, xPosition, pointValue.getValue());
		return graphPoint;
	}
	
	private VfPoint getCapBankToZoneVfPoint(YukonUserContext userContext, CapControlCache cache, CapBankToZoneMapping bankToZone, Integer pointId, Zone zone, double graphStartPosition) {
		CapBankDevice bank = cache.getCapBankDevice(bankToZone.getDeviceId());
		YukonPao yukonPao = paoDao.getYukonPao(bank.getControlDeviceID());
        DisplayablePao displayablePointPao = paoLoadingService.getDisplayablePao(yukonPao);
		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
		String pointTimestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
		String pointDescription = pointFormattingService.getValueString(pointValue, Format.VALUE, userContext) + 
								  "\n" + displayablePointPao.getName() + "\n" + pointTimestamp + "\n" + zone.getName();
		
		if (bankToZone.getDistance() != 0) {
			pointDescription +=  "\nDist: " + bankToZone.getDistance(); 
		}
		
		double xPosition = graphStartPosition + bankToZone.getPosition();
		setMaxXPosition(xPosition);
		VfPoint graphPoint = new VfPoint(pointDescription, xPosition, pointValue.getValue());
		return graphPoint;
	}
	
    private void setMaxXPosition(double xPosition) {
    	if (maxXPosition < xPosition) {
    		maxXPosition = xPosition;
    	}
	}

	public double getUpperVoltLimitForSubBus(CapControlCache cache, int subBusId) {
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
    
    public double getLowerVoltLimitForSubBus(CapControlCache cache, int subBusId) {
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
    
    @Autowired
    public void setPointFormattingService(PointFormattingService pointFormattingService) {
		this.pointFormattingService = pointFormattingService;
	}
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
		this.paoLoadingService = paoLoadingService;
	}
}
