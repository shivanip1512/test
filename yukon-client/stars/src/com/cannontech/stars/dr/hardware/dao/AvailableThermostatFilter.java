package com.cannontech.stars.dr.hardware.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.google.common.collect.Lists;

public class AvailableThermostatFilter implements SqlFilter {
    
    private Set<Integer> energyCompanyIds;
    
    public AvailableThermostatFilter(Set<Integer> energyCompanyIds) {
        this.energyCompanyIds = energyCompanyIds;
    }
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        
        List<HardwareType> hardwareTypes = Lists.newArrayList(HardwareType.values());
        List<Integer> thermostatTypeDefIds = Lists.newArrayList();
        
        for(HardwareType type : hardwareTypes) {
            if (type.isThermostat()) {
                thermostatTypeDefIds.add(type.getDefinitionId());
            }
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("lmhw.inventoryId IN (");
        sql.append("  SELECT ib.inventoryId FROM inventoryBase ib ");
        sql.append("    JOIN ECToInventoryMapping etim ON etim.InventoryId = ib.InventoryId");
        sql.append("    JOIN LMHardwareBase lmhb ON lmhb.inventoryId = ib.InventoryId");
        sql.append("    JOIN YukonListEntry yle on yle.EntryID = lmhw.LMHardwareTypeID");
        sql.append("  WHERE etim.EnergyCompanyId ").in(energyCompanyIds);
        sql.append("    AND ib.accountId = 0 ");
        sql.append("    AND yle.YukonDefinitionID ").in(thermostatTypeDefIds).append(")");

        return sql;
    }
    
}