package com.cannontech.amr.meter.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
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
import com.cannontech.database.SqlProvidingRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MeterDaoImpl implements MeterDao, InitializingBean {
    private DBPersistentDao dbPersistentDao;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private JdbcOperations jdbcOps;
    private SqlProvidingRowMapper<Meter> meterRowMapper;
    private PaoDao paoDao;
    private RolePropertyDao rolePropertyDao;
    
    private String retrieveOneByIdSql;
    private String retrieveOneByMeterNumberSql;
    private String retrieveOneByPaoNameSql;
    private String retrieveOneByPhysicalAddressSql;

    @Override
    public void afterPropertiesSet() throws Exception {

        retrieveOneByIdSql = meterRowMapper.getSql() + "where ypo.paObjectId = ? ";
        retrieveOneByMeterNumberSql = meterRowMapper.getSql() + "WHERE UPPER(DeviceMeterGroup.MeterNumber) = UPPER(?) ";
        retrieveOneByPaoNameSql = meterRowMapper.getSql() + "WHERE UPPER(ypo.PaoName) = UPPER(?) ";
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

                Map<PaoIdentifier, Meter> metersForYukonDevices = getMetersForYukonDevices(identifiers);

                Map<PaoIdentifier, DisplayablePao> result = Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());

                for (Entry<PaoIdentifier, Meter> entry : metersForYukonDevices.entrySet()) {
                    DisplayableMeter displayableMeter = new DisplayableMeter(entry.getValue(), meterDisplayFieldEnum);
                    PaoIdentifier key = entry.getKey();
                    result.put(key, displayableMeter);
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

                Map<PaoIdentifier, Meter> metersForYukonDevices = getMetersForYukonDevices(identifiers);

                Map<PaoIdentifier, DeviceCollectionReportDevice> result = Maps.newHashMapWithExpectedSize(metersForYukonDevices.size());

                for (Entry<PaoIdentifier, Meter> entry : metersForYukonDevices.entrySet()) {
                    PaoIdentifier key = entry.getKey();
                    DeviceCollectionReportDevice dcrd = new DeviceCollectionReportDevice(key.getPaoIdentifier());
                    Meter meter = entry.getValue();
                    dcrd.setName(getFormattedDeviceName(meter));
                    dcrd.setAddress(meter.getAddress());
                    dcrd.setMeterNumber(meter.getMeterNumber());
                    dcrd.setRoute(meter.getRoute());
                    result.put(key, dcrd);
                }

                return result;
            }
        };
    }
    
    public <I extends YukonPao> Map<I, Meter> getMetersForYukonDevices(Iterable<I> identifiers) {
    	final ImmutableMap<Integer, I> deviceLookup = Maps.uniqueIndex(identifiers, new Function<I, Integer>() {
    		@Override
    		public Integer apply(I device) {
    			return device.getPaoIdentifier().getPaoId();
    		}
    	});
    	ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
    	
    	List<Meter> meters = template.query(new SqlFragmentGenerator<Integer>() {
    		public SqlFragmentSource generate(List<Integer> subList) {
    			SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
    			sql.append("where ypo.paObjectId in (").appendList(subList).append(")");
    			return sql;
    		}
    	}, deviceLookup.keySet(), meterRowMapper);
    	
    	ImmutableMap<I, Meter> result = Maps.uniqueIndex(meters, new Function<Meter, I>() {
    		public I apply(Meter meter) {
    			return deviceLookup.get(meter.getPaoIdentifier().getPaoId());
    		}
    	});
    	
    	return result;
    }
    
    @Override
    public List<Meter> getMetersForMeterNumbers(final List<String> meterNumbers) {

    	if (meterNumbers.size() == 0) {
    		return Collections.emptyList();
    	}
    	
    	ChunkingSqlTemplate<String> template = new ChunkingSqlTemplate<String>(simpleJdbcTemplate);
    	
    	List<Meter> meters = template.query(new SqlFragmentGenerator<String>() {
    		public SqlFragmentSource generate(List<String> subList) {
    		    for (String meter : subList)
    		        meter = meter.toUpperCase();

    		    SqlStatementBuilder sql = new SqlStatementBuilder(meterRowMapper.getSql());
    			sql.append("WHERE UPPER(DeviceMeterGroup.MeterNumber) IN (").appendArgumentList(subList).append(")");
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
        sqlBuilder.append("WHERE UPPER(MeterNumber) > UPPER(?) ");
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
        sqlBuilder.append("WHERE UPPER(PaoName) > UPPER(?) ");
        sqlBuilder.append("ORDER BY PaoName ");

        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters,
                                                                       meterRowMapper);
        jdbcOps.query(sqlBuilder.getSql(),
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
