package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public class GroupDevicesModel extends BareReportModelBase<GroupDevicesModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    private MeterDao meterDao = null;
    
    // inputs
    String groupName;

    // member variables
    private static String title = "Group Devices Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public String name;
        public String meterNumber;
        public String deviceName;
        public String deviceType;
        public String address;
        public String route;
    }
    
    public void doLoadData() {
        
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        List<YukonDevice> deviceList = deviceGroupDao.getChildDevices(group);
        
        for (YukonDevice device : deviceList) {
            
            int deviceId = device.getDeviceId();
            Meter meter = meterDao.getForId(deviceId);
            
            GroupDevicesModel.ModelRow row = new GroupDevicesModel.ModelRow();
            row.name = meterDao.getFormattedDeviceName(meter);
            row.meterNumber = meter.getMeterNumber();
            row.deviceName = meter.getName();
            row.deviceType = meter.getTypeStr();
            row.address = meter.getAddress();
            row.route = meter.getRoute();
            data.add(row);
        }
        
        CTILogger.info("Report Records Collected from Database: " + deviceList.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(LiteYukonUser user) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        
        info.put("Group", groupName);
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
    
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
}
