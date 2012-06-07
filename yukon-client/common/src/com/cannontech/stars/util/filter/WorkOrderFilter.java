package com.cannontech.stars.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.workOrder.WorkOrderJoinTables;

public class WorkOrderFilter extends AbstractFilter<LiteWorkOrderBase> {
    private static final String baseSql;
    private static final String workOrderIdSelectSql;
    private static final String countSelectSql;
    
    private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    
    static {
        baseSql = "FROM WorkOrderBase wob " +
        		  "JOIN ServiceCompany sc ON sc.CompanyID = wob.ServiceCompanyID " +
        		  "JOIN YukonListEntry yle ON yle.EntryId = wob.CurrentStateID " +
        		  "JOIN YukonListEntry yle2 ON yle2.EntryID = wob.WorkTypeID";
        
        workOrderIdSelectSql = "SELECT wob.OrderID " + baseSql;
        
        countSelectSql = "SELECT COUNT(*) as total " + baseSql;
        
    }
    
    @Override
    protected String getSelectCountSql() {
        return countSelectSql;
    }
    
    @Override
    protected String getSelectIdSql() {
        return workOrderIdSelectSql;
    }
    
    @Override
    protected List<LiteWorkOrderBase> getList(List<Integer> ids) {
        List<LiteWorkOrderBase> list = createSortedList(ids, starsWorkOrderBaseDao.getByIdsMap(ids));
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
                return Arrays.<JoinTable>asList(WorkOrderJoinTables.ENERGY_COMPANY_TO_WORKORDER_MAPPING);
            }
            @Override
            public List<Object> getParameterValues() {
                return Collections.emptyList();
            }
            @Override
            public String getSql() {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("ectwm.EnergyCompanyID IN (");
                sqlBuilder.append(energyCompanyIdList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        };
    }
    
    @Autowired
    public void setStarsWorkOrderBaseDao(
            StarsWorkOrderBaseDao starsWorkOrderBaseDao) {
        this.starsWorkOrderBaseDao = starsWorkOrderBaseDao;
    }
    
}
