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

public class LoadGroupsProvider extends BinningDeviceGroupProviderBase<String> {

    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAOName ");
        sql.append("FROM LMGroup lmg ");
        sql.append("  JOIN LMHardwareControlGroup lmhcg ON lmg.DeviceID=lmhcg.LMGroupID ");
        sql.append("  JOIN YukonPAObject ypo ON ypo.PAObjectID = lmg.DeviceID ");
        sql.append("WHERE lmhcg.InventoryId IN ");
        sql.append("    (SELECT ib.InventoryId ");
        sql.append("     FROM InventoryBase ib,YukonPAObject ypo ");
        sql.append("     WHERE ib.DeviceID = ypo.PAObjectID AND ypo.Type").in(PaoType.getTwoWayLcrTypes()).append(")");
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
        sql.append("  JOIN YukonPaobject lmGroup ON lmGroup.PAObjectId = hdconf.AddressingGroupId");
        sql.append("WHERE lmGroup.Category").eq_k(PaoCategory.DEVICE);
        sql.append("  AND lmGroup.PAOClass").eq_k(PaoClass.GROUP);
        sql.append("  AND lmGroup.PAOName").eq(bin);
        return sql;
    }

    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaobjectId");
        sql.append("FROM YukonPaobject");
        sql.append("WHERE Category").eq_k(PaoCategory.DEVICE);
        sql.append("  AND PAOClass").eq_k(PaoClass.GROUP);
        return sql;
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        return Collections.emptySet();
    }

}
