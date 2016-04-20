package com.cannontech.amr.meter.dao.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.meter.model.IedMeter;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterDaoImpl implements MeterDao {
    
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Autowired private MeterRowMapper meterRowMapper;
    
    private YukonRowMapper<SimpleMeter> simpleMeterMapper = new YukonRowMapper<SimpleMeter>() {

        @Override
        public SimpleMeter mapRow(YukonResultSet rs) throws SQLException {
            
            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("paobjectId",  "type");
            String meterNumber = rs.getString("meterNumber");
            
            SimpleMeter simpleMeter = new SimpleMeter(paoIdentifier, meterNumber);
            return simpleMeter;
        }
    };
    
    @Override
    @Transactional
    public void update(YukonMeter meter) {
        // Update the meter's name and the meter's type
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonPaobject")
        .set("PaoName", meter.getName(), 
             "Type", meter.getPaoType(), 
             "DisableFlag", YNBoolean.valueOf(meter.isDisabled()));
        sql.append("WHERE PaobjectId").eq(meter.getDeviceId());
        
        jdbcTemplate.update(sql);
        
        // Update the meter's meter number
        sql = new SqlStatementBuilder();
        sql.append("UPDATE DeviceMeterGroup").set("MeterNumber", meter.getMeterNumber());
        sql.append("WHERE DeviceId").eq(meter.getDeviceId());
        jdbcTemplate.update(sql);
        
        if (meter instanceof PlcMeter) {
            // Update the meter's address
            sql = new SqlStatementBuilder();
            sql.append("UPDATE DeviceCarrierSettings").set("Address", ((PlcMeter) meter).getAddress());
            sql.append("WHERE DeviceId").eq(meter.getDeviceId());
            jdbcTemplate.update(sql);
            
            // Update the meter's route
            sql = new SqlStatementBuilder();
            sql.append("UPDATE DeviceRoutes").set("RouteId", ((PlcMeter) meter).getRouteId());
            sql.append("WHERE DeviceId").eq(meter.getDeviceId());
            jdbcTemplate.update(sql);
        } else if (meter instanceof RfnMeter) {
            // Update the rfn address
            RfnIdentifier rfn = ((RfnMeter) meter).getRfnIdentifier();
            sql = new SqlStatementBuilder();
            sql.append("UPDATE RfnAddress")
            .set("SerialNumber", rfn.getSensorSerialNumber(),
                 "Manufacturer", rfn.getSensorManufacturer(),
                 "Model", rfn.getSensorModel());
            sql.append("WHERE DeviceId").eq(meter.getDeviceId());
            try {
                jdbcTemplate.update(sql);
            } catch (DataIntegrityViolationException e) {
                throw new DuplicateException("Duplicate rfn address.", e);
            }
        } else if (meter instanceof IedMeter) {
            // do nothing special
        }
        
        sendDBChangeMessage(meter);
    }
    
    @Override
    public YukonMeter getForId(int id) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE ypo.paObjectId").eq(id);
            return jdbcTemplate.queryForObject(sql, meterRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unknown meter id " + id);
        }
    }
    
    @Override
    public List<SimpleMeter> getAllSimpleMeters() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type, MeterNumber");
        sql.append("FROM YukonPaObject ypo");
        sql.append("JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DeviceId");
        
        List<SimpleMeter> meters = jdbcTemplate.query(sql, simpleMeterMapper);
        
        return meters;
    }
    
    @Override
    public SimpleMeter getSimpleMeterForId(int id) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type, MeterNumber");
        sql.append("FROM YukonPaObject ypo");
        sql.append("JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DeviceId");
        sql.append("where ypo.PaobjectId").eq(id);
        
        SimpleMeter yukonMeter = jdbcTemplate.queryForObject(sql, simpleMeterMapper);
        
        return yukonMeter;
    }
    
    @Override
    public PlcMeter getPlcMeterForId(int id) {
        YukonMeter meter = getForId(id);
        if (meter instanceof PlcMeter) {
            return (PlcMeter) meter;
        }
        
        throw new NotFoundException("Unknown Plc meter id " + id);
    }
    
    @Override
    public RfnMeter getRfnMeterForId(int id) throws NotFoundException {
        
        YukonMeter meter = getForId(id);
        if (meter instanceof RfnMeter) {
            return (RfnMeter) meter;
        }
        
        throw new NotFoundException("Unknown RFN meter id " + id);
    }
    
    @Override
    public YukonMeter getForMeterNumber(String meterNumber) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE UPPER(dmg.MeterNumber)").eq(meterNumber.toUpperCase());
            YukonMeter meter = jdbcTemplate.queryForObject(sql, meterRowMapper);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown meter number " + meterNumber);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Duplicate meters were found for this meter number " + meterNumber);
        }
    }
    
    @Override
    public List<YukonMeter> getForMeterNumber(String meterNumber, Integer... excludedIds) {
            
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        sql.append("WHERE UPPER(dmg.MeterNumber)").eq(meterNumber.toUpperCase());
        if (excludedIds != null && excludedIds.length != 0) {
            sql.append("AND ypo.PAObjectId").notIn(Arrays.asList(excludedIds));
        }
        List<YukonMeter> meters = jdbcTemplate.query(sql, meterRowMapper);
        
        return meters;
    }
    
    @Override
    public YukonMeter getForPaoName(String paoName) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE UPPER(ypo.PaoName)").eq(paoName.toUpperCase());

            YukonMeter meter = jdbcTemplate.queryForObject(sql, meterRowMapper);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown pao name " + paoName);
        }
    }
    
    @Override
    public YukonMeter findForPaoName(String paoName) {
        try {
            return getForPaoName(paoName);
        } catch (NotFoundException e) {
            return null;
        }
    }
    
    @Override
    public String getFormattedDeviceName(YukonMeter device) throws IllegalArgumentException {
        MeterDisplayFieldEnum meterDisplayFieldEnum =
            globalSettingDao.getEnum(GlobalSettingType.DEVICE_DISPLAY_TEMPLATE, MeterDisplayFieldEnum.class);
            
        String formattedName = new DisplayableMeter(device, meterDisplayFieldEnum).getName();
        return formattedName;
    }
    
    @Override
    public PaoLoader<DisplayablePao> getDisplayableDeviceLoader() {
        return new PaoLoader<DisplayablePao>() {
            @Override
            public Map<PaoIdentifier, DisplayablePao> getForPaos(Iterable<PaoIdentifier> identifiers) {
                MeterDisplayFieldEnum meterDisplayFieldEnum =
                    globalSettingDao.getEnum(GlobalSettingType.DEVICE_DISPLAY_TEMPLATE, MeterDisplayFieldEnum.class);
                
                List<YukonMeter> metersForYukonDevices = getMetersForPaoIdentifiers(identifiers);
                
                Map<PaoIdentifier, DisplayablePao> result =
                    Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());
                
                for (YukonMeter meter : metersForYukonDevices) {
                    DisplayableMeter displayableMeter = new DisplayableMeter(meter, meterDisplayFieldEnum);
                    result.put(meter.getPaoIdentifier(), displayableMeter);
                }
                
                return result;
            }
        };
    }
    
    @Override
    public PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader() {
        return new PaoLoader<DeviceCollectionReportDevice>() {
            @Override
            public Map<PaoIdentifier, DeviceCollectionReportDevice> getForPaos(Iterable<PaoIdentifier> identifiers) {
                
                List<YukonMeter> metersForYukonDevices = getMetersForPaoIdentifiers(identifiers);
                
                Map<PaoIdentifier, DeviceCollectionReportDevice> result =
                    Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());
                
                for (YukonMeter meter : metersForYukonDevices) {
                    PaoIdentifier paoIdentifier = meter.getPaoIdentifier();
                    DeviceCollectionReportDevice dcrd = new DeviceCollectionReportDevice(paoIdentifier);
                    dcrd.setName(getFormattedDeviceName(meter));
                    dcrd.setAddress(meter.getSerialOrAddress());
                    dcrd.setMeterNumber(meter.getMeterNumber());
                    dcrd.setRoute(meter.getRoute());
                    result.put(paoIdentifier, dcrd);
                }
                
                return result;
            }
        };
    }
    
    @Override
    public List<YukonMeter> getMetersForYukonPaos(Iterable<? extends YukonPao> identifiers) {
        List<YukonMeter> result = getMetersForPaoIdentifiers(identifiers);
        return Collections.unmodifiableList(result);
    }
    
    private <I extends YukonPao> List<YukonMeter> getMetersForPaoIdentifiers(Iterable<I> identifiers) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        
        SqlFragmentGenerator<I> sqlGenerator = new SqlFragmentGenerator<I>() {
            @Override
            public SqlFragmentSource generate(List<I> subList) {
                ImmutableList<Integer> paoIdList = PaoUtils.asPaoIdList(subList);
                SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
                sql.append("WHERE ypo.paObjectId").in(paoIdList);
                return sql;
            }
        };
        return template.query(sqlGenerator, identifiers, meterRowMapper);
    }
    
    @Override
    public List<YukonMeter> getMetersForMeterNumbers(final List<String> meterNumbers) {
        if (meterNumbers.size() == 0) {
            return Collections.emptyList();
        }
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        
        SqlFragmentGenerator<String> fragmentGenerator = new SqlFragmentGenerator<String>() {
            @Override
            public SqlFragmentSource generate(List<String> subList) {
                
                List<String> subListUppercase = Lists.transform(subList, new Function<String, String>() {
                    @Override
                    public String apply(String str) {
                        return str.toUpperCase();
                    }
                });
                
                SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
                sql.append("WHERE UPPER(dmg.MeterNumber) IN (").appendArgumentList(subListUppercase).append(")");
                return sql;
            }
        };
        List<YukonMeter> meters = template.query(fragmentGenerator, meterNumbers, meterRowMapper);
        
        return meters;
    }
    
    @Override
    public ListMultimap<String, YukonMeter> getMetersMapForMeterNumbers(final List<String> meterNumbers) {
        List<YukonMeter> meters = getMetersForMeterNumbers(meterNumbers);
        ListMultimap<String, YukonMeter> metersMap = ArrayListMultimap.create();
        for (YukonMeter yukonMeter : meters) {
            metersMap.put(yukonMeter.getMeterNumber(), yukonMeter);
        }
        return metersMap;
    }

    @Override
    public int getMeterCount() {
        SqlStatementBuilder sql = new SqlStatementBuilder("SELECT COUNT(*) FROM DeviceMeterGroup");
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public Comparator<YukonMeter> getMeterComparator() {
        final NaturalOrderComparator naturalOrderComparator = new NaturalOrderComparator();
        return new Comparator<YukonMeter>() {
            @Override
            public int compare(YukonMeter o1, YukonMeter o2) {
                String n1 = getFormattedDeviceName(o1);
                String n2 = getFormattedDeviceName(o2);
                return naturalOrderComparator.compare(n1, n2);
            }
        };
    }
    
    @Override
    public List<Integer> getMetersWithDisconnectCollarAddress(Iterable<Integer> ids) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DeviceID FROM DeviceMCT400Series ");
                sql.append("WHERE DeviceID").in(subList);
                return sql;
            }
        };
        List<Integer> deviceIds = template.query(sqlGenerator, ids, RowMapper.INTEGER);
        return deviceIds;
    }
    
    private void sendDBChangeMessage(YukonMeter meter) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject) dbPersistentDao.retrieveDBPersistent(liteYukonPAO);
        dbPersistentDao.performDBChange(yukonPaobject, TransactionType.UPDATE);
    }
    
    /**
     * Returns true if the DeviceMCT400Series table has a row returned for mctID, else false.
     * @param mctID
     * @return
     */
    public Integer getDisconnectAddress(Integer mctID) {
        
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DisconnectAddress FROM DeviceMCT400Series ");
        sql.append("WHERE DeviceID = ").appendArgument(mctID);
        Integer address;
        try {
            address = template.queryForInt(sql.getSql(), sql.getArguments());
        } catch (EmptyResultDataAccessException e) {
            // if no results, then it doesn't exist
            return null;
        }
        return address;
    }
    
}