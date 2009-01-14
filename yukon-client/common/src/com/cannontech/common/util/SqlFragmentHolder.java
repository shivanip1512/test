package com.cannontech.common.util;

import java.util.Arrays;
import java.util.List;


public class SqlFragmentHolder implements SqlFragmentSource {
    private String sql;
    private List<Object> arguments;
    
    public SqlFragmentHolder() {
        
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
    
    public void setArguments(Object[] arguments) {
    	this.arguments = Arrays.asList(arguments);
    }
    
    public void setArguments(List<Object> arguments) {
    	this.arguments = arguments;
    }
    
    public List<Object> getArgumentList() {
        return arguments;
    }
    
    @Override
    public Object[] getArguments() {
    	return arguments.toArray();
    }

}
