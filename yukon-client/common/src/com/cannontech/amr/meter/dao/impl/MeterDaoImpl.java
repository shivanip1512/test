package com.cannontech.amr.meter.dao.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.SqlProvidingRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.ConfigurationRole;
import com.cannontech.util.NaturalOrderComparator;

public class MeterDaoImpl implements MeterDao, InitializingBean {
    private DBPersistentDao dbPersistentDao;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private JdbcOperations jdbcOps;
    private SqlProvidingRowMapper<Meter> meterRowMapper;
    private PaoDao paoDao;
    private AuthDao authDao;
    
    private String retrieveOneByIdSql;
    private String retrieveOneByMeterNumberSql;
    private String retrieveOneByPaoNameSql;
    private String retrieveOneByPhysicalAddressSql;

    @Override
    public void afterPropertiesSet() throws Exception {

        retrieveOneByIdSql = meterRowMapper.getSql() + "where ypo.paObjectId = ? ";
        retrieveOneByMeterNumberSql = meterRowMapper.getSql() + "where DeviceMeterGroup.MeterNumber = ? ";
        retrieveOneByPaoNameSql = meterRowMapper.getSql() + "where ypo.PaoName = ? ";
        retrieveOneByPhysicalAddressSql = meterRowMapper.getSql() + "where DeviceCarrierSettings.Address = ? ";
        
    }
    
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

    public void save(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    public String getFormattedDeviceName(Meter device) throws IllegalArgumentException{
        
        MeterDisplayFieldEnum meterDisplayFieldEnumVal = authDao.getRolePropertyValue(MeterDisplayFieldEnum.class,
                                                                                      ConfigurationRole.DEVICE_DISPLAY_TEMPLATE);

        String formattedName = meterDisplayFieldEnumVal.getField(device);
        if (formattedName == null) {
            String defaultName = "n/a";
            if (!meterDisplayFieldEnumVal.equals(MeterDisplayFieldEnum.DEVICE_NAME)) {
                return defaultName + " (" + device.getName() + ")";
            }
            return defaultName;
        }
        return formattedName;
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

        String sql = meterRowMapper.getSql() + " AND MeterNumber > ? ORDER BY MeterNumber";

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

        String sql = meterRowMapper.getSql() + " AND PaoName > ? ORDER BY PaoName";

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

    public void setMeterRowMapper(SqlProvidingRowMapper<Meter> meterRowMapper) {
        this.meterRowMapper = meterRowMapper;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
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
