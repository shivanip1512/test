package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.PointToZoneMapping;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
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
    
    //Change these to alter the color on the chart.
    private final String zoneLineColor = "#047D08";
    private final String zoneTransitionColor = "#E32222";
    private final String blackAreaColor = "#000000";
    
    private final int grayAreaId = -2;
    private final int zoneTransitionId = -3;
    
    private final String graphLabel = "Voltage Profile";
    private final String yAxisLabel = "Volts";
    
    private final String bulletRoundOutlined = "round_outlined";
    
    //Every Graph must have a unique id
    private AtomicInteger graphId = new AtomicInteger();
    
    //Used for generating the Lower and Upper gray areas on the graph
    public static double getMaxXPosition(List<VfLine> lines) {
    	double maxXPosition = 0;
    	
    	for (VfLine line : lines) {
    		for (VfPoint point : line.getPoints()) {
	    		if (maxXPosition < point.getX()) {
	    			maxXPosition = point.getX();
	    		}
    		}
    	}
    	
    	return maxXPosition;
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

        //Build A line for each zone on the bus.
        for (Zone zone : zones) {
            VfLine line = buildLineDataForZone(userContext, cache, zone);
            lines.add(line);
            
            //Vertical Zone Border line - Colored Red (don't add this if the zone starts on the Y-Axis)
    		double xRegPosition = getGraphStartPositionForZone(zone);
    		if (xRegPosition != 0) {
        		VfPoint pointHigh = new VfPoint(xRegPosition, upperLimit);
        		VfPoint pointLow = new VfPoint(xRegPosition, lowerLimit);
        		List<VfPoint> points = Lists.newArrayList();
        		points.add(pointHigh);
        		points.add(pointLow);
        		VfLine zoneLine = new VfLine();
        		zoneLine.setId(zoneTransitionId);
        		zoneLine.setPoints(points);
        		VfLineSettings lineSetting = new VfLineSettings(zoneTransitionColor, 0, null, false, false, false, true);
        		zoneLine.setSettings(lineSetting);
        		lines.add(zoneLine);
        	}
        }
        
        double maxXPosition = getMaxXPosition(lines);
        
        VfLine lowerGray = getLowerGrayArea(lowerLimit, maxXPosition, cache);
        VfLine upperGray = getUpperGrayArea(upperLimit, maxXPosition, cache);
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
        double upperLimit = getUpperVoltLimitForSubBus(cache, zone.getSubstationBusId());
        double lowerLimit = getLowerVoltLimitForSubBus(cache, zone.getSubstationBusId());
        
        VfLine line = buildLineDataForZone(userContext, cache, zone);
        lines.add(line);
        
        double maxXPosition = getMaxXPosition(lines);
        VfLine lowerGray = getLowerGrayArea(lowerLimit, maxXPosition, cache);
        VfLine upperGray = getUpperGrayArea(upperLimit, maxXPosition, cache);
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
	
	private VfLine getUpperGrayArea(double upperVoltLimit, double maxXPosition, CapControlCache cache) {
    	VfPoint point1 = new VfPoint(-10, upperVoltLimit);
		VfPoint point2 = new VfPoint(-10, 300);
		VfPoint point3 = new VfPoint(maxXPosition+100, 300);
		VfPoint point4 = new VfPoint(maxXPosition+100, upperVoltLimit);
		
		List<VfPoint> points = Lists.newArrayListWithCapacity(4);
		points.add(point1);
		points.add(point2);
		points.add(point3);
		points.add(point4);
		
		VfLine grayAreaLine = new VfLine();
		grayAreaLine.setId(grayAreaId);
		grayAreaLine.setPoints(points);
		
		VfLineSettings lineSettings = new VfLineSettings(blackAreaColor, 10, null, false, false, false, false);
		grayAreaLine.setSettings(lineSettings);
		
		return grayAreaLine;
	}

	private VfLine getLowerGrayArea(double lowerVoltLimit, double maxXPosition, CapControlCache cache) {
		VfPoint point1 = new VfPoint(-10, -10);
		VfPoint point2 = new VfPoint(-10, lowerVoltLimit);
		VfPoint point3 = new VfPoint(maxXPosition+100, lowerVoltLimit);
		VfPoint point4 = new VfPoint(maxXPosition+100, -10);
		
		List<VfPoint> points = Lists.newArrayListWithCapacity(4);
		points.add(point1);
		points.add(point2);
		points.add(point3);
		points.add(point4);
		
		VfLine grayAreaLine = new VfLine();
		grayAreaLine.setId(grayAreaId);
		grayAreaLine.setPoints(points);
		
		VfLineSettings lineSettings = new VfLineSettings(blackAreaColor, 10, null, false, false, false, false);
		grayAreaLine.setSettings(lineSettings);
		
		return grayAreaLine;
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
            //Regulator point
            int regulatorId = zone.getRegulatorId();
            YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
            LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE);
            allGraphPoints.add(regulatorPoint.getPointID());

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
         long largestTime = 0;
         Set<Integer> allGraphPoints = getAllPointsInSubBusGraph(subBusId);
         Set<? extends PointValueQualityHolder> pointValues = dynamicDataSource.getPointValue(allGraphPoints);

         for (PointValueQualityHolder point : pointValues) {
             long pointTime = point.getPointDataTimeStamp().getTime();
             if (largestTime < pointTime) {
                 largestTime = pointTime;
             }
         }
         return largestTime;
     }

    private Set<Integer> getAllPointsInZoneGraph(int zoneId) {
        Set<Integer> allGraphPoints = Sets.newHashSet();

        //Regulator point
        Zone zone = zoneService.getZoneById(zoneId);
        int regulatorId = zone.getRegulatorId();
        YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
        LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE);
        allGraphPoints.add(regulatorPoint.getPointID());

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
        long largestTime = 0;
        Set<Integer> allGraphPoints = getAllPointsInZoneGraph(zoneId);
        Set<? extends PointValueQualityHolder> pointValues = dynamicDataSource.getPointValue(allGraphPoints);

        for (PointValueQualityHolder point : pointValues) {
            long pointTime = point.getPointDataTimeStamp().getTime();
            if (largestTime < pointTime) {
                largestTime = pointTime;
            }
        }
        return largestTime;
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
        
        //Add the cap banks
        for (CapBankToZoneMapping bankToZone : banksToZone) {
            List<Integer> bankPoints = zoneService.getMonitorPointsForBank(bankToZone.getDeviceId());
            for (Integer pointId : bankPoints) {
        		VfPoint graphPoint = getCapBankToZoneVfPoint(userContext, cache, bankToZone, pointId, zone, graphStartPosition);
                points.add(graphPoint);
            }
        }
        
        //Add the additional points
        for (PointToZoneMapping pointToZone : pointsToZone) {
        	VfPoint graphPoint = getPointToZoneVfPoint(userContext, pointToZone, zone, graphStartPosition);
            points.add(graphPoint);
        }
        
        Ordering<VfPoint> positionOrderer = Ordering.natural().onResultOf(new Function<VfPoint, Double>() {
            @Override
            public Double apply(VfPoint from) {
                return from.getX();
            }
        });
        
        Collections.sort(points, positionOrderer);
        
        line.setZoneName(zone.getName());
        line.setPoints(points);
        line.setId(graphId.getAndIncrement());
        
        VfLineSettings lineSetting = new VfLineSettings(zoneLineColor, 0, bulletRoundOutlined, true, true, true, false);
        line.setSettings(lineSetting);
        
        return line;
    }
	
	private VfPoint getRegulatorVfPoint(YukonUserContext userContext, Zone zone, double graphStartPosition) {
		int regulatorId = zone.getRegulatorId();
        YukonPao regulatorPao = paoDao.getYukonPao(regulatorId);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(regulatorPao);
        LitePoint regulatorPoint = attributeService.getPointForAttribute(regulatorPao, BuiltInAttribute.VOLTAGE);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(regulatorPoint.getLiteID());
        String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
        String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String description = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndNoDist", 
        			pointValueString, displayablePao.getName(), timestamp, zone.getName());
        
        VfPoint regulatorGraphPoint = new VfPoint(description, graphStartPosition, pointValue.getValue());
        return regulatorGraphPoint;
	}
	
	private VfPoint getPointToZoneVfPoint(YukonUserContext userContext, PointToZoneMapping pointToZone, Zone zone, double graphStartPosition) {
		int pointId = pointToZone.getPointId();
		LitePoint litePoint = pointDao.getLitePoint(pointId);
		YukonPao yukonPao = paoDao.getYukonPao(litePoint.getPaobjectID());
		DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
		String litePointString = litePoint.getPointName();
		String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
		String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
		double distance = pointToZone.getDistance();
		String description = "";
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		
		if (distance != 0) {
			description = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.pointAndDist", 
					pointValueString, litePointString, displayablePao.getName(), timestamp, zone.getName(), distance);
		} else {
			description = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.pointAndNoDist", 
					pointValueString, litePointString, displayablePao.getName(), timestamp, zone.getName());
		}
		
		double xPosition = graphStartPosition + pointToZone.getGraphPositionOffset();
		VfPoint graphPoint = new VfPoint(description, xPosition, pointValue.getValue());
		return graphPoint;
	}
	
	private VfPoint getCapBankToZoneVfPoint(YukonUserContext userContext, CapControlCache cache, CapBankToZoneMapping bankToZone, Integer pointId, Zone zone, double graphStartPosition) {
		CapBankDevice bank = cache.getCapBankDevice(bankToZone.getDeviceId());
		YukonPao yukonPao = paoDao.getYukonPao(bank.getControlDeviceID());
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
		PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
		String pointValueString = pointFormattingService.getValueString(pointValue, Format.SHORT, userContext);
		String timestamp = dateFormattingService.format(pointValue.getPointDataTimeStamp(), DateFormatEnum.BOTH, userContext);
		double distance = bankToZone.getDistance();
		String description = "";
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		
		if (distance != 0) {
			description = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndDist", 
					pointValueString, displayablePao.getName(), timestamp, zone.getName(), distance);
		} else {
			description = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.voltProfileGraph.balloonText.noPointAndNoDist", 
					pointValueString, displayablePao.getName(), timestamp, zone.getName());
		}
		
		double xPosition = graphStartPosition + bankToZone.getGraphPositionOffset();
		VfPoint graphPoint = new VfPoint(description, xPosition, pointValue.getValue());
		return graphPoint;
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
