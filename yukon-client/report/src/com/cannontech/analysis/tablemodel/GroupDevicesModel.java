package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.user.YukonUserContext;

public class GroupDevicesModel extends BareReportModelBase<GroupDevicesModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private DeviceGroupService deviceGroupService = null;
    private MeterDao meterDao = null;
    
    // inputs
    private String groupName;
    private boolean includeSubGroups = false;

    // member variables
    private static String title = "Group Devices Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    
    static public class ModelRow {
        public String deviceName;
        public String meterNumber;
        public String deviceType;
        public String address;
        public String route;
    }
    
    public void doLoadData() {
        
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        
        List<Meter> deviceList;
        if (includeSubGroups) {
            deviceList = meterDao.getMetersByGroup(group);
        } else {
            deviceList = meterDao.getChildMetersByGroup(group);
        }
        
        for (Meter meter : deviceList) {
            
            GroupDevicesModel.ModelRow row = new GroupDevicesModel.ModelRow();
            row.deviceName = meter.getName();
            row.meterNumber = meter.getMeterNumber();
            row.deviceType = meter.getTypeStr();
            row.address = meter.getAddress();
            row.route = meter.getRoute();
            data.add(row);
        }
        
        CTILogger.info("Report Records Collected from Database: " + deviceList.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        
        info.put("Group", groupName);
        info.put("Include Sub Groups", BooleanUtils.toStringYesNo(includeSubGroups));
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
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    public boolean isIncludeSubGroups() {
        return includeSubGroups;
    }

    public void setIncludeSubGroups(boolean includeSubGroups) {
        this.includeSubGroups = includeSubGroups;
    }
}
