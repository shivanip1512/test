package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
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
    public boolean isChildDevice(DeviceGroup group, SimpleDevice device) {
        boolean result = isDeviceDisabled(device);
        return result;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    private boolean isDeviceDisabled(SimpleDevice device) {
        LiteYukonPAObject devicePao = paoDao.getLiteYukonPAO(device.getDeviceId());
        return devicePao.getDisableFlag().equals(disableFlag);
    }

    @Override
	public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier, " IN ( ");
        sql.append("SELECT ypo.PAObjectID ");
        sql.append("FROM YukonPAObject ypo ");
        sql.append("JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DEVICEID ");
        sql.append("WHERE ypo.DisableFlag = ").appendArgument(disableFlag).append(") ");
        return sql;
	    
	}
	
}
