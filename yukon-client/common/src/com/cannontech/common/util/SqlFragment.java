package com.cannontech.common.util;

import java.util.Arrays;
import java.util.List;


public class SqlFragment implements SqlFragmentSource {
    private String sql;
    private Object[] arguments;
    
    public SqlFragment(String sql, Object... arguments) {
        this.sql = sql;
        this.arguments = arguments;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
    
    public List<Object> getArgumentList() {
        return Arrays.asList(arguments);
    }
    
    @Override
    public Object[] getArguments() {
    	return arguments;
    }

}
