package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.user.YukonUserContext;

public class DeviceCollectionModel extends BareReportModelBase<DeviceCollectionModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupProviderDao = null;
    private PaoLoadingService paoLoadingService = null;
    
    // inputs
    private String tempGroupName;
    private String collectionDescription;

    // member variables
    private static String title = "Device Collection Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    static public class ModelRow {
        public String deviceName;
        public String meterNumber;
        public String deviceType;
        public String address;
        public String route;
    }
    
    public void doLoadData() {
        
        DeviceGroup group = deviceGroupService.resolveGroupName(tempGroupName);
        Set<SimpleDevice> devicesToLoad = deviceGroupProviderDao.getChildDevices(group);
        
        List<DeviceCollectionReportDevice> deviceCollectionReportDevices = paoLoadingService.getDeviceCollectionReportDevices(devicesToLoad);

        for (DeviceCollectionReportDevice deviceReport : deviceCollectionReportDevices) {
            
            DeviceCollectionModel.ModelRow row = new DeviceCollectionModel.ModelRow();
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
        
        info.put("Collection Description", collectionDescription);
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
    
    public String getTempGroupName() {
		return tempGroupName;
	}
    public void setTempGroupName(String tempGroupName) {
		this.tempGroupName = tempGroupName;
	}
    
    public String getCollectionDescription() {
		return collectionDescription;
	}
    public void setCollectionDescription(String collectionDescription) {
		this.collectionDescription = collectionDescription;
	}

    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Autowired
    public void setDeviceGroupProviderDao(
            DeviceGroupProviderDao deviceGroupProviderDao) {
        this.deviceGroupProviderDao = deviceGroupProviderDao;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
}
