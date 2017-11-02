package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;

public class LoadProgramsProvider extends BinningDeviceGroupProviderBase<String> {

    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAOName ");
        sql.append("FROM LMProgramWebPublishing pwp JOIN YukonWebConfiguration ywc ");
        sql.append("ON pwp.WebsettingsID = ywc.ConfigurationID ");
        sql.append("  JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID ");
        sql.append("  JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID ");
        sql.append("  JOIN LMHardwareControlGroup lmhcg ON lmhcg.ProgramID = pwp.ProgramID ");
        sql.append("WHERE lmhcg.InventoryId IN ");
        sql.append("    (SELECT ib.InventoryId ");
        sql.append("     FROM InventoryBase ib,YukonPAObject ypo ");
        sql.append("     WHERE ib.DeviceID=ypo.PAObjectID AND ypo.Type").in(PaoType.getTwoWayLcrTypes()).append(")");
        sql.append("  AND LMHCG.groupEnrollStart IS NOT NULL");
        sql.append("  AND LMHCG.groupEnrollStop IS NULL");
        sql.append("ORDER BY PAOName");
        List<String> bins = getJdbcTemplate().query(sql, TypeRowMapper.STRING);
        return bins;
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT inv.DeviceId");
        sql.append("FROM InventoryBase inv JOIN LMHardwareBase lmbase ON inv.InventoryId = lmbase.InventoryId");
        sql.append("  JOIN LMHardwareConfiguration hdconf ON lmbase.InventoryId = hdconf.InventoryId");
        sql.append("  JOIN ApplianceBase appbase ON hdconf.ApplianceId = appbase.ApplianceId");
        sql.append("  JOIN LMHardwareConfiguration lmhc ON lmbase.InventoryId = lmhc.InventoryId");
        sql.append("  JOIN LMProgramDirectGroup LMPDG ON LMPDG.LMGroupDeviceId= lmhc.AddressingGroupId");
        sql.append("  JOIN YukonPaobject ypo ON ypo.PAObjectId = LMPDG.DeviceId");
        sql.append("WHERE Category").eq_k(PaoCategory.LOADMANAGEMENT);
        sql.append("  AND PAOClass").eq_k(PaoClass.LOADMANAGEMENT);
        sql.append("  AND PAOName").eq(bin);
        return sql;
    }

    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT LMPDG.LMGroupDeviceId ");
        sql.append("FROM LMProgramDirectGroup LMPDG ");
        sql.append("WHERE LMPDG.DeviceId ");
        sql.append("IN (SELECT deviceid FROM YukonPAObject ypo JOIN LMPROGRAM lmp ON (ypo.PAObjectID = lmp.deviceid))");
        return sql;
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        return Collections.emptySet();
    }

}