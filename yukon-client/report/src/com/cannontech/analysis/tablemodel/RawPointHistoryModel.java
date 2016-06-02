package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;

public class RawPointHistoryModel extends BareReportModelBase<RawPointHistoryModel.ModelRow> implements ReportModelMetaInfo {

    // dependencies
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private PointDao pointDao;
    @Autowired private DateFormattingService dateFormattingService;

    // inputs
    int pointId;
    Long startDate;
    Long stopDate;

    // member variables
    private static String title = "Archived Data";
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
        
        List<PointValueHolder> pvhList = rphDao.getPointData(pointId, startDateDate, stopDateDate);

        for(PointValueHolder pvh : pvhList) {
            RawPointHistoryModel.ModelRow row = new RawPointHistoryModel.ModelRow();
            Date pointDataTimeStamp = pvh.getPointDataTimeStamp();
            row.pointDataTimeStamp = pointDataTimeStamp;
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
        LiteYukonPAObject device = DefaultDatabaseCache.getInstance().getAllPaosMap().get(deviceId);

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