package com.cannontech.core.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.roles.yukon.ConfigurationRole;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class DeviceDaoImpl implements DeviceDao {
    
    private PaoDao paoDao;
    private RoleDao roleDao;
    private IDatabaseCache databaseCache;
        
/**
 * PointFuncs constructor comment.
 */
public DeviceDaoImpl() {
	super();
}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getLiteDevice(int)
 */
public LiteYukonPAObject getLiteDevice(final int deviceID) {
	return paoDao.getLiteYukonPAO( deviceID );
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getLiteDeviceMeterNumber(int)
 */
public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID)
{
List allDevMtrGrps = databaseCache.getAllDeviceMeterGroups();
    
    LiteDeviceMeterNumber ldmn = null;
    for (int i = 0; i < allDevMtrGrps.size(); i++)
    {
        ldmn = (LiteDeviceMeterNumber)allDevMtrGrps.get(i);
        if (ldmn.getDeviceID() == deviceID)
            return ldmn;
    }
    return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPaobjectByMeterNumber(java.lang.String)
 */
public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber)
{
	List allDevMtrGrps = databaseCache.getAllDeviceMeterGroups();
    
	LiteDeviceMeterNumber ldmn = null;
	for (int i = 0; i < allDevMtrGrps.size(); i++)
	{
		ldmn = (LiteDeviceMeterNumber)allDevMtrGrps.get(i);
		if (ldmn.getMeterNumber().equals(meterNumber))
		{
			LiteYukonPAObject lPao = (LiteYukonPAObject)databaseCache.getAllPAOsMap().get(new Integer(ldmn.getDeviceID()));
			return lPao;
		}
	}
	return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPaobjectByDeviceName(java.lang.String)
 */
public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName)
{
    List allDevices = databaseCache.getAllDevices();
    
    LiteYukonPAObject lPao = null;
    for (int i = 0; i < allDevices.size(); i++)
    {
        lPao = (LiteYukonPAObject)allDevices.get(i);
        if (lPao.getPaoName().equals(deviceName))
            return lPao;
    }
    return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPAObject(java.lang.String, int, int, int)
 */
public LiteYukonPAObject getLiteYukonPAObject(String deviceName, int category, int paoClass, int type)
{
    List allDevices = databaseCache.getAllDevices();
    for (Object obj : allDevices) {
        LiteYukonPAObject lPao = (LiteYukonPAObject) obj;
        boolean foundMatch = true;
        foundMatch &= lPao.getPaoName().equals(deviceName);
        foundMatch &= lPao.getCategory() == category;
        foundMatch &= lPao.getPaoClass() == paoClass;
        foundMatch &= lPao.getType() == type;
        if (foundMatch) {
            return lPao;
        }
    }
    return null;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPAObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
 */
public LiteYukonPAObject getLiteYukonPAObject(String deviceName, String category, String paoClass, String type)
{
    int categoryInt = PAOGroups.getCategory(category);
    int paoClassInt = PAOGroups.getPAOClass(category, paoClass);
    int typeInt = PAOGroups.getPAOType(category, type);
    return getLiteYukonPAObject(deviceName, categoryInt, paoClassInt, typeInt);
}

public String getFormattedDeviceName(LiteYukonPAObject device) {
    // the formatting string could be from a role property
    TemplateProcessor templateProcessor = new SimpleTemplateProcessor();
    String formattingStr = roleDao.getGlobalPropertyValue(ConfigurationRole.DEVICE_DISPLAY_TEMPLATE);
    Validate.notNull(formattingStr, "Device display template role property does not exist.");
    boolean needMeterNumber = templateProcessor.contains(formattingStr, "meterNumber");
    Map<String, String> values = new HashMap<String, String>(6);
    if (needMeterNumber) {
        LiteDeviceMeterNumber liteDeviceMeterNumber = getLiteDeviceMeterNumber(device.getLiteID());
        
        String meterNumber;
        if (liteDeviceMeterNumber == null) {
            meterNumber = "n/a";
        } else {
            meterNumber = liteDeviceMeterNumber.getMeterNumber();
        }
        values.put("meterNumber", meterNumber);
    }
    values.put("name", device.getPaoName());
    values.put("description", device.getPaoDescription());
    values.put("id", Integer.toString(device.getLiteID()));
    values.put("address", Integer.toString(device.getAddress()));
    String result = templateProcessor.process(formattingStr, values);
    return result;
}

/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getDevicesByPort(int)
 */
public List getDevicesByPort(int portId)
{
    List devices = databaseCache.getDevicesByCommPort(portId);
    return devices;  
}
/* (non-Javadoc)
 * @see com.cannontech.core.dao.DeviceDao#getDevicesByDeviceAddress(java.lang.Integer, java.lang.Integer)
 */
public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
    List devicesByAddress = databaseCache.getDevicesByDeviceAddress(masterAddress, slaveAddress);
    return devicesByAddress;
}
public void setDatabaseCache(IDatabaseCache databaseCache) {
    this.databaseCache = databaseCache;
}
@Required
public void setPaoDao(PaoDao paoDao) {
    this.paoDao = paoDao;
}
@Required
public void setRoleDao(RoleDao roleDao) {
    this.roleDao = roleDao;
}
}
