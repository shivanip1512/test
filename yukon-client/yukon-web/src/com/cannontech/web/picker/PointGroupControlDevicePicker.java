package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PointGroupControlDevicePicker extends DatabasePicker<Map<String, Object>> {

    private final static String[] searchColumnNames = new String[] {
            "pointName", "paoName", "type"
    };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();

        columns.add(new OutputColumn("pointName", "yukon.web.picker.point.pointName"));
        columns.add(new OutputColumn("pointId", "yukon.web.picker.point.pointId"));
        columns.add(new OutputColumn("deviceName", "yukon.web.picker.point.deviceName"));
        columns.add(new OutputColumn("type", "yukon.web.picker.controlDevice.deviceType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    public PointGroupControlDevicePicker() {
        super(new ControlDevicePointRowMapper(), searchColumnNames);
    }

    @Override
    public String getIdFieldName() {
        return "pointId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "point.pointId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class ControlDevicePointRowMapper extends
            AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();

            Set<PaoType> paoTypes = Sets.newHashSet();
            paoTypes.addAll(PaoType.getCbcTypes());
            paoTypes.addAll(PaoType.getIonTypes());
            paoTypes.addAll(PaoType.getRtuTypes());
            paoTypes.addAll(PaoType.getMctTypes());

            retVal.append(
                    "SELECT point.pointId, point.pointName, yukonPAObject.paoName, yukonPAObject.type, yukonPAObject.paObjectId");
            retVal.append("FROM point JOIN pointStatusControl");
            retVal.append("ON point.pointId = pointStatusControl.pointId");
            retVal.append("JOIN yukonPAObject");
            retVal.append("ON point.paObjectId = yukonPAObject.paObjectId");
            retVal.append("WHERE yukonPAObject.type").in(paoTypes);
            return retVal;
        }

        @Override
        public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();

            map.put("pointName", rs.getString("pointName"));
            map.put("pointId", rs.getInt("pointId"));
            map.put("deviceName", rs.getString("paoName"));
            map.put("type", rs.getString("type"));
            map.put("paObjectId", rs.getString("paObjectId"));

            return map;
        }
    }
}
