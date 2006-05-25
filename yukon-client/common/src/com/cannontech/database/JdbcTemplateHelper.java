package com.cannontech.database;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cannontech.spring.YukonSpringHook;

public class JdbcTemplateHelper {

    public static JdbcOperations getYukonTemplate() {
        DataSource dataSource = (DataSource) YukonSpringHook.getBean("yukonDataSource");
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template;
    }
}
