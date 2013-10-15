package com.cannontech.amr.meter.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.SimpleMeterRowMapper;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterDaoImpl implements MeterDao {
    private JdbcOperations jdbcOps;
    private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private MeterRowMapper meterRowMapper;
    @Autowired private PaoDao paoDao;
    @Autowired private GlobalSettingDao globalSettingDao;

    
    public void delete(YukonMeter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    @Override
    @Transactional
    public void update(YukonMeter newMeterInfo) {
        String sqlUpdate;

        // Updates the meter's name and the meter's type
        sqlUpdate = " UPDATE YukonPAObject" + " SET PAOName = ?, Type = ?" + " WHERE PAObjectID = ?";
        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getName(),
                newMeterInfo.getPaoType().getDatabaseRepresentation(), newMeterInfo.getDeviceId() });

        // Updates the meter's meter number
        sqlUpdate = " UPDATE DEVICEMETERGROUP" + " SET METERNUMBER = ?" + " WHERE DEVICEID = ? ";

        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getMeterNumber(),
                newMeterInfo.getDeviceId() });

        if (newMeterInfo instanceof PlcMeter) {
            // Updates the meter's address
            sqlUpdate = " UPDATE DeviceCarrierSettings" + " SET Address = ?" + " WHERE DEVICEID = ? ";
            
            jdbcOps.update(sqlUpdate, new Object[] { ((PlcMeter)newMeterInfo).getAddress(),
                    newMeterInfo.getDeviceId() });
            // Updates the meter's route
            sqlUpdate = " UPDATE DeviceRoutes" + " SET RouteId = ?" + " WHERE DeviceId = ? ";
            
            jdbcOps.update(sqlUpdate, new Object[] { ((PlcMeter)newMeterInfo).getRouteId(),
                    newMeterInfo.getDeviceId() });
        }
        sendDBChangeMessage(newMeterInfo);
    }

    @Override
    public YukonMeter getForId(Integer id) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE ypo.paObjectId").eq(id);
            return yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unknown meter id " + id);
        }
    }
    
    @Override
    public SimpleMeter getSimpleMeterForId(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type, MeterNumber");
        sql.append("FROM YukonPaObject ypo");
        sql.append(  "JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DeviceId");
        sql.append("where ypo.PaobjectId").eq(id);
        
        SimpleMeter yukonMeter = yukonJdbcTemplate.queryForObject(sql, new SimpleMeterRowMapper());
        return yukonMeter;
    }
    
    @Override
    public PlcMeter getPlcMeterForId(int id) {
        YukonMeter meter = getForId(id);
        if (meter instanceof PlcMeter) {
            return (PlcMeter)meter;
        } else {
            throw new NotFoundException("Unknown Plc meter id " + id);
        }
    }

    @Override
    public RfnMeter getRfnMeterForId(int id) throws NotFoundException {

        YukonMeter meter = getForId(id);
        if (meter instanceof RfnMeter) {
            return (RfnMeter)meter;
        } else {
            throw new NotFoundException("Unknown RFN meter id " + id);
        }
    }
    
    @Override
    public YukonMeter getForYukonDevice(YukonDevice yukonDevice) {
        if (yukonDevice instanceof PlcMeter) {
            return (PlcMeter) yukonDevice;
        } else if (yukonDevice instanceof RfnMeter) {
            return (RfnMeter) yukonDevice;
        } else {
            return getForId(yukonDevice.getPaoIdentifier().getPaoId());
        }
    }

    @Override
    public YukonMeter getForMeterNumber(String meterNumber) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE UPPER(dmg.MeterNumber)").eq(meterNumber.toUpperCase());
            YukonMeter meter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown meter number " + meterNumber);
        }
    }

    @Override
    public PlcMeter getForPhysicalAddress(String address) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE dcs.Address").eq(address);
            YukonMeter meter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
            //Casting to PlcMeter because we forced an Address, making this only good for PLC meters
            return (PlcMeter)meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown physical address " + address);
        }
    }

    @Override
    public YukonMeter getForPaoName(String paoName) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            sql.append("WHERE UPPER(ypo.PaoName)").eq(paoName.toUpperCase());
            
            YukonMeter meter = yukonJdbcTemplate.queryForObject(sql, meterRowMapper);
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
    public Map<PaoIdentifier, YukonMeter> getPaoIdMeterMap(Iterable<PaoIdentifier> paos) {
        
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(meterRowMapper.getSql());
                sql.append("WHERE ypo.PAObjectId").in(subList);
                return sql;
            }
        };
        
        Function<PaoIdentifier, Integer> transform = new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier pao) {
                return pao.getPaoId();
            }
        };
        
        YukonRowMapper<Map.Entry<Integer, YukonMeter>> mappingRowMapper =
            new YukonRowMapper<Map.Entry<Integer, YukonMeter>>() {
                @Override
                public Entry<Integer, YukonMeter> mapRow(YukonResultSet rs)
                        throws SQLException {
                    YukonMeter meter = meterRowMapper.mapRow(rs);
                    return Maps.immutableEntry(meter.getDeviceId(), meter);
                }
        };

        Map<PaoIdentifier, YukonMeter> meterMap = template.mappedQuery(sqlGenerator, paos, mappingRowMapper, transform);
        
        return meterMap;
    }

    public void save(PlcMeter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    @Override
    public String getFormattedDeviceName(YukonMeter device) throws IllegalArgumentException{
        
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

                Map<PaoIdentifier, DisplayablePao> result = Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());

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

                Map<PaoIdentifier, DeviceCollectionReportDevice> result = Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());

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
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
               
               SqlFragmentGenerator<I> sqlGenerator = new SqlFragmentGenerator<I>() {
                   @Override
                public SqlFragmentSource generate(List<I> subList) {
                       ImmutableList<Integer> paoIdList = PaoUtils.asPaoIdList(subList);
                      SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
                      sql.append("WHERE ypo.paObjectId").in(paoIdList);
                      return sql;
                   }
               };

               YukonRowMapper<YukonMeter> rowMapper = new YukonRowMapper<YukonMeter>() {
                   @Override
                   public YukonMeter mapRow(YukonResultSet rs) throws SQLException {
                       YukonMeter meter = meterRowMapper.mapRow(rs);
                       return meter;
                   }
               };

         return template.query(sqlGenerator, identifiers, rowMapper);
     }

    @Override
    public List<YukonMeter> getMetersForMeterNumbers(final List<String> meterNumbers) {

    	if (meterNumbers.size() == 0) {
    		return Collections.emptyList();
    	}
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
    	
    	List<YukonMeter> meters = template.query(new SqlFragmentGenerator<String>() {
    		@Override
            public SqlFragmentSource generate(List<String> subList) {
    		    
    		    List<String> subListUppercase = 
    		        Lists.transform(subList, new Function<String, String>() {
    		            @Override
                        public String apply(String str) {
    		                return str.toUpperCase();
    		            }
    		        });
    		    
    		    SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
    			sql.append("WHERE UPPER(dmg.MeterNumber) IN (").appendArgumentList(subListUppercase).append(")");
    			return sql;
    		}
    	}, meterNumbers, meterRowMapper);
    	
    	
    	return meters;
    }
    
    @Override
    public int getMeterCount() {
    	
    	String sql = "SELECT COUNT(*) FROM DeviceMeterGroup";
    	
    	return jdbcOps.queryForInt(sql);
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

    private void sendDBChangeMessage(YukonMeter meter) {
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(meter.getDeviceId());
        YukonPAObject yukonPaobject = (YukonPAObject) dbPersistentDao.retrieveDBPersistent(liteYukonPAO);
        dbPersistentDao.performDBChange(yukonPaobject, TransactionType.UPDATE);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
        jdbcOps = yukonJdbcTemplate.getJdbcOperations();
    }
}
