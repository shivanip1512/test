package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoMacAddress;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TransactionType;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class DeviceDaoImpl implements DeviceDao {
    private YukonDeviceRowMapper deviceRowMapper;

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private IDatabaseCache cache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private MeterDao meterDao;

    public static final YukonRowMapper<SimpleDevice> SIMPLE_DEVICE_MAPPER = new YukonRowMapper<>() {
        @Override
        public SimpleDevice mapRow(YukonResultSet rs) throws SQLException {
            int deviceId = rs.getInt("paobjectId");
            PaoType paoType = rs.getEnum("type", PaoType.class);
            return new SimpleDevice(deviceId, paoType);
        }
    };
    
    public static final YukonRowMapper<DisplayableDevice> DISPLAYABLE_DEVICE_MAPPER = rs ->
        new DisplayableDevice(rs.getPaoIdentifier("PAObjectID", "Type"), rs.getString("PAOName"));

    public static final YukonRowMapper<PaoMacAddress> PAO_MAC_ROW_MAPPER = (YukonResultSet rs) -> {
        PaoIdentifier paoIdentifier = rs.getPaoIdentifier("DeviceId", "Type");
        String macAddress = rs.getString("MacAddress");
        return new PaoMacAddress(paoIdentifier, macAddress);
    };
    
    public static final YukonRowMapper<Entry<Integer, String>> DEVICEID_GUID_ROW_MAPPER = (YukonResultSet rs) -> {
        Integer deviceId = rs.getInt("DeviceId");
        String guid = rs.getString("Guid");
        return Maps.immutableEntry(deviceId, guid);
    };
    
    @PostConstruct
    public void init() {
        deviceRowMapper = new YukonDeviceRowMapper();
    }

    @Override
    public void disableDevice(YukonDevice device) {
        enableDisableDevice(device, YNBoolean.YES);
    }

    @Override
    public void enableDevice(YukonDevice device) {
        enableDisableDevice(device, YNBoolean.NO);
    }
    
    @Override
    public void updateSecondaryMacAddress(PaoType type, int deviceId, String macAddress) {        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.update("DeviceMacAddress");
        params.addValue("SecondaryMacAddress", macAddress);
        sql.append("WHERE DeviceId").eq(deviceId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void updateDeviceMacAddress(PaoType type, int deviceId, String macAddress) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM DeviceMacAddress");
        sql.append("WHERE DeviceId").eq(deviceId);

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("DeviceMacAddress");
            params.addValue("MacAddress", macAddress);
            updateCreateSql.append("WHERE DeviceId").eq(deviceId);
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("DeviceMacAddress");
            params.addValue("DeviceId", deviceId);
            params.addValue("MacAddress", macAddress);
        }
        jdbcTemplate.update(updateCreateSql);
    }
    
    @Override
    public int getDeviceIdByName(String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.PAObjectId");
        sql.append("FROM YukonPAObject PAO");
        sql.append("WHERE UPPER(PAO.PAOName) = UPPER(").appendArgument(name).append(")");
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public String getDeviceMacAddress(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MacAddress");
        sql.append("FROM DeviceMacAddress");
        sql.append("WHERE DeviceId").eq(deviceId);
        return jdbcTemplate.queryForString(sql);
    }
    
    @Override
    public int getDeviceIdFromMacAddress(String macAddress) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM DeviceMacAddress");
        sql.append("WHERE MacAddress").eq(macAddress);
        sql.append("OR SecondaryMacAddress").eq(macAddress);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No Device ID found for MAC " + macAddress, e);
        }
    }
    
    @Override
    public String getSecondaryMacAddressForDevice(int deviceId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SecondaryMacAddress");
        sql.append("FROM DeviceMacAddress");
        sql.append("WHERE DeviceId").eq(deviceId);
        try {
            return jdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No entry found for device ID " + deviceId, e);
        }
    }
    
    @Override
    public List<PaoMacAddress> findAllDevicesWithNoSecondaryMacAddress() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT dma.DeviceId, dma.MacAddress, ypo.Type");
        sql.append("FROM DeviceMacAddress dma");
        sql.append("JOIN YukonPaObject ypo ON ypo.paObjectId = dma.deviceId");
        sql.append("WHERE SecondaryMacAddress IS NULL");
        
        return jdbcTemplate.query(sql, PAO_MAC_ROW_MAPPER);
    }
    
    @Override
    public Map<Integer, String> getDeviceMacAddresses(Collection<Integer> deviceIds) {
        
        SqlFragmentGenerator<Integer> sqlFragmentGenerator = (List<Integer> subList) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DeviceId, MacAddress");
            sql.append("FROM DeviceMacAddress");
            sql.append("WHERE DeviceId").in(subList);
            return sql;
        };
        
        Map<Integer, String> result = new HashMap<>();
        ChunkingSqlTemplate chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        chunkingSqlTemplate.query(sqlFragmentGenerator, deviceIds, (YukonResultSet rs) -> {
            result.put(rs.getInt("DeviceId"), rs.getString("macAddress"));
        });
        return result;
    }
    
    @Override
    public boolean isMacAddressExists(String macAddress) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MacAddress");
        sql.append("FROM DeviceMacAddress");
        sql.append("WHERE MacAddress").eq(macAddress);
        try {
            jdbcTemplate.queryForString(sql);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
    
    private void enableDisableDevice(YukonDevice device, YNBoolean disableFlag) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE yukonpaobject SET disableflag").eq_k(disableFlag);
        sql.append("WHERE paobjectid").eq(device.getPaoIdentifier().getPaoId());
        jdbcTemplate.update(sql);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void removeDevice(YukonDevice device) {
        LiteYukonPAObject liteDevice = cache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
        DBPersistent persistent = dbPersistentDao.retrieveDBPersistent(liteDevice);
        dbPersistentDao.performDBChange(persistent, TransactionType.DELETE);
    }
    
    @Override
    public void removeDevice(int id) {
        LiteYukonPAObject liteDevice = cache.getAllPaosMap().get(id);
        DBPersistent persistent = dbPersistentDao.retrieveDBPersistent(liteDevice);
        dbPersistentDao.performDBChange(persistent, TransactionType.DELETE);
    }

    @Override
    public SimpleDevice getYukonDevice(int paoId) {
        LiteYukonPAObject litePao = cache.getAllPaosMap().get(paoId);
        if (litePao == null) {
            throw new NotFoundException("A PAObject with id " + paoId + " cannot be found.");
        }
        return getYukonDevice(litePao);
        
    }

    @Override
    public SimpleDevice getYukonDevice(LiteYukonPAObject yukonPAObject) {
        SimpleDevice device = new SimpleDevice(yukonPAObject.getYukonID(), yukonPAObject.getPaoType());
        return device;
    }

    @Override
    public List<SimpleDevice> getYukonDeviceObjectByIds(Iterable<Integer> ids) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<>() {
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
    
    @Override
    public List<SimpleDevice> getDisabledDevices(Iterable<Integer> ids) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.Type");
                sql.append("FROM YukonPaObject ypo");
                sql.append("WHERE ypo.PAObjectID").in(subList);
                sql.append("AND disableFlag").eq_k(YNBoolean.YES);
                return sql;
            }
        };

        return template.query(sqlGenerator, ids, SIMPLE_DEVICE_MAPPER);
    }
    
    @Override
    public List<SimpleDevice> getDevicesForPaoTypes(Iterable<PaoType> types) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<PaoType> sqlGenerator = new SqlFragmentGenerator<>() {
            @Override
            public SqlFragmentSource generate(List<PaoType> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ypo.PAObjectID, ypo.Type");
                sql.append("FROM YukonPaObject ypo");
                sql.append("WHERE ypo.Type").in(subList);
                return sql;
            }
        };

        List<SimpleDevice> devices = template.query(sqlGenerator, types, SIMPLE_DEVICE_MAPPER);
        return devices;
    }


    @Override
    public SimpleDevice getYukonDeviceObjectByName(String name) {
        ImmutableSet<PaoClass> allowedClasses =
            ImmutableSet.of(PaoClass.CARRIER, PaoClass.METER, PaoClass.IED, PaoClass.RFMESH, PaoClass.THERMOSTAT, PaoClass.VIRTUAL);

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
        sqlBuilder.append("WHERE UPPER(DMG.MeterNumber) = UPPER(").appendArgument(meterNumber).append(")");
        SimpleDevice device = jdbcTemplate.queryForObject(sqlBuilder, deviceRowMapper);
        return device;
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByAddress(Long address) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type FROM YukonPaObject ypo ");
        sql.append("INNER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DeviceID ");
        sql.append("WHERE dcs.ADDRESS").eq(address);
        SimpleDevice device = jdbcTemplate.queryForObject(sql, deviceRowMapper);
        return device;
    }

    @Override
    public LiteDeviceMeterNumber getLiteDeviceMeterNumber(int deviceId) {
        
        SimpleMeter meter = cache.getAllMeters().get(deviceId);
        if (meter != null) {
            return new LiteDeviceMeterNumber(deviceId, meter.getMeterNumber(), meter.getPaoIdentifier().getPaoType());
        }
        
        return null;
    }

    @Override
    public LiteYukonPAObject getLiteYukonPaobjectByMeterNumber(String meterNumber) {
        
        for (SimpleMeter meter : cache.getAllMeters().values()) {
            if (meter.getMeterNumber().equalsIgnoreCase(meterNumber)) {
                return cache.getAllPaosMap().get(meter.getPaoIdentifier().getPaoId());
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
    public List<DisplayableDevice> getChildDevices(int parentId) {
        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT PAObjectID, Type, PAOName");
        sqlBuilder.append("FROM YukonPaObject Y");
        sqlBuilder.append("   JOIN DeviceParent DP ON Y.PaObjectId = DP.DeviceId");
        sqlBuilder.append("WHERE DP.ParentId").eq(parentId);

        return jdbcTemplate.query(sqlBuilder, DISPLAYABLE_DEVICE_MAPPER);
    }

    @Override
    public LiteYukonPAObject getLiteYukonPaobjectByDeviceName(String deviceName) {
        List<LiteYukonPAObject> allDevices = cache.getAllDevices();

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
        List<LiteYukonPAObject> allDevices = cache.getAllDevices();
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
    public List<DisplayableDevice> getDevicesByPortAndDeviceAddress(int portId, int masterAddress, int slaveAddress) {
        var sql = new SqlStatementBuilder();
        
        sql.append("SELECT");
        sql.append(    "yp.PAObjectID, yp.Type, yp.PAOName");
        sql.append("FROM YukonPAObject yp");
        sql.append(    "JOIN DeviceDirectCommSettings ddcs ON yp.PAObjectId = ddcs.DeviceId");
        sql.append(    "JOIN DeviceAddress da ON yp.PAObjectId = da.DeviceId");
        sql.append("WHERE ddcs.PortId").eq(portId);
        sql.append(    "AND da.MasterAddress").eq(masterAddress);
        sql.append(    "AND da.SlaveAddress").eq(slaveAddress);
        
        return jdbcTemplate.query(sql, DISPLAYABLE_DEVICE_MAPPER);
    }

    @Override
    public List<SimpleDevice> getDevicesForRouteId(int routeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT yp.PAObjectId, yp.Type FROM YukonPAObject yp ");
        sql.append("JOIN Device d ON yp.PAObjectId = d.DeviceId ");
        sql.append("JOIN DeviceRoutes dr ON d.DeviceId = dr.DeviceId ");
        sql.append("WHERE dr.RouteId").eq(routeId);
        List<SimpleDevice> devices = jdbcTemplate.query(sql, new YukonDeviceRowMapper());
        return devices;
    }

    @Override
    public int getRouteDeviceCount(int routeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder("SELECT COUNT(*) FROM DeviceRoutes WHERE RouteId").eq(routeId);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public void changeRoute(YukonDevice device, int newRouteId) {
        // Updates the meter's meter number
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DeviceRoutes").set("RouteId", newRouteId);
        sql.append("WHERE DeviceID").eq(device.getPaoIdentifier().getPaoId());
        jdbcTemplate.update(sql);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeName(YukonDevice device, String newName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonPAObject").set("PAOName", newName);
        sql.append("WHERE PAObjectID").eq(device.getPaoIdentifier().getPaoId());
        jdbcTemplate.update(sql);
        
        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeAddress(YukonDevice device, int newAddress) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DeviceCarrierSettings").set("ADDRESS", newAddress);
        sql.append("WHERE DeviceID").eq(device.getPaoIdentifier().getPaoId());;
        jdbcTemplate.update(sql);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeMeterNumber(YukonDevice device, String newMeterNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE DEVICEMETERGROUP").set("METERNUMBER", newMeterNumber);
        sql.append("WHERE DeviceID").eq(device.getPaoIdentifier().getPaoId());
        jdbcTemplate.update(sql);

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public String getFormattedName(YukonDevice device) {
        if (device instanceof YukonMeter) {
            return meterDao.getFormattedDeviceName((YukonMeter) device);
        }

        LiteYukonPAObject paoObj = cache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
        return paoObj.getPaoName();
    }

    @Override
    public String getFormattedName(int deviceId) {
        try {
            YukonMeter meter = meterDao.getForId(deviceId);
            return meterDao.getFormattedDeviceName(meter);
        } catch (NotFoundException e) {
            LiteYukonPAObject paoObj = cache.getAllPaosMap().get(deviceId);
            return paoObj.getPaoName();
        }
    }

    @Override
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
        return new PaoLoader<>() {
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

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<>() {
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

        Function<PaoIdentifier, Integer> inputTypeToSqlGeneratorTypeMapper = new Function<>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

        RowMapper<Entry<Integer, String>> rowMapper =
            new RowMapper<>() {
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
    
    @Override
    public boolean isGuidExists(String guid) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Guid");
        sql.append("FROM DeviceGuid");
        sql.append("WHERE Guid").eq(guid);
        try {
            jdbcTemplate.queryForString(sql);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public void insertGuid(int deviceId, String guid) {
        try {
            SqlStatementBuilder createSql = new SqlStatementBuilder();
            SqlParameterSink params = createSql.insertInto("DeviceGuid");
            params.addValue("DeviceId", deviceId);
            params.addValue("Guid", guid);
            jdbcTemplate.update(createSql);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to add guid. Guid may already exist.", e);
        }
    }

    @Override
    public void updateGuid(int deviceId, String guid) {
        try {
            SqlStatementBuilder updateSql = new SqlStatementBuilder();
            SqlParameterSink params = updateSql.update("DeviceGuid");
            params.addValue("Guid", guid);
            updateSql.append("WHERE DeviceId").eq(deviceId);
            jdbcTemplate.update(updateSql);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to add guid. Guid may already exist.", e);
        }
    }

    @Override
    public String getGuid(int deviceId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Guid");
        sql.append("FROM DeviceGuid");
        sql.append("WHERE DeviceId").eq(deviceId);
        try {
            return jdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Guid is not found for device id " + deviceId, e);
        }
    }

    @Override
    public Map<Integer, String> getGuids(Iterable<Integer> deviceIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = (List<Integer> subList) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DeviceId, Guid");
            sql.append("FROM DeviceGuid");
            sql.append("WHERE DeviceId").in(subList);
            return sql;
        };

        return template.mappedQuery(sqlGenerator,
                                    deviceIds, 
                                    DEVICEID_GUID_ROW_MAPPER,
                                    Functions.identity());
    }

    @Override
    public List<String> getGuids(){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Guid");
        sql.append("FROM DeviceGuid");
        try {
            return jdbcTemplate.query(sql, TypeRowMapper.STRING);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No Guid's found ", e);
        }
    }
    
    @Override
    public List<Integer> getDeviceIdsWithGuids(){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId");
        sql.append("FROM DeviceGuid");
        try {
            return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No device ids found ", e);
        }
    }
}
