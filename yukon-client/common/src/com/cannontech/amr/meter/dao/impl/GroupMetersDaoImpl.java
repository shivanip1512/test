package com.cannontech.amr.meter.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.GroupMetersDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class GroupMetersDaoImpl implements GroupMetersDao {

    @Autowired private MeterRowMapper meterRowMapper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DeviceGroupProviderDao deviceGroupProviderDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    private static Map<MeterDisplayFieldEnum, String> orderByMap = new HashMap<MeterDisplayFieldEnum, String>();
    static {
        orderByMap.put(MeterDisplayFieldEnum.DEVICE_NAME, "ypo.paoName");
        orderByMap.put(MeterDisplayFieldEnum.METER_NUMBER, "dmg.meterNumber");
        orderByMap.put(MeterDisplayFieldEnum.ADDRESS, "dcs.address");
        orderByMap.put(MeterDisplayFieldEnum.ID, "ypo.paObjectId");
    }
    
    private static String getOrderByFromMeterDisplayFieldEnum(MeterDisplayFieldEnum meterDisplayFieldEnum) {
        return orderByMap.get(meterDisplayFieldEnum);
    }
    
    private String getOrderBySql() {
        
        MeterDisplayFieldEnum meterDisplayFieldEnumVal = globalSettingDao.getEnum(GlobalSettingType.DEVICE_DISPLAY_TEMPLATE, 
                                                                                              MeterDisplayFieldEnum.class);
        return getOrderByFromMeterDisplayFieldEnum(meterDisplayFieldEnumVal);
    }

    private SqlFragmentSource getChildMetersByGroupSql(DeviceGroup group) {
        
        SqlFragmentSource sqlWhereClause = deviceGroupProviderDao.getChildDeviceGroupSqlWhereClause(group,
                                                                                         "d.deviceId");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        sql.append("where");
        sql.appendFragment(sqlWhereClause);
        sql.append("ORDER BY", getOrderBySql());

        return sql;
    }
    
    @Override
    public List<YukonMeter> getChildMetersByGroup(DeviceGroup group) {
        SqlFragmentSource sql = getChildMetersByGroupSql(group);

        List<YukonMeter> meterList = jdbcTemplate.query(sql,  meterRowMapper);

        return meterList;
    }
}
