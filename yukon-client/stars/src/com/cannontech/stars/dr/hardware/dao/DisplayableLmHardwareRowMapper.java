package com.cannontech.stars.dr.hardware.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlUtils;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;

public class DisplayableLmHardwareRowMapper extends
        AbstractRowMapperWithBaseQuery<DisplayableLmHardware> {

    public DisplayableLmHardwareRowMapper() {
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT lmhw.inventoryId inventoryId, " +
                      "lmhw.manufacturerSerialNumber serialNumber," +
                      "yle.entryText deviceType," +
                      "ib.deviceLabel deviceLabel");
        retVal.append("FROM LMHardwareBase lmhw");
        retVal.append("JOIN InventoryBase ib ON ib.inventoryId = lmhw.inventoryId");
        retVal.append("JOIN YukonListEntry yle ON yle.entryId = lmhw.LMHardwareTypeId");
        return retVal;
    }

    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("ORDER BY LOWER(lmhw.manufacturerSerialNumber)");
        return retVal;
    }

    @Override
    public DisplayableLmHardware mapRow(ResultSet rs, int rowNum) throws SQLException {
        int inventoryId = rs.getInt("inventoryId");
        String serialNumber = rs.getString("serialNumber");
        String deviceType = rs.getString("deviceType");
        String deviceLabel = SqlUtils.convertDbValueToString(rs.getString("deviceLabel"));

        DisplayableLmHardware retVal = new DisplayableLmHardware();
        retVal.setInventoryId(inventoryId);
        retVal.setSerialNumber(serialNumber);
        retVal.setDeviceType(deviceType);
        retVal.setLabel(deviceLabel);

        return retVal;
    }
    
    public boolean needsWhere() {
        return true;
    }
}