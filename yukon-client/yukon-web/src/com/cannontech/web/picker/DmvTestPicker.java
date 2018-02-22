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

public class DmvTestPicker extends DatabasePicker<Map<String, Object>> {

    private final static String[] searchColumnNames = { "dmvTestName" };

    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("dmvTestName", "yukon.web.picker.dmvTest.name"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    protected DmvTestPicker() {
        super(new DmvTestRowMapper(), searchColumnNames);
    }

    @Override
    public String getIdFieldName() {
        return "dmvTestId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class DmvTestRowMapper extends AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT dmvTestId, dmvTestName");
            retVal.append("FROM DmvTest");
            return retVal;
        }

        @Override
        public SqlFragmentSource getOrderBy() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("ORDER By dmvTestName");
            return retVal;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }

        @Override
        public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();
            map.put("dmvTestId", rs.getInt("dmvTestId"));
            map.put("dmvTestName", rs.getString("dmvTestName"));
            return map;
        }
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "dmvTestId";
    }

}
