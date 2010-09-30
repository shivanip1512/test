package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.model.Zone;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphData;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfLineData;
import com.cannontech.web.capcontrol.ivvc.models.VfLineSettings;
import com.cannontech.web.capcontrol.ivvc.models.VfPoint;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.ivvc.service.ZoneService;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.google.common.collect.Lists;

public class VoltageFlatnessGraphServiceImpl implements VoltageFlatnessGraphService {
    
    private AttributeService attributeService;
    private PaoDao paoDao;
    private DynamicDataSource dynamicDataSource;
    private StrategyDao strategyDao;
    private ZoneService zoneService;
    private FilterCacheFactory filterCacheFactory;
    
    //Change these to alter the color on the chart.
    private final String zoneLineColor = "000000";
    private final String outsideRangeColor = "CCCCCC";
    
    //Every line must have a unique id. 
    private final int lowerOutsideRangeBoxId = -2;
    private final int upperOutsideRangeBoxId = -3;
    
    @Override
    public VfGraphData getSubBusGraphData(LiteYukonUser user, int subBusId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        return buildSubBusGraphData(subBusId,cache);
    }

    @Override
    public VfGraphSettings getSubBusGraphSettings(LiteYukonUser user, int subBusId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        return buildSubBusGraphSettings(subBusId,cache);
    }

    @Override
    public VfGraphData getZoneGraphData(LiteYukonUser user, int zoneId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        return buildZoneGraphData(zoneId,cache);
    }

