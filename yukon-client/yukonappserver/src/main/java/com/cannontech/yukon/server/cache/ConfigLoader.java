package com.cannontech.yukon.server.cache;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.spring.YukonSpringHook;

public class ConfigLoader implements Runnable {
    private List<LiteConfig> allConfigs = null;

    private static final YukonRowMapper<LiteConfig> liteConfigRowMapper = new YukonRowMapper<LiteConfig>() {
        @Override
        public LiteConfig mapRow(YukonResultSet rs) throws SQLException {
            int configId = rs.getInt("ConfigId");
            String configName = rs.getString("ConfigName").trim();
            int configType = rs.getInt("ConfigType");
            
            LiteConfig liteConfig = new LiteConfig(configId, configName, configType);
            return liteConfig;
        }
    };
    
    public ConfigLoader(List<LiteConfig> allConfigs) {
        this.allConfigs = allConfigs;
    }

    @Override
    public void run() {
        Date timerStart = new Date();
        
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ConfigId, ConfigName, ConfigType");
        sql.append("FROM MCTCONFIG");
        sql.append("WHERE CONFIGID").gte(0);
        sql.append("ORDER BY ConfigName");

        allConfigs.addAll(jdbcTemplate.query(sql, liteConfigRowMapper));
        
        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001
                       + " Secs for ConfigLoader (" + allConfigs.size() + " loaded)");
    }

    public static LiteConfig getForId(int configId) {
        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ConfigId, ConfigName, ConfigType");
        sql.append("FROM MCTCONFIG");
        sql.append("WHERE CONFIGID").eq(configId);

        return jdbcTemplate.queryForObject(sql, liteConfigRowMapper);
    }
}