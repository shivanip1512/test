package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.model.ZigbeeControlledDevice;
import com.cannontech.user.YukonUserContext;

public class ZigbeeControlledDeviceModel extends BareDatedReportModelBase<ZigbeeControlledDeviceModel.ModelRow> implements UserContextModelAttributes{

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private YukonUserContext userContext;
    
    private ZigbeeControlEventDao zigbeeControlEventDao = YukonSpringHook.getBean("zigbeeControlEventDao", ZigbeeControlEventDao.class);
    private DateFormattingService dateFormattingService = YukonSpringHook.getBean("dateFormattingService", DateFormattingService.class);
    
    public class ModelRow {
        public Integer eventId;
        public String deviceName;
        public String loadGroupName;
        public String ack;
        public String start;
        public String stop;
        public String canceled;
    }
    
    public void doLoadData() {
        List<ZigbeeControlledDevice> eventDevices = zigbeeControlEventDao.getDeviceEvents(getStartDateAsInstant().toInstant(),getStopDateAsInstant().toInstant());
        
        for (ZigbeeControlledDevice device:eventDevices) {
            ModelRow modelRow = new ModelRow();
            
            modelRow.eventId = device.getEventId();
            modelRow.deviceName = device.getDeviceName();
            modelRow.loadGroupName = device.getLoadGroupName();
            modelRow.ack = device.isAck()?"Yes":"No";

            Instant startTime = device.getStart();
            if (startTime != null) {
                modelRow.start = dateFormattingService.format(startTime, DateFormatEnum.BOTH, userContext);
            } else {
                modelRow.start = CtiUtilities.STRING_DASH_LINE;
            }
            
            Instant stopTime = device.getStop();
            if (stopTime != null) {
                modelRow.stop = dateFormattingService.format(stopTime, DateFormatEnum.BOTH, userContext);
            } else {
                modelRow.stop = CtiUtilities.STRING_DASH_LINE;
            }
            Boolean canceled = device.isCanceled();
            if (canceled != null) {
                modelRow.canceled = canceled?"Yes":"No";
            } else {
                modelRow.canceled = CtiUtilities.STRING_DASH_LINE;
            }
            
            data.add(modelRow);
        }
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getTitle() {
        return "ZigBee Controlled Device Report";
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
}
