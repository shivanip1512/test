package com.cannontech.amr.meter.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.roles.yukon.ConfigurationRole;

public class MeterDaoImpl implements MeterDao {
    private SimpleJdbcOperations simpleJdbcTemplate;
    private JdbcOperations jdbcOps;
    private ParameterizedRowMapper<Meter> meterRowMapper;
    private RoleDao roleDao;
    
    String retrieveMeterSql = 
        "select YukonPaObject.*, DeviceMeterGroup.*, DeviceCarrierSettings.*, DeviceRoutes.*, yporoute.paoName as route "
        + "from YukonPaObject "
        + "join Device on YukonPaObject.paObjectId = Device.deviceId "
        + "join DeviceMeterGroup on Device.deviceId = DeviceMeterGroup.deviceId "
        + "left outer join DeviceCarrierSettings on Device.deviceId = DeviceCarrierSettings.deviceId "
        + "left outer join DeviceRoutes on Device.deviceId = DeviceRoutes.deviceId "
        + "left outer join YukonPaObject yporoute on DeviceRoutes.routeId = yporoute.paObjectId ";

    String retrieveOneByIdSql = retrieveMeterSql + "where YukonPaObject.paObjectId = ? ";
    String retrieveOneByMeterNumberSql = retrieveMeterSql + "where DeviceMeterGroup.MeterNumber = ? ";
    String retrieveOneByPaoNameSql = retrieveMeterSql + "where YukonPaobject.PaoName = ? ";
    
    public void delete(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    public Meter getForId(Integer id) {
        try {
            Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneByIdSql, meterRowMapper, id);
            return meter;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unknown meter id " + id);
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
    
    public String getFormattedDeviceName(Meter device) {
        TemplateProcessor templateProcessor = new SimpleTemplateProcessor();
        String formattingStr = roleDao.getGlobalPropertyValue(ConfigurationRole.DEVICE_DISPLAY_TEMPLATE);
        Validate.notNull(formattingStr, "Device display template role property does not exist.");
        Map<String, String> values = new HashMap<String, String>(6);
        String meterNumber = device.getMeterNumber();
        if (meterNumber == null) {
            meterNumber = "n/a";
        }
        values.put("meterNumber", meterNumber);
        values.put("name", device.getName());
        values.put("id", Integer.toString(device.getDeviceId()));
        values.put("address", device.getAddress());
        String result = templateProcessor.process(formattingStr, values);
        return result;
    }
    
    /**
     * Returns a list of Meter objects.  LastReceived is the minimum MeterNumber 
     * (order ASC by MeterNumber) to return.  MaxRecordCount is the maximum number of records
     * to return in the Meter list.
     */
    public List<Meter> getMetersByMeterNumber(String lastReceived, int maxRecordCount) {
        if( lastReceived == null)
            lastReceived = "";

        String sql = retrieveMeterSql + " AND MeterNumber > ? ORDER BY MeterNumber";
        
        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, meterRowMapper);
        jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount));
        return mspMeters;
    }

    /**
     * Returns a list of Meter objects.  LastReceived is the minimum PaoName 
     * (order ASC by PaoName) to return.  MaxRecordCount is the maximum number of records
     * to return in the Meter list.
     */
    public List<Meter> getMetersByPaoName(String lastReceived, int maxRecordCount) {
        if( lastReceived == null)
            lastReceived = "";

        String sql = retrieveMeterSql + " AND PaoName > ? ORDER BY PaoName";
        
        List<Meter> mspMeters = new ArrayList<Meter>();
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(mspMeters, meterRowMapper);
        jdbcOps.query(sql, new Object[]{lastReceived}, new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount));
        return mspMeters;
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
}
