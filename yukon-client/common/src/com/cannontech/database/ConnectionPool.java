package com.cannontech.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;


public class ConnectionPool implements ConnectionPoolMBean
{
    private String name;
    private String URL;
    private String user;
    private String password;
    private int maxConns;
    private int timeOut;
    private int initConns;
    
    private int checkedOut;
    private List<Connection> freeConnections = new LinkedList<Connection>();
    private int connectionsCreated = 0;
    private int connectionsClosed = 0;
    private int waitCount = 0;
    private int connectionGetFailureCount;
    
    
    public ConnectionPool(String name, String URL, String user, 
            String password, int maxConns, int initConns, int timeOut )
    {
        
        this.name = name;
        this.URL = URL;
        this.user = user;
        this.password = password;
        this.maxConns = maxConns;
        this.timeOut = timeOut > 0 ? timeOut : 5;
        this.initConns = initConns;
        
        String lf = System.getProperty("line.separator");
        CTILogger.info("Creating new DB connection pool...");
        CTILogger.info(lf +
                        " url=" + URL + lf +
                        " user=" + user + lf +
                        " initconns=" + initConns + lf +
                        " maxconns=" + maxConns + lf +
                        " logintimeout=" + this.timeOut );
        
        //create the pools
        initPool();
        
        
        CTILogger.debug( getStats() );
    }
    
    public void freeConnection(Connection conn)
    {
        if( conn == null ) {
            CTILogger.info("ConnectionPool.freeConnection() called with null connection.");
            return;
        }
        
        // Put the connection at the end of the list
        synchronized (this) {
            if (freeConnections.size() < maxConns) {
                freeConnections.add(conn);
                notifyAll();
            } else {
                // are maxConns must have shrunk (JMX anyone?)
                // just close the connection
                closeConnection(conn);
            }
            checkedOut--;
        }
        
        CTILogger.debug(
                        "Returned/Added connection to pool (conn= 0x" +
                        Integer.toHexString(conn.hashCode()) + ")" );
        
        CTILogger.debug( getStats() );
    }
    
    public Connection getConnection() throws SQLException 
    {
    
        Connection conn = getConnection(timeOut * 1000);
        ConnectionWrapper cw = new ConnectionWrapper(conn, this);
        
        CTILogger.debug( "Request for a DB connection granted" );
        
        return cw;
    }                        
    private Connection getConnection(long timeout) 
    throws SQLException
    {
        // Get a pooled Connection from the cache or a new one.
        // Wait if all are checked out and the max limit has
        // been reached.
        long startTime = System.currentTimeMillis();
        long remaining = timeout;
        Connection conn = null;
        
        while( (conn = getPooledConnection()) == null )
        {
            try
            {
                //CTILogger.debug("Waiting for connection. Timeout=" + remaining + " millis");
                synchronized(this) {
                    waitCount++;
                    wait(remaining);
                }
            }
            catch (InterruptedException e)
            { }
            
            remaining = timeout - (System.currentTimeMillis() - startTime);
            if (remaining <= 0)
            {
                synchronized(this) {
                    connectionGetFailureCount++;
                }
                // Timeout has expired
                CTILogger.debug("Time-out while waiting for connection" );
                
                throw new SQLException("getConnection() timed-out");
            }
        }
        
        CTILogger.debug(
                        "Delivered connection from pool (conn= 0x" +
                        Integer.toHexString(conn.hashCode()) + ")" );
        
        CTILogger.debug( getStats() );
        
        return conn;
        
    }   
    
    public String getName() {
        return name;
    }
    
    private Connection getPooledConnection() throws SQLException
    {
        Connection conn = null;
        synchronized(this) {
            if (freeConnections.size() > 0)
            {
                // Pick the first Connection in the Vector
                // to get round-robin usage
                conn = freeConnections.get(0);		 
                freeConnections.remove(0);
                if (!isConnectionOK(conn)) {
                    // the connection had some how gone bad
                    // create a new one to replace it and
                    // return it instead
                    conn = newConnection();
                }
                checkedOut++;
            }
            else if (maxConns == 0 || checkedOut < maxConns )
            {
                // we assume that new connections are always okay
                conn = newConnection();
                checkedOut++;
            }
        }
        
        return conn;
    }
    
