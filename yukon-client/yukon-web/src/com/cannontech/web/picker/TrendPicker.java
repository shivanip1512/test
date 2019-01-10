package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.google.common.collect.Lists;

public class TrendPicker extends DatabasePicker<Map<String, Object>> {

    private final static String[] searchColumnNames = new String[] { "Name", "GraphDefinitionId" };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.trendPicker.";
        columns.add(new OutputColumn("Name", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("GraphDefinitionId", titleKeyPrefix + "graphDefinitionId"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    public TrendPicker() {
        super(new GraphDefinitionRowMapper(), searchColumnNames);
    }

    @Override
    public String getIdFieldName() {
        return "GraphDefinitionId";
    }
    
    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class GraphDefinitionRowMapper extends AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT GraphDefinitionId, Name");
            sql.append("FROM GraphDefinition");
            sql.append("WHERE 1=1");
            return sql;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("ORDER BY Name");
            return sql;
        }

        @Override
        public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();
            int graphDefinitionId = rs.getInt("GraphDefinitionId");
            String name = rs.getString("Name");
            map.put("Name", name);
            map.put("GraphDefinitionId", graphDefinitionId);
            return map;
        }
    }
}
