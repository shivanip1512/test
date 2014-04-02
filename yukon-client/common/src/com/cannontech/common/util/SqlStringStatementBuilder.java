package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * A helper class for building SQL statements.
 *   SqlStatementBuilder sql = new SqlStatementBuilder();
 *   
 * Supports chaining (note white space added automatically):
 *   sql.append("select *").append("from");
 *   
 * Supports variable arguments (easily intermix constants and strings):
 *   sql.append("select * from", TABLE_NAME); // look ma, no + sign!
 *   
 * Supports IN clause lists:
 *   sql.append("foo in (").appendList(someIntList).append(")");
 *   
 * Supports IN clause lists as variable arguments:
 *   sql.append("foo in (", someIntList, ")");
 *   
 */
public class SqlStringStatementBuilder {
    private StringBuilder statement;
    
    public SqlStringStatementBuilder() {
        this("");
    }

    public SqlStringStatementBuilder(Object... args) {
        statement = new StringBuilder();
        append(args);
    }

    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendList(java.lang.Object[])
     */
    public SqlStringStatementBuilder appendList(Object[] array) {
        statement.append(convertToSqlLikeList(Arrays.asList(array)));
        appendSpace();
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendList(java.util.Collection)
     */
    public SqlStringStatementBuilder appendList(Collection<?> array) {
        statement.append(convertToSqlLikeList(array));
        appendSpace();
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#append(java.lang.Object)
     */
    public SqlStringStatementBuilder append(Object... args) {
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
            } else if (object instanceof Collection<?>) {
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
     * Adds a single space to the output.
     */
    protected void appendSpace() {
        statement.append(" ");
    }
    
    /**
     * Essentially the same as calling StringUtils.join with a comma,
     * but if the list is empty "null" is returned instead of "".
     * This makes it easy to put the output of this method in parenthesis
     * for use in a SQL IN clause.
     * @param ids Any collection, toString will be called on each value
     * @return
     */
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
