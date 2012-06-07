package com.cannontech.stars.dr.hardware.dao;

import java.util.Set;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AvailableLmHardwareFilter implements SqlFilter {
    
    private Iterable<Integer> energyCompanyIds;
    private HardwareClass lmHardwareClass;
    
    public AvailableLmHardwareFilter(Iterable<Integer> energyCompanyIds, HardwareClass lmHardwareClass) {
        this.energyCompanyIds = energyCompanyIds;
        this.lmHardwareClass = lmHardwareClass;
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        
        Set<HardwareType> lmHardwareTypes = HardwareType.getForClass(lmHardwareClass);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("lmhw.inventoryId IN (");
        sql.append("  SELECT ib.inventoryId FROM inventoryBase ib ");
        sql.append("    JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("    JOIN LMHardwareBase lmhb ON lmhb.inventoryId = ib.InventoryId");
        sql.append("    JOIN YukonListEntry yle on yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append("  WHERE etim.EnergyCompanyId ").in(energyCompanyIds);
        sql.append("    AND ib.accountId = 0 ");
        sql.append("    AND yle.YukonDefinitionID ").in(lmHardwareTypes).append(")");

        return sql;
    }
    
}