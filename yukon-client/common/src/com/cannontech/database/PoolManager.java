package com.cannontech.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.debug.LoggingDataSource;

@ManagedResource
public class PoolManager {
    private static final Logger dsLog = YukonLogManager.getLogger("com.cannontech.datasource");
    private static final Logger log = YukonLogManager.getLogger(PoolManager.class);
	
    public static final String[] ALL_DRIVERS = { 
        "oracle.jdbc.OracleDriver",
        "com.microsoft.jdbc.sqlserver.SQLServerDriver",
        "net.sourceforge.jtds.jdbc.Driver", 
        "sun.jdbc.odbc.JdbcOdbcDriver" // only exists for backwards compatibility
    };

    static private PoolManager instance;

    private DataSource mainPool = null;
    private DataSource wrappedPool = null;

    private static ConfigurationSource configSource = null;
    private String primaryUrl;
    private String primaryUser;
	private BasicDataSource bds;
	
	enum Databases { UNKNOWN_DATABASE, ORACLE_DATABASE, MSSQL_DATABASE };

    private PoolManager() {
        init();
    }

    private String getConnectionUrl() {
        String jdbcUrl;
        jdbcUrl = configSource.getString("DB_JAVA_URL");
        if (StringUtils.isNotBlank(jdbcUrl)) {
            log.debug("Using DB_JAVA_URL=" + jdbcUrl);
            return jdbcUrl;
        }
        
        // otherwise, see what dll is setup
        
        String rwDllName = configSource.getString("DB_RWDBDLL");
        String dbTypeName = configSource.getString("DB_TYPE");
        Databases dbType = Databases.UNKNOWN_DATABASE;
        
        //This establishes the precedence of DB_TYPE over DB_RWDBDLL
        if ("mssql".equalsIgnoreCase(dbTypeName))
        	dbType = Databases.MSSQL_DATABASE;
        else if ("oracle".equalsIgnoreCase(dbTypeName))
        	dbType = Databases.ORACLE_DATABASE;
        else if ("msq15d.dll".equalsIgnoreCase(rwDllName))
        	dbType = Databases.MSSQL_DATABASE;
        else if ("ora15d.dll".equalsIgnoreCase(rwDllName))
        	dbType = Databases.ORACLE_DATABASE;
        
        if (dbType == Databases.MSSQL_DATABASE) {
            // configure as microsoft
            // example: jdbc:jtds:sqlserver://mn1db02:1433;APPNAME=yukon-client;TDS=8.0
            StringBuilder url = new StringBuilder();
            url.append("jdbc:jtds:sqlserver://");
            String host = configSource.getRequiredString("DB_SQLSERVER");
            url.append(host);
            url.append(":1433;APPNAME=yukon-client;TDS=8.0");
            log.debug("Found msq15d.dll, url=" + url);
            return url.toString();
        }
        
        if (dbType == Databases.ORACLE_DATABASE) {
            try {
                // configure as Oracle
                // example: jdbc:oracle:thin:@mn1db02:1521:xcel
                StringBuilder url = new StringBuilder();
                url.append("jdbc:oracle:thin:@");
                String host = configSource.getRequiredString("DB_SQLSERVER_HOST");
                url.append(host);
                url.append(":1521:");
                String tnsName = configSource.getRequiredString("DB_SQLSERVER");
                url.append(tnsName);
                
                log.debug("Found ora15d.dll, url=" + url);
                return url.toString();
            } catch (UnknownKeyException e) {
                throw new BadConfigurationException("Cannot connect to Oracle without DB_SQLSERVER_HOST and DB_SQLSERVER being specified.", e);
            }
        }
        
        throw new BadConfigurationException("Unrecognized DB_RWDBDLL in master.cfg: " + rwDllName);
    }

