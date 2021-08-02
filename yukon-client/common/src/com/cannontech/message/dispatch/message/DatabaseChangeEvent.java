package com.cannontech.message.dispatch.message;

import java.io.Serializable;

public interface DatabaseChangeEvent extends Serializable{
    public int getPrimaryKey();
    public DbChangeType getChangeType();
    public DbChangeCategory getChangeCategory();
}
