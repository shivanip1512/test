package com.cannontech.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class DeviceDaoImpl implements DeviceDao, InitializingBean {

    private static final String litePaoSql = "SELECT y.PAObjectID, y.Category, y.PAOName, " + 
    "y.Type, y.PAOClass, y.Description, y.DisableFlag, d.PORTID, dcs.ADDRESS, dr.routeid " + 
    "FROM yukonpaobject y left outer join devicedirectcommsettings d " + 
    "on y.paobjectid = d.deviceid " + 
    "left outer join devicecarriersettings DCS ON Y.PAOBJECTID = DCS.DEVICEID " + 
    "left outer join deviceroutes dr on y.paobjectid = dr.deviceid ";

    private final RowMapper litePaoRowMapper = new LitePaoRowMapper();
    private YukonDeviceRowMapper yukonDeviceRowMapper = null;
    
    private JdbcOperations jdbcOps;
    private PaoDao paoDao;
    private IDatabaseCache databaseCache;
    private DBPersistentDao dbPersistantDao;
    private PaoGroupsWrapper paoGroupsWrapper;
    private MeterDao meterDao;
        
/**
 * PointFuncs constructor comment.
 */
public DeviceDaoImpl() {
	super();
}

    public void afterPropertiesSet() {
        
        this.yukonDeviceRowMapper = new YukonDeviceRowMapper(paoGroupsWrapper);
    }

    public void disableDevice(YukonDevice device) {
        this.enableDisableDevice(device, "Y");
        
    }
    public void enableDevice(YukonDevice device) {
        this.enableDisableDevice(device, "N");
    }
    
    private void enableDisableDevice(YukonDevice device, String disableFlag) {
        
        String sql = "UPDATE yukonpaobject SET disableflag = ? WHERE paobjectid = ?";
        jdbcOps.update(sql, new Object[] { disableFlag, device.getDeviceId() });

        processDeviceUpdateDBChange(device);
    }
    
    public void removeDevice(YukonDevice device) {
        
        LiteYukonPAObject liteDevice = this.getLiteDevice(device.getDeviceId());
        DBPersistent persistent = dbPersistantDao.retrieveDBPersistent(liteDevice);
        dbPersistantDao.performDBChange(persistent, Transaction.DELETE);
        
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

/**
 * A leaner version of getYukonDevice()
 */
public YukonDevice getYukonDeviceObjectById(int deviceId) {
    
    String sql = "SELECT ypo.PAObjectID, ypo.Type FROM YukonPaObject ypo WHERE ypo.PAObjectID = ?";
    YukonDevice device = (YukonDevice)jdbcOps.queryForObject(sql, new Object[] {deviceId}, this.yukonDeviceRowMapper);
    return device;
}

public YukonDevice getYukonDeviceObjectByName(String name) {
    
    String sql = "SELECT ypo.PAObjectID, ypo.Type " +
                 "FROM YukonPaObject ypo " +
                 "WHERE ypo.Category = 'DEVICE' " +
                 "AND ypo.PAOClass IN ('CARRIER','METER','IED') " +
                 "AND ypo.PAOName = ?";
    YukonDevice device = (YukonDevice)jdbcOps.queryForObject(sql, new Object[] {name}, this.yukonDeviceRowMapper);
    return device;
}

public YukonDevice getYukonDeviceObjectByMeterNumber(String meterNumber) {
    
    String sql = "SELECT ypo.PAObjectID, ypo.Type " + 
    " FROM YukonPaObject ypo " +
    " INNER JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DeviceID " +
    " WHERE dmg.METERNUMBER = ? ";
    YukonDevice device = (YukonDevice)jdbcOps.queryForObject(sql, new Object[] {meterNumber}, this.yukonDeviceRowMapper);
    return device;
}

public YukonDevice getYukonDeviceObjectByAddress(String address) {
    
    String sql = "SELECT ypo.PAObjectID, ypo.Type " + 
    " FROM YukonPaObject ypo " +
    " INNER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DeviceID " +
    " WHERE dcs.ADDRESS = ? ";
    YukonDevice device = (YukonDevice)jdbcOps.queryForObject(sql, new Object[] {address}, this.yukonDeviceRowMapper);
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

public void changeRoute(YukonDevice device, int newRouteId) {
    
    // Updates the meter's meter number
    String sql = " UPDATE DeviceRoutes SET RouteID = ? WHERE DeviceID = ?";
    jdbcOps.update(sql, new Object[] {newRouteId, device.getDeviceId()});
    
    processDeviceUpdateDBChange(device);
}

public void changeName(YukonDevice device, String newName) {
    
    String sql = " UPDATE YukonPAObject SET PAOName = ? WHERE PAObjectID = ?";
    jdbcOps.update(sql, new Object[] {newName, device.getDeviceId()});
    
    processDeviceUpdateDBChange(device);
}

public void changeAddress(YukonDevice device, int newAddress) {
    
    String sql = " UPDATE " + DeviceCarrierSettings.TABLE_NAME +
                 " SET ADDRESS = ? WHERE DeviceID = ?";
    jdbcOps.update(sql, new Object[] {newAddress, device.getDeviceId()});
    
    processDeviceUpdateDBChange(device);
}

public void changeMeterNumber(YukonDevice device, String newMeterNumber) {
    
    String sql = " UPDATE DEVICEMETERGROUP SET METERNUMBER = ? WHERE DeviceID = ?";
    jdbcOps.update(sql, new Object[] {newMeterNumber, device.getDeviceId()});

    processDeviceUpdateDBChange(device);
}

public String getFormattedName(YukonDevice device) {
    
    if (device instanceof Meter) {
        return meterDao.getFormattedDeviceName((Meter)device);
    }
    
    LiteYukonPAObject paoObj = paoDao.getLiteYukonPAO(device.getDeviceId());
    return paoObj.getPaoName();
}

public String getFormattedName(int deviceId) {
    
    try {
        Meter meter = meterDao.getForId(deviceId);
        return meterDao.getFormattedDeviceName(meter);
        
    } catch (NotFoundException e) {
        
        LiteYukonPAObject paoObj = paoDao.getLiteYukonPAO(deviceId);
        return paoObj.getPaoName();
    }
}

private void processDeviceUpdateDBChange(YukonDevice device) {

    DBChangeMsg msg = new DBChangeMsg(device.getDeviceId(),
                                      DBChangeMsg.CHANGE_PAO_DB,
                                      PAOGroups.STRING_CAT_DEVICE,
                                      paoGroupsWrapper.getPAOTypeString(device.getType()),
                                      DBChangeMsg.CHANGE_TYPE_UPDATE );

    dbPersistantDao.processDBChange(msg);
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

@Required
public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
    this.dbPersistantDao = dbPersistantDao;
}

@Required
public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
    this.paoGroupsWrapper = paoGroupsWrapper;
}

@Required
public void setMeterDao(MeterDao meterDao) {
    this.meterDao = meterDao;
}
}
