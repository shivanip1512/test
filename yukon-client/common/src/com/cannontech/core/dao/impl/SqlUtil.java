package com.cannontech.core.dao.impl;

import org.apache.commons.lang.StringUtils;

public class SqlUtil {
    
    /**
     * 
     * @param table
     * @param column
     * @param args
     * @param sql
     */
    public static void buildInClause(String prefix, String table, String column, String[] args, StringBuilder sql) {
        if(args != null && args.length > 0) {
            
            sql.append(prefix)
                .append(" ")
                .append(table)
                .append(".")
                .append(column)
                .append(" in ('")
                .append(StringUtils.join(args,"','"))
                .append("') ");
        }
        
    }
    
    public static void buildInClause(String prefix, String table, String column, Object[] args, StringBuilder sql) {
        if(args != null && args.length > 0) {
            sql.append(prefix)
                .append(" ")
                .append(table)
                .append(".") 
                .append(column)
                .append(" in (")
                .append(StringUtils.join(args,","))
                .append(") ");
        }
        
    }
}