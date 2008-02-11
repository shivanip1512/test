package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;

public class RawPointHistoryModel extends BareReportModelBase<RawPointHistoryModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RawPointHistoryDao rphDao;
    private PaoDao paoDao;
    private PointDao pointDao;
    private DateFormattingService dateFormattingService;
    
    // inputs
    int pointId;
    Long startDate;
    Long stopDate;

    // member variables
    private static String title = "Archived Data";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    HashMap<Date, MaxValRow> maxValsByDate = new HashMap<Date, MaxValRow>();
    
    
    static public class ModelRow {
        public Date pointDataTimeStamp;
        public PointValueHolder valueHolder;
    }
    
    public void doLoadData() {
        
        Date startDateDate = new Date();
        startDateDate.setTime(startDate);
        
        Date stopDateDate = new Date();
        stopDateDate.setTime(stopDate);
        
        List<PointValueHolder> pvhList = rphDao.getPointData(pointId, startDateDate, stopDateDate);
        
//        Integer currentRowIdx = 0;
        for(PointValueHolder pvh : pvhList) {
            
            RawPointHistoryModel.ModelRow row = new RawPointHistoryModel.ModelRow();
            
            Date pointDataTimeStamp = pvh.getPointDataTimeStamp();
//            Double pointValue = pvh.getValue();
            
            row.pointDataTimeStamp = pointDataTimeStamp;
            row.valueHolder = pvh;
            data.add(row);
            
// CODE TO FIND MAX VALUE PER DAY - MAY BE USED TO CREATE HIGHLIGHTER FUNCTION
//            Date day = DateUtils.truncate(pointDataTimeStamp, Calendar.DATE);
//            
//            if (maxValsByDate.containsKey(day)) {
//                MaxValRow maxValRow = maxValsByDate.get(day);
//                if (pointValue >= maxValRow.getValue()) {
//                    maxValRow.setRowIdx(currentRowIdx);
//                    maxValRow.setValue(pointValue);
//                }
//            }
//            else {
//                MaxValRow maxValRow = new MaxValRow(currentRowIdx, pointValue);
//                maxValsByDate.put(day, maxValRow);
//            }
//            
//            currentRowIdx += 1;
        }

        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    private class MaxValRow {
        
        private Integer rowIdx;
        private Double value;
        
        public MaxValRow(Integer rowIdx, Double value) {
            this.rowIdx = rowIdx;
            this.value = value;
        }
        
        public Integer getRowIdx() {
            return rowIdx;
        }
        public void setRowIdx(Integer rowIdx) {
            this.rowIdx = rowIdx;
        }
        public Double getValue() {
            return value;
        }
        public void setValue(Double value) {
            this.value = value;
        }
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

        LitePoint litePoint = pointDao.getLitePoint(getPointId());
        int deviceId = litePoint.getPaobjectID();
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        
        info.put("Device Name", device.getPaoName());
        info.put("Point", litePoint.getPointName() +  " (id: " + Integer.toString(getPointId()) + ")");
        info.put("Start Date", dateFormattingService.formatDate(new Date(startDate), DateFormattingService.DateFormatEnum.BOTH, userContext));
        info.put("Stop Date", dateFormattingService.formatDate(new Date(stopDate), DateFormattingService.DateFormatEnum.BOTH, userContext));
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

}
