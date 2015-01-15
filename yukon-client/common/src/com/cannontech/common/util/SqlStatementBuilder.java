package com.cannontech.common.util;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;

import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.database.SqlParameterBase;
import com.cannontech.database.SqlParameterSink;
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
    private final DeferedSqlFragmentHelper base = new DeferedSqlFragmentHelper();
    
    private static class SpaceAppendingSqlFragmentSource implements SqlFragmentSource {
        private final SqlFragmentSource delegate;
        public SpaceAppendingSqlFragmentSource(SqlFragmentSource delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public String getSql() {
            return delegate.getSql().trim() + " ";
        }

        @Override
        public List<Object> getArgumentList() {
            return delegate.getArgumentList();
        }

        @Override
        public Object[] getArguments() {
            return delegate.getArguments();
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
    
    public SqlStatementBuilder() {
        this("");
    }

    public SqlStatementBuilder(String initial) {
        addString(initial);
        appendSpace();
    }

    private void addString(String statement) {
        base.appendStatement(statement);
    }

    private void addRawArgument(Object arg) {
        base.appendArgument(arg);
    }
    
    @Override
    public SqlStatementBuilder appendFragment(SqlFragmentSource fragment) {
        base.appendFragment(new SpaceAppendingSqlFragmentSource(fragment));
        return this;
    }
    
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendList(java.lang.Object[])
     */
    @Override
    public SqlStatementBuilder appendList(Object[] array) {
        addString(convertToSqlLikeList(Arrays.asList(array)));
        appendSpace();
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendList(java.util.Collection)
     */
    @Override
    public SqlStatementBuilder appendList(Collection<?> array) {
        addString(convertToSqlLikeList(array));
        appendSpace();
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#append(java.lang.Object)
     */
    @Override
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
            } else if (object instanceof RowMapperWithBaseQuery<?>) {
                appendFragment(((RowMapperWithBaseQuery<?>)object).getBaseQuery());
            } else {
                // Trim off any leading or trailing space and then add a space to the end
                addString(object.toString().trim());
                appendSpace();
            }
        }
        return this;
    }

    /**
     * Adds a single space to the output.
     */
    protected void appendSpace() {
        addString(" ");
    }
    
    @Override
    public SqlStatementBuilder eq(Object argument) {
        addString("= ");
        appendArgument(argument);
        return this;
    }
    
    @Override
    public SqlStatementBuilder eq_k(int constant) {
        addString("= ");
        addString(Integer.toString(constant));
        appendSpace();
        return this;
    }
    
    @Override
    public SqlStatementBuilder eq_k(Enum<?> constant) {
        addString("=");
        return appendArgument_k(constant);
    }
    
    @Override
    public SqlStatementBuilder neq(Object argument) {
        addString("!= ");
        appendArgument(argument);
        return this;
    }
    
    @Override
    public SqlStatementBuilder neq_k(int constant) {
        addString("!= ");
        addString(Integer.toString(constant));
        appendSpace();
        return this;
    }
    
    @Override
    public SqlStatementBuilder neq_k(Enum<?> constant) {
        addString("!= '");
        if (constant instanceof DatabaseRepresentationSource) {
            addString(((DatabaseRepresentationSource) constant).getDatabaseRepresentation().toString());
        } else {
            addString(((Enum<?>) constant).name());
        }
        addString("' ");
        return this;
    }
    
    @Override
    public SqlStatementBuilder lt(Object argument) {
        addString("< ");
        appendArgument(argument);
        return this;
    }
    
    @Override
    public SqlStatementBuilder gt(Object argument) {
        addString("> ");
        appendArgument(argument);
        return this;
    }
    
    @Override
     public SqlStatementBuilder gt_k(long constant) {
         addString("> ");
         addString(Long.toString(constant));
         appendSpace();
         return this;
     }
    
    @Override
    public SqlStatementBuilder gte(Object argument) {
        addString(">= ");
        appendArgument(argument);
        return this;
    }
    
    @Override
    public SqlStatementBuilder lte(Object argument) {
        addString("<= ");
        appendArgument(argument);
        return this;
    }
    
    @Override
    public SqlStatementBuilder lte_k(long constant) {
        addString("<= ");
        addString(Long.toString(constant));
        appendSpace();
        return this;
    }
    
    @Override
    public SqlStatementBuilder in(Iterable<?> list) {
        addString("in (");
        appendArgumentList(list);
        addString(") ");
        return this;
    }

    @Override
    public SqlStatementBuilder in_k(Iterable<? extends Enum<?>> constants) {
        addString("in (");

        Iterator<? extends Enum<?>> iter = constants.iterator();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                Enum<?> argument = iter.next();
                appendArgument_k(argument);
                if (iter.hasNext()) {
                    addString(",");
                }
            }
        } else {
            addString("null");
        }

        addString(") ");

        return this;
    }

    @Override
    public SqlStatementBuilder notIn(Iterable<?> list) {
        addString("not ");
        return in(list);
    }
    
    /**
     * Takes a Map of column names to values, and creates the SET clause.
     * For example, a map with
     * 
     * "UserId" -> 17,
     * "UserName" -> "Yukon"
     * 
     * would produce the SQL fragment
     * 
     * set UserId=17, UserName='Yukon'
     */
    public SqlStatementBuilder set(Map<String, Object> valueMap) {
        checkArgument(valueMap != null && !valueMap.isEmpty(), "Value map must not be empty.");
        append("set");
        String separator = "";
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();
            append(separator).append(columnName).append("=").appendArgument(value);
            separator = ", ";
        }
        return this;
    }
    
    public SqlStatementBuilder set(String columnName, Object value, Object... remaining) {
        
        if (remaining.length % 2 != 0) {
            throw new IllegalArgumentException("Must specify column names and values in pairs.");
        }
        append("set");
        append(columnName);
        append("=");
        appendArgument(value);
        for (int index = 0; index < remaining.length; index += 2) {
            if (remaining[index] instanceof String) {
                append(", ");
                append(remaining[index]);
                append("=");
                appendArgument(remaining[index + 1]);
            } else {
                throw new IllegalArgumentException("column names must be string values");
            }
        }
        return this;
    }
    
    public SqlStatementBuilder values(Object first, Object... remaining) {
        List<Object> list = Lists.newArrayListWithCapacity(1 + remaining.length);
        list.add(first);
        list.addAll(Arrays.asList(remaining));
        return values(list);
    }
    
    public SqlStatementBuilder values(Iterable<?> values) {
        addString("values (");
        appendArgumentList(values);
        addString(") ");
        return this;
    }
    
    @Override
    public SqlStatementBuilder in(SqlFragmentSource sqlFragmentSource) {
        addString("in (");
        appendFragment(sqlFragmentSource);
        addString(") ");
        return this;
    }
    
    @Override
    public SqlStatementBuilder notIn(SqlFragmentSource sqlFragmentSource) {
        addString("not ");
        return in(sqlFragmentSource);
    }
    
    @Override
    public SqlStatementBuilder startsWith(String argument) {
        addString("like ");
        appendArgument(argument + "%");
        return this;
    }
    
    public SqlStatementBuilder startsWithUppercase(String argument) {
        addString("like upper(");
        appendArgument(argument + "%");
        addString(") ");
        return this;
    }
    
    @Override
    public SqlStatementBuilder endsWith(String argument) {
        addString("like ");
        appendArgument("%" + argument);
        return this;
    }
    
    @Override
    public SqlStatementBuilder contains(String argument) {
        addString("like ");
        appendArgument("%" + argument + "%");
        return this;
    }
    
    public SqlStatementBuilder upperAppend(String statement) {
        addString("upper(");
        append(statement);
        addString(") ");
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendArgument(java.lang.Object)
     */
    @Override
    public SqlStatementBuilder appendArgument(Object argument) {
        addString("? ");
    	addArgument(argument);
    	return this;
    }
    
    @Override
    public SqlStatementBuilder appendArgument_k(Enum<?> constant) {
        addString(" '");
        if (constant instanceof DatabaseRepresentationSource) {
            addString(((DatabaseRepresentationSource) constant).getDatabaseRepresentation().toString());
        } else {
            addString(((Enum<?>) constant).name());
        }
        addString("' ");
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.common.util.SqlBuilder#appendArgumentList(java.util.Collection)
     */
    @Override
    public SqlStatementBuilder appendArgumentList(Iterable<?> objects) {
        Iterator<?> iter = objects.iterator();
        if(!iter.hasNext()) {
            addString("null");
            return this;
        } 

        while(iter.hasNext()) {
            Object argument = iter.next();
            if(iter.hasNext()) {
                addString("?, ");
            } else {
                addString("? ");
            }
            addArgument(argument);
        }
        return this;
    }

    private void addArgument(Object argument) {
        addRawArgument(convertArgumentToJdbcObject(argument));
    }
    
    /** 
     * This method takes in a java object and tries to convert it over to a database
     * friendly representation of the object.
     * 
     * Has specific handling for:
     *   DatabaseRepresentationSource
     *   Enum
     *   ReadableInstant
     *   ReadablePeriod
     * 
     * @param argument - A java object
     * @return An SQL friendly object
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

    public SqlParameterSink insertInto(String tableName) {
        assertSqlIdentifier(tableName);
        append("INSERT INTO", tableName);
        
        final List<String> columnNames = Lists.newArrayList();
        final List<Object> columnValues = Lists.newArrayList();
        
        SqlParameterSink sink = new SqlParameterBase() {
            @Override
            public void addValueRaw(String paramName, Object value) {
                assertSqlIdentifier(paramName);
                columnNames.add(paramName);
                columnValues.add(value);
            }
        };
        
        SqlFragmentSource fragment = new SqlFragmentSource() {
            
            @Override
            public String getSql() {
                StringBuilder s = new StringBuilder();
                s.append("(");
                s.append(StringUtils.join(columnNames, ","));
                s.append(") VALUES (");
                s.append(StringUtils.join(Collections.nCopies(columnValues.size(), "?"), ","));
                s.append(")");
                return s.toString();
            }
            
            @Override
            public Object[] getArguments() {
                return columnValues.toArray();
            }
            
            @Override
            public List<Object> getArgumentList() {
                return columnValues;
            }
            
            @Override
            public String getDebugSql() {
                StringBuilder builder = new StringBuilder("Query: ");
                builder.append(getSql());
                builder.append(" Arguments: ");
                builder.append(Arrays.toString(getArguments()));
                return builder.toString();
            }
        };
        appendFragment(fragment);
        return sink;
    }

    public SqlParameterSink update(String tableName) {
        assertSqlIdentifier(tableName);
        append("UPDATE", tableName, "SET");
        
        final SqlFragmentCollection fragmentCollection = new SqlFragmentCollection(", ", false);
        
        SqlParameterSink sink = new SqlParameterBase() {
            @Override
            public void addValueRaw(String paramName, Object value) {
                assertSqlIdentifier(paramName);
                SqlFragment fragment = new SqlFragment(paramName + " = ? ", value);
                fragmentCollection.add(fragment);
            }
        };
        
        appendFragment(fragmentCollection);
        return sink;
    }
    
    private void assertSqlIdentifier(String tableName) {
        // this check could be fancier, but it is good enough to prevent injection
        if (!StringUtils.isAlphanumeric(tableName)) {
            throw new IllegalArgumentException(tableName + " should be alphanumeric");
        }
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
        return base.getSql();
    }
    
    @Override
    public String getSql() {
    	return base.getSql();
    }
    
    @Override
    public List<Object> getArgumentList() {
    	return Collections.unmodifiableList(base.getArgumentList());
    }
    
    @Override
    public Object[] getArguments() {
    	return base.getArguments();
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
