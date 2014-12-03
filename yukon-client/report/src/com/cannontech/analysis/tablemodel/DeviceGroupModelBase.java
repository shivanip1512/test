package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.service.PaoLoadingService;

public abstract class DeviceGroupModelBase extends BareReportModelBase<DeviceGroupModelBase.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private DeviceGroupProviderDao deviceGroupProviderDao = null;
    private PaoLoadingService paoLoadingService = null;
    
    // inputs
    protected DeviceGroup deviceGroup;

    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    static public class ModelRow {
        public String deviceName;
        public String meterNumber;
        public String deviceType;
        public String address;
        public String route;
    }
    
    protected abstract boolean isIncludeSubGroups();
    
    @Override
    public void doLoadData() {
        
        Set<SimpleDevice> devicesToLoad;
        if (isIncludeSubGroups()) {
            devicesToLoad = deviceGroupProviderDao.getDevices(deviceGroup);
        } else {
            devicesToLoad = deviceGroupProviderDao.getChildDevices(deviceGroup);
        }
        
        List<DeviceCollectionReportDevice> deviceCollectionReportDevices = paoLoadingService.getDeviceCollectionReportDevices(devicesToLoad);
        Collections.sort(deviceCollectionReportDevices);
        
        for (DeviceCollectionReportDevice deviceReport : deviceCollectionReportDevices) {
            
        	DeviceGroupModelBase.ModelRow row = new DeviceGroupModelBase.ModelRow();
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
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    public DeviceGroup getDeviceGroup() {
		return deviceGroup;
	}
    public void setDeviceGroup(DeviceGroup deviceGroup) {
		this.deviceGroup = deviceGroup;
	}
    
    @Autowired
    public void setDeviceGroupProviderDao(DeviceGroupProviderDao deviceGroupProviderDao) {
        this.deviceGroupProviderDao = deviceGroupProviderDao;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
}
