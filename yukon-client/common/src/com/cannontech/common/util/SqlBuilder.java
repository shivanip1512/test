package com.cannontech.common.util;

import java.util.Collection;

public interface SqlBuilder {

    /**
     * Converts the array to an SQL safe comma-separated list, appends it to the output,
     * and appends a space.
     * @param array
     * @return this object for chaining
     */
    public SqlBuilder appendList(Object[] array);

    /**
     * Converts the array to an SQL safe comma-separated list, appends it to the output,
     * and appends a space.
     * @param array
     * @return this object for chaining
     */
    public SqlBuilder appendList(Collection<?> array);

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
    public SqlBuilder append(Object... args);

    /**
     * Adds a "? " to the output and adds the argument to the argument list.
     * 
     * This method has special handling for some arguments based on the type
     * of Object passed in:
     * 
     *   DatabaseRepresentationSource - call getDatabaseRepresentation() and append result
     *   Enum - call name() and append result
     *   ReadableInstant - call toDate() and append result
     * @param argument
     * @return
     */
    public SqlBuilder appendArgument(Object argument);

    public SqlBuilder appendArgumentList(Collection<?> list);

    /**
     * Appends the SQL from the fragment to the output and the argument list
     * to the arguments.
     * 
     * The passed in fragment is left unchanged.
     * @param fragment
     * @return
     */
    public SqlBuilder appendFragment(SqlFragmentSource fragment);
    
    public SqlBuilder eq(Object argument);
    
    public SqlBuilder neq(Object argument);
    
    public SqlBuilder lt(Object argument);
    
    public SqlBuilder gt(Object argument);
    
    public SqlBuilder gte(Object argument);
    
    public SqlBuilder lte(Object argument);
    
    public SqlBuilder in(Collection<?> list);
    
    public SqlBuilder in(SqlFragmentSource sqlFragmentSource);
    
    public SqlBuilder startsWith(String argument);
    
    public SqlBuilder endsWith(String argument);
    
    public SqlBuilder contains(String argument);

}