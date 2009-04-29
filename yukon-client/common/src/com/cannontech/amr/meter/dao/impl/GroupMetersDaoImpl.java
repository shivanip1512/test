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
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.ListRowCallbackHandler;
import com.cannontech.database.MaxRowCalbackHandlerRse;
import com.cannontech.database.SqlProvidingRowMapper;

public class GroupMetersDaoImpl implements GroupMetersDao {

    private SqlProvidingRowMapper<Meter> meterRowMapper;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private DeviceGroupProviderDao deviceGroupProviderDao;
    private RolePropertyDao rolePropertyDao;
    
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
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
        
        MeterDisplayFieldEnum meterDisplayFieldEnumVal = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEVICE_DISPLAY_TEMPLATE, 
                                                                                              MeterDisplayFieldEnum.class, null);
        return getOrderByFromMeterDisplayFieldEnum(meterDisplayFieldEnumVal);
    }

    public List<Meter> getMetersByGroup(DeviceGroup group) {
        SqlFragmentSource sql = getChildMetersByGroupSql(group);

        List<Meter> meterList = simpleJdbcTemplate.query(sql.getSql(), meterRowMapper, sql.getArguments());

        return meterList;
    }
    
    private SqlFragmentSource getChildMetersByGroupSql(DeviceGroup group) {
        
        SqlFragmentSource sqlWhereClause = deviceGroupProviderDao.getChildDeviceGroupSqlWhereClause(group,
                                                                                         "Device.deviceId");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        sql.append("where");
        sql.appendFragment(sqlWhereClause);
        sql.append("ORDER BY", getOrderBySql());

        return sql;
    }
    
    public List<Meter> getChildMetersByGroup(DeviceGroup group) {
        SqlFragmentSource sql = getChildMetersByGroupSql(group);

        List<Meter> meterList = simpleJdbcTemplate.query(sql.getSql(), meterRowMapper, sql.getArguments());

        return meterList;
    }
    
    public List<Meter> getChildMetersByGroup(DeviceGroup group, final int maxRecordCount) {
        SqlFragmentSource sql = getChildMetersByGroupSql(group);

        final List<Meter> meterList = new ArrayList<Meter>();
        
        ListRowCallbackHandler lrcHandler = new ListRowCallbackHandler(meterList,
                                                                       meterRowMapper);

        MaxRowCalbackHandlerRse rse = new MaxRowCalbackHandlerRse(lrcHandler, maxRecordCount);
            
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rse);

        return meterList;
    }

}
