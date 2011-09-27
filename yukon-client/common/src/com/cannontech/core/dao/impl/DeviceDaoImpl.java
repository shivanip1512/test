package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

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

    private YukonDeviceRowMapper yukonDeviceRowMapper = null;

    private JdbcOperations jdbcOps;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDao paoDao;
    private IDatabaseCache databaseCache;
    private DBPersistentDao dbPersistantDao;
    private MeterDao meterDao;

    /**
     * PointFuncs constructor comment.
     */
    public DeviceDaoImpl() {
        super();
    }

    public void afterPropertiesSet() {

        this.yukonDeviceRowMapper = new YukonDeviceRowMapper();
    }

    public void disableDevice(YukonDevice device) {
        this.enableDisableDevice(device, "Y");

    }
    public void enableDevice(YukonDevice device) {
        this.enableDisableDevice(device, "N");
    }

    private void enableDisableDevice(YukonDevice device, String disableFlag) {

        String sql = "UPDATE yukonpaobject SET disableflag = ? WHERE paobjectid = ?";
        jdbcOps.update(sql, new Object[] { disableFlag, device.getPaoIdentifier().getPaoId() });

        processDeviceUpdateDBChange(device);
    }

    public void removeDevice(YukonDevice device) {
        LiteYukonPAObject liteDevice = this.getLiteDevice(device.getPaoIdentifier().getPaoId());
        DBPersistent persistent = dbPersistantDao.retrieveDBPersistent(liteDevice);
        dbPersistantDao.performDBChange(persistent, TransactionType.DELETE);
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DeviceDao#getLiteDevice(int)
     */
    public LiteYukonPAObject getLiteDevice(final int deviceID) {
        return paoDao.getLiteYukonPAO( deviceID );
    }

    public SimpleDevice getYukonDevice(int paoId) {
        return getYukonDevice(paoDao.getLiteYukonPAO(paoId));
    }

    public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
        SimpleDevice device = new SimpleDevice(yukonPAObject.getYukonID(), yukonPAObject.getPaoType());
        return device;
    }

    /**
     * A leaner version of getYukonDevice()
     */
    public SimpleDevice getYukonDeviceObjectById(int deviceId) {

        String sql = "SELECT ypo.PAObjectID, ypo.Type FROM YukonPaObject ypo WHERE ypo.PAObjectID = ?";
        SimpleDevice device = (SimpleDevice)jdbcOps.queryForObject(sql, new Object[] {deviceId}, this.yukonDeviceRowMapper);
        return device;
    }

    public SimpleDevice getYukonDeviceObjectByName(String name) {
        ImmutableSet<PaoClass> allowedClasses = ImmutableSet.of(PaoClass.CARRIER, PaoClass.METER, PaoClass.IED, PaoClass.RFMESH);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.PAObjectId, PAO.Type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE PAO.Category").eq(PaoCategory.DEVICE);
        sql.append(  "AND PAO.PAOClass").in(allowedClasses);
        sql.append(  "AND UPPER(PAO.PAOName) = UPPER(").appendArgument(name).append(")");
        
        SimpleDevice device = yukonJdbcTemplate.queryForObject(sql, this.yukonDeviceRowMapper);
        return device;
    }
    
    public SimpleDevice getYukonDeviceObjectByNameAndClass(String name, PaoClass paoClass) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.PAObjectId, PAO.Type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE PAO.Category").eq(PaoCategory.DEVICE);
        sql.append(  "AND PAO.PAOClass").eq(paoClass);
        sql.append(  "AND UPPER(PAO.PAOName) = UPPER(").appendArgument(name).append(")");
        
        SimpleDevice device = yukonJdbcTemplate.queryForObject(sql, this.yukonDeviceRowMapper);
        
        return device;
    }

    public SimpleDevice findYukonDeviceObjectByName(String name) {

        SimpleDevice device = null;
        try {
            device = getYukonDeviceObjectByName(name);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
        return device;
    }

    public SimpleDevice getYukonDeviceObjectByMeterNumber(String meterNumber) {

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PAO.PAObjectID, PAO.Type "); 
        sqlBuilder.append("FROM YukonPAObject PAO ");
        sqlBuilder.append("INNER JOIN DeviceMeterGroup DMG ON PAO.PAObjectID = DMG.DeviceID ");
        sqlBuilder.append("WHERE UPPER(DMG.MeterNumber) = UPPER(?) ");
        SimpleDevice device = (SimpleDevice)jdbcOps.queryForObject(sqlBuilder.getSql(), new Object[] {meterNumber}, this.yukonDeviceRowMapper);
        return device;
    }

    public SimpleDevice getYukonDeviceObjectByAddress(Long address) {

        String sql = "SELECT ypo.PAObjectID, ypo.Type " + 
                     " FROM YukonPaObject ypo " +
                     " INNER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DeviceID " +
                     " WHERE dcs.ADDRESS = ? ";
        SimpleDevice device = (SimpleDevice)jdbcOps.queryForObject(sql, new Object[] {address}, this.yukonDeviceRowMapper);
        return device;
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DeviceDao#getLiteDeviceMeterNumber(int)
     */
    public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID)
    {
        List<LiteDeviceMeterNumber> allDevMtrGrps = databaseCache.getAllDeviceMeterGroups();

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
        List<LiteDeviceMeterNumber> allDevMtrGrps = databaseCache.getAllDeviceMeterGroups();

        LiteDeviceMeterNumber ldmn = null;
        for (int i = 0; i < allDevMtrGrps.size(); i++)
        {
            ldmn = (LiteDeviceMeterNumber)allDevMtrGrps.get(i);
            if (ldmn.getMeterNumber().equalsIgnoreCase(meterNumber))
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
    public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(litePaoSql);
        sqlBuilder.append("LEFT OUTER JOIN DeviceMeterGroup DMG ON y.PAObjectId = DMG.DeviceId ");
        sqlBuilder.append("WHERE UPPER(DMG.MeterNumber) = UPPER(?)");

        List<LiteYukonPAObject> paos = yukonJdbcTemplate.query(sqlBuilder.toString(), 
        													   new LitePaoRowMapper(), 
        													   meterNumber);

        return paos;
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPaobjectByDeviceName(java.lang.String)
     */
    public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName)
    {
        List<LiteYukonPAObject> allDevices = databaseCache.getAllDevices();

        LiteYukonPAObject lPao = null;
        for (int i = 0; i < allDevices.size(); i++)
        {
            lPao = (LiteYukonPAObject)allDevices.get(i);
            if (lPao.getPaoName().equalsIgnoreCase(deviceName))
                return lPao;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DeviceDao#getLiteYukonPAObject(java.lang.String, int, int, int)
     */
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName, int category, int paoClass, int type)
    {
        List<LiteYukonPAObject> allDevices = databaseCache.getAllDevices();
        for (Object obj : allDevices) {
            LiteYukonPAObject lPao = (LiteYukonPAObject) obj;
            boolean foundMatch = true;
            foundMatch &= lPao.getPaoName().equalsIgnoreCase(deviceName);
            foundMatch &= lPao.getPaoType().getPaoCategory().getCategoryId() == category;
            foundMatch &= lPao.getPaoType().getPaoClass().getPaoClassId() == paoClass;
            foundMatch &= lPao.getPaoType().getDeviceTypeId() == type;
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
    public List<Integer> getDevicesByPort(int portId)
    {
        List<Integer> devices = databaseCache.getDevicesByCommPort(portId);
        return devices;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.DeviceDao#getDevicesByDeviceAddress(java.lang.Integer, java.lang.Integer)
     */
    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        List<Integer> devicesByAddress = databaseCache.getDevicesByDeviceAddress(masterAddress, slaveAddress);
        return devicesByAddress;
    }

    @Override
    public List<SimpleDevice> getDevicesForRouteId(int routeId){
        String sql = "SELECT yp.PAObjectId, yp.Type FROM YukonPAObject yp "
                        + "JOIN Device d ON yp.PAObjectId = d.DeviceId "
                        + "JOIN DeviceRoutes dr ON d.DeviceId = dr.DeviceId "
                        + "WHERE dr.RouteId = ?";
        List<SimpleDevice> devices = jdbcOps.query(sql, new Integer[] {routeId}, new YukonDeviceRowMapper());
        return devices;
    }
    
    @Override
    public int getRouteDeviceCount(int routeId){
        String sql = "SELECT COUNT(*) FROM DeviceRoutes WHERE RouteId = ?";
        return jdbcOps.queryForInt(sql, new Object[]{routeId});
    }
    
    public void changeRoute(YukonDevice device, int newRouteId) {

        // Updates the meter's meter number
        String sql = " UPDATE DeviceRoutes SET RouteID = ? WHERE DeviceID = ?";
        jdbcOps.update(sql, new Object[] {newRouteId, device.getPaoIdentifier().getPaoId()});

        processDeviceUpdateDBChange(device);
    }

    public void changeName(YukonDevice device, String newName) {

        String sql = " UPDATE YukonPAObject SET PAOName = ? WHERE PAObjectID = ?";
        jdbcOps.update(sql, new Object[] {newName, device.getPaoIdentifier().getPaoId()});

        processDeviceUpdateDBChange(device);
    }

    public void changeAddress(YukonDevice device, int newAddress) {

        String sql = " UPDATE " + DeviceCarrierSettings.TABLE_NAME +
                     " SET ADDRESS = ? WHERE DeviceID = ?";
        jdbcOps.update(sql, new Object[] {newAddress, device.getPaoIdentifier().getPaoId()});

        processDeviceUpdateDBChange(device);
    }

    public void changeMeterNumber(YukonDevice device, String newMeterNumber) {

        String sql = " UPDATE DEVICEMETERGROUP SET METERNUMBER = ? WHERE DeviceID = ?";
        jdbcOps.update(sql, new Object[] {newMeterNumber, device.getPaoIdentifier().getPaoId()});

        processDeviceUpdateDBChange(device);
    }

    public String getFormattedName(YukonDevice device) {

        if (device instanceof Meter) {
            return meterDao.getFormattedDeviceName((Meter)device);
        }

        LiteYukonPAObject paoObj = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
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

    @Override
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
        return new PaoLoader<DeviceCollectionReportDevice>() {
            @Override
            public Map<PaoIdentifier, DeviceCollectionReportDevice> getForPaos(Iterable<PaoIdentifier> identifiers) {
                Map<PaoIdentifier, String> namesForYukonDevices = getNamesForYukonDevices(identifiers);
                Map<PaoIdentifier, DeviceCollectionReportDevice> result = Maps.newHashMapWithExpectedSize(namesForYukonDevices.size());

                for (Entry<PaoIdentifier, String> entry : namesForYukonDevices.entrySet()) {
                    DeviceCollectionReportDevice dcrd = new DeviceCollectionReportDevice(entry.getKey().getPaoIdentifier());
                    dcrd.setName(entry.getValue());
                    result.put(entry.getKey(), dcrd);
                }

                return result;
            }
        };
    }

    public Map<PaoIdentifier, String> getNamesForYukonDevices(Iterable<PaoIdentifier> identifiers) {
    	
    	ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
		
		SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
		    public SqlFragmentSource generate(List<Integer> subList) {
		        SqlStatementBuilder sql = new SqlStatementBuilder();
		        sql.append("select ypo.paoname, d.deviceid");
		        sql.append("from yukonpaobject ypo");
		        sql.append("join device d on ypo.paobjectid = d.deviceid");
		        sql.append("where ypo.paObjectId").in(subList);
		        return sql;
		    }
		};
        
        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<PaoIdentifier, Integer>() {
			public Integer apply(PaoIdentifier from) {
				return from.getPaoId();
			}
		};

        ParameterizedRowMapper<Entry<Integer, String>> rowMapper = new ParameterizedRowMapper<Entry<Integer, String>>() {
            public Entry<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                return Maps.immutableEntry(rs.getInt("deviceid"), rs.getString("paoname"));
            }
        };
        

        return template.mappedQuery(sqlGenerator, identifiers, rowMapper, inputTypeToSqlGeneratorTypeMapper);
    }

    public SimpleDevice getYukonDeviceForDevice(DeviceBase oldDevice) {
        String typeStr = oldDevice.getPAOType();
        PaoType paoType = PaoType.getForDbString(typeStr);
    	PaoIdentifier paoIdentifier = new PaoIdentifier(oldDevice.getPAObjectID(), paoType);
        SimpleDevice device = new SimpleDevice(paoIdentifier);

        return device;
    }

    private void processDeviceUpdateDBChange(YukonDevice device) {

        DBChangeMsg msg = new DBChangeMsg(device.getPaoIdentifier().getPaoId(),
                                          DBChangeMsg.CHANGE_PAO_DB,
                                          PaoCategory.DEVICE.getDbString(),
                                          device.getPaoIdentifier().getPaoType().getDbString(),
                                          DbChangeType.UPDATE );

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
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
