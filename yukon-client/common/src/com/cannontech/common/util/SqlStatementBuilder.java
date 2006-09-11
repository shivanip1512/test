package com.cannontech.common.util;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class SqlStatementBuilder {
    StringBuilder statement;
    
    public SqlStatementBuilder() {
        this("");
    }

    public SqlStatementBuilder(String initial) {
        statement = new StringBuilder(initial);
    }

    public SqlStatementBuilder appendList(Object[] array) {
        statement.append(convertToSqlLikeList(Arrays.asList(array)));
        appendSpace();
        return this;
    }
    
    public SqlStatementBuilder appendList(Collection array) {
        statement.append(convertToSqlLikeList(array));
        appendSpace();
        return this;
    }
    
    public SqlStatementBuilder append(Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];
            if (object.getClass().isArray()) {
                // is this the right assumption?
                appendList((Object[]) object);
            } else if (object instanceof Collection) {
                // is this the right assumption?
                appendList((Collection)object);
            } else {
                statement.append(object.toString());
                appendSpace();
            }
        }
        return this;
    }
    
    protected void appendSpace() {
        statement.append(" ");
    }
    
    
    public static String convertToSqlLikeList(Collection ids) {
        String groupIdStr;
        if (!ids.isEmpty()) {
            groupIdStr = StringUtils.join(ids.iterator(), ",");
        } else {
            groupIdStr = "null";
        }
        return groupIdStr;
    }
    
    @Override
    public String toString() {
        return statement.toString();
    }
    

}
