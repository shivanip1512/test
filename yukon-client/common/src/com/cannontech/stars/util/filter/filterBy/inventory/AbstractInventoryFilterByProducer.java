package com.cannontech.stars.util.filter.filterBy.inventory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.FilterByProducer;

public abstract class AbstractInventoryFilterByProducer implements FilterByProducer {
    
    @Autowired protected YukonJdbcTemplate jdbcTemplate;
    
    protected static final FilterBy NON_DUMMY_METER = new FilterBy() {
        @Override
        public Collection<JoinTable> getJoinTables() {
            return JoinTable.EMPTY_JOINTABLES;
        }
        @Override
        public String getSql() {
            return "yle.YukonDefinitionId IN (?,?)";
        }
        @Override
        public List<Object> getParameterValues() {
            return Arrays.<Object>asList(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC,
                                         YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC);
        }
    };
   
    protected static final FilterBy MCT_METER = new FilterBy() {
        @Override
        public Collection<JoinTable> getJoinTables() {
            return JoinTable.EMPTY_JOINTABLES;
        }
        @Override
        public String getSql() {
            return "yle.YukonDefinitionId IN (?)";
        }
        @Override
        public List<Object> getParameterValues() {
            return Arrays.<Object>asList(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_YUKON_METER);
        }
    };
    
    protected static final FilterBy NON_DUMMY_OR_MCT_METER = new FilterBy() {
        @Override
        public Collection<JoinTable> getJoinTables() {
            return JoinTable.EMPTY_JOINTABLES;
        }
        @Override
        public String getSql() {
            return "yle.YukonDefinitionId IN (?,?,?)";
        }
        @Override
        public List<Object> getParameterValues() {
            return Arrays.<Object>asList(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC,
                                         YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC,
                                         YukonListEntryTypes.YUK_DEF_ID_INV_CAT_YUKON_METER);
        }
    };
    
    protected static final FilterBy NOT_IN_WAREHOUSE = new FilterBy() {
        @Override
        public Collection<JoinTable> getJoinTables() {
            return JoinTable.EMPTY_JOINTABLES;
        }
        @Override
        public String getSql() {
            return "ib.InventoryID NOT IN (SELECT InventoryID FROM InventoryToWareHouseMapping)";
        }
        @Override
        public List<Object> getParameterValues() {
            return Collections.emptyList();
        }
    };
    
}
