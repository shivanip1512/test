package com.cannontech.amr.device.search.dao.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.cannontech.amr.device.search.model.DeviceSearchResultEntry;
import com.cannontech.amr.device.search.model.SearchField;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class DeviceSearchRowMapper implements YukonRowMapper<DeviceSearchResultEntry> {

    public SqlFragmentSource getSelectFragment(List<SearchField> fields) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT");
        if(fields == null || fields.isEmpty()) {
            sql.append("COUNT(1)");
        } else {
            for(SearchField field : fields) {
                sql.append(field.getQueryField() + " AS " + field.getFieldName());
                if(fields.indexOf(field) < fields.size() - 1) {
                    sql.append(",");
                }
            }
        }
        sql.append("FROM YukonPaObject ypo");
        sql.append("JOIN Device d ON ypo.paObjectId = d.deviceId");
        sql.append("LEFT JOIN DeviceMeterGroup dmg ON d.deviceId = dmg.deviceId");
        sql.append("LEFT JOIN DeviceCarrierSettings dcs ON d.deviceId = dcs.deviceId");
        sql.append("LEFT JOIN DeviceRoutes dr ON d.deviceId = dr.deviceId");
        sql.append("LEFT JOIN YukonPaObject rypo ON dr.routeId = rypo.paObjectId");
        sql.append("LEFT JOIN DeviceCBC cbc ON d.deviceId = cbc.deviceId");
        sql.append("LEFT JOIN DeviceDirectCommSettings ddcs ON d.deviceId = ddcs.deviceId");
        sql.append("LEFT JOIN YukonPaObject pypo ON ddcs.portId = pypo.paObjectId");
        sql.append("LEFT JOIN (");
        sql.append(getLmGroupSelect());
        sql.append(") lmg ON d.deviceId = lmg.deviceId");
        
        return sql;
    }

    private SqlFragmentSource getLmGroupSelect() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT lmg.deviceId, kwCapacity, serialNumber, routeId, ypo.paoName AS routeName");
        sql.append("FROM LMGroup lmg");
        sql.append("JOIN (SELECT deviceId, serialAddress AS serialNumber, routeId FROM LMGroupVersacom");
        sql.append("UNION SELECT LMGroupId AS deviceId, serialNumber, routeId FROM LMGroupExpressCom");
        sql.append("UNION SELECT deviceId, NULL, routeId FROM LMGroupEmetcon");
        sql.append("UNION SELECT deviceId, NULL, routeId FROM LMGroupMCT) lmgs ON lmg.deviceId = lmgs.deviceId");
        sql.append("JOIN YukonPaObject ypo ON ypo.paObjectId = lmgs.routeId");
        
        return sql;
    }

    @Override
    public DeviceSearchResultEntry mapRow(YukonResultSet rs) throws SQLException {
        DeviceSearchResultEntry entry = new DeviceSearchResultEntry();
        
        // Getting the base result set so we have access to metadata and columnIndex functions
        ResultSet wrappedResultSet = rs.getResultSet();
        
        // We don't know what we fetched here, so we loop through all the columns and get the values as string if they're not null
        ResultSetMetaData metaData = wrappedResultSet.getMetaData();
        for(int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            Object value = wrappedResultSet.getObject(columnIndex);
            if(value != null) {
                entry.putField(metaData.getColumnLabel(columnIndex), value.toString());
            }
        }
        
        return entry;
    }
}
