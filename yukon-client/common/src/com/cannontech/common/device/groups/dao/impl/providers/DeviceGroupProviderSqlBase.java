package com.cannontech.common.device.groups.dao.impl.providers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.collect.Sets;

public abstract class DeviceGroupProviderSqlBase extends DeviceGroupProviderBase {
    @Autowired protected YukonJdbcTemplate jdbcTemplate;
    private String countSql;
    private String deviceSql;
    
    private final Logger log = YukonLogManager.getLogger(DeviceGroupProviderSqlBase.class); 
    
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
        int result = jdbcTemplate.queryForInt(sql);
        return result;
    }
    
    @Override
    public int getDeviceCount(DeviceGroup group) {
        SqlFragmentSource deviceGroupSqlWhereClause = getDeviceGroupSqlWhereClause(group, "d.deviceId");
        SqlStatementBuilder sql = new SqlStatementBuilder(countSql);
        sql.appendFragment(deviceGroupSqlWhereClause);
        int result = jdbcTemplate.queryForInt(sql);
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
        
        final RowMapper<SimpleDevice> mapper = new YukonDeviceRowMapper();
        
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
        
        jdbcTemplate.query(sql, rse);
    }
    
    @Override
    public Set<SimpleDevice> getDevices(DeviceGroup group) {
        SqlStatementBuilder sql = new SqlStatementBuilder(deviceSql);
        sql.appendFragment(getDeviceGroupSqlWhereClause(group, "ypo.paobjectId"));
        Set<SimpleDevice> result = new HashSet<SimpleDevice>();
        CollectionRowCallbackHandler<SimpleDevice> rch = new CollectionRowCallbackHandler<SimpleDevice>(new YukonDeviceRowMapper(), result);
        jdbcTemplate.query(sql, rch);
        return result;
    }

}
