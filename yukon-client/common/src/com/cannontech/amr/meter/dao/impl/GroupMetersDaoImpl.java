package com.cannontech.amr.meter.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.SqlProvidingRowMapper;
import com.cannontech.roles.yukon.ConfigurationRole;

public class GroupMetersDaoImpl implements GroupMetersDao {

    private SqlProvidingRowMapper<Meter> meterRowMapper;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private DeviceGroupProviderDao deviceGroupProviderDao;
    private RoleDao roleDao;
    
    private static Map<MeterDisplayFieldEnum, String> orderByMap = new HashMap<MeterDisplayFieldEnum, String>();
    static {
        orderByMap.put(MeterDisplayFieldEnum.DEVICE_NAME, "ypo.paoName");
        orderByMap.put(MeterDisplayFieldEnum.METER_NUMBER, "DeviceMeterGroup.meterNumber");
        orderByMap.put(MeterDisplayFieldEnum.ADDRESS, "DeviceCarrierSettings.address");
        orderByMap.put(MeterDisplayFieldEnum.ID, "ypo.paObjectId");
    }
    
    @Required
    public void setMeterRowMapper(SqlProvidingRowMapper<Meter> meterRowMapper) {
        this.meterRowMapper = meterRowMapper;
    }
    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    @Required
    public void setDeviceGroupProviderDao(
            DeviceGroupProviderDao deviceGroupProviderDao) {
        this.deviceGroupProviderDao = deviceGroupProviderDao;
    }
    
    private static String getOrderByFromMeterDisplayFieldEnum(MeterDisplayFieldEnum meterDisplayFieldEnum) {
        return orderByMap.get(meterDisplayFieldEnum);
    }
    
    private String getOrderBySql() {
        
        MeterDisplayFieldEnum meterDisplayFieldEnumVal = roleDao.getGlobalRolePropertyValue(MeterDisplayFieldEnum.class,
                                                                                      ConfigurationRole.DEVICE_DISPLAY_TEMPLATE);
        return getOrderByFromMeterDisplayFieldEnum(meterDisplayFieldEnumVal);
    }

    public List<Meter> getMetersByGroup(DeviceGroup group) {
        String sqlWhereClause = deviceGroupProviderDao.getDeviceGroupSqlWhereClause(group,
                                                                                    "Device.deviceId");
        String sql = meterRowMapper.getSql() + " where " + sqlWhereClause + " ORDER BY " + getOrderBySql();

        List<Meter> meterList = simpleJdbcTemplate.query(sql, meterRowMapper);

        return meterList;
    }
    
    private String getChildMetersByGroupSql(DeviceGroup group) {
        
        String sqlWhereClause = deviceGroupProviderDao.getChildDeviceGroupSqlWhereClause(group,
                                                                                         "Device.deviceId");
        return meterRowMapper.getSql() + " where " + sqlWhereClause + " ORDER BY " + getOrderBySql();
    }
    
    public List<Meter> getChildMetersByGroup(DeviceGroup group) {
        String sql = getChildMetersByGroupSql(group);

        List<Meter> meterList = simpleJdbcTemplate.query(sql, meterRowMapper);

        return meterList;
    }
    
    public List<Meter> getChildMetersByGroup(DeviceGroup group, final int maxRecordCount) {
        String sql = getChildMetersByGroupSql(group);

        final List<Meter> meterList = new ArrayList<Meter>();
        
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(meterList,
                                                                       meterRowMapper);

        MaxRowCalbackHandlerRse rse = new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount);
            
        simpleJdbcTemplate.getJdbcOperations().query(sql, rse);

        return meterList;
    }

}
