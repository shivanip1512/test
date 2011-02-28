package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;

import com.google.common.collect.Lists;

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
 * Supports PreparedStatement friendly argument tracking:
 *   sql.append("select * from foo where bar = ").appendArgument(barArgumentValue);
 *   assert sql.getSQL().equals("select * from foo where bar = ?");
 *   assert sql.getArguments()[0] == barArgumentValue
 *   
 * Supports concatenating fragments (arguments come too):
 *   SqlFragmentSource whereClause = getWhereClause(); // this method may use a SqlStatementBuilder
 *   sql.append("select * from foo where", whereClause);
 *   
 * Supports expanded syntax for adding arguments with simple operators (eq, neq, lt, gt, lte, gte, in):
 *   sql.append("where foo").eq(fooArg); // appends "= ? " to the string and adds the argument
 *   sql.append("  and size").lte(99);
 *   sql.append("  and id").in(idList); // appends "in (...)", handles arguments like appendArgumentList
 */
public class SqlStatementBuilder implements SqlFragmentSource, SqlBuilder {
    private StringBuilder statement;
    private List<Object> arguments = new ArrayList<Object>(0); // usually not used
    
    public SqlStatementBuilder() {
        this("");
    }

    public SqlStatementBuilder(String initial) {
        statement = new StringBuilder(initial);
        appendSpace();
    }

    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendList(java.lang.Object[])
     */
    public SqlStatementBuilder appendList(Object[] array) {
        statement.append(convertToSqlLikeList(Arrays.asList(array)));
        appendSpace();
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendList(java.util.Collection)
     */
    public SqlStatementBuilder appendList(Collection<?> array) {
        statement.append(convertToSqlLikeList(array));
        appendSpace();
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#append(java.lang.Object)
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
            } else if (object instanceof Collection<?>) {
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
    
    public SqlStatementBuilder eq(Object argument) {
        statement.append("= ");
        appendArgument(argument);
        return this;
    }
    
    public SqlStatementBuilder eq_k(int constant) {
        statement.append("= ");
        statement.append(Integer.toString(constant));
        statement.append(" ");
        return this;
    }
    
    public SqlStatementBuilder eq_k(Enum<?> constant) {
        statement.append("= '");
        if (constant instanceof DatabaseRepresentationSource) {
            statement.append(((DatabaseRepresentationSource) constant).getDatabaseRepresentation());
        } else {
            statement.append(((Enum<?>) constant).name());
        }
        statement.append("'");
        return this;
    }
    
    public SqlStatementBuilder neq(Object argument) {
        statement.append("!= ");
        appendArgument(argument);
        return this;
    }
    
    public SqlStatementBuilder lt(Object argument) {
        statement.append("< ");
        appendArgument(argument);
        return this;
    }
    
    public SqlStatementBuilder gt(Object argument) {
        statement.append("> ");
        appendArgument(argument);
        return this;
    }
    
    public SqlStatementBuilder gte(Object argument) {
        statement.append(">= ");
        appendArgument(argument);
        return this;
    }
    
    public SqlStatementBuilder lte(Object argument) {
        statement.append("<= ");
        appendArgument(argument);
        return this;
    }
    
    public SqlStatementBuilder in(Iterable<?> list) {
        statement.append("in (");
        appendArgumentList(list);
        statement.append(") ");
        return this;
    }
    
    public SqlStatementBuilder values(Object first, Object... remaining) {
        statement.append("values (");
        List<Object> list = Lists.newArrayListWithCapacity(1 + remaining.length);
        list.add(first);
        list.addAll(Arrays.asList(remaining));
        appendArgumentList(list);
        statement.append(") ");
        return this;
    }
    
    public SqlStatementBuilder in(SqlFragmentSource sqlFragmentSource) {
        statement.append("in (");
        appendFragment(sqlFragmentSource);
        statement.append(") ");
        return this;
    }
    
    public SqlStatementBuilder startsWith(String argument) {
        statement.append("like ");
        appendArgument(argument + "%");
        return this;
    }
    
    public SqlStatementBuilder endsWith(String argument) {
        statement.append("like ");
        appendArgument("%" + argument);
        return this;
    }
    
    public SqlStatementBuilder contains(String argument) {
        statement.append("like ");
        appendArgument("%" + argument + "%");
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendArgument(java.lang.Object)
     */
    public SqlStatementBuilder appendArgument(Object argument) {
    	statement.append("? ");
    	addArgument(argument);
    	return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendArgumentList(java.util.Collection)
     */
    public SqlStatementBuilder appendArgumentList(Iterable<?> objects) {
        Iterator<?> iter = objects.iterator();
        if(!iter.hasNext()) {
            statement.append("null");
            return this;
        } 

        while(iter.hasNext()) {
            Object argument = iter.next();
            if(iter.hasNext()) {
                statement.append("?, ");
            } else {
                statement.append("? ");
            }
            addArgument(argument);
        }
        return this;
    }

    private void addArgument(Object argument) {
        arguments.add(convertArgumentToJdbcObject(argument));
    }
    
    /** 
     * This method takes in a java object and tries to convert it over to a database
     * friendly representation of the object.
     * 
     * @param argument - A java object
     * @return - A Sql friendly object
     */
    public static Object convertArgumentToJdbcObject(Object argument) {
        if (argument instanceof DatabaseRepresentationSource) {
            return ((DatabaseRepresentationSource) argument).getDatabaseRepresentation();
        } else if (argument instanceof Enum<?>) {
            Enum<?> e = (Enum<?>) argument;
            return e.name();
        } else if (argument instanceof ReadableInstant) {
            return ((ReadableInstant) argument).toInstant().toDate();
        } else if (argument instanceof ReadablePeriod) {
            return ISOPeriodFormat.standard().print((ReadablePeriod) argument);
        } else {
            return argument;
        }
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendFragment(com.cannontech.common.util.SqlFragmentSource)
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
