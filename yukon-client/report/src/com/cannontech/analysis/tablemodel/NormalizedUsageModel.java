package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.cannontech.common.chart.service.impl.ChartNormalizedDeltaConverter;
import com.cannontech.common.chart.service.impl.ChartDeltaWaterConverter;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.user.YukonUserContext;

public class NormalizedUsageModel extends BareReportModelBase<NormalizedUsageModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RawPointHistoryDao rphDao;
    private PaoDao paoDao;
    private PointDao pointDao;
    private DateFormattingService dateFormattingService;
    
    // inputs
    int pointId;
    Long startDate;
    Long stopDate;
    Attribute attribute;

    // member variables
    private static String title = "Normalized Usage Data";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public Date pointDataTimeStamp;
        public PointValueHolder valueHolder;
    }
    
    public void doLoadData() {
        
        Date startDateDate = new Date();
        startDateDate.setTime(startDate);
        
        Date stopDateDate = new Date();
        stopDateDate.setTime(stopDate);
        
        // get raw data
        List<PointValueHolder> pvhList = rphDao.getPointData(pointId, startDateDate, stopDateDate);
        
        // convert raw PointValueHolder list into raw ChartValue list
        List<ChartValue<Double>> chartValueList = new ArrayList<ChartValue<Double>>();
        
        for(PointValueHolder pvh : pvhList) {
        	
        	ChartValue<Double> chartValue = new ChartValue<Double>();
        	chartValue.setId(pvh.getId());
        	chartValue.setTime(pvh.getPointDataTimeStamp().getTime());
        	chartValue.setValue(pvh.getValue());
        	
        	chartValueList.add(chartValue);
        }
        
        // normalize ChartValue list
        ChartDataConverter normalizer = getChartDataConverter();
        List<ChartValue<Double>> normalizedCharValueList = normalizer.convertValues(chartValueList, null);
        
        // convert normalized ChartValue back to POintValueHolder, add data to report rows
        for(ChartValue<Double> cv : normalizedCharValueList) {
        	
            int cvId = ((Long)cv.getId()).intValue();
            Date cvDate = new Date(cv.getTime());
            double cvValue = cv.getValue();
            
            PointValueHolder pvh = new SimplePointValue(cvId, cvDate, PointTypes.PULSE_ACCUMULATOR_POINT, cvValue);
            
            NormalizedUsageModel.ModelRow row = new NormalizedUsageModel.ModelRow();
            row.pointDataTimeStamp = cvDate;
            row.valueHolder = pvh;
            data.add(row);
        }

        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        LitePoint litePoint = pointDao.getLitePoint(getPointId());
        int deviceId = litePoint.getPaobjectID();
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        
        info.put("Device Name", device.getPaoName());
        info.put("Point", litePoint.getPointName() +  " (id: " + Integer.toString(getPointId()) + ")");
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

    /**
     * Returns a converter based on attribute.
     * Defaults to ChartNormalizedDeltaConverter.
     * @return
     */
    private ChartDataConverter getChartDataConverter() {
	    if (getAttribute() == BuiltInAttribute.USAGE_WATER) {
	    	return new ChartDeltaWaterConverter();
	    } if (getAttribute() == BuiltInAttribute.USAGE) {
	    	return new ChartNormalizedDeltaConverter();
	    } else {	//default to regular normalized converter
	    	return new ChartNormalizedDeltaConverter();
	    }
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
    
    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getPointId() {
        return pointId;
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


    public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
    
    public Attribute getAttribute() {
		return attribute;
	}
}
