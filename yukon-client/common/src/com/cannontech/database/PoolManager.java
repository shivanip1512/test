package com.cannontech.database;

import static com.cannontech.common.config.MasterConfigBooleanKeysEnum.DB_JAVA_TEST_ON_BORROW;
import static com.cannontech.common.config.MasterConfigBooleanKeysEnum.DB_JAVA_TEST_ON_RETURN;
import static com.cannontech.common.config.MasterConfigBooleanKeysEnum.DB_JAVA_TEST_WHILE_IDLE;
import static com.cannontech.common.config.MasterConfigBooleanKeysEnum.DB_SSL_ENABLED;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_JAVA_DRIVER;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_JAVA_URL;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_JAVA_VALIDATION_QUERY;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_PASSWORD;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_SQLSERVER;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_SQLSERVER_HOST;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_TYPE;
import static com.cannontech.common.config.MasterConfigStringKeysEnum.DB_USERNAME;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
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
    
    private static final class ConnectionDescription {
        public ConnectionDescription(String connectionUrl, DatabaseVendor type) {
            this.connectionUrl = connectionUrl;
            this.type = type;
        }
        String connectionUrl;
        DatabaseVendor type;
    }
    
    private enum UrlStyle {
        COLON,
        SLASH
    };

    static private PoolManager instance;

    private DataSource mainPool = null;
    private DataSource wrappedPool = null;

    private static ConfigurationSource configSource = null;
    private String primaryUrl;
    private String primaryUser;
	private BasicDataSource bds;
	
	private enum DatabaseVendor { ORACLE_DATABASE, ORACLE12_DATABASE, MSSQL_DATABASE }

    private PoolManager() {
        init();
    }

    private ConnectionDescription getConnectionUrl() {

        String dbTypeName = configSource.getString(DB_TYPE);
        DatabaseVendor dbType = null;

        if ("mssql".equalsIgnoreCase(dbTypeName)) {
            dbType = DatabaseVendor.MSSQL_DATABASE;
        } else if ("oracle".equalsIgnoreCase(dbTypeName)) {
            dbType = DatabaseVendor.ORACLE_DATABASE;
        } else if ("oracle12".equalsIgnoreCase(dbTypeName)) {
            dbType = DatabaseVendor.ORACLE12_DATABASE;
        } else {
            throw new BadConfigurationException("Unrecognized database type (DB_TYPE) in master.cfg: " + dbTypeName);
        }

        String jdbcUrl = configSource.getString(DB_JAVA_URL);
        if (StringUtils.isNotBlank(jdbcUrl)) {
            log.debug("Using DB_JAVA_URL=" + jdbcUrl);
            return new ConnectionDescription(jdbcUrl, dbType);
        }
        
        if (dbType == DatabaseVendor.MSSQL_DATABASE) {
            // configure as microsoft
            // example: jdbc:jtds:sqlserver://mn1db02:1433;APPNAME=yukon-client;TDS=8.0
            StringBuilder url = new StringBuilder();
            url.append("jdbc:jtds:sqlserver://");
            String host = configSource.getRequiredString(DB_SQLSERVER);
            Pattern pattern = Pattern.compile("([^\\\\]+)\\\\(.+)");
            Matcher matcher = pattern.matcher(host);
            if (matcher.matches()) {
                host = matcher.group(1);
            }
            url.append(host);
            url.append(":1433;APPNAME=yukon-client;TDS=8.0");
            //setup the connection for SSL
            if(configSource.getBoolean(DB_SSL_ENABLED, false)){
            	url.append(";ssl=require;socketKeepAlive=true");
            }
            log.debug("Found MSSQL");
            return new ConnectionDescription(url.toString(), dbType);
        } else if (dbType == DatabaseVendor.ORACLE_DATABASE) {
            try {
                // Configure using SID, which is used by Oracle 9, 10, and 11. 
                // format: jdbc:oracle:thin:@<host>:<port>:<SID>
                // Note: As of Oracle version 12c, use of SID has been deprecated in favor of using service name.
                StringBuilder url = buildOracleJdbcUrl(UrlStyle.COLON);
                
                log.debug("Found Oracle");
                return new ConnectionDescription(url.toString(), dbType);
            } catch (UnknownKeyException e) {
                throw new BadConfigurationException("Cannot connect to Oracle without DB_SQLSERVER_HOST and DB_SQLSERVER being specified.", e);
            }
        } else if (dbType == DatabaseVendor.ORACLE12_DATABASE) {
            try {
                // Configure using service name, not SID.  Required by Oracle 12c and later.
                // format: jdbc:oracle:thin:@<host>:<port>/<serviceName>
                StringBuilder url = buildOracleJdbcUrl(UrlStyle.SLASH);
                
                log.debug("Found Oracle 12C");
                return new ConnectionDescription(url.toString(), dbType);
            } catch (UnknownKeyException e) {
                throw new BadConfigurationException("Cannot connect to Oracle without DB_SQLSERVER_HOST and DB_SQLSERVER being specified.", e);
            }
        }
        
        //unreachable
        throw new BadConfigurationException("Unable to generate connection URL");
    }

    private StringBuilder buildOracleJdbcUrl(UrlStyle style) {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:oracle:thin:@");
        String host = configSource.getRequiredString(DB_SQLSERVER_HOST);
        url.append(host);
        url.append(":1521");
        if (style == UrlStyle.SLASH) {
            url.append("/");
        } else {
            url.append(":");
        }
        String tnsName = configSource.getRequiredString(DB_SQLSERVER);
        url.append(tnsName);
        return url;
    }

    private void createPools() {
        
        // see if we have some very specific settings
        registerDriver();
        
        ConnectionDescription connectionDescription = getConnectionUrl();
        primaryUrl = connectionDescription.connectionUrl;

        primaryUser = configSource.getRequiredString(DB_USERNAME);
        String password = configSource.getRequiredString(DB_PASSWORD);
        
        String maxActiveConns = configSource.getString("DB_JAVA_MAXCONS");
        int maxActive = -1;
        if (StringUtils.isNotBlank(maxActiveConns)) {
            maxActive = Integer.valueOf(maxActiveConns);
        }
        log.info("DB maxActive=" + maxActive);

        String maxIdleConns = configSource.getString("DB_JAVA_MAXIDLECONS");
        int maxIdle = CtiUtilities.isRunningAsClient() ? 4 : 15;
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
        
        String defaultValidationQuery;
        switch (connectionDescription.type) {
        case ORACLE_DATABASE:
        case ORACLE12_DATABASE:
            defaultValidationQuery = "select 1 from dual";
            break;
        default:
            defaultValidationQuery = "select 1";
        }
        String validationQuery = configSource.getString(DB_JAVA_VALIDATION_QUERY, defaultValidationQuery);
        log.info("DB validationQuery=" + validationQuery);
        
        boolean testOnBorrow = configSource.getBoolean(DB_JAVA_TEST_ON_BORROW, false);
        log.info("DB testOnBorrow=" + testOnBorrow);
        
        boolean testOnReturn = configSource.getBoolean(DB_JAVA_TEST_ON_RETURN, false);
        log.info("DB testOnReturn=" + testOnReturn);
        
        boolean testWhileIdle = configSource.getBoolean(DB_JAVA_TEST_WHILE_IDLE, true);
        log.info("DB testWhileIdle=" + testWhileIdle);
        
        long timeBetweenEvictionRunsMillis = configSource.getLong("DB_JAVA_TIME_BETWEEN_EVICTION_RUNS_MILLIS", TimeUnit.MINUTES.toMillis(2));
        log.info("DB timeBetweenEvictionRunsMillis=" + timeBetweenEvictionRunsMillis);
        
        int numTestsPerEvictionRun = configSource.getInteger("DB_JAVA_NUM_TESTS_PER_EVICTION_RUN", 1);
        log.info("DB numTestsPerEvictionRun=" + numTestsPerEvictionRun);
        
        long minEvictableIdleTimeMillis = configSource.getLong("DB_JAVA_MIN_EVICTABLE_IDLE_TIME_MILLIS", TimeUnit.MINUTES.toMillis(10)); 
        log.info("DB minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis);
        
        bds = new BasicDataSource();
        bds.setUrl(primaryUrl);
        bds.setUsername(primaryUser);
        bds.setPassword(password);
        bds.setInitialSize(initialSize);
        bds.setMaxTotal(maxActive + maxIdle);
        bds.setMaxIdle(maxIdle);
        bds.setMinIdle(minIdle);
        bds.setMaxWaitMillis(TimeUnit.SECONDS.toMillis(60));
        bds.setValidationQuery(validationQuery);
        bds.setTestOnBorrow(testOnBorrow);
        bds.setTestOnReturn(testOnReturn);
        bds.setTestWhileIdle(testWhileIdle);
        bds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        bds.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        bds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
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
            driverName = configSource.getString(DB_JAVA_DRIVER);
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
