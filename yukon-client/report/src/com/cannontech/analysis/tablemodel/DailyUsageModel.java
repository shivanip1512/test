package com.cannontech.analysis.tablemodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.user.YukonUserContext;

public class DailyUsageModel extends BareReportModelBase<DailyUsageModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private PointDao pointDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DBPersistentDao dbPersistentDao;

    // inputs
    int pointId;
    Date startDate;
    Date stopDate;

    // member variables
    private static String title = "Daily Usage";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    // skipping the usual format using the DailyUsageReportLayoutData so that we can get ricky
    // and display "---" for no value days for this report
    private DecimalFormat valueFormatter = new DecimalFormat("#.###");
    
    static public class ModelRow {
        public Date date;
        public String value;
        public String units;
    }
    
    public void doLoadData() {
        
        // load profile interval
        LitePoint litePoint = pointDao.getLitePoint(getPointId());
        int deviceId = litePoint.getPaobjectID();
        LiteYukonPAObject device = DefaultDatabaseCache.getInstance().getAllPaosMap().get(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        Integer intervalRate = deviceLoadProfile.getLoadProfileDemandRate() / 60;
        
        // interval per hour?
        Integer intervalsPerHour = 60 / intervalRate;
        
        // truncate date range to nearest dates
        Date d1 = DateUtils.truncate(startDate, Calendar.DATE);
        
        Date d2 = DateUtils.truncate(stopDate, Calendar.DATE);
        d2 = DateUtils.addDays(d2, 1);
        d2 = DateUtils.addHours(d2, -1);
        
        Date todayDay = d1;
        Date prevDay = d1;
        
        // loop creating 1 hour ranges within the total date range
        Map<Date, Double> dayValues = new LinkedHashMap<Date, Double>();
        List<Double> hourValues = null;
        while (d1.compareTo(d2) <= 0) {

            // if new day, average the hour values, add to days list, reset the hours array
            todayDay = DateUtils.truncate(d1, Calendar.DATE);
            if(todayDay.compareTo(prevDay) > 0) {

                // must have complete 24 hours worth of data
                // hours in which there was too much/little data have not ben added
                // to the hoursValues list, making the entire day "incomplete"
                if (hourValues != null && hourValues.size() == 24) {

                    Double dayValue = 0.0;
                    for (Double hourVal : hourValues) {
                        dayValue += hourVal;
                    }
                    dayValues.put(prevDay, dayValue);
                }
                else {
                    dayValues.put(prevDay, null);
                }

                hourValues = null;
            }

            // create a 1 hour range
            Date range1 = d1;
            Date range2 = DateUtils.addHours(d1, 1);

            Range<Date> dateRange = new Range<Date>(range1, true, range2, false);
            // get rph data for range
            List<PointValueHolder> pvhList = rphDao.getPointData(pointId,
                                                                 dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD);

            Integer rphCount = pvhList.size();
            Double hourTotal = 0.0;
            Double hourAvg = 0.0;

            // sum values within hour
            for (PointValueHolder pvh : pvhList) {
                hourTotal += pvh.getValue();
            }

            // hour average values will only be added to the hoursValues if there is the correct
            // amount of values retrieved, based on the current intervalRate
            if (pvhList.size() > 0) {

                if (rphCount == intervalsPerHour)  {

                    if (hourValues == null) {
                        hourValues = new ArrayList<Double>();
                    }

                    hourAvg = hourTotal / (double)intervalsPerHour;
                    hourValues.add(hourAvg);
                }
            }

            // advance counter
            prevDay = DateUtils.truncate(d1, Calendar.DATE);
            d1 = range2;

        }
        
        // add report row for each day's calculated value
        for (Date date : dayValues.keySet()) {

            DailyUsageModel.ModelRow row = new DailyUsageModel.ModelRow();
            row.date = date;
            
            Double dayValue = dayValues.get(date);
            if (dayValue != null) {
                row.value = valueFormatter.format(dayValues.get(date));
                row.units = "kWh";
            }
            else {
                row.value = "---";
                row.units = "";
            }
            data.add(row);
        }

        CTILogger.info("Report Records Calculated: " + data.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        LitePoint litePoint = pointDao.getLitePoint(getPointId());
        int deviceId = litePoint.getPaobjectID();
        LiteYukonPAObject device = DefaultDatabaseCache.getInstance().getAllPaosMap().get(deviceId);

        info.put("Device Name", device.getPaoName());
        info.put("Point", litePoint.getPointName() +  " (id: " + getPointId() + ")");
        info.put("Start Date", dateFormattingService.format(startDate, DateFormattingService.DateFormatEnum.BOTH, userContext));
        info.put("Stop Date", dateFormattingService.format(stopDate, DateFormattingService.DateFormatEnum.BOTH, userContext));
        info.put("Note", "Values of \"---\" indicate that there is incomplete data for that date.");
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
