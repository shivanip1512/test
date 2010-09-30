package com.cannontech.stars.dr.hardware.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class LMHardwareBaseWithECIdRowMapper extends AbstractRowMapperWithBaseQuery<LMHardwareBase> {

    public LMHardwareBaseWithECIdRowMapper() {}

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT LMHB.InventoryId, LMHB.ManufacturerSerialNumber, LMHB.LMHardwareTypeId,");
        retVal.append(       "LMHB.RouteId, LMHB.ConfigurationId");
        retVal.append("FROM LMHardwareBase LMHB");
        retVal.append("JOIN ECToInventoryMapping ECIM ON ECIM.InventoryId = LMHB.InventoryId");
        return retVal;
    }

    @Override
    public boolean needsWhere() {
        return true;
    };
    
    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("ORDER BY LOWER(LMHB.ManufacturerSerialNumber)");
        return retVal;
    }

    @Override
    public LMHardwareBase mapRow(ResultSet rs, int rowNum) throws SQLException {
        final LMHardwareBase lmHardwareBase = new LMHardwareBase();
        lmHardwareBase.setInventoryId(rs.getInt("InventoryId"));
        lmHardwareBase.setManufacturerSerialNumber(SqlUtils.convertDbValueToString(rs, "ManufacturerSerialNumber"));
        lmHardwareBase.setLMHarewareTypeId(rs.getInt("LMHardwareTypeId"));
        lmHardwareBase.setRouteId(rs.getInt("RouteId"));
        lmHardwareBase.setConfigurationId(rs.getInt("ConfigurationId"));
        return lmHardwareBase;
    }
}
