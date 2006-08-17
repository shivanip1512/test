package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.CBCPointTimestampParams;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class DeviceDaoImpl implements DeviceDao {
    
    private PaoDao paoDao;
    private IDatabaseCache databaseCache;
    private JdbcOperations jdbcOps;    
    
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
public void setPaoDao(PaoDao paoDao) {
    this.paoDao = paoDao;
}
 
public void setJdbcOps(JdbcOperations jdbcOps) {
    this.jdbcOps = jdbcOps;
}


}
