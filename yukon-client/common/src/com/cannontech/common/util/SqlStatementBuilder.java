package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

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
 * Supports PreparedStatement friendly argument tracking
 *   sql.append("select * from foo where bar = ").appendArgument(barArgumentValue);
 *   assert sql.getSQL().equals("select * from foo where bar = ?");
 *   assert sql.getArguments()[0] == barArgumentValue
 *   
 * Supports concatenating fragments (arguments come too):
 *   SqlFragmentSource whereClause = getWhereClause(); // this method may use a SqlStatementBuilder
 *   sql.append("select * from foo where", whereClause);
 */
public class SqlStatementBuilder implements SqlFragmentSource {
    private StringBuilder statement;
    private List<Object> arguments = new ArrayList<Object>(0); // usually not used
    
    public SqlStatementBuilder() {
        this("");
    }

    public SqlStatementBuilder(String initial) {
        statement = new StringBuilder(initial);
        appendSpace();
    }

    /**
     * Converts the array to an SQL safe comma-separated list, appends it to the output,
     * and appends a space.
     * @param array
     * @return this object for chaining
     */
    public SqlStatementBuilder appendList(Object[] array) {
        statement.append(convertToSqlLikeList(Arrays.asList(array)));
        appendSpace();
        return this;
    }
    
    /**
     * Converts the array to an SQL safe comma-separated list, appends it to the output,
     * and appends a space.
     * @param array
     * @return this object for chaining
     */
    public SqlStatementBuilder appendList(Collection<?> array) {
        statement.append(convertToSqlLikeList(array));
        appendSpace();
        return this;
    }
    
    /**
     * For each argument, handling is based on type:
     *   int[]: same as calling appendList with a List<Integer>
     *   long[]: same as calling appendList with a List<Long>
     *   Object[]: same as calling appendList with an array
     *   Collection: same as calling appendList with a Collection
     *   SqlFragmentSource: same as calling appendFragment
     *   else: call toString, trim, append, and append a space
     * @param args
     * @return this object for chaining
     */
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
            } else if (object instanceof SqlFragmentSource) {
            	appendFragment((SqlFragmentSource) object);
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
     *  
     * This method should not be used anymore. See appendArgument.
     * @param arg
     * @return
     */
    @Deprecated
    public SqlStatementBuilder appendQuotedString(Object arg) {
        statement.append('\'');
        statement.append(StringEscapeUtils.escapeSql(arg.toString()));
        statement.append('\'');
        return this;
    }
    
    /**
     * Adds a single space to the output.
     */
    protected void appendSpace() {
        statement.append(" ");
    }
    
    /**
     * Adds a "? " to the output and adds the argument to the argument list.
     * @param argument
     * @return
     */
    public SqlStatementBuilder appendArgument(Object argument) {
    	statement.append("? ");
    	arguments.add(argument);
    	return this;
    }
    
    public SqlStatementBuilder appendArgumentList(Collection<?> list) {
        if(list.isEmpty()) {
            statement.append("null");
            return this;
        } 
        Iterator<?> iter = list.iterator();
        while(iter.hasNext()) {
            Object argument = iter.next();
            if(iter.hasNext()) {
                statement.append("?, ");
            } else {
                statement.append("? ");
            }
            arguments.add(argument);
        }
        return this;
    }
    
    /**
     * Appends the SQL from the fragment to the output and the argument list
     * to the arguments.
     * 
     * The passed in fragment is left unchanged.
     * @param fragment
     * @return
     */
    public SqlStatementBuilder appendFragment(SqlFragmentSource fragment) {
    	statement.append(fragment.getSql().trim());
    	appendSpace();
    	arguments.addAll(fragment.getArgumentList());
    	return this;
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
    
    /** 
     * Prefer getSql() over this method.
     * 
     */
    @Override
    public String toString() {
        return statement.toString();
    }
    
    @Override
    public String getSql() {
    	return statement.toString();
    }
    
    public List<Object> getArgumentList() {
    	return Collections.unmodifiableList(arguments);
    }
    
    @Override
    public Object[] getArguments() {
    	return arguments.toArray();
    }

}
