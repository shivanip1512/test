package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class KVarWattRPHModel extends BareReportModelBase<KVarWattRPHModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RawPointHistoryDao rphDao;
    private PaoDao paoDao;
    private DateFormattingService dateFormattingService;
    
    // inputs
    int targetId;
    int currentVarLoadPointId;
    int estimatedVarLoadPointId;
    int currentWattLoadPointId;
    Long startDate;
    Long stopDate;

    // member variables
    private static String title = "kVar / Watt Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public Date pointDataTimeStamp;
        public PointValueHolder currentVarLoad;
        public PointValueHolder estimatedVarLoad;
        public PointValueHolder currentWattLoad;
    }
    
    public void doLoadData() {
        
        Date startDateDate = new Date();
        startDateDate.setTime(startDate);
        
        Date stopDateDate = new Date();
        stopDateDate.setTime(stopDate);
        
        // gather point data
        List<PointValueHolder> currVarLoadPvh = rphDao.getPointData(currentVarLoadPointId, startDateDate, stopDateDate);
        List<PointValueHolder> estVarLoadPvh = rphDao.getPointData(estimatedVarLoadPointId, startDateDate, stopDateDate);
        List<PointValueHolder> currWattLoadPvh = rphDao.getPointData(currentWattLoadPointId, startDateDate, stopDateDate);
        
        Map<Date, PointValueHolder> currVarLoadDateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> estVarLoadDateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> currWattLoadDateMap = new HashMap<Date, PointValueHolder>();
        
        // make list of all point date stamps, sort them
        // make maps key'd by date for each point
        List<Date> allDates = new ArrayList<Date>();
        for(PointValueHolder pvh : currVarLoadPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!currVarLoadDateMap.containsKey(dateTime)) {
        		currVarLoadDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = currVarLoadDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			currVarLoadDateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        for(PointValueHolder pvh : estVarLoadPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!estVarLoadDateMap.containsKey(dateTime)) {
        		estVarLoadDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = estVarLoadDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			estVarLoadDateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        for(PointValueHolder pvh : currWattLoadPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!currWattLoadDateMap.containsKey(dateTime)) {
        		currWattLoadDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = currWattLoadDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			currWattLoadDateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        
        Collections.sort(allDates);
        
        // add data rows for each date
        for(Date d : allDates) {
        	KVarWattRPHModel.ModelRow row = new KVarWattRPHModel.ModelRow();
            row.pointDataTimeStamp = d;
            row.currentVarLoad = currVarLoadDateMap.get(d);
            row.estimatedVarLoad = estVarLoadDateMap.get(d);
            row.currentWattLoad = currWattLoadDateMap.get(d);
            data.add(row);
        }

        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(LiteYukonUser user) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        
        info.put("Name", paoDao.getYukonPAOName(getTargetId()));
        info.put("Start Date", dateFormattingService.formatDate(new Date(startDate), DateFormattingService.DateFormatEnum.BOTH, user));
        info.put("Stop Date", dateFormattingService.formatDate(new Date(stopDate), DateFormattingService.DateFormatEnum.BOTH, user));
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
    public int getCurrentVarLoadPointId() {
		return currentVarLoadPointId;
	}

	public void setCurrentVarLoadPointId(int currentVarLoadPointId) {
		this.currentVarLoadPointId = currentVarLoadPointId;
	}

	public int getEstimatedVarLoadPointId() {
		return estimatedVarLoadPointId;
	}

	public void setEstimatedVarLoadPointId(int estimatedVarLoadPointId) {
		this.estimatedVarLoadPointId = estimatedVarLoadPointId;
	}

	public int getCurrentWattLoadPointId() {
		return currentWattLoadPointId;
	}

	public void setCurrentWattLoadPointId(int currentWattLoadPointId) {
		this.currentWattLoadPointId = currentWattLoadPointId;
	}
	
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    
    public Long getStartDate() {
        return startDate;
    }

    public void setStopDate(Long stopDate) {
        this.stopDate = stopDate;
    }
    
    public Long getStopDate() {
        return stopDate;
    }

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

}
