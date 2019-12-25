package com.cannontech.database;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JDBC template class used to query the Network Manager Database. This is mainly used to fetch
 * the system data from Network Manager and publish that over the topic to be used by the interested
 * listener.
 */
public class NetworkManagerJdbcTemplate extends JdbcTemplate {

    public NetworkManagerJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }
}
