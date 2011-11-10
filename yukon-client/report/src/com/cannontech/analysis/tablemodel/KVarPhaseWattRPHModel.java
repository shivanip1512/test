package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;

public class KVarPhaseWattRPHModel extends BareReportModelBase<KVarPhaseWattRPHModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RawPointHistoryDao rphDao;
    private PaoDao paoDao;
    private DateFormattingService dateFormattingService;
    private CapControlCache capControlCache = null;
    private SubstationBusDao substationBusDao = null;
	private FeederDao feederDao = null;
	
    // inputs
    int targetId;
    Long startDate;
    Long stopDate;

    // member variables
    int phaseAPointId;
    int phaseBPointId;
    int phaseCPointId;
    int estimatedVarLoadPointId;
    int currentWattLoadPointId;
    private static String title = "kVar Phase / Watt Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public Date pointDataTimeStamp;
        public PointValueHolder phaseA;
        public PointValueHolder phaseB;
        public PointValueHolder phaseC;
        public PointValueHolder estimatedVarLoad;
        public PointValueHolder currentWattLoad;
    }
    
    public void doLoadData() {
        
        Date startDateDate = new Date();
        startDateDate.setTime(startDate);
        
        Date stopDateDate = new Date();
        stopDateDate.setTime(stopDate);
        
        if(capControlCache.isSubBus(targetId)) {
        	SubBus subBus_cache = capControlCache.getSubBus(targetId);
        	SubstationBus subBus_dao = substationBusDao.getById(targetId);
        	
        	phaseAPointId = subBus_cache.getCurrentVarLoadPointID();
        	phaseBPointId = subBus_dao.getPhaseb();
        	phaseCPointId = subBus_dao.getPhasec();
        	estimatedVarLoadPointId = subBus_cache.getEstimatedVarLoadPointID();
        	currentWattLoadPointId = subBus_cache.getCurrentWattLoadPointID();
        }
        else if(capControlCache.isFeeder(targetId)) {
        	com.cannontech.message.capcontrol.streamable.Feeder feeder_cache = capControlCache.getFeeder(targetId);
        	com.cannontech.capcontrol.model.Feeder feeder_dao = feederDao.getById(targetId);
        	
        	phaseAPointId = feeder_cache.getCurrentVarLoadPointID();
        	phaseBPointId = feeder_dao.getPhaseb();
        	phaseCPointId = feeder_dao.getPhasec();
        	estimatedVarLoadPointId = feeder_cache.getEstimatedVarLoadPointID();
        	currentWattLoadPointId = feeder_cache.getCurrentWattLoadPointID();
        }
        
        // gather point data
        List<PointValueHolder> phaseAPvh = rphDao.getPointData(phaseAPointId, startDateDate, stopDateDate);
        List<PointValueHolder> phaseBPvh = rphDao.getPointData(phaseBPointId, startDateDate, stopDateDate);
        List<PointValueHolder> phaseCPvh = rphDao.getPointData(phaseCPointId, startDateDate, stopDateDate);
        List<PointValueHolder> estVarLoadPvh = rphDao.getPointData(estimatedVarLoadPointId, startDateDate, stopDateDate);
        List<PointValueHolder> currWattLoadPvh = rphDao.getPointData(currentWattLoadPointId, startDateDate, stopDateDate);
        
        Map<Date, PointValueHolder> phaseADateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> phaseBDateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> phaseCDateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> estVarLoadDateMap = new HashMap<Date, PointValueHolder>();
        Map<Date, PointValueHolder> currWattLoadDateMap = new HashMap<Date, PointValueHolder>();
        
        // make list of all point date stamps, sort them
        // make maps key'd by date for each point
        List<Date> allDates = new ArrayList<Date>();
        for(PointValueHolder pvh : phaseAPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!phaseADateMap.containsKey(dateTime)) {
        		phaseADateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = phaseADateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			phaseADateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        for(PointValueHolder pvh : phaseBPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!phaseBDateMap.containsKey(dateTime)) {
        		phaseBDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = phaseBDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			phaseBDateMap.put(dateTime, pvh);
        		}
        	}
        	
        	if(!allDates.contains(dateTime)) {
        		allDates.add(dateTime);
        	}
        }
        for(PointValueHolder pvh : phaseCPvh) {
        	Date dateTime = pvh.getPointDataTimeStamp();
        	
        	if(!phaseCDateMap.containsKey(dateTime)) {
        		phaseCDateMap.put(dateTime, pvh);
        	}
        	else {
        		PointValueHolder maxPvh = phaseCDateMap.get(dateTime);
        		if(pvh.getValue() > maxPvh.getValue()) {
        			phaseCDateMap.put(dateTime, pvh);
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
        	KVarPhaseWattRPHModel.ModelRow row = new KVarPhaseWattRPHModel.ModelRow();
            row.pointDataTimeStamp = d;
            row.phaseA = phaseADateMap.get(d);
            row.phaseB = phaseBDateMap.get(d);
            row.phaseC = phaseCDateMap.get(d);
            row.estimatedVarLoad = estVarLoadDateMap.get(d);
            row.currentWattLoad = currWattLoadDateMap.get(d);
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
	
    @Required
    public void setSubstationBusDao(SubstationBusDao substationBusDao) {
		this.substationBusDao = substationBusDao;
	}

    @Required
	public void setFeederDao(FeederDao feederDao) {
		this.feederDao = feederDao;
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
