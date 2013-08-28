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
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.google.common.collect.Lists;

public class UnassignedGearPicker extends DatabasePicker<Map<String, Object>> {

    private final static String[] searchColumnNames = new String[] {
        "gearName", "gearId", "controlMethod"
    };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();

        columns.add(new OutputColumn("gearName", "yukon.web.picker.unassignedGearPicker.gearName"));
        columns.add(new OutputColumn("gearId", "yukon.web.picker.unassignedGearPicker.gearId"));
        columns.add(new OutputColumn("controlMethod", "yukon.web.picker.unassignedGearPicker.controlMethod"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public UnassignedGearPicker() {
        super(new UnassignedGearRowMapper(), searchColumnNames);
    }

    @Override
    public String getIdFieldName() {
        return "gearId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "gearId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class UnassignedGearRowMapper extends
        AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT gearName, gearId, controlMethod");
            retVal.append("FROM LMProgramDirectGear");
            retVal.append("WHERE GearId NOT IN");
            retVal.append(    "(SELECT GearId FROM EstimatedLoadFormulaAssignment WHERE GearID IS NOT NULL)");
            return retVal;
        }

        @Override
        public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();

            map.put("gearName", rs.getString("gearName"));
            map.put("gearId", rs.getInt("gearId"));
            map.put("controlMethod", rs.getEnum("controlMethod", GearControlMethod.class));

            return map;
        }
    }
}
