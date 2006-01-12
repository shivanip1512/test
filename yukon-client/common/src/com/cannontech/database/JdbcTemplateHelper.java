package com.cannontech.database;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.common.util.CtiUtilities;

public class JdbcTemplateHelper {

    public JdbcOperations getYukonTemplate() {
        DataSource dataSource = new YukonDataSource(CtiUtilities.getDatabaseAlias());
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template;
    }
}
