package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
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
    
    public SqlStatementBuilder appendList(Collection<?> array) {
        statement.append(convertToSqlLikeList(array));
        appendSpace();
        return this;
    }
    
    public SqlStatementBuilder append(Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object object = args[i];

            // must check for primitive arrays before the .isArray() check (because it will pass - then fail later)
            // convert to corresponding Object type and rebuild list before calling appendList()
            if (object instanceof int[]) {
                int[] primitiveIDs = (int[])object;
                List<Integer> objIDs = new ArrayList<Integer>();
                for (int idIdx = 0; idIdx < primitiveIDs.length; idIdx++) {
                    objIDs.add(primitiveIDs[idIdx]);
                }
                appendList(objIDs);
            } else if (object instanceof long[]) {
                long[] primitiveIDs = (long[])object;
                List<Long> objIDs = new ArrayList<Long>();
                for (int idIdx = 0; idIdx < primitiveIDs.length; idIdx++) {
                    objIDs.add(primitiveIDs[idIdx]);
                }
                appendList(objIDs);
            } else if (object instanceof byte[] || 
                        object instanceof short[] || 
                        object instanceof float[] || 
                        object instanceof double[] || 
                        object instanceof boolean[] ||
                        object instanceof char[]) {
                
                throw new RuntimeException("Primitive arrays of type (" + object.getClass().getName() + ") are not supported by SqlStatementBuilder.");
                
            // non-primitives
            } else if (object.getClass().isArray()) {
                // is this the right assumption?
                appendList((Object[]) object);
            } else if (object instanceof Collection) {
                // is this the right assumption?
                appendList((Collection<?>)object);            
            } else {
                // Trim off any leading or trailing space and then add a space to the end
                statement.append(object.toString().trim());
                appendSpace();
            }
        }
        return this;
    }
    
    /**
     * This will insert a quoted string into the output.
     * Example:
     *   In:
     *          the cat's leg
     *   Out: 
     *          'the cat''s leg'
     *          
     *  Unlike other methods, the input argument is not trimmed.
     * @param arg
     * @return
     */
    public SqlStatementBuilder appendQuotedString(Object arg) {
        statement.append('\'');
        statement.append(StringEscapeUtils.escapeSql(arg.toString()));
        statement.append('\'');
        return this;
    }
    
    protected void appendSpace() {
        statement.append(" ");
    }
    
    
    public static String convertToSqlLikeList(Collection<?> ids) {
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
