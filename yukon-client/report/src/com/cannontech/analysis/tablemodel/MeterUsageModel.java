package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.google.common.collect.ListMultimap;

public class MeterUsageModel extends FilteredReportModelBase<MeterUsageModel.MeterUsageRow> {

    private RawPointHistoryDao rawPointHistoryDao;
    private MeterDao meterDao;

    private Logger log = YukonLogManager.getLogger(MeterUsageModel.class);

	/** A string for the title of the data */
	private static String title = "Meter Usage Report";
    
    /** Class fields */
    private List<MeterUsageRow> data = new ArrayList<MeterUsageRow>();
    private Attribute attribute;
    private boolean excludeDisabledDevices = false; 
    
    static public class MeterUsageRow {
        public String deviceName;
        public String meterNumber;
        public PaoType deviceType;
        public Date timestamp;
        public Double reading;
        public Double previousReading;
        public Double totalUsage;
    }
    
    @Override
    protected MeterUsageRow getRow(int rowIndex) {
    	return data.get(rowIndex);
    }
    
    @Override
    protected Class<MeterUsageRow> getRowClass() {
    	return MeterUsageRow.class;
    }
    
    @Override
    public int getRowCount() {
    	return data.size();
    }

    @Override
    public String getTitle() {
    	return title;
    }
    
    @Override
    public void doLoadData() {
    	
        Iterable<? extends YukonPao> devices = getYukonPaoList();
        List<YukonMeter> meters = meterDao.getMetersForYukonPaos(devices);
        Range<Date> dateRange = new Range<Date>( getStartDate(), false, getStopDate(), true);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> intermediateResults = 
        		rawPointHistoryDao.getAttributeData(meters, attribute,excludeDisabledDevices, 
						dateRange.translate(CtiUtilities.INSTANT_FROM_DATE),
						Order.FORWARD, null);
           
        for (YukonMeter meter : meters) {
        	Double previousReading = null;
            List<PointValueQualityHolder> values = intermediateResults.get(meter.getPaoIdentifier());
            for (PointValueQualityHolder pointValueHolder : values) {
            	MeterUsageRow meterUsage = new MeterUsageRow();
        	    meterUsage.deviceName = meter.getName();
        	    meterUsage.meterNumber = meter.getMeterNumber();
        	    meterUsage.deviceType = meter.getPaoType();
        	    meterUsage.timestamp = pointValueHolder.getPointDataTimeStamp();
        	    meterUsage.reading = pointValueHolder.getValue();

				if (previousReading != null) {
					meterUsage.previousReading = previousReading;
					meterUsage.totalUsage = meterUsage.reading - previousReading;
				} else {
					meterUsage.totalUsage = null;
					meterUsage.previousReading = null;
				}

				data.add(meterUsage);
				previousReading = meterUsage.reading;
            }
        }

        log.info("Report Records Collected from Database: " + data.size());
    }
    
	public void setExcludeDisabledDevices(boolean excludeDisabledDevices) {
		this.excludeDisabledDevices = excludeDisabledDevices;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
		this.rawPointHistoryDao = rawPointHistoryDao;
	}
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
		this.meterDao = meterDao;
	}
}
