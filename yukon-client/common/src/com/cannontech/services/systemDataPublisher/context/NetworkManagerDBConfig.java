package com.cannontech.services.systemDataPublisher.context;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
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
        String url = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_URL);
        if (StringUtils.isNotBlank(url))
            dataSource.setUrl(url);
        else {
            String networkManagerHost = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_HOSTNAME);
            dataSource.setUrl(buildUrl(networkManagerHost));
        }
        String userName = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_USER);
        String password = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_PASSWORD);
        dataSource.setUsername(userName);
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

    /**
     * Check whether Network Manager DB Connection configurations are provided in Global setting or not.
     * 
     * @return true: If Global setting for Network Manager DB configurations () are present return true
     */
    public boolean isNetworkManagerDBConnectionConfigured() {

        String userName = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_USER);
        String password = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_PASSWORD);
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return false;
        }
        String url = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_URL);
        if (StringUtils.isNotEmpty(url)) {
            return true;
        }
        String networkManagerHost = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_DB_HOSTNAME);
        if (StringUtils.isNotEmpty(networkManagerHost)) {
            return true;
        }
        return false;
    }
}
