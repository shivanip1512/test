package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.point.PointType;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class CapControlCBCOrphanPicker extends DatabasePicker<Map<String, Object>> {
    private String selectedCBCId;
    private final static String[] searchColumnNames = new String[] { "paoName", "type", "pointName" };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("paoName", "yukon.web.picker.pao.name"));
        columns.add(new OutputColumn("type", "yukon.web.picker.pao.type"));
        columns.add(new OutputColumn("pointName", "yukon.web.picker.point.pointName"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public CapControlCBCOrphanPicker() {
        super(new CapControlCBCOrphanRowMapper(), searchColumnNames);
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public String getIdFieldName() {
        return "paoId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "ypo.PAObjectId";
    }

    @Override
    protected void updateFilters(List<SqlFilter> filters, List<PostProcessingFilter<Map<String, Object>>> postFilters,
            String extraArgs, YukonUserContext userContext) {
        selectedCBCId = extraArgs;
        SqlFilter filter = new SqlFilter() {
            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder orphanClause = new SqlStatementBuilder();
                orphanClause.append("p.PAObjectId = ypo.PAObjectId ");
                SqlStatementBuilder controlDeviceInClause = new SqlStatementBuilder();
                controlDeviceInClause.append("SELECT DISTINCT ControlDeviceId FROM CapBank");
                orphanClause.append("AND ( ypo.PAObjectId ").notIn(controlDeviceInClause);
                orphanClause.append("OR ypo.PAObjectId").eq(selectedCBCId);
                orphanClause.append(")");
                orphanClause.append("AND p.PointOffset = 1 ");
                orphanClause.append("AND p.PointType").eq_k(PointType.Status);
                orphanClause.append("AND Type").in(PaoType.getCbcTypes());
                return orphanClause;
            }
        };

        filters.add(filter);
    }

    public static class CapControlCBCOrphanRowMapper extends AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT ypo.PaoName AS PaoName ");
            retVal.append(", ypo.Type AS Type");
            retVal.append(", ypo.PAObjectId AS PaObjectId");
            retVal.append(", p.PointName AS PointName ");
            retVal.append("FROM Point p, YukonPAObject ypo ");
            return retVal;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            return new SimpleSqlFragment("ORDER BY ypo.PaoName");
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

}