    private void createPools() {
        
        // see if we have some very specific settings
        registerDriver();

        primaryUrl = getConnectionUrl();
        log.info("DB URL=" + primaryUrl);

        primaryUser = configSource.getRequiredString("DB_USERNAME");
        log.info("DB username=" + primaryUser);
        String password = configSource.getRequiredString("DB_PASSWORD");
        
        String maxActiveConns = configSource.getString("DB_JAVA_MAXCONS");
        int maxActive = CtiUtilities.isRunningAsClient() ? 4 : 15;
        if (StringUtils.isNotBlank(maxActiveConns)) {
            maxActive = Integer.valueOf(maxActiveConns);
        }
        log.info("DB maxActive=" + maxActive);

        String maxIdleConns = configSource.getString("DB_JAVA_MAXIDLECONS");
        int maxIdle = maxActive;
        if (StringUtils.isNotBlank(maxIdleConns)) {
            maxIdle = Integer.valueOf(maxIdleConns);
        }
        log.info("DB maxIdle=" + maxIdle);
        
        String minIdleConns = configSource.getString("DB_JAVA_MINIDLECONS");
        int minIdle = 0;
        if (StringUtils.isNotBlank(minIdleConns)) {
            minIdle = Integer.valueOf(minIdleConns);
        }
        log.info("DB minIdle=" + minIdle);
        
        String initConns = configSource.getString("DB_JAVA_INITCONS");
        int initialSize = 0;
        if (StringUtils.isNotBlank(initConns)) {
            initialSize = Integer.valueOf(initConns);
        }
        log.info("DB initialSize=" + initialSize);
        
        String validationQuery = configSource.getString("DB_JAVA_VALIDATION_QUERY", null);
        log.info("DB validationQuery=" + validationQuery);
        
        boolean testOnBorrow = configSource.getBoolean("DB_JAVA_TEST_ON_BORROW", false);
        log.info("DB testOnBorrow=" + testOnBorrow);
        
        boolean testOnReturn = configSource.getBoolean("DB_JAVA_TEST_ON_RETURN", false);
        log.info("DB testOnReturn=" + testOnReturn);
        
        bds = new BasicDataSource();
        bds.setUrl(primaryUrl);
        bds.setUsername(primaryUser);
        bds.setPassword(password);
        bds.setInitialSize(initialSize);
        bds.setMaxActive(maxActive);
        bds.setMaxIdle(maxIdle);
        bds.setMinIdle(minIdle);
        bds.setMaxWait(TimeUnit.SECONDS.toMillis(60));
        bds.setValidationQuery(validationQuery);
        bds.setTestOnBorrow(testOnBorrow);
        bds.setTestOnReturn(testOnReturn);
        log.debug("Created BasicDataSource:" + bds);

        DataSource actualDs = bds;
        if (dsLog.isDebugEnabled()) {
            actualDs = new LoggingDataSource(bds);
            log.info("DataSource logging has been enabled. DataSource is now: " + actualDs);
        }
        
        mainPool = actualDs;
        wrappedPool = new TransactionAwareDataSourceProxy(actualDs);
    }

    private void registerDriver() {
        try {
            String driverName;
            driverName = configSource.getString("DB_JAVA_DRIVER");
            if (StringUtils.isBlank(driverName)) {
                return;
            }
            Class.forName(driverName);
            log.info("Registered JDBC driver (forced) " + driverName);
        } catch (Exception e) {
            log.warn("Unable to force register driver", e);
        }
    }
    
    public DataSource getMainPool() {
        return mainPool;
    }
    
    public DataSource getWrappedPool() {
        return wrappedPool;
    }
    
    public static DataSource getYukonDataSource() {
        return getInstance().getMainPool();
    }

    public static DataSource getWrappedYukonDataSource() {
        return getInstance().getWrappedPool();
    }
    
    /**
     * Convenience method for
     *   PoolManager.getInstance().getConnection("yukon");
     * or
     *   PoolManager.getWrappedYukonDataSource().getConnection();
     * @return
     */
    public static Connection getYukonConnection() {
        try {
            return getWrappedYukonDataSource().getConnection();
        } catch (SQLException e) {
            throw new DataAccessResourceFailureException("Unable to get database connection",e);
        }
    }
    
    /**
     * Returns a connection from the pool of available connections. If a
     * connection is not available, a RuntimeException will be thrown. The
     * connection returned will be configured to participate in any
     * transactions.
     * @param name of database (always "yukon")
     * @return
     */
    public Connection getConnection(String name) {
        if (!StringUtils.equals(CtiUtilities.getDatabaseAlias(), name)) {
            // check for historical reasons, name will always equal "yukon"
            throw new RuntimeException("Connection can only be returned for \"yukon\"");
        }
        try {
            Connection connection = wrappedPool.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get database connection.", e);
        }
    }

    static synchronized public PoolManager getInstance() {
        if (instance == null) {
            instance = new PoolManager();
        }
        return instance;
    }

    private void init() {
        if (configSource == null) {
            configSource = MasterConfigHelper.getConfiguration();
        }

        loadDrivers();
        createPools();
    }

    private void loadDrivers() {
        for (String drivername : ALL_DRIVERS) {
            try {
                Class.forName(drivername);
                log.info("Registered JDBC driver " + drivername);
            } catch (ClassNotFoundException e) {
                log.error("Can't register JDBC driver: " + drivername,e);
            }
        }
    }

    /**
     * This can be used to set a new ConfigurationSource. Used primarily by the 
     * clients after the user enters the host and login information.
     * @param configSource
     */
    public static void setConfigurationSource(ConfigurationSource configSource) {
        log.debug("Setting ConfigurationSource to " + configSource);
        PoolManager.configSource = configSource;
        instance = null;
    }

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public String getPrimaryUser() {
        return primaryUser;
    }
    
    @ManagedAttribute
    public int getNumActive() {
    	return bds.getNumActive();
    }
    
    @ManagedAttribute
    public int getNumIdle() {
    	return bds.getNumIdle();
    }

}
