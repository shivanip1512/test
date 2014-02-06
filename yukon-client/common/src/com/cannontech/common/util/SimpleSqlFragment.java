package com.cannontech.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SimpleSqlFragment implements SqlFragmentSource {
    private String sql;
    
    public SimpleSqlFragment() {
    }
    
    public SimpleSqlFragment(String sql) {
        this.sql = sql;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public String getSql() {
        return sql;
    }
    
    public List<Object> getArgumentList() {
        return Collections.emptyList();
    }
    
    @Override
    public Object[] getArguments() {
    	return Collections.emptyList().toArray();
    }

    @Override
    public String getDebugSql() {
        StringBuilder builder = new StringBuilder("Query: ");
        builder.append(getSql());
        builder.append(" Arguments: ");
        builder.append(Arrays.toString(getArguments()));
        return builder.toString();
    }
}