    @Override
    public VfGraphSettings getZoneGraphSettings(LiteYukonUser user, int zoneId) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        return buildZoneGraphSettings(zoneId,cache);
    }
    
    private VfGraphData buildZoneGraphData(int zoneId, CapControlCache cache) {
        List<VfLineData> lines = Lists.newArrayList();
        
        int xPosition = 1;
        VfLineData line = buildLineDataForZone(cache, zoneId,xPosition);
        lines.add(line);
        xPosition = line.getPoints().size();
        
        List<VfLineData> limitLines = buildBoundaryDataByZoneId(cache, zoneId, xPosition);
        lines.addAll(limitLines);
        
        //Make graph object and add the lines.
        VfGraphData graph = new VfGraphData();
        graph.setLines(lines);
        
        return graph;
    }
    
    private VfGraphData buildSubBusGraphData(int subBusId, CapControlCache cache) {
        List<VfLineData> lines = Lists.newArrayList();
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        
        int xPosition = 1;
        
        //Build A line for each zone on the bus.
        for (Zone zone : zones) {
            VfLineData line = buildLineDataForZone(cache, zone.getId(),xPosition);
            xPosition += line.getPoints().size();
            lines.add(line);
        }

        List<VfLineData> limitLines = buildBoundaryDataBySubBusId(cache, subBusId, xPosition);
        lines.addAll(limitLines);
        
        //Make graph object and add the lines.
        VfGraphData graph = new VfGraphData();
        graph.setLines(lines);
        
        return graph;
    }

    private VfLineData buildLineDataForZone(CapControlCache cache, int zoneId, int xPos) {
        VfLineData line = new VfLineData();
        List<VfPoint> points = Lists.newArrayList();
        List<Integer> bankIds = zoneService.getCapBankIdsForZoneId(zoneId);
        
        //Add a point on the line for each bank on the zone.
        for (Integer bankId : bankIds) {
            CapBankDevice bank = cache.getCapBankDevice(bankId);
            
            LiteYukonPAObject pao = paoDao.getLiteYukonPAO(bank.getControlDeviceID());
            
            //Using the Voltage attribute to get the bank voltage. This gets the voltage on the CBC.
            //If we want to change to a different point, do so here.
            LitePoint point = attributeService.getPointForAttribute(pao, BuiltInAttribute.VOLTAGE);
            PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(point.getLiteID());
            
            VfPoint graphPoint = new VfPoint(xPos++,pointValue.getValue());
            
            points.add(graphPoint);
        }
        
        line.setPoints(points);
        line.setId(zoneId); 
        
        return line;
    }
    
    /**
     * The boundaries are based on the strategy settings. We want to make a highlighted
     *  area to indicate voltage points that are out of bounds on the strategy.
     *  
     * @param cache
     * @param zoneId
     * @param points
     * @return
     */
    private List<VfLineData> buildBoundaryDataByZoneId(CapControlCache cache, int zoneId, int points) {
        Zone zone = zoneService.getZoneById(zoneId);
        return buildBoundaryDataBySubBusId(cache,zone.getSubstationBusId(),points);
    }

    /**
     * Generates the boundary areas based on te strategy attached to the subbus. 
     * We will look for the upper and lower volt limits to indicate on the graph.
     * 
     * @param cache
     * @param subBusId
     * @param points
     * @return
     */
    private List<VfLineData> buildBoundaryDataBySubBusId(CapControlCache cache, int subBusId, int points) {
        CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());
        //Default Upper value
        double yUpper = Double.parseDouble(TargetSettingType.UPPER_VOLT_LIMIT.getDefaultValue());
        for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
            if ( TargetSettingType.UPPER_VOLT_LIMIT.getDisplayName().equals((setting.getName())) ) {
                yUpper = Double.parseDouble(setting.getPeakValue());
            }
        }
        
        //Default lower value
        double yLower = Double.parseDouble(TargetSettingType.LOWER_VOLT_LIMIT.getDefaultValue());;
        for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
            if ( TargetSettingType.LOWER_VOLT_LIMIT.getDisplayName().equals((setting.getName())) ) {
                yLower = Double.parseDouble(setting.getPeakValue());
            }
        }
        List<VfLineData> lines = Lists.newArrayList();
        
        lines.add(buildUpperOutsideRangeData(yUpper,yLower,upperOutsideRangeBoxId));
        lines.add(buildLowerOutsideRangeData(yUpper,yLower,lowerOutsideRangeBoxId));
        
        return lines;
    }

    private VfLineData buildUpperOutsideRangeData(double yUpper, double yLower, int id) {
        VfLineData box = new VfLineData();
        List<VfPoint> points = Lists.newArrayList();
        
        //Making the box extremely big so we will always be highlighted
        points.add(new VfPoint(1000,1000));
        points.add(new VfPoint(-10,1000));
        points.add(new VfPoint(-10,yUpper));
        points.add(new VfPoint(1000,yUpper));
        
        box.setPoints(points); 
        box.setId(id);
        return box;
    }
    
    private VfLineData buildLowerOutsideRangeData(double yUpper, double yLower, int id) {
        VfLineData box = new VfLineData();
        List<VfPoint> points = Lists.newArrayList();

        //Making the box extremely big so we will always be highlighted
        points.add(new VfPoint(1000,yLower));
        points.add(new VfPoint(-10,yLower));
        points.add(new VfPoint(-10,-1000));
        points.add(new VfPoint(1000,-1000));
        
        box.setPoints(points);
        box.setId(id);
        return box;
    }
    
    private VfGraphSettings buildZoneGraphSettings(int zoneId, CapControlCache cache) {
        List<VfLineSettings> lines = Lists.newArrayList();
        
        VfLineSettings line = new VfLineSettings(zoneId,zoneLineColor,0,true);  
        lines.add(line);
        
        List<VfLineSettings> limitLines = buildBoundarySettings();
        lines.addAll(limitLines);
        
        VfGraphSettings graphSettings = new VfGraphSettings();
        graphSettings.setLines(lines);
        
        return graphSettings;
    }
    
    private VfGraphSettings buildSubBusGraphSettings(int subBusId, CapControlCache cache) {
        List<VfLineSettings> lines = Lists.newArrayList();
        List<Zone> zones = zoneService.getZonesBySubBusId(subBusId);
        
        for (Zone zone : zones) {
            VfLineSettings line = new VfLineSettings(zone.getId(),zoneLineColor,0,true);  
            lines.add(line);
        }
        
        List<VfLineSettings> limitLines = buildBoundarySettings();
        lines.addAll(limitLines);
        
        VfGraphSettings graphSettings = new VfGraphSettings();
        graphSettings.setLines(lines);
        
        return graphSettings;
    }

    private List<VfLineSettings> buildBoundarySettings() {
        List<VfLineSettings> lines = Lists.newArrayList();
        //Important to set the boolean minMax to false. If it is true the amCharts will use these lines
        //  to decide the viewable area.
        lines.add(new VfLineSettings(upperOutsideRangeBoxId,outsideRangeColor,25,false));
        lines.add(new VfLineSettings(lowerOutsideRangeBoxId,outsideRangeColor,25,false));
        return lines;
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
}
