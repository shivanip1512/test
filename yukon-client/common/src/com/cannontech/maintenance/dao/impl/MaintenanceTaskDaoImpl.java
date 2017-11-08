package com.cannontech.maintenance.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.maintenance.MaintenanceTasks;
import com.cannontech.maintenance.MaintenanceTasksSettings;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;


public class MaintenanceTaskDaoImpl implements MaintenanceTaskDao {

    @Autowired private YukonJdbcTemplate yukonTemplate;

    @Override
    public Map<MaintenanceTasksSettings, String> getTaskSettings(MaintenanceTasks taskName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Attribute, Value");
        sql.append("FROM MaintenaceTask ms");
        sql.append("  JOIN MaintenanceTaskSettings msp");
        sql.append("  ON ms.TaskId = msp.TaskId");
        sql.append("AND TaskName").eq(taskName);

        final Map<MaintenanceTasksSettings, String> taskSettings = new HashMap<>();

        yukonTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                taskSettings.put(rs.getEnum("Attribute", MaintenanceTasksSettings.class), rs.getString("Value"));
            }
        });
        return taskSettings;
    }

}
