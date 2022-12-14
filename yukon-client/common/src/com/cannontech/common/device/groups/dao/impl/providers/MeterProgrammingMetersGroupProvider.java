package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.TypeRowMapper;

public class MeterProgrammingMetersGroupProvider extends BinningDeviceGroupProviderBase<String> {

    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT DeviceID");
        sql.append("FROM MeterProgramAssignment mpa");
        return sql;
    }

    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder("SELECT Name FROM MeterProgram");
        return getJdbcTemplate().query(sql, TypeRowMapper.STRING);
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT mp.Name");
        sql.append("FROM MeterProgram mp");
        sql.append("JOIN MeterProgramAssignment mpa on mpa.Guid = mp.Guid");
        sql.append("WHERE mpa.DeviceId").eq(device.getPaoIdentifier().getPaoId());
        try {
            List<String> bins = getJdbcTemplate().query(sql, TypeRowMapper.STRING);
            return Set.copyOf(bins);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptySet();
        }
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceID");
        sql.append("FROM MeterProgramAssignment mpa");
        sql.append("JOIN MeterProgram mp on mpa.Guid = mp.Guid");
        sql.append("WHERE mp.Name").eq(bin);
        return sql;
    }

}