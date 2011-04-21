package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.google.common.collect.Sets;

public abstract class DeviceGroupProviderSqlBase extends DeviceGroupProviderBase {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private String countSql;
    private String deviceSql;
    
    private Logger log = YukonLogManager.getLogger(DeviceGroupProviderSqlBase.class); 
    
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
        
        SqlFragmentSource childDeviceGroupSqlWhereClause = getChildDeviceGroupSqlWhereClause(group, "d.deviceId");
        SqlStatementBuilder sql = new SqlStatementBuilder(countSql);
        sql.appendFragment(childDeviceGroupSqlWhereClause);
        int result = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
        return result;
    }
    
    @Override
    public int getDeviceCount(DeviceGroup group) {
        SqlFragmentSource deviceGroupSqlWhereClause = getDeviceGroupSqlWhereClause(group, "d.deviceId");
        SqlStatementBuilder sql = new SqlStatementBuilder(countSql);
        sql.appendFragment(deviceGroupSqlWhereClause);
        int result = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
        return result;
    }
    
    @Override
    public Set<SimpleDevice> getChildDevices(DeviceGroup group) {
        
        Set<SimpleDevice> deviceSet = new HashSet<SimpleDevice>();
        collectChildDevices(group, deviceSet, Integer.MAX_VALUE);
        return deviceSet;
    }
    
    @Override
    public Set<SimpleDevice> getChildDevices(DeviceGroup group, int maxSize) {
    	Set<SimpleDevice> result = Sets.newHashSetWithExpectedSize(maxSize == Integer.MAX_VALUE ? 200 : maxSize);
    	collectChildDevices(group, result, maxSize);
    	return result;
    }
    
    @Override
    public void collectChildDevices(DeviceGroup group, final Set<SimpleDevice> deviceSet, final int maxSize) {
        
        if (maxSize < Integer.MAX_VALUE) {
            log.debug("Collecting " + (maxSize - deviceSet.size()) + " child devices from group " + group.getFullName() + ".");
        }
        else {
            log.debug("Collecting all child devices from group " + group.getFullName() + ".");
        }
        SqlStatementBuilder sql = new SqlStatementBuilder(deviceSql);
        sql.appendFragment(getChildDeviceGroupSqlWhereClause(group, "ypo.paobjectId"));
        
        final ParameterizedRowMapper<SimpleDevice> mapper = new YukonDeviceRowMapper();
        
        ResultSetExtractor rse = new ResultSetExtractor() {

            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                
                int i = 0;
                while (deviceSet.size() < maxSize && rs.next()) {
                    SimpleDevice device = mapper.mapRow(rs, i);
                    deviceSet.add(device);
                    i++;
                }
                return null;
            }

        };
        
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rse);
    }
    
    @Override
    public Set<SimpleDevice> getDevices(DeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder(deviceSql);
        sql.appendFragment(getDeviceGroupSqlWhereClause(group, "ypo.paobjectId"));
        Set<SimpleDevice> result = new HashSet<SimpleDevice>();
        CollectionRowCallbackHandler<SimpleDevice> rch = new CollectionRowCallbackHandler<SimpleDevice>(new YukonDeviceRowMapper(), result);
        simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rch);
        return result;
    }
    
    @Autowired
    public final void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public SimpleJdbcTemplate getSimpleJdbcTemplate() {
        return simpleJdbcTemplate;
    }
}
