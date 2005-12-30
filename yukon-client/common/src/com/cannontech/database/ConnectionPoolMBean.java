package com.cannontech.database;

public interface ConnectionPoolMBean {
    public int getInitConns();
    public int getMaxConns();
    public void setMaxConns(int maxConns);
    public int getCheckedOutCount();
    public int getConnectionsClosed();
    public int getConnectionsCreated();
    public int getGetConnectionFailureCount();
    public int getWaitLoopCount();
}
