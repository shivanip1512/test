package com.cannontech.message.dispatch.message;

public interface DatabaseChangeEvent {
    public int getPrimaryKey();
    public DbChangeType getChangeType();
    public DbChangeCategory getChangeCategory();
}
