package com.cannontech.amr.meter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.model.YukonMeter;
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
import com.cannontech.core.dao.impl.YukonPaoRowMapper;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
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

public class MeterDaoImpl implements MeterDao, InitializingBean {
    private JdbcOperations jdbcOps;
    private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private MeterRowMapper meterRowMapper;
    @Autowired private PaoDao paoDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    private String retrieveOneByIdSql;
    private String retrieveOneByMeterNumberSql;
    private String retrieveOneByPaoNameSql;
    private String retrieveOneByPhysicalAddressSql;

    @Override
    public void afterPropertiesSet() throws Exception {
        retrieveOneByIdSql = meterRowMapper.getSql() + "WHERE ypo.paObjectId = ? ";
        retrieveOneByMeterNumberSql = meterRowMapper.getSql() + "WHERE UPPER(DeviceMeterGroup.MeterNumber) = UPPER(?) ";
        retrieveOneByPaoNameSql = meterRowMapper.getSql() + "WHERE UPPER(ypo.PaoName) = UPPER(?) ";
        retrieveOneByPhysicalAddressSql = meterRowMapper.getSql() + "WHERE DeviceCarrierSettings.Address = ? ";
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
                newMeterInfo.getPaoType().getDatabaseRepresentation(), newMeterInfo.getDeviceId() });

        // Updates the meter's meter number
        sqlUpdate = " UPDATE DEVICEMETERGROUP" + " SET METERNUMBER = ?" + " WHERE DEVICEID = ? ";

        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getMeterNumber(),
                newMeterInfo.getDeviceId() });

        // Updates the meter's address
        sqlUpdate = " UPDATE DeviceCarrierSettings" + " SET Address = ?" + " WHERE DEVICEID = ? ";
        
        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getAddress(),
                newMeterInfo.getDeviceId() });
        
        // Updates the meter's route
        sqlUpdate = " UPDATE DeviceRoutes" + " SET RouteId = ?" + " WHERE DeviceId = ? ";
        
        jdbcOps.update(sqlUpdate, new Object[] { newMeterInfo.getRouteId(),
                newMeterInfo.getDeviceId() });

        sendDBChangeMessage(newMeterInfo);

    }

    @Override
    public Meter getForId(Integer id) {
        try {
            return yukonJdbcTemplate.queryForObject(retrieveOneByIdSql, meterRowMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unknown meter id " + id);
        }
    }
    
    @Override
    public YukonMeter getYukonMeterForId(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type, MeterNumber");
        sql.append("FROM YukonPaObject ypo");
        sql.append(  "JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DeviceId");
        sql.append("where ypo.PaobjectId").eq(id);
        
        
        YukonMeter yukonMeter = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<YukonMeter>() {
            public YukonMeter mapRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectID", "Type");
                String meterNumber = rs.getString("MeterNumber").trim();
                YukonMeter yukonMeter = new YukonMeter(paoIdentifier, meterNumber);
                
                return yukonMeter;
            }
        });
        return yukonMeter;
    }
    
    @Override
    public Meter getForYukonDevice(YukonDevice yukonDevice) {
        if (yukonDevice instanceof Meter) {
            return (Meter) yukonDevice;
        } else {
            return getForId(yukonDevice.getPaoIdentifier().getPaoId());
        }
    }

    @Override
    public Meter getForMeterNumber(String meterNumber) {
        try {
            Meter meter = yukonJdbcTemplate.queryForObject(retrieveOneByMeterNumberSql,
                                                            meterRowMapper,
                                                            meterNumber);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown meter number " + meterNumber);
        }
    }
    
    @Override
    public YukonMeter getYukonMeterForMeterNumber(String meterNumber) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAObjectID, ypo.Type, MeterNumber");
        sql.append("FROM YukonPaObject ypo");
        sql.append(  "JOIN DeviceMeterGroup dmg ON ypo.PAObjectID = dmg.DeviceId");
        sql.append("WHERE UPPER(dmg.MeterNumber)").eq(meterNumber.toUpperCase());
        
        try {
            PaoIdentifier pao = yukonJdbcTemplate.queryForObject(sql, new YukonPaoRowMapper());
            YukonMeter yukonMeter = new YukonMeter(pao, meterNumber);
            return yukonMeter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no meter matches " + meterNumber);
        }
    }
    
    @Override
    public Meter getForPhysicalAddress(String address) {
        try {
            Meter meter = yukonJdbcTemplate.queryForObject(retrieveOneByPhysicalAddressSql,
                                                            meterRowMapper,
                                                            address);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown physical address " + address);
        }
    }

    @Override
    public Meter getForPaoName(String paoName) {
        try {
            Meter meter = yukonJdbcTemplate.queryForObject(retrieveOneByPaoNameSql,
                                                            meterRowMapper,
                                                            paoName);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown pao name " + paoName);
        }
    }
    
    @Override
    public Map<PaoIdentifier, Meter> getPaoIdMeterMap(Iterable<PaoIdentifier> paos) {
        
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
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
        
        YukonRowMapper<Map.Entry<Integer, Meter>> mappingRowMapper =
            new YukonRowMapper<Map.Entry<Integer, Meter>>() {
                @Override
                public Entry<Integer, Meter> mapRow(YukonResultSet rs)
                        throws SQLException {
                    Meter meter = meterRowMapper.mapRow(rs.getResultSet(), 0);
                    return Maps.immutableEntry(meter.getDeviceId(), meter);
                }
        };

        Map<PaoIdentifier, Meter> meterMap =  template.mappedQuery(sqlGenerator, paos, mappingRowMapper, transform);
        
        return meterMap;
    }

    public void save(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    @Override
    public String getFormattedDeviceName(Meter device) throws IllegalArgumentException{
        
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

                List<Meter> metersForYukonDevices = getMetersForPaoIdentifiers(identifiers);

                Map<PaoIdentifier, DisplayablePao> result = Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());

                for (Meter meter : metersForYukonDevices) {
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

                List<Meter> metersForYukonDevices = getMetersForPaoIdentifiers(identifiers);

                Map<PaoIdentifier, DeviceCollectionReportDevice> result = Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());

                for (Meter meter : metersForYukonDevices) {
                    PaoIdentifier paoIdentifier = meter.getPaoIdentifier();
                    DeviceCollectionReportDevice dcrd = new DeviceCollectionReportDevice(paoIdentifier);
                    dcrd.setName(getFormattedDeviceName(meter));
                    dcrd.setAddress(meter.getAddress());
                    dcrd.setMeterNumber(meter.getMeterNumber());
                    dcrd.setRoute(meter.getRoute());
                    result.put(paoIdentifier, dcrd);
                }

                return result;
            }
        };
    }

    @Override
    public List<Meter> getMetersForYukonPaos(Iterable<? extends YukonPao> identifiers) {
        List<Meter> result = getMetersForPaoIdentifiers(identifiers);
        return Collections.unmodifiableList(result);
    }

    public <I extends YukonPao> List<Meter> getMetersForPaoIdentifiers(Iterable<I> identifiers) {
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
               
               SqlFragmentGenerator<I> sqlGenerator = new SqlFragmentGenerator<I>() {
                   public SqlFragmentSource generate(List<I> subList) {
                       ImmutableList<Integer> paoIdList = PaoUtils.asPaoIdList(subList);
                      SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
                      sql.append("WHERE ypo.paObjectId").in(paoIdList);
                      return sql;
                   }
               };
         
         ParameterizedRowMapper<Meter> rowMapper = new ParameterizedRowMapper<Meter>() {
             public Meter mapRow(ResultSet rs, int rowNum) throws SQLException {
               Meter meter = meterRowMapper.mapRow(rs, rowNum);
                 return meter;
             }
         };

         return template.query(sqlGenerator, identifiers, rowMapper);
     }

    @Override
    public List<Meter> getMetersForMeterNumbers(final List<String> meterNumbers) {

    	if (meterNumbers.size() == 0) {
    		return Collections.emptyList();
    	}
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
    	
    	List<Meter> meters = template.query(new SqlFragmentGenerator<String>() {
    		public SqlFragmentSource generate(List<String> subList) {
    		    
    		    List<String> subListUppercase = 
    		        Lists.transform(subList, new Function<String, String>() {
    		            public String apply(String str) {
    		                return str.toUpperCase();
    		            }
    		        });
    		    
    		    SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
    			sql.append("WHERE UPPER(DeviceMeterGroup.MeterNumber) IN (").appendArgumentList(subListUppercase).append(")");
    			return sql;
    		}
    	}, meterNumbers, meterRowMapper);
    	
    	
    	return meters;
    }

    /**
     * Returns a list of Meter objects. LastReceived is the minimum MeterNumber
     * (order ASC by MeterNumber) to return. MaxRecordCount is the maximum
     * number of records to return in the Meter list.
     */
    @Override
    public List<Meter> getMetersByMeterNumber(String lastReceived,
            int maxRecordCount) {
        if (lastReceived == null)
            lastReceived = "";

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(meterRowMapper.getSql());
        sqlBuilder.append("WHERE MeterNumber > ? ");
        sqlBuilder.append("ORDER BY MeterNumber");

        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters,
                                                                       meterRowMapper);
        jdbcOps.query(sqlBuilder.getSql(),
                      new Object[] { lastReceived },
                      new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount));
        return mspMeters;
    }

    /**
     * Returns a list of Meter objects. LastReceived is the minimum PaoName
     * (order ASC by PaoName) to return. MaxRecordCount is the maximum number of
     * records to return in the Meter list.
     */
    @Override
    public List<Meter> getMetersByPaoName(String lastReceived,
            int maxRecordCount) {
        if (lastReceived == null)
            lastReceived = "";

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder(meterRowMapper.getSql());
        sqlBuilder.append("WHERE PaoName > ? ");
        sqlBuilder.append("ORDER BY PaoName ");

        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters,
                                                                       meterRowMapper);
        jdbcOps.query(sqlBuilder.getSql(),
                      new Object[] { lastReceived },
                      new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount));
        return mspMeters;
    }
    
    @Override
    public int getMeterCount() {
    	
    	String sql = "SELECT COUNT(*) FROM DeviceMeterGroup";
    	
    	return jdbcOps.queryForInt(sql);
    }

    @Override
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
        dbPersistentDao.performDBChange(yukonPaobject, TransactionType.UPDATE);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
        jdbcOps = yukonJdbcTemplate.getJdbcOperations();
    }
}
