package com.cannontech.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import com.cannontech.clientutils.CTILogManager;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

public class PoolManager {
    public static final String DB_PROPERTIES_FILE = "/db.properties";

    public static final String DRV_SQLSERVER = "jdbc:microsoft:sqlserver:";
    public static final String DRV_ORACLE = "jdbc:oracle:thin:";
    public static final String DRV_JTDS = "jdbc:jtds:sqlserver:";

    public static final String URL = ".url";
    public static final String USER = ".user";
    public static final String PASSWORD = ".password";
    public static final String MAXCONNS = ".maxconns";
    public static final String INITCONNS = ".initconns";

    // derived props stored from other props
    public static final String HOST = "db.host";
    public static final String PORT = "db.port";
    public static final String SERVICE = "db.servicename"; // oracle only

    public static final String[] ALL_DRIVERS = { "oracle.jdbc.OracleDriver",
            "com.microsoft.jdbc.sqlserver.SQLServerDriver",
            "net.sourceforge.jtds.jdbc.Driver", "sun.jdbc.odbc.JdbcOdbcDriver" // only
                                                                                // exists
                                                                                // for
                                                                                // backwards
                                                                                // compatibility
    };

    static private Properties dbProps;
    static private PoolManager instance;

    private Map<String, DataSource> pools = new HashMap<String, DataSource>();
    private Map<String, DataSource> wrappedPools = new HashMap<String, DataSource>();

    private PoolManager() {
        init();
    }

    /**
     * defaults to get only properties for the DatabaseAlias in CTIUtilities
     * @param key_
     * @return
     */
    public String getProperty(String key_) {
        return dbProps.getProperty(CtiUtilities.getDatabaseAlias() + key_);
    }

    public boolean isMS() {
        String url = getProperty(URL);
        if (url != null)
            return url.indexOf(DRV_SQLSERVER) >= 0;
        else
            return false;
    }

    public boolean isJTDS() {
        String url = getProperty(URL);
        if (url != null)
            return url.indexOf(DRV_JTDS) >= 0;
        else
            return false;
    }

    public boolean isOracle() {
        String url = getProperty(URL);
        if (url != null)
            return url.indexOf(DRV_ORACLE) >= 0;
        else
            return false;
    }


    private void createPools(Properties props) {

        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            if (name.endsWith(URL)) {
                String poolName = name.substring(0, name.lastIndexOf("."));
                String url = props.getProperty(poolName + URL);
                if (url == null) {
                    CTILogger.error("No URL specified for " + poolName);
                    continue;
                }

                String user = props.getProperty(poolName + USER);
                String password = props.getProperty(poolName + PASSWORD);

                String maxConns = props.getProperty(poolName + MAXCONNS, "0");
                int max;
                try {
                    max = Integer.valueOf(maxConns).intValue();
                } catch (NumberFormatException e) {
                    CTILogger.error("Invalid maxconns value " + maxConns + " for " + poolName);
                    max = 0;
                }

                String initConns = props.getProperty(poolName + INITCONNS, "0");
                int init;
                try {
                    init = Integer.valueOf(initConns).intValue();
                } catch (NumberFormatException e) {
                    CTILogger.error("Invalid initconns value " + initConns + " for " + poolName);
                    init = 0;
                }

                String loginTimeOut = props.getProperty(poolName + ".logintimeout",
                                                        "5");
                int timeOut;
                try {
                    timeOut = Integer.valueOf(loginTimeOut).intValue();
                } catch (NumberFormatException e) {
                    CTILogger.info("Invalid logintimeout value " + loginTimeOut + " for " + poolName + ", defaulting to 5");
                    timeOut = 5;
                }

                // ConnectionPool pool =
                // new ConnectionPool(poolName, url, user, password,
                // max, init, timeOut );
                // MBeanUtil.tryRegisterMBean("type=ConnectionPool,poolName=" +
                // poolName, pool);

                BasicDataSource bds = new BasicDataSource();
                bds.setUrl(url);
                bds.setUsername(user);
                bds.setPassword(password);
                bds.setInitialSize(init);
                bds.setMaxActive(max);
                pools.put(poolName, bds);
                wrappedPools.put(poolName,
                                 new TransactionAwareDataSourceProxy(bds));
            }
        }
    }

    public DataSource getDataSource(String name) {
        return pools.get(name);
    }

    /**
     * Returns a DataSoruce that has been wrapped with a 
     * TransactionAwareDataSourceProxy so that it can transparently
     * participate in Spring transactions.
     * @param name
     * @return
     */
    public DataSource getWrappedDataSource(String name) {
        return wrappedPools.get(name);
    }
    
    /**
     * Convenience method for
     *   PoolManager.getInstance().getDataSource("yukon");
     * @return
     */
    public static DataSource getYukonDataSource() {
        return getInstance().getDataSource(CtiUtilities.getDatabaseAlias());
    }

    /**
     * Convenience method for
     *   PoolManager.getInstance().getWrappedDataSource("yukon");
     * @return
     */
    public static DataSource getWrappedYukonDataSource() {
        return getInstance().getWrappedDataSource(CtiUtilities.getDatabaseAlias());
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
        try {
            Connection connection = wrappedPools.get(name).getConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get database connection.", e);
        }
    }

    public static final URL getPropertyURL() {
        try {
            URL retURL = PoolManager.class.getResource(DB_PROPERTIES_FILE);

            if (retURL == null) // not in CLASSPATH, check catalina
            {
                File f = new File(CTILogManager.FILE_BASE + DB_PROPERTIES_FILE);
                retURL = f.toURL();
            }

            return retURL;
        } catch (Exception ex) {
            CTILogger.error("Something went wrong with the URL of a file", ex);
            return null;
        }

    }

    static synchronized public Properties loadDBProperties() {
        try {
            InputStream is = getDBInputStream();
            
            dbProps = new Properties();
            dbProps.load(is);
            
            is.close();
        } catch (Exception e) {
            CTILogger.error("Can't read the properties file. " + 
                            "Make sure db.properties is in the CLASSPATH" + 
                            (CTILogManager.FILE_BASE != null ? " or in the directory: " + CTILogManager.FILE_BASE
                                    : ""));
        }
        
        return dbProps;

    }

    static synchronized public InputStream getDBInputStream() throws Exception {
        InputStream is = PoolManager.class.getResourceAsStream(DB_PROPERTIES_FILE);

        if (is == null) //not in CLASSPATH, check catalina
        {
            File f = new File(CTILogManager.FILE_BASE + DB_PROPERTIES_FILE);
            is = new FileInputStream(CTILogManager.FILE_BASE + DB_PROPERTIES_FILE);

            CTILogger.info(" Searching for db.properties in : " + f.getAbsolutePath());
            CTILogger.info("   catalina.base = " + CTILogManager.FILE_BASE);
        } else {
            CTILogger.info(" Using db.properties found in CLASSPATH");
        }

        return is;
    }

    static synchronized public void setDBProperties(Properties props) {
        dbProps = props;
    }

    static synchronized public PoolManager getInstance() {
        if (instance == null) {
            instance = new PoolManager();
        }
        return instance;
    }

    private void init() {
        if (dbProps == null)
            dbProps = loadDBProperties();

        loadDrivers(dbProps);
        createPools(dbProps);
    }

    private void loadDrivers(Properties props) {
        for (String drivername : ALL_DRIVERS) {
            try {
                Class.forName(drivername);
                CTILogger.info("Registered JDBC driver " + drivername);
            } catch (ClassNotFoundException e) {
                CTILogger.error("Can't register JDBC driver: " + drivername,e);
            }
        }
    }

}
