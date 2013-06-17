package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;

public class PowerFactorRPHModel extends BareReportModelBase<PowerFactorRPHModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RawPointHistoryDao rphDao;
    private PaoDao paoDao;
    private DateFormattingService dateFormattingService;
    private CapControlCache capControlCache = null;
    
    // inputs
    int targetId;
    Long startDate;
    Long stopDate;

    // member variables
    int powerFactorPointId;
    int estimatedPowerFactorPointId;
    private static String title = "Power Factor Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public Date pointDataTimeStamp;
        public PointValueHolder powerFactor;
        public PointValueHolder estimatedPowerFactor;
    }
    
    public void doLoadData() {
        
        Date startDateDate = new Date();
        startDateDate.setTime(startDate);
        
        Date stopDateDate = new Date();
        stopDateDate.setTime(stopDate);
        
        if(capControlCache.isSubBus(targetId)) {
    		SubBus subBus = capControlCache.getSubBus(targetId);
    		powerFactorPointId = subBus.getPowerFactorPointId();
    		estimatedPowerFactorPointId = subBus.getEstimatedPowerFactorPointId();
    	}
    	else if(capControlCache.isFeeder(targetId)) {
    		Feeder feeder = capControlCache.getFeeder(targetId);
    		powerFactorPointId = feeder.getPowerFactorPointID();		
    		estimatedPowerFactorPointId = feeder.getEstimatedPowerFactorPointID();
    	}
        
        // gather point data
        List<PointValueHolder> powerFactorPvh = rphDao.getPointData(powerFactorPointId, startDateDate, stopDateDate);
        List<PointValueHolder> estPowerFactorPvh = rphDao.getPointData(estimatedPowerFactorPointId, startDateDate, stopDateDate);
        
        Map<Date, PointValueHolder> powerFactorDateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> estPowerFactorDateMap = new HashMap<Date, PointValueHolder>();
        
        // make list of all point date stamps, sort them
        // make maps key'd by date for each point
        List<Date> allDates = new ArrayList<Date>();
        for(PointValueHolder pvh : powerFactorPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!powerFactorDateMap.containsKey(dateTime)) {
        		powerFactorDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = powerFactorDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			powerFactorDateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        for(PointValueHolder pvh : estPowerFactorPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!estPowerFactorDateMap.containsKey(dateTime)) {
        		estPowerFactorDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = estPowerFactorDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			estPowerFactorDateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        
        Collections.sort(allDates);
        
        // add data rows for each date
        for(Date d : allDates) {
        	PowerFactorRPHModel.ModelRow row = new PowerFactorRPHModel.ModelRow();
            row.pointDataTimeStamp = d;
            row.powerFactor = powerFactorDateMap.get(d);
            row.estimatedPowerFactor = estPowerFactorDateMap.get(d);
            data.add(row);
        }

        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        
        info.put("Name", paoDao.getYukonPAOName(getTargetId()));
        info.put("Start Date", dateFormattingService.format(new Date(startDate), DateFormattingService.DateFormatEnum.BOTH, userContext));
        info.put("Stop Date", dateFormattingService.format(new Date(stopDate), DateFormattingService.DateFormatEnum.BOTH, userContext));
        return info;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return title;
    }
    
    @Required
    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }
    
    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
	public void setCapControlCache(CapControlCache capControlCache) {
		this.capControlCache = capControlCache;
	}
    
    // INPUT SETTER-GETTERS
    public int getTargetId() {
		return targetId;
	}
    
    public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
    
    public Long getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    
    public void setStopDate(Long stopDate) {
        this.stopDate = stopDate;
    }
    
    public Long getStopDate() {
        return stopDate;
    }

}
