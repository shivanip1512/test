package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
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
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class DeviceDaoImpl implements DeviceDao {
    private YukonDeviceRowMapper deviceRowMapper;

    @Autowired private JdbcOperations jdbcOps;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private MeterDao meterDao;

    public static final YukonRowMapper<SimpleDevice> SIMPLE_DEVICE_MAPPER = new YukonRowMapper<SimpleDevice>() {
        @Override
        public SimpleDevice mapRow(YukonResultSet rs) throws SQLException {
            int deviceId = rs.getInt("paobjectId");
            PaoType paoType = rs.getEnum("type", PaoType.class);
            return new SimpleDevice(deviceId, paoType);
        }
    };

    @PostConstruct
    public void init() {
        deviceRowMapper = new YukonDeviceRowMapper();
    }

    @Override
    public void disableDevice(YukonDevice device) {
        enableDisableDevice(device, "Y");

    }

    @Override
    public void enableDevice(YukonDevice device) {
        enableDisableDevice(device, "N");
    }

    private void enableDisableDevice(YukonDevice device, String disableFlag) {
        String sql = "UPDATE yukonpaobject SET disableflag = ? WHERE paobjectid = ?";
        jdbcOps.update(sql, new Object[] { disableFlag, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void removeDevice(YukonDevice device) {
        LiteYukonPAObject liteDevice = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
        DBPersistent persistent = dbPersistentDao.retrieveDBPersistent(liteDevice);
        dbPersistentDao.performDBChange(persistent, TransactionType.DELETE);
    }

    @Override
    public SimpleDevice getYukonDevice(int paoId) {
        return getYukonDevice(paoDao.getLiteYukonPAO(paoId));
    }

    @Override
    public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
        SimpleDevice device = new SimpleDevice(yukonPAObject.getYukonID(), yukonPAObject.getPaoType());
        return device;
    }

    @Override
    public List<SimpleDevice> getYukonDeviceObjectByIds(Iterable<Integer> ids) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.Type");
                sql.append("FROM YukonPaObject ypo");
                sql.append("WHERE ypo.PAObjectID").in(subList);
                return sql;
            }
        };

        List<SimpleDevice> devices = template.query(sqlGenerator, ids, SIMPLE_DEVICE_MAPPER);
        return devices;
    }

    /**
     * A leaner version of getYukonDevice()
     */
    @Override
    public SimpleDevice getYukonDeviceObjectById(int deviceId) {
        String sql = "SELECT ypo.PAObjectID, ypo.Type FROM YukonPaObject ypo WHERE ypo.PAObjectID = ?";
        SimpleDevice device = jdbcOps.queryForObject(sql, new Object[] { deviceId }, deviceRowMapper);
        return device;
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByName(String name) {
        ImmutableSet<PaoClass> allowedClasses =
            ImmutableSet.of(PaoClass.CARRIER, PaoClass.METER, PaoClass.IED, PaoClass.RFMESH);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.PAObjectId, PAO.Type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE PAO.Category").eq(PaoCategory.DEVICE);
        sql.append("AND PAO.PAOClass").in(allowedClasses);
        sql.append("AND UPPER(PAO.PAOName) = UPPER(").appendArgument(name).append(")");

        SimpleDevice device = jdbcTemplate.queryForObject(sql, deviceRowMapper);
        return device;
    }

    public SimpleDevice getYukonDeviceObjectByNameAndClass(String name, PaoClass paoClass) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.PAObjectId, PAO.Type");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE PAO.Category").eq(PaoCategory.DEVICE);
        sql.append("AND PAO.PAOClass").eq(paoClass);
        sql.append("AND UPPER(PAO.PAOName) = UPPER(").appendArgument(name).append(")");

        SimpleDevice device = jdbcTemplate.queryForObject(sql, deviceRowMapper);

        return device;
    }

    @Override
    public SimpleDevice findYukonDeviceObjectByName(String name) {
        SimpleDevice device = null;
        try {
            device = getYukonDeviceObjectByName(name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return device;
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByMeterNumber(String meterNumber) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PAO.PAObjectID, PAO.Type ");
        sqlBuilder.append("FROM YukonPAObject PAO ");
        sqlBuilder.append("INNER JOIN DeviceMeterGroup DMG ON PAO.PAObjectID = DMG.DeviceID ");
        sqlBuilder.append("WHERE UPPER(DMG.MeterNumber) = UPPER(?) ");
        SimpleDevice device =
            jdbcOps.queryForObject(sqlBuilder.getSql(), new Object[] { meterNumber }, deviceRowMapper);
        return device;
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByAddress(Long address) {
        String sql =
            "SELECT ypo.PAObjectID, ypo.Type " + " FROM YukonPaObject ypo "
                + " INNER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DeviceID " + " WHERE dcs.ADDRESS = ? ";
        SimpleDevice device = jdbcOps.queryForObject(sql, new Object[] { address }, deviceRowMapper);
        return device;
    }

    @Override
    public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceID) {
        List<LiteDeviceMeterNumber> allDevMtrGrps = databaseCache.getAllDeviceMeterGroups();

        LiteDeviceMeterNumber ldmn = null;
        for (int i = 0; i < allDevMtrGrps.size(); i++) {
            ldmn = allDevMtrGrps.get(i);
            if (ldmn.getDeviceID() == deviceID) {
                return ldmn;
            }
        }
        return null;
    }

    @Override
    public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber) {
        List<LiteDeviceMeterNumber> allDevMtrGrps = databaseCache.getAllDeviceMeterGroups();

        LiteDeviceMeterNumber ldmn = null;
        for (int i = 0; i < allDevMtrGrps.size(); i++) {
            ldmn = allDevMtrGrps.get(i);
            if (ldmn.getMeterNumber().equalsIgnoreCase(meterNumber)) {
                LiteYukonPAObject lPao = databaseCache.getAllPAOsMap().get(new Integer(ldmn.getDeviceID()));
                return lPao;
            }
        }
        return null;
    }

    @Override
    public List<LiteYukonPAObject> getLiteYukonPaobjectListByMeterNumber(String meterNumber) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT Y.PAObjectID, Y.Category, Y.PAOName, Y.Type, Y.PAOClass,");
        sqlBuilder.append("   Y.Description, Y.DisableFlag, D.PORTID, DCS.ADDRESS, DR.routeid");
        sqlBuilder.append("FROM YukonPaObject Y");
        sqlBuilder.append("   LEFT JOIN DeviceDirectCommSettings D ON Y.PaObjectId = D.DeviceId");
        sqlBuilder.append("   LEFT JOIN DeviceCarrierSettings DCS ON Y.PaObjectId = DCS.DeviceId");
        sqlBuilder.append("   LEFT JOIN DeviceRoutes DR ON Y.PaObjectId = DR.DeviceId");
        sqlBuilder.append("   LEFT JOIN DeviceMeterGroup DMG ON y.PAObjectId = DMG.DeviceId");
        sqlBuilder.append("WHERE UPPER(DMG.MeterNumber) = UPPER(").appendArgument(meterNumber).append(")");

        List<LiteYukonPAObject> paos = jdbcTemplate.query(sqlBuilder, new LitePaoRowMapper());

        return paos;
    }

    @Override
    public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName) {
        List<LiteYukonPAObject> allDevices = databaseCache.getAllDevices();

        LiteYukonPAObject lPao = null;
        for (int i = 0; i < allDevices.size(); i++) {
            lPao = allDevices.get(i);
            if (lPao.getPaoName().equalsIgnoreCase(deviceName)) {
                return lPao;
            }
        }
        return null;
    }

    @Override
    public LiteYukonPAObject getLiteYukonPAObject(String deviceName, PaoType paoType) {
        List<LiteYukonPAObject> allDevices = databaseCache.getAllDevices();
        for (Object obj : allDevices) {
            LiteYukonPAObject lPao = (LiteYukonPAObject) obj;
            boolean foundMatch = true;
            foundMatch &= lPao.getPaoName().equalsIgnoreCase(deviceName);
            foundMatch &= lPao.getPaoType().getPaoCategory() == paoType.getPaoCategory();
            foundMatch &= lPao.getPaoType().getPaoClass() == paoType.getPaoClass();
            foundMatch &= lPao.getPaoType() == paoType;
            if (foundMatch) {
                return lPao;
            }
        }
        return null;
    }
    
    @Override
    public List<Integer> getDevicesByPort(int portId) {
        List<Integer> devices = databaseCache.getDevicesByCommPort(portId);
        return devices;
    }

    @Override
    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        List<Integer> devicesByAddress = databaseCache.getDevicesByDeviceAddress(masterAddress, slaveAddress);
        return devicesByAddress;
    }

    @Override
    public List<SimpleDevice> getDevicesForRouteId(int routeId) {
        String sql =
            "SELECT yp.PAObjectId, yp.Type FROM YukonPAObject yp " + "JOIN Device d ON yp.PAObjectId = d.DeviceId "
                + "JOIN DeviceRoutes dr ON d.DeviceId = dr.DeviceId " + "WHERE dr.RouteId = ?";
        List<SimpleDevice> devices = jdbcOps.query(sql, new Integer[] { routeId }, new YukonDeviceRowMapper());
        return devices;
    }

    @Override
    public int getRouteDeviceCount(int routeId) {
        String sql = "SELECT COUNT(*) FROM DeviceRoutes WHERE RouteId = ?";
        return jdbcOps.queryForInt(sql, new Object[] { routeId });
    }

    @Override
    public void changeRoute(YukonDevice device, int newRouteId) {
        // Updates the meter's meter number
        String sql = " UPDATE DeviceRoutes SET RouteID = ? WHERE DeviceID = ?";
        jdbcOps.update(sql, new Object[] { newRouteId, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeName(YukonDevice device, String newName) {
        String sql = " UPDATE YukonPAObject SET PAOName = ? WHERE PAObjectID = ?";
        jdbcOps.update(sql, new Object[] { newName, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeAddress(YukonDevice device, int newAddress) {
        String sql = " UPDATE " + DeviceCarrierSettings.TABLE_NAME + " SET ADDRESS = ? WHERE DeviceID = ?";
        jdbcOps.update(sql, new Object[] { newAddress, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeMeterNumber(YukonDevice device, String newMeterNumber) {
        String sql = " UPDATE DEVICEMETERGROUP SET METERNUMBER = ? WHERE DeviceID = ?";
        jdbcOps.update(sql, new Object[] { newMeterNumber, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public String getFormattedName(YukonDevice device) {
        if (device instanceof YukonMeter) {
            return meterDao.getFormattedDeviceName((YukonMeter) device);
        }

        LiteYukonPAObject paoObj = paoDao.getLiteYukonPAO(device.getPaoIdentifier().getPaoId());
        return paoObj.getPaoName();
    }

    @Override
    public String getFormattedName(int deviceId) {
        try {
            YukonMeter meter = meterDao.getForId(deviceId);
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
                Map<PaoIdentifier, DeviceCollectionReportDevice> result =
                    Maps.newHashMapWithExpectedSize(namesForYukonDevices.size());

                for (Entry<PaoIdentifier, String> entry : namesForYukonDevices.entrySet()) {
                    DeviceCollectionReportDevice dcrd =
                        new DeviceCollectionReportDevice(entry.getKey().getPaoIdentifier());
                    dcrd.setName(entry.getValue());
                    result.put(entry.getKey(), dcrd);
                }

                return result;
            }
        };
    }

    public Map<PaoIdentifier, String> getNamesForYukonDevices(Iterable<PaoIdentifier> identifiers) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
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
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

        ParameterizedRowMapper<Entry<Integer, String>> rowMapper =
            new ParameterizedRowMapper<Entry<Integer, String>>() {
                @Override
                public Entry<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Maps.immutableEntry(rs.getInt("deviceid"), rs.getString("paoname"));
                }
            };

        return template.mappedQuery(sqlGenerator, identifiers, rowMapper, inputTypeToSqlGeneratorTypeMapper);
    }

    @Override
    public SimpleDevice getYukonDeviceForDevice(DeviceBase oldDevice) {
        PaoType paoType = oldDevice.getPaoType();
        PaoIdentifier paoIdentifier = new PaoIdentifier(oldDevice.getPAObjectID(), paoType);
        SimpleDevice device = new SimpleDevice(paoIdentifier);

        return device;
    }
}
