package com.cannontech.services.systemDataPublisher.context;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import com.cannontech.database.NetworkManagerJdbcTemplate;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

@Component
public class NetworkManagerDBConfig {

    @Autowired private GlobalSettingDao globalSettingDao;

    
    @Bean(name = "nmDataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String networkManagerHost = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_HOSTNAME);
        String userId = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_USER);
        String password = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_PASSWORD);
        dataSource.setUrl(buildUrl(networkManagerHost));
        dataSource.setUsername(userId);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public NetworkManagerJdbcTemplate jdbcTemplate(@Qualifier("nmDataSource") DataSource dataSource) {
        NetworkManagerJdbcTemplate networkManagerJdbcTemplate = new NetworkManagerJdbcTemplate(dataSource);
        return networkManagerJdbcTemplate;
    }

    /**
     * Build Url for Network Manager DB connection using global setting NM host name.
     */
    private String buildUrl(String hostName) {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:jtds:sqlserver://");
        url.append(hostName);
        url.append(":1433;");
        return url.toString();
    }
}
