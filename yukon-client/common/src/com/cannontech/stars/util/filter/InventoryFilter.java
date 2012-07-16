package com.cannontech.stars.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.inventory.InventoryJoinTable;

public class InventoryFilter extends AbstractFilter<LiteInventoryBase> {
    private static final String baseSql;
    private static final String inventoryIdSelectSql;
    private static final String countSelectSql;
    
    private InventoryBaseDao inventoryBaseDao;
    
    static {
        baseSql = "FROM InventoryBase ib " +
                  "LEFT OUTER JOIN LMHardwareBase lmhb ON lmhb.InventoryId = ib.InventoryId " +
                  "JOIN YukonListEntry yle ON yle.EntryId = ib.CategoryId ";

        inventoryIdSelectSql = "SELECT ib.InventoryID " + baseSql;
        
        countSelectSql = "SELECT COUNT(*) as total " + baseSql;
        
    }
    
    @Override
    protected String getSelectCountSql() {
        return countSelectSql;
    }
    
    @Override
    protected String getSelectIdSql() {
        return inventoryIdSelectSql;
    }
    
    @Override
    protected List<LiteInventoryBase> getList(List<Integer> ids) {
        List<LiteInventoryBase> list = createSortedList(ids, inventoryBaseDao.getByIdsMap(ids));
        return list;
    }
    
    @Override
    protected void applyFilterBySpecificSettings(List<Integer> energyCompanyIdList,
            Collection<FilterBy> filterBys) {
        
        FilterBy filterBy = createFilterBy(energyCompanyIdList);
        filterBys.add(filterBy);
    }
    
    private FilterBy createFilterBy(final List<Integer> energyCompanyIdList) {
        return new FilterBy() {
            @Override
            public Collection<JoinTable> getJoinTables() {
                return Arrays.<JoinTable>asList(InventoryJoinTable.ENERGY_COMPANY_TO_INVENTORY_MAPPING);
            }
            @Override
            public List<Object> getParameterValues() {
                return Collections.emptyList();
            }
            @Override
            public String getSql() {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("etim.EnergyCompanyID IN (");
                sqlBuilder.append(energyCompanyIdList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        };
    }
    
    @Autowired
    public void setInventoryBaseDao(
            InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
}
