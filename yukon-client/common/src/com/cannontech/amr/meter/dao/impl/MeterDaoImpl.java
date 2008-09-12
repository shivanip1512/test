package com.cannontech.amr.meter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.ConfigurationRole;
import com.cannontech.util.NaturalOrderComparator;

public class MeterDaoImpl implements MeterDao {
    private DBPersistentDao dbPersistentDao;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private JdbcOperations jdbcOps;
    private ParameterizedRowMapper<Meter> meterRowMapper;
    private PaoDao paoDao;
    private RoleDao roleDao;
    private DeviceGroupProviderDao deviceGroupProviderDao;

    String retrieveMeterSql = "select YukonPaObject.*, DeviceMeterGroup.*, DeviceCarrierSettings.*, DeviceRoutes.*, yporoute.paoName as route " + "from YukonPaObject " + "join Device on YukonPaObject.paObjectId = Device.deviceId " + "join DeviceMeterGroup on Device.deviceId = DeviceMeterGroup.deviceId " + "left outer join DeviceCarrierSettings on Device.deviceId = DeviceCarrierSettings.deviceId " + "left outer join DeviceRoutes on Device.deviceId = DeviceRoutes.deviceId " + "left outer join YukonPaObject yporoute on DeviceRoutes.routeId = yporoute.paObjectId ";

    String retrieveOneByIdSql = retrieveMeterSql + "where YukonPaObject.paObjectId = ? ";
    String retrieveOneByMeterNumberSql = retrieveMeterSql + "where DeviceMeterGroup.MeterNumber = ? ";
    String retrieveOneByPaoNameSql = retrieveMeterSql + "where YukonPaobject.PaoName = ? ";
    String retrieveOneByPhysicalAddressSql = retrieveMeterSql + "where DeviceCarrierSettings.Address = ? ";

    public void delete(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    @Transactional
    public void update(Meter newMeterInfo) {
        String sqlUpdate;

        // Updates the meter's name and the meter's type
        sqlUpdate = " UPDATE YukonPAObject" + " SET PAOName = ?, Type = ?" + " WHERE PAObjectID = ?";
        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getName(),
                newMeterInfo.getTypeStr(), newMeterInfo.getDeviceId() });

