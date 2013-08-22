package com.cannontech.web.picker.v2;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.dr.estimatedload.Formula;
import com.google.common.collect.Lists;

public class AppCatFormulaPicker extends DatabasePicker<Map<String, Object>> {

    private final static String[] searchColumnNames = new String[] {
        "name", "calculationType"
    };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        
        columns.add(new OutputColumn("name", "yukon.web.picker.appCatFormulaPicker.name"));
        columns.add(new OutputColumn("calculationType", "yukon.web.picker.appCatFormulaPicker.calculationType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public AppCatFormulaPicker() {
        super(new EstimatedLoadFormulaRowMapper(), searchColumnNames);
    }

    @Override
    public String getIdFieldName() {
        return "name";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "estimatedLoadFormulaId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class EstimatedLoadFormulaRowMapper extends
        AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT name, formulaType, calculationType, estimatedLoadFormulaId");
            retVal.append("FROM EstimatedLoadFormula");
            retVal.append("WHERE formulaType").eq(Formula.Type.APPLIANCE_CATEGORY);
            return retVal;
        }

        @Override
        public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();

            map.put("name", rs.getString("name"));
            map.put("calculationType", rs.getEnum("calculationType", Formula.CalculationType.class));
            map.put("formulaId", rs.getInt("estimatedLoadFormulaId"));
            
            return map;
        }
    }
}
