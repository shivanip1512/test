package com.cannontech.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class DeviceDaoImpl implements DeviceDao {

    private static final String litePaoSql = "SELECT y.PAObjectID, y.Category, y.PAOName, " + 
    "y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " + 
    "FROM yukonpaobject y left outer join devicedirectcommsettings d " + 
    "on y.paobjectid = d.deviceid " + 
    "left outer join devicecarriersettings DCS ON Y.PAOBJECTID = DCS.DEVICEID " + 
    "left outer join deviceroutes dr on y.paobjectid = dr.deviceid ";

    private final RowMapper litePaoRowMapper = new LitePaoRowMapper();
    
    private JdbcOperations jdbcOps;
    private PaoDao paoDao;
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

public YukonDevice getYukonDevice(int paoId) {
    return getYukonDevice(paoDao.getLiteYukonPAO(paoId));
}

public YukonDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
    YukonDevice device = new YukonDevice(yukonPAObject.getYukonID(),yukonPAObject.getType());
    return device;
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
 * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPaobjectByMeterNumber(java.lang.String)
 */
public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber)
{

    StringBuilder sql = new StringBuilder(litePaoSql);
    sql.append("left outer join devicemetergroup dmg on y.paobjectid=dmg.deviceid " +
               "where dmg.meternumber = ?");
    
    List<LiteYukonPAObject> paos = jdbcOps.query(sql.toString(), new Object[] {meterNumber}, litePaoRowMapper);
    
    return paos;
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
@Required
public void setPaoDao(PaoDao paoDao) {
    this.paoDao = paoDao;
}
@Required
public void setJdbcOps(JdbcOperations jdbcOps) {
    this.jdbcOps = jdbcOps;
}
}
