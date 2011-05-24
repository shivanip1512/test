package com.cannontech.amr.meter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MeterDaoImpl implements MeterDao, InitializingBean {
    private DBPersistentDao dbPersistentDao;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private JdbcOperations jdbcOps;
    private MeterRowMapper meterRowMapper;
    private PaoDao paoDao;
    private RolePropertyDao rolePropertyDao;
    
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

    public Meter getForId(Integer id) {
        try {
            return simpleJdbcTemplate.queryForObject(retrieveOneByIdSql, meterRowMapper, id);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unknown meter id " + id);
        }
    }
    
    public Meter getForYukonDevice(YukonDevice yukonDevice) {
        if (yukonDevice instanceof Meter) {
            return (Meter) yukonDevice;
        } else {
            return getForId(yukonDevice.getPaoIdentifier().getPaoId());
        }
    }

    public Meter getForMeterNumber(String meterNumber) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByMeterNumberSql,
                                                            meterRowMapper,
                                                            meterNumber);
            return meter;
        } catch (EmptyResultDataAccessException e) {
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
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown physical address " + address);
        }
    }

    public Meter getForPaoName(String paoName) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByPaoNameSql,
                                                            meterRowMapper,
                                                            paoName);
            return meter;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Unknown pao name " + paoName);
        }
    }

    public void save(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    public String getFormattedDeviceName(Meter device) throws IllegalArgumentException{
        
        MeterDisplayFieldEnum meterDisplayFieldEnum = 
        	rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEVICE_DISPLAY_TEMPLATE, MeterDisplayFieldEnum.class, null);

        String formattedName = new DisplayableMeter(device, meterDisplayFieldEnum).getName();
        return formattedName;
    }
    
    @Override
    public PaoLoader<DisplayablePao> getDisplayableDeviceLoader() {
        return new PaoLoader<DisplayablePao>() {
            @Override
            public Map<PaoIdentifier, DisplayablePao> getForPaos(Iterable<PaoIdentifier> identifiers) {
                MeterDisplayFieldEnum meterDisplayFieldEnum = 
                    rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEVICE_DISPLAY_TEMPLATE, MeterDisplayFieldEnum.class, null);

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
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);
               
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
    	
    	ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);
    	
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
    
    public int getMeterCount() {
    	
    	String sql = "SELECT COUNT(*) FROM DeviceMeterGroup";
    	
    	return jdbcOps.queryForInt(sql);
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
        dbPersistentDao.performDBChange(yukonPaobject, TransactionType.UPDATE);

    }

    public void setMeterRowMapper(MeterRowMapper meterRowMapper) {
        this.meterRowMapper = meterRowMapper;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

    @Required
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
    	this.rolePropertyDao = rolePropertyDao;
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
