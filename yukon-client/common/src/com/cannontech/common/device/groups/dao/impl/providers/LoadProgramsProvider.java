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

public class LoadProgramsProvider extends BinningDeviceGroupProviderBase<String> {
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    protected List<String> getAllBins() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAOName ");
        sql.append("FROM LMProgramWebPublishing pwp JOIN YukonWebConfiguration ywc ");
        sql.append("ON pwp.WebsettingsID = ywc.ConfigurationID ");
        sql.append("JOIN YukonPAObject ypo ON ypo.PAObjectID = pwp.DeviceID ");
        sql.append("JOIN YukonListEntry yle ON yle.EntryID = pwp.ChanceOfControlID ");
        sql.append("JOIN LMHardwareControlGroup lmhcg ON lmhcg.ProgramID = pwp.ProgramID ");
        sql.append("WHERE lmhcg.InventoryId IN (SELECT ib.InventoryId ");
        sql.append("FROM InventoryBase ib,YukonPAObject ypo ");
        sql.append("WHERE ib.DeviceID=ypo.PAObjectID AND ypo.Type").in(PaoType.getTwoWayLcrTypes());
        sql.append(") AND NOT LMHCG.groupEnrollStart IS NULL");
        sql.append("AND LMHCG.groupEnrollStop IS NULL");
        sql.append("ORDER BY PAOName");
        List<String> bins = getJdbcTemplate().query(sql, new StringRowMapper());
        return bins;
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(String bin) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inv.DeviceID ");
        sql.append("FROM LMHardwareBase lmbase , ApplianceBase appbase,LMHardwareConfiguration hdconf, ");
        sql.append("InventoryBase inv LEFT OUTER JOIN DynamicLcrCommunications dynlcr ");
        sql.append("ON (inv.DeviceID = dynlcr.DeviceId) ");
        sql.append("WHERE inv.InventoryID = lmbase.InventoryID AND lmbase.InventoryID = hdconf.InventoryID ");
        sql.append("AND hdconf.ApplianceID = appbase.ApplianceID ");
        sql.append("AND lmbase.InventoryID IN (SELECT DISTINCT InventoryId ");
        sql.append("FROM LMHardwareConfiguration ");
        sql.append("WHERE AddressingGroupID IN (SELECT DISTINCT LMPDG.LMGroupDeviceId ");
        sql.append("FROM LMProgramDirectGroup LMPDG ");
        sql.append("WHERE LMPDG.DeviceId = (SELECT paObjectId FROM yukonPAObject ");
        sql.append("WHERE type").in(paoTypes);
        sql.append("AND PAOName = ").appendArgument(bin);
        sql.append(")))");
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