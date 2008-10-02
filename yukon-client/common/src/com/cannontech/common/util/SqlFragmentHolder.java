package com.cannontech.common.util;


public class SqlFragmentHolder {
    private String sql;
    private Object[] arguments;
    
    public SqlFragmentHolder() {
        
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
    
    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
    
    public Object[] getArguments() {
        return arguments;
    }

}
