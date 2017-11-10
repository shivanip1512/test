package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;

public class LoadProgramsProvider extends BinningDeviceGroupProviderBase<String> {

    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT  DISTINCT lmProg.PAOName");
        sql.append("FROM InventoryBase inv JOIN LMHardwareBase lmbase ON inv.InventoryId = lmbase.InventoryId");
        sql.append("  JOIN LMHardwareConfiguration hdconf ON lmbase.InventoryId = hdconf.InventoryId");
        sql.append("  JOIN LMProgramDirectGroup lmpdg ON lmpdg.LMGroupDeviceId= hdconf.AddressingGroupId");
        sql.append("  JOIN YukonPaobject lmProg ON lmProg.PAObjectId = lmpdg.DeviceId");
        sql.append("WHERE Category").eq_k(PaoCategory.LOADMANAGEMENT);
        sql.append("  AND PAOClass").eq_k(PaoClass.LOADMANAGEMENT);
        sql.append("  AND inv.DeviceId").neq(0);
        List<String> bins = getJdbcTemplate().query(sql, TypeRowMapper.STRING);
        return bins;
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT inv.DeviceId");
        sql.append("FROM InventoryBase inv JOIN LMHardwareBase lmbase ON inv.InventoryId = lmbase.InventoryId");
        sql.append("  JOIN LMHardwareConfiguration hdconf ON lmbase.InventoryId = hdconf.InventoryId");
        sql.append("  JOIN LMProgramDirectGroup lmpdg ON lmpdg.LMGroupDeviceId= hdconf.AddressingGroupId");
        sql.append("  JOIN YukonPaobject ypo ON ypo.PAObjectId = lmpdg.DeviceId");
        sql.append("WHERE Category").eq_k(PaoCategory.LOADMANAGEMENT);
        sql.append("  AND PAOClass").eq_k(PaoClass.LOADMANAGEMENT);
        sql.append("  AND inv.DeviceId").neq_k(0);
        sql.append("  AND PAOName").eq(bin);
        return sql;
    }

    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT  DISTINCT inv.DeviceId");
        sql.append("FROM InventoryBase inv JOIN LMHardwareBase lmbase ON inv.InventoryId = lmbase.InventoryId");
        sql.append("  JOIN LMHardwareConfiguration hdconf ON lmbase.InventoryId = hdconf.InventoryId");
        sql.append("  JOIN LMProgramDirectGroup lmpdg ON lmpdg.LMGroupDeviceId= hdconf.AddressingGroupId");
        sql.append("  JOIN YukonPaobject lmProg ON lmProg.PAObjectId = lmpdg.DeviceId");
        sql.append("WHERE Category").eq_k(PaoCategory.LOADMANAGEMENT);
        sql.append("  AND PAOClass").eq_k(PaoClass.LOADMANAGEMENT);
        sql.append("  AND inv.DeviceId").neq(0);
        return sql;
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        return Collections.emptySet();
    }

}