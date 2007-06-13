package com.cannontech.amr.meter.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.roles.yukon.ConfigurationRole;

public class MeterDaoImpl implements MeterDao {
    private SimpleJdbcOperations simpleJdbcTemplate;
    private ParameterizedRowMapper<Meter> meterRowMapper;
    private RoleDao roleDao;
    
    String retrieveOneSql = 
        "select YukonPaObject.*, DeviceMeterGroup.*, DeviceCarrierSettings.*, DeviceRoutes.*, yporoute.paoName as route "
        + "from YukonPaObject "
        + "join Device on YukonPaObject.paObjectId = Device.deviceId "
        + "left join DeviceMeterGroup on Device.deviceId = DeviceMeterGroup.deviceId "
        + "left join DeviceCarrierSettings on Device.deviceId = DeviceCarrierSettings.deviceId "
        + "left join DeviceRoutes on Device.deviceId = DeviceRoutes.deviceId "
        + "left join YukonPaObject yporoute on DeviceRoutes.routeId = yporoute.paObjectId "
        + "where YukonPaObject.paObjectId = ?";

    public void delete(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }

    public Meter getForId(Integer id) {
        Meter meter = simpleJdbcTemplate.queryForObject(retrieveOneSql, meterRowMapper, id);
        return meter;
    }

    public void save(Meter object) {
        throw new UnsupportedOperationException("maybe someday...");
    }
    
    public String getFormattedDeviceName(Meter device) {
        // the formatting string could be from a role property
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
    
    public void setMeterRowMapper(ParameterizedRowMapper<Meter> meterRowMapper) {
        this.meterRowMapper = meterRowMapper;
    }
    
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
