package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public abstract class DeviceGroupProviderSqlBase extends DeviceGroupProviderBase {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    private String countSql;
    private String deviceSql;
    
    {
        SqlStatementBuilder countBuilder = new SqlStatementBuilder();
        countBuilder.append("select count(*)");
        countBuilder.append("from Device d");
        countBuilder.append("where ");
        countSql = countBuilder.toString();
        
        SqlStatementBuilder deviceBuilder = new SqlStatementBuilder();
        deviceBuilder.append("select distinct ypo.paobjectId, ypo.type");
        deviceBuilder.append("from YukonPaObject ypo");
        deviceBuilder.append("join Device d on (d.deviceId = ypo.paobjectId)");
        deviceBuilder.append("where ");
        deviceSql = deviceBuilder.toString();
    }
    

    @Override
    public int getChildDeviceCount(DeviceGroup group) {
        String sql = countSql + getChildDeviceGroupSqlWhereClause(group, "d.deviceId");
        int result = simpleJdbcTemplate.queryForInt(sql);
        return result;
    }
    
    @Override
    public int getDeviceCount(DeviceGroup group) {
        String sql = countSql + getDeviceGroupSqlWhereClause(group, "d.deviceId");
        int result = simpleJdbcTemplate.queryForInt(sql);
        return result;
    }
    
    @Override
    public Set<YukonDevice> getChildDevices(DeviceGroup group) {
        String sql = deviceSql + getChildDeviceGroupSqlWhereClause(group, "ypo.paobjectId");
        Set<YukonDevice> result = new HashSet<YukonDevice>();
        CollectionRowCallbackHandler<YukonDevice> rch = new CollectionRowCallbackHandler<YukonDevice>(new YukonDeviceRowMapper(paoGroupsWrapper), result);
        simpleJdbcTemplate.getJdbcOperations().query(sql, rch);
        return result;
    }
    
    @Override
    public Set<YukonDevice> getDevices(DeviceGroup group) {
        String sql = deviceSql + getDeviceGroupSqlWhereClause(group, "ypo.paobjectId");
        Set<YukonDevice> result = new HashSet<YukonDevice>();
        CollectionRowCallbackHandler<YukonDevice> rch = new CollectionRowCallbackHandler<YukonDevice>(new YukonDeviceRowMapper(paoGroupsWrapper), result);
        simpleJdbcTemplate.getJdbcOperations().query(sql, rch);
        return result;
    }
    
    @Autowired
    public final void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public SimpleJdbcTemplate getSimpleJdbcTemplate() {
        return simpleJdbcTemplate;
    }
    
    @Autowired
    public final void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    public PaoGroupsWrapper getPaoGroupsWrapper() {
        return paoGroupsWrapper;
    }

}
