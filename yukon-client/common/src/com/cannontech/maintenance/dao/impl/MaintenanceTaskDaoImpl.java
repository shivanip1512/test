package com.cannontech.maintenance.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTaskSettings;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;

public class MaintenanceTaskDaoImpl implements MaintenanceTaskDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public Map<MaintenanceTaskSettings, String> getTaskSettings(MaintenanceTaskName taskName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Attribute, Value");
        sql.append("FROM MaintenanceTask ms");
        sql.append("  JOIN MaintenanceTaskSettings msp");
        sql.append("  ON ms.TaskId = msp.TaskId");
        sql.append("AND TaskName").eq(taskName);

        final Map<MaintenanceTaskSettings, String> taskSettings = new HashMap<>();

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                taskSettings.put(rs.getEnum("Attribute", MaintenanceTaskSettings.class), rs.getString("Value"));
            }
        });
        return taskSettings;
    }

    @Override
    public List<MaintenanceTaskName> getMaintenanceTaskNames(boolean includeDisabledTask) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT TaskName");
        sql.append("FROM MaintenanceTask");
        if (!includeDisabledTask) {
            sql.append("WHERE Status").eq_k(1);
        }
        List<MaintenanceTaskName> maintenanceTaskNames = new ArrayList<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                maintenanceTaskNames.add(rs.getEnum("TaskName", MaintenanceTaskName.class));
            }
        });
        return maintenanceTaskNames;
    }

}