    private String getStats() 
    {
        return "POOL STATE: Total connections: " + 
        (freeConnections.size() + checkedOut) + ", " +
        checkedOut + "/" + freeConnections.size() +
        " (Checked-out/Available)";
    }
    
    private void initPool() {
        try {
            synchronized(this) {
                for (int i = 0; i < initConns; i++) {
                    Connection pc = newConnection();
                    freeConnections.add(pc);
                }
            }
        } catch (SQLException e) {
            CTILogger.error("Error initializing pool", e);
        }
    }
    
    private boolean isConnectionOK(Connection conn)
    {
        
        try
        {
            if (!conn.isClosed())
            {
                //something used to query the DB, works for all drivers
                conn.getMetaData().getTypeInfo().close();
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            CTILogger.warn( "Pooled Connection was NOT okay", e );
            
            return false;
        }
        
        return true;
    }
    
    private Connection newConnection() throws SQLException
    {
        Connection conn = null;
        
        /*************************************************************************/
        /** THESE PROPERTIES MUST BE RECOGNIZED BY THE DB DRIVER TO WORK!!!     **/
        /*************************************************************************/
        java.util.Properties p = new java.util.Properties();
        p.put("user", user);
        p.put("password", password);
        p.put("programname", System.getProperty("cti.app.name", "YukonClient"));
        //p.put("timeout", "5");
        
        conn = DriverManager.getConnection(URL, p);
        if (conn == null) {
            // should this be runtime?
            throw new SQLException("DriverManager.getConnection() returned null");
        }
        synchronized(this) {
            connectionsCreated++;
        }
        
        CTILogger.info("New database connection to " + URL);
        
        
        return conn;
    }
    
    public void release() {
        // This method really doesn't work well.
        // To fix it, it needs to prevent new/free connections from being handed
        // out while it is waiting in this loop.
        int to = 0, interval = 20; // 20 * 500ms = 10 seconds
        while (checkedOut > 0 && to < interval) {
            CTILogger.debug("..waiting for CheckedOut dbconnections to be freed (CheckedOut= " + checkedOut + ")");
            try {
                synchronized(this) {
                    wait(500);
                }
                to++;
            } catch (InterruptedException ie) {}
        }
        
        if (to >= interval && checkedOut > 0) {
            throw new IllegalStateException("DB connection pool timed-out during release() operation");
        }
        
        synchronized (this) {
            Iterator connectionIter = freeConnections.iterator();
            while (connectionIter.hasNext()) {
                Connection con = (Connection)connectionIter.next();
                closeConnection(con);
                connectionIter.remove();
            }
        }
        
    }
    
    private void closeConnection(Connection con) {
        try {
            con.close();
            synchronized(this) {
                connectionsClosed++;
            }
        } catch (Exception e) {
            CTILogger.error("Couldn't close connection", e);
        }
    }
    
    
    /**
     * Insert the method's description here.
     * Creation date: (2/20/2002 10:43:51 AM)
     * @return java.lang.String
     */
    public String toString() 
    {
        //private String name;
        //private String URL;
        //private String user;
        //private String password;
        return user + " @ " + URL;
    }

    public void wrapperClosed(Connection realConn) {
        freeConnection(realConn);
    }

    public int getInitConns() {
        return initConns;
    }

    public int getMaxConns() {
        return maxConns;
    }

    public synchronized void setMaxConns(int maxConns) {
        this.maxConns = maxConns;
    }

    public int getCheckedOutCount() {
        return checkedOut;
    }

    public int getConnectionsClosed() {
        return connectionsClosed;
    }

    public int getConnectionsCreated() {
        return connectionsCreated;
    }

    public int getGetConnectionFailureCount() {
        return connectionGetFailureCount;
    }

    public int getWaitLoopCount() {
        return waitCount;
    }
    
}
