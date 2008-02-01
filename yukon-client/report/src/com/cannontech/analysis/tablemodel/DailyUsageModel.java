package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.user.YukonUserContext;

public class DailyUsageModel extends BareReportModelBase<DailyUsageModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RawPointHistoryDao rphDao;
    private PaoDao paoDao;
    private PointDao pointDao;
    private DateFormattingService dateFormattingService;
    private DBPersistentDao dbPersistentDao;
    
    // inputs
    int pointId;
    Date startDate;
    Date stopDate;

    // member variables
    private static String title = "Daily Usage";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public Date date;
        public Double value;
    }
    
    public void doLoadData() {
        
        // load profile interval
        LitePoint litePoint = pointDao.getLitePoint(getPointId());
        int deviceId = litePoint.getPaobjectID();
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        Integer intervalRate = deviceLoadProfile.getLoadProfileDemandRate() / 60;
        
        // interval per hour?
        Integer intervalsPerHour = 60 / intervalRate;
        
        // truncate date range to nearest dates
        Date d1 = startDate;
        d1 = DateUtils.truncate(d1, Calendar.DATE);
        
        Date d2 = stopDate;
        d2 = DateUtils.truncate(d2, Calendar.DATE);
        d2 = DateUtils.addDays(d2, 1);
        
        Date todayDay = d1;
        Date prevDay = d1;
        
        // loop creating 1 hour ranges within the total date range
        Map<Date, Double> dayValues = new LinkedHashMap<Date, Double>();
        List<Double> hourValues = new ArrayList<Double>();
        while (d1.compareTo(d2) <= 0) {
        	
        	// if new day, average the hour values, add to days list, reset the hours array
        	todayDay = DateUtils.truncate(d1, Calendar.DATE);
        	if(todayDay.compareTo(prevDay) > 0) {
        		
        		Double dayValue = 0.0;
        		for (Double hourVal : hourValues) {
        			dayValue += hourVal;
        		}
        		dayValues.put(prevDay, dayValue);
        		
        		hourValues = new ArrayList<Double>();
        	}
        	
        	// create a 1 hour range
        	Date range1 = d1;
        	Date range2 = DateUtils.addHours(d1, 1);
        	
        	// get rph data for range
        	List<PointValueHolder> pvhList = rphDao.getPointData(pointId, range1, range2);
        	
        	Integer rphCount = pvhList.size();
        	Double hourTotal = 0.0;
        	Double hourAvg = 0.0;
        	
        	// sum values within hour
        	for (PointValueHolder pvh : pvhList) {
        		hourTotal += pvh.getValue();
        	}
        	
        	// calculate the "avg" hour value, add to hours list
        	// there should be as many rph values found as there are intervals per hour
        	// if not, create a fake hour value by dividing by what we have
        	if (rphCount < intervalsPerHour && rphCount > 0) {
    			hourAvg = hourTotal / (double)rphCount;
        	}
        	else if (intervalsPerHour > 0) {
    			hourAvg = hourTotal / (double)intervalsPerHour;
        	}
        	hourValues.add(hourAvg);
        	
        	// advance counter
        	prevDay = DateUtils.truncate(d1, Calendar.DATE);
        	d1 = range2;
        	
        }
        
        // add report row for each day's calculated value
        for (Date date : dayValues.keySet()) {
        	
        	DailyUsageModel.ModelRow row = new DailyUsageModel.ModelRow();
            row.date = date;
            row.value = dayValues.get(date);
            data.add(row);
        }

        CTILogger.info("Report Records Calculated: " + data.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        LitePoint litePoint = pointDao.getLitePoint(getPointId());
        int deviceId = litePoint.getPaobjectID();
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        
        info.put("Device Name", device.getPaoName());
        info.put("Point", litePoint.getPointName() +  " (id: " + getPointId() + ")");
        info.put("Start Date", dateFormattingService.formatDate(startDate, DateFormattingService.DateFormatEnum.BOTH, userContext));
        info.put("Stop Date", dateFormattingService.formatDate(stopDate, DateFormattingService.DateFormatEnum.BOTH, userContext));
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
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
		this.dbPersistentDao = dbPersistentDao;
	}
    
    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getPointId() {
        return pointId;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getStartDate() {
        return startDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }
    
    public Date getStopDate() {
        return stopDate;
    }


}