        // Updates the meter's meter number
        sqlUpdate = " UPDATE DEVICEMETERGROUP" + " SET METERNUMBER = ?" + " WHERE DEVICEID = ? ";

        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getMeterNumber(),
                newMeterInfo.getDeviceId() });

        // Updates the meter's address
        sqlUpdate = " UPDATE DeviceCarrierSettings" + " SET Address = ?" + " WHERE DEVICEID = ? ";

        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getAddress(),
                newMeterInfo.getDeviceId() });

        sendDBChangeMessage(newMeterInfo);

    }

    public Meter getForId(Integer id) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByIdSql,
                                                            meterRowMapper,
                                                            id);
            return meter;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unknown meter id " + id);
        }
    }
    
    public Meter getForYukonDevice(YukonDevice yukonDevice) {
        if (yukonDevice instanceof Meter) {
            return (Meter) yukonDevice;
        } else {
            return getForId(yukonDevice.getDeviceId());
        }
    }

    public Meter getForMeterNumber(String meterNumber) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByMeterNumberSql,
                                                            meterRowMapper,
                                                            meterNumber);
            return meter;
        } catch (DataAccessException e) {
            throw new NotFoundException("Unknown meter number " + meterNumber);
        }
    }
    @Override
    public Meter getForPhysicalAddress(String address) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByPhysicalAddressSql,
                                                            meterRowMapper,
                                                            address);
            return meter;
        } catch (DataAccessException e) {
            throw new NotFoundException("Unknown physical address " + address);
        }
    }

    public Meter getForPaoName(String paoName) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByPaoNameSql,
                                                            meterRowMapper,
                                                            paoName);
            return meter;
        } catch (DataAccessException e) {
            throw new NotFoundException("Unknown pao name " + paoName);
        }
    }

    public List<Meter> getMetersByGroup(DeviceGroup group) {
        String sqlWhereClause = deviceGroupProviderDao.getDeviceGroupSqlWhereClause(group,
                                                                                    "Device.deviceId");
        String sql = retrieveMeterSql + " where " + sqlWhereClause;

        List<Meter> meterList = simpleJdbcTemplate.query(sql, meterRowMapper);

        return meterList;
    }
    
    private String getChildMetersByGroupSql(DeviceGroup group) {
        
        String sqlWhereClause = deviceGroupProviderDao.getChildDeviceGroupSqlWhereClause(group,
                                                                                         "Device.deviceId");
        return retrieveMeterSql + " where " + sqlWhereClause;
    }
    
    public List<Meter> getChildMetersByGroup(DeviceGroup group) {
        String sql = getChildMetersByGroupSql(group);

        List<Meter> meterList = simpleJdbcTemplate.query(sql, meterRowMapper);

        return meterList;
    }
    
    public List<Meter> getChildMetersByGroup(DeviceGroup group, final int maxSize) {
        String sql = getChildMetersByGroupSql(group);

        final List<Meter> meterList = new ArrayList<Meter>();
            
        ResultSetExtractor rse = new ResultSetExtractor() {

            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                
                int i = 0;
                while (meterList.size() < maxSize && rs.next()) {
                    Meter meter = meterRowMapper.mapRow(rs, i);
                    meterList.add(meter);
                    i++;
                }
                return null;
            }
        };
        
        simpleJdbcTemplate.getJdbcOperations().query(sql, rse);

        return meterList;
    }

    public void save(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    public String getFormattedDeviceName(Meter device) {
        
        String formattingStr = roleDao.getGlobalPropertyValue(ConfigurationRole.DEVICE_DISPLAY_TEMPLATE);
        Validate.notNull(formattingStr, "Device display template role property does not exist.");
        
        try {
            MeterDisplayFieldEnum meterDisplayFieldEnumVal = MeterDisplayFieldEnum.valueOf(formattingStr);
            
            String formattedName = meterDisplayFieldEnumVal.getField(device);
            if (formattedName == null) {
                String defaultName = "n/a";
                if (!meterDisplayFieldEnumVal.equals(MeterDisplayFieldEnum.DEVICE_NAME)) {
                    return defaultName + " (" + device.getName() + ")";
                }
                return defaultName;
            }
            return formattedName;
            
        } catch (IllegalArgumentException e) {
            return formattingStr;
        }
    }

    /**
     * Returns a list of Meter objects. LastReceived is the minimum MeterNumber
     * (order ASC by MeterNumber) to return. MaxRecordCount is the maximum
     * number of records to return in the Meter list.
     */
    public List<Meter> getMetersByMeterNumber(String lastReceived,
            int maxRecordCount) {
        if (lastReceived == null)
            lastReceived = "";

        String sql = retrieveMeterSql + " AND MeterNumber > ? ORDER BY MeterNumber";

        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters,
                                                                       meterRowMapper);
        jdbcOps.query(sql,
                      new Object[] { lastReceived },
                      new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount));
        return mspMeters;
    }

    /**
     * Returns a list of Meter objects. LastReceived is the minimum PaoName
     * (order ASC by PaoName) to return. MaxRecordCount is the maximum number of
     * records to return in the Meter list.
     */
    public List<Meter> getMetersByPaoName(String lastReceived,
            int maxRecordCount) {
        if (lastReceived == null)
            lastReceived = "";

        String sql = retrieveMeterSql + " AND PaoName > ? ORDER BY PaoName";

        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters,
                                                                       meterRowMapper);
        jdbcOps.query(sql,
                      new Object[] { lastReceived },
                      new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount));
        return mspMeters;
    }

    public Comparator<Meter> getMeterComparator() {
        final NaturalOrderComparator naturalOrderComparator = new NaturalOrderComparator();
        return new Comparator<Meter>() {
            public int compare(Meter o1, Meter o2) {
                String n1 = getFormattedDeviceName(o1);
                String n2 = getFormattedDeviceName(o2);
                return naturalOrderComparator.compare(n1, n2);
            }
        };
    }

    private void sendDBChangeMessage(Meter meter) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject) dbPersistentDao.retrieveDBPersistent(liteYukonPAO);
        dbPersistentDao.performDBChange(yukonPaobject,
                                        DBChangeMsg.CHANGE_TYPE_UPDATE);

    }

    public void setMeterRowMapper(ParameterizedRowMapper<Meter> meterRowMapper) {
        this.meterRowMapper = meterRowMapper;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

    @Required
    public void setDeviceGroupProviderDao(
            DeviceGroupProviderDao deviceGroupProviderDao) {
        this.deviceGroupProviderDao = deviceGroupProviderDao;
    }

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
