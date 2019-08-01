package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.YukonResultSet;

class CapControlCBCOrphanRowMapper extends AbstractRowMapperWithBaseQuery<Map<String, Object>> {

    private CbcHelperService cbcHelperService;

    public CapControlCBCOrphanRowMapper() {
        super();
    }
    
    public CapControlCBCOrphanRowMapper(CbcHelperService cbcHelperService) {
        this.cbcHelperService = cbcHelperService;
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        return cbcHelperService.getOrphanSql();
    }
    
    @Override
    public SqlFragmentSource getOrderBy() {
        return new SimpleSqlFragment("ORDER BY PaoName");
    }

    @Override
    public boolean needsWhere() {
        return true;
    }

    @Override
    public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("paoName", rs.getString("PaoName"));
        map.put("pointName", rs.getString("PointName"));
        map.put("type", rs.getString("Type"));
        map.put("paoId", rs.getString("PaObjectId"));
        return map;
    }
}