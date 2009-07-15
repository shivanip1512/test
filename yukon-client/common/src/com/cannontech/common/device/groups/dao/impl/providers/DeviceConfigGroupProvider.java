package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.StringRowMapper;

public class DeviceConfigGroupProvider extends BinningDeviceGroupProviderBase<String> {

    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select distinct DeviceID");
        sql.append("from DeviceConfigurationDeviceMap dcdm");
        return sql;
    }

    @Override
    protected List<String> getAllBins() {
        String sql = "SELECT Name FROM DeviceConfiguration";
        List<String> bins = getJdbcTemplate().query(sql, new StringRowMapper());
        return bins;
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select dc.Name ");
        sql.append("from DeviceConfiguration dc");
        sql.append("join DeviceConfigurationDeviceMap dcdm on dcdm.DeviceConfigurationId = dc.DeviceConfigurationId");
        sql.append("where dcdm.DeviceId = ").appendArgument(new Integer(device.getDeviceId()));
        try {
            String bin = getJdbcTemplate().queryForObject(sql.getSql(), new StringRowMapper(), sql.getArguments());
            return Collections.singleton(bin);
        }catch(EmptyResultDataAccessException e) {
            return Collections.emptySet();
        }
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select DeviceID");
        sql.append("from DeviceConfigurationDeviceMap dcdm");
        sql.append("join DeviceConfiguration dc on dcdm.DeviceConfigurationId = dc.DeviceConfigurationId");
        sql.append("where dc.Name = ").appendArgument(bin);
        return sql;
    }
    
}
