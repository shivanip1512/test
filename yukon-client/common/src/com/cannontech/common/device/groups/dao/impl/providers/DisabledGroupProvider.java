package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DisabledGroupProvider extends DeviceGroupProviderSqlBase {
    
    private PaoDao paoDao;
    private final static String disableFlag = "Y";

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        boolean result = isDeviceDisabled(device);
        return result;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    private boolean isDeviceDisabled(YukonDevice device) {
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        return devicePao.getDisableFlag().equals(disableFlag);
    }

    @Override
	public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
        String whereString = identifier + " IN ( " +
                            " SELECT ypo.PAObjectID " +
                            " FROM YukonPAObject ypo " + 
                            " JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DEVICEID " +
                            " WHERE ypo.DisableFlag = '" + disableFlag + "') "; 
        return whereString;
	    
	}
	
}
