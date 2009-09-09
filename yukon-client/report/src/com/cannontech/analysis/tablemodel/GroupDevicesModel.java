package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.user.YukonUserContext;

public class GroupDevicesModel extends BareReportModelBase<GroupDevicesModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupProviderDao = null;
    private PaoLoadingService paoLoadingService = null;
    
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
        
        Set<SimpleDevice> devicesToLoad;
        if (includeSubGroups) {
            devicesToLoad = deviceGroupProviderDao.getDevices(group);
        } else {
            devicesToLoad = deviceGroupProviderDao.getChildDevices(group);
        }
        
        List<DeviceCollectionReportDevice> deviceCollectionReportDevices = paoLoadingService.getDeviceCollectionReportDevices(devicesToLoad);

        for (DeviceCollectionReportDevice deviceReport : deviceCollectionReportDevices) {
            
            GroupDevicesModel.ModelRow row = new GroupDevicesModel.ModelRow();
            row.deviceName = deviceReport.getName();
            row.meterNumber = deviceReport.getMeterNumber();
            row.deviceType = deviceReport.getType();
            row.address = deviceReport.getAddress();
            row.route = deviceReport.getRoute();
            data.add(row);
        }
        
        CTILogger.info("Report Records Collected from Database: " + devicesToLoad.size());
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
    public void setDeviceGroupProviderDao(
            DeviceGroupProviderDao deviceGroupProviderDao) {
        this.deviceGroupProviderDao = deviceGroupProviderDao;
    }
    
    @Required
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }

    public boolean isIncludeSubGroups() {
        return includeSubGroups;
    }

    public void setIncludeSubGroups(boolean includeSubGroups) {
        this.includeSubGroups = includeSubGroups;
    }
}
