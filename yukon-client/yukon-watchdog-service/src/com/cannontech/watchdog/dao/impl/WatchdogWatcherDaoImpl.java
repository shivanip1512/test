package com.cannontech.watchdog.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.watchdog.dao.WatchdogWatcherDao;

public class WatchdogWatcherDaoImpl implements WatchdogWatcherDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public boolean paoClassPaoExists(PaoClass paoClass) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE PAOClass ").eq_k(paoClass);

        return jdbcTemplate.queryForInt(sql) > 0;
    }

}
