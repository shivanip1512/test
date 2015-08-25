package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
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
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class DeviceDaoImpl implements DeviceDao {
    private YukonDeviceRowMapper deviceRowMapper;

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache cache;
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
        jdbcTemplate.update(sql, new Object[] { disableFlag, device.getPaoIdentifier().getPaoId() });

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
        SimpleDevice device = jdbcTemplate.queryForObject(sql, new Object[] { deviceId }, deviceRowMapper);
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
            jdbcTemplate.queryForObject(sqlBuilder.getSql(), new Object[] { meterNumber }, deviceRowMapper);
        return device;
    }

    @Override
    public SimpleDevice getYukonDeviceObjectByAddress(Long address) {
        String sql =
            "SELECT ypo.PAObjectID, ypo.Type " + " FROM YukonPaObject ypo "
                + " INNER JOIN DeviceCarrierSettings dcs ON ypo.PAObjectID = dcs.DeviceID " + " WHERE dcs.ADDRESS = ? ";
        SimpleDevice device = jdbcTemplate.queryForObject(sql, new Object[] { address }, deviceRowMapper);
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
    public List<Integer> getDevicesByPort(int portId) {
        List<Integer> devices = cache.getDevicesByCommPort(portId);
        return devices;
    }

    @Override
    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        List<Integer> devicesByAddress = cache.getDevicesByDeviceAddress(masterAddress, slaveAddress);
        return devicesByAddress;
    }

    @Override
    public List<SimpleDevice> getDevicesForRouteId(int routeId) {
        String sql =
            "SELECT yp.PAObjectId, yp.Type FROM YukonPAObject yp " + "JOIN Device d ON yp.PAObjectId = d.DeviceId "
                + "JOIN DeviceRoutes dr ON d.DeviceId = dr.DeviceId " + "WHERE dr.RouteId = ?";
        List<SimpleDevice> devices = jdbcTemplate.query(sql, new Integer[] { routeId }, new YukonDeviceRowMapper());
        return devices;
    }

    @Override
    public int getRouteDeviceCount(int routeId) {
        String sql = "SELECT COUNT(*) FROM DeviceRoutes WHERE RouteId = ?";
        return jdbcTemplate.queryForInt(sql, new Object[] { routeId });
    }

    @Override
    public void changeRoute(YukonDevice device, int newRouteId) {
        // Updates the meter's meter number
        String sql = " UPDATE DeviceRoutes SET RouteID = ? WHERE DeviceID = ?";
        jdbcTemplate.update(sql, new Object[] { newRouteId, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeName(YukonDevice device, String newName) {
        String sql = " UPDATE YukonPAObject SET PAOName = ? WHERE PAObjectID = ?";
        jdbcTemplate.update(sql, new Object[] { newName, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeAddress(YukonDevice device, int newAddress) {
        String sql = " UPDATE " + DeviceCarrierSettings.TABLE_NAME + " SET ADDRESS = ? WHERE DeviceID = ?";
        jdbcTemplate.update(sql, new Object[] { newAddress, device.getPaoIdentifier().getPaoId() });

        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    }

    @Override
    public void changeMeterNumber(YukonDevice device, String newMeterNumber) {
        String sql = " UPDATE DEVICEMETERGROUP SET METERNUMBER = ? WHERE DeviceID = ?";
        jdbcTemplate.update(sql, new Object[] { newMeterNumber, device.getPaoIdentifier().getPaoId() });

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
    
    @Override
    public Device getDevice(int deviceId){
        //ParameterizedRowMapper<Device> rowMapper = new DeviceRowMapper();
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT AlarmInhibit,ControlInhibit ");
        sql.append("FROM Device ");
        sql.append("WHERE DeviceId").eq(deviceId);

        ParameterizedRowMapper<Device> rowMapper=new ParameterizedRowMapper<Device>() {
            @Override
            public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
                Device device = new Device();
                device.setAlarmInhibit(new Character(rs.getString("AlarmInhibit").charAt(0)));
                device.setControlInhibit(new Character(rs.getString("ControlInhibit").charAt(0)));
                return device;
            }
        };
            Device device = jdbcTemplate.queryForObject(sql, rowMapper);
            return device;
        
    }
    
    @Override
    public DeviceDirectCommSettings getDeviceDirectCommSettings(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT PortID ");
        sql.append("FROM DeviceDirectCommSettings ");
        sql.append("WHERE DeviceId").eq(deviceId);
        ParameterizedRowMapper<DeviceDirectCommSettings> rowMapper = new ParameterizedRowMapper<DeviceDirectCommSettings>() {
            @Override
            public DeviceDirectCommSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceDirectCommSettings deviceDirectCommSettings = new DeviceDirectCommSettings();
                deviceDirectCommSettings.setPortID(rs.getInt("PortID"));
                return deviceDirectCommSettings;
            }
        };

        DeviceDirectCommSettings deviceDirectCommSettings = jdbcTemplate.queryForObject(sql, rowMapper);
        return deviceDirectCommSettings;
    }
    
    @Override
    public DeviceDialupSettings getDeviceDialupSettings(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT PhoneNumber,MinConnectTime, MaxConnectTime, LineSettings, BaudRate ");
        sql.append("FROM DeviceDialupSettings ");
        sql.append("WHERE DeviceId").eq(deviceId);
        ParameterizedRowMapper<DeviceDialupSettings> rowMapper = new ParameterizedRowMapper<DeviceDialupSettings>() {
            @Override
            public DeviceDialupSettings mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceDialupSettings deviceDialupSettings = new DeviceDialupSettings();
                deviceDialupSettings.setPhoneNumber(rs.getString("PhoneNumber"));
                deviceDialupSettings.setPhoneNumber(rs.getString("MinConnectTime"));
                deviceDialupSettings.setPhoneNumber(rs.getString("MaxConnectTime"));
                deviceDialupSettings.setPhoneNumber(rs.getString("LineSettings"));
                deviceDialupSettings.setPhoneNumber(rs.getString("BaudRate"));
                return deviceDialupSettings;
            }
        };
        DeviceDialupSettings deviceDialupSettings = null;
        try {
            deviceDialupSettings = jdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {

        }
        return deviceDialupSettings;
    }
    
    @Override
    public DeviceAddress getDeviceAddress(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT MasterAddress, SlaveAddress, PostCommWait ");
        sql.append("FROM DeviceAddress ");
        sql.append("WHERE DeviceId").eq(deviceId);
        ParameterizedRowMapper<DeviceAddress> rowMapper = new ParameterizedRowMapper<DeviceAddress>() {
            @Override
            public DeviceAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceAddress deviceAddress = new DeviceAddress();
                deviceAddress.setMasterAddress(rs.getInt("MasterAddress"));
                deviceAddress.setSlaveAddress(rs.getInt("SlaveAddress"));
                deviceAddress.setPostCommWait(rs.getInt("PostCommWait"));
                return deviceAddress;
            }
        };

        DeviceAddress deviceAddress = jdbcTemplate.queryForObject(sql, rowMapper);
        return deviceAddress;
    }
    
    @Override
    public DeviceWindow getDeviceWindow(int deviceId) {
        ParameterizedRowMapper<DeviceWindow> rowMapper = new ParameterizedRowMapper<DeviceWindow>() {
            @Override
            public DeviceWindow mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceWindow deviceWindow = new DeviceWindow();
                deviceWindow.setType(rs.getString("Type"));
                deviceWindow.setWinOpen(rs.getInt("WinOpen"));
                deviceWindow.setWinClose(rs.getInt("WinClose"));
                deviceWindow.setAlternateOpen(rs.getInt("AlternateOpen"));
                deviceWindow.setAlternateClose(rs.getInt("AlternateClose"));
                return deviceWindow;
            }
        };

        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Type, WinOpen, WinClose, AlternateOpen, AlternateClose ");
        sql.append("FROM DeviceWindow ");
        sql.append("WHERE DeviceID").eq(deviceId);
        DeviceWindow deviceWindow=null;
        try {
            deviceWindow = jdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            
        }
        return deviceWindow;

    }
    
    @Override
    public DeviceCBC getDeviceCBC(int deviceId) {
        ParameterizedRowMapper<DeviceCBC> rowMapper = new ParameterizedRowMapper<DeviceCBC>() {
            @Override
            public DeviceCBC mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceCBC deviceCBC = new DeviceCBC();
                deviceCBC.setSerialNumber(rs.getInt("SerialNumber"));
                deviceCBC.setRouteID(rs.getInt("RouteID"));
                return deviceCBC;
            }

        };
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT SerialNumber, RouteID ");
        sql.append("FROM DeviceCBC");
        sql.append("WHERE DeviceID").eq(deviceId);

        try {
            DeviceCBC deviceCBC = jdbcTemplate.queryForObject(sql, rowMapper);
            return deviceCBC;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Parent CBC was not found for cbc with ID: " + deviceId);
        }
    }
    
    @Override
    @Transactional
    public int saveDeviceAddress(DeviceAddress deviceAddress,int cbcId) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE DeviceAddress");
        updateSql.append("SET MasterAddress").eq(deviceAddress.getMasterAddress());
        updateSql.append(  ", SlaveAddress").eq(deviceAddress.getSlaveAddress());
        updateSql.append(  ", PostCommWait").eq(deviceAddress.getPostCommWait());
        updateSql.append("WHERE DEVICEID").eq(cbcId);
        jdbcTemplate.update(updateSql);
        return cbcId;
    }

    @Override
    @Transactional
    public int saveDeviceCBC(DeviceCBC deviceCBC, int cbcId) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE DeviceCBC");
        updateSql.append("SET SERIALNUMBER").eq(deviceCBC.getSerialNumber());
        updateSql.append(", ROUTEID").eq(deviceCBC.getRouteID());
        updateSql.append("WHERE DEVICEID").eq(cbcId);
        jdbcTemplate.update(updateSql);
        return cbcId;
    }
    
    @Override
    @Transactional
    public int saveYukonPao(String paoName, String disableFlag, int cbcId) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE YukonPAObject");
        updateSql.append("SET PAOName").eq(paoName);
        updateSql.append(", DisableFlag").eq(disableFlag);
        updateSql.append("WHERE PAObjectID").eq(cbcId);
        jdbcTemplate.update(updateSql);
        return cbcId;
    }
    
    @Override
    public List<DeviceScanRate> getDeviceScanRates(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT IntervalRate, ScanType, ScanGroup, AlternateRate");
        sql.append("FROM DeviceScanRate");
        sql.append("where DeviceID").eq(deviceId);
        ParameterizedRowMapper<DeviceScanRate> rowMapper = new ParameterizedRowMapper<DeviceScanRate>() {
            @Override
            public DeviceScanRate mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceScanRate deviceScanRate = new DeviceScanRate();
                deviceScanRate.setIntervalRate(rs.getInt("IntervalRate"));
                deviceScanRate.setScanType(rs.getString("ScanType"));
                deviceScanRate.setScanGroup(rs.getInt("ScanGroup"));
                deviceScanRate.setAlternateRate(rs.getInt("AlternateRate"));
                return deviceScanRate;
            }
        };
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    @Transactional
    public int saveDeviceDirectCommSettings(int portId,int cbcId) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE DeviceDirectCommSettings");
        updateSql.append("SET PORTID").eq(portId);
        updateSql.append("WHERE DEVICEID").eq(cbcId);
        jdbcTemplate.update(updateSql);
        return cbcId;
    }
    
    @Override
    public int saveDeviceScanRates(DeviceScanRate deviceScanRate,int deviceId,String type) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE DEVICESCANRATE");
        updateSql.append("SET INTERVALRATE").eq(deviceScanRate.getIntervalRate());
        updateSql.append(", SCANGROUP").eq(deviceScanRate.getScanGroup());
        updateSql.append(", AlternateRate").eq(deviceScanRate.getAlternateRate());
        updateSql.append("WHERE DEVICEID").eq(deviceId);
        updateSql.append("AND SCANTYPE").eq(type);
        jdbcTemplate.update(updateSql);
        return deviceId;
    }
}
