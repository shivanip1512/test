package com.cannontech.dispatch;


public interface DatabaseChangeEvent {
    public int getPrimaryKey();
    public DbChangeType getChangeType();
    public DbChangeCategory getChangeCategory();
}
