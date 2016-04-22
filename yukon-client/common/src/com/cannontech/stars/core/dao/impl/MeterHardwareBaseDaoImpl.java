package com.cannontech.stars.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.core.dao.MeterHardwareBaseDao;

public class MeterHardwareBaseDaoImpl implements MeterHardwareBaseDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public boolean hasSwitchAssigned(int meterId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append("from LMHardwareToMeterMapping");
        sql.append("where MeterInventoryId ").eq(meterId);

        return yukonJdbcTemplate.queryForInt(sql) > 0;
    }

    @Override
    public List<Integer> getSwitchAssignmentsForMeter(int meterId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select LMHardwareInventoryId");
        sql.append("from LMHardwareToMeterMapping");
        sql.append("where MeterInventoryId ").eq(meterId);

        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    @Override
    public int getInventoryId(int accountId, String meterNumber, int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ib.inventoryId");
        sql.append("from meterHardwareBase mhb, inventoryBase ib, ecToInventoryMapping map");
        sql.append("where mhb.inventoryId = ib.inventoryId");
        sql.append(    "and mhb.meterNumber").eq(meterNumber);
        sql.append(    "and ib.accountId").eq(accountId);
        sql.append(    "and ib.inventoryId = map.inventoryId");
        sql.append(    "and map.energyCompanyId").eq(ecId);

        return yukonJdbcTemplate.queryForInt(sql);
    }
}
