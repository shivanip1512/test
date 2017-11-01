package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.StringRowMapper;

public class LoadGroupsProvider extends BinningDeviceGroupProviderBase<String> {
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAOName ");
        sql.append("FROM LMGroup lmg ");
        sql.append("JOIN LMHardwareControlGroup lmhcg ON lmg.DeviceID=lmhcg.LMGroupID ");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectID = lmg.DeviceID ");
        sql.append("WHERE lmhcg.InventoryId IN (SELECT ib.InventoryId ");
        sql.append("FROM InventoryBase ib,YukonPAObject ypo ");
        sql.append("WHERE ib.DeviceID = ypo.PAObjectID AND ypo.Type").in(PaoType.getTwoWayLcrTypes());
        sql.append(") AND NOT LMHCG.groupEnrollStart IS NULL");
        sql.append("AND LMHCG.groupEnrollStop IS NULL");
        sql.append("ORDER BY PAOName");
        List<String> bins = getJdbcTemplate().query(sql, new StringRowMapper());
        return bins;
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_GROUP);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inv.DeviceID ");
        sql.append("FROM LMHardwareBase lmbase , ApplianceBase appbase,LMHardwareConfiguration hdconf, ");
        sql.append("InventoryBase inv LEFT OUTER JOIN DynamicLcrCommunications dynlcr ");
        sql.append("ON (inv.DeviceID = dynlcr.DeviceId) ");
        sql.append("WHERE inv.InventoryID = lmbase.InventoryID AND lmbase.InventoryID = hdconf.InventoryID ");
        sql.append("AND hdconf.ApplianceID = appbase.ApplianceID ");
        sql.append("AND lmbase.InventoryID IN (SELECT DISTINCT InventoryId ");
        sql.append("FROM LMHardwareConfiguration ");
        sql.append("WHERE AddressingGroupID IN (SELECT PAObjectID FROM YukonPAObject ");
        sql.append("WHERE type").in(paoTypes);
        sql.append("AND PAOName = ").appendArgument(bin);
        sql.append("))");
        return sql;
    }

    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT deviceid ");
        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN LMGroup lmg ON (ypo.PAObjectID = lmg.deviceid)");
        return sql;
    }

    @Override
    protected Set<String> getBinsForDevice(YukonDevice device) {
        return Collections.emptySet();
    }

}
