package com.cannontech.watchdog.dao.impl;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.dao.WatchdogWatcherDao;

public class WatchdogWatcherDaoImpl implements WatchdogWatcherDao {

    private static final Logger log = YukonLogManager.getLogger(WatchdogWatcherDaoImpl.class);
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public boolean isValidService(YukonServices serviceName) {
        PaoCategory category = null;
        if (serviceName == YukonServices.CAPCONTROL) {
            category = PaoCategory.CAPCONTROL;
        } else if (serviceName == YukonServices.LOADMANAGEMENT) {
            category = PaoCategory.LOADMANAGEMENT;
        } else {
            log.info("Incorrect service name");
            return true;
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE Category ").eq_k(category);

        return jdbcTemplate.queryForInt(sql) > 0;
    }

}
