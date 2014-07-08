package com.cannontech.stars.util.filter.filterBy.workOrder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.FilterByProducer;

public abstract class AbstractWorkOrderFilterByProducer implements FilterByProducer {
    
    @Autowired YukonJdbcTemplate jdbcTemplate;
        
    protected static final FilterBy RESIDENTIAL_CUSTOMER_TYPE = new FilterBy() {
        @Override
        public Collection<JoinTable> getJoinTables() {
            return Arrays.<JoinTable>asList(WorkOrderJoinTables.CUSTOMER);
        }
        @Override
        public String getSql() {
            return "cus.CustomerTypeID = ?";
        }
        @Override
        public List<Object> getParameterValues() {
            return Arrays.<Object>asList(CustomerTypes.CUSTOMER_RESIDENTIAL);
        }
    };
    
    protected static final FilterBy COMMERCIAL_CUSTOMER_TYPE = new FilterBy() {
        @Override
        public Collection<JoinTable> getJoinTables() {
            return Arrays.<JoinTable>asList(WorkOrderJoinTables.CUSTOMER);
        }
        @Override
        public String getSql() {
            return "cus.CustomerTypeID = ?";
        }
        @Override
        public List<Object> getParameterValues() {
            return Arrays.<Object>asList(CustomerTypes.CUSTOMER_CI);
        }
    };
    
}
