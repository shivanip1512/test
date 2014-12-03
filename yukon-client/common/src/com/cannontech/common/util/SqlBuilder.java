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
    
    /**
     * something
     * @param constant
     * @return
     */
    public SqlBuilder appendArgument_k(Enum<?> constant);

    public SqlBuilder appendArgumentList(Iterable<?> list);

    /**
     * Appends the SQL from the fragment to the output and the argument list
     * to the arguments. Note that the SqlFragmentSource is evaluated when
     * the next method on this object is invoked. This means that the fragment
     * can be changed, after it is appended, up until another method is invoked.
     * This behavior usually will make no difference, but it may enable certain
     * builder syntaxes to be created more easily.
     * 
     * The passed in fragment is left unchanged.
     * @param fragment
     * @return
     */
    public SqlBuilder appendFragment(SqlFragmentSource fragment);
    
    public SqlBuilder eq(Object argument);
    
    public SqlBuilder eq_k(int constant);
    
    public SqlBuilder eq_k(Enum<?> constant);
    
    public SqlBuilder neq(Object argument);
    
    public SqlBuilder neq_k(int constant);
    
    public SqlBuilder neq_k(Enum<?> constant);
    
    public SqlBuilder lt(Object argument);
    
    public SqlBuilder gt(Object argument);
    
    public SqlBuilder gte(Object argument);
    
    public SqlBuilder lte(Object argument);
    
    /**Appends the constant to the current query as a string 
     * rather than as a variable.Preferably use the gt/lte methods
     * NOTE:Contact Stacey before using these methods  
     * @param long
     * @return
     */
    public SqlBuilder gt_k(long constant);
    
    /**Appends the constant to the current query as a string 
     * rather than as a variable.Preferably use the gt/lte methods
     * NOTE:Contact Stacey before using these methods  
     * @param long
     * @return
     */
    public SqlBuilder lte_k(long constant);
    
    /**
     * Appends the list of arguments to the current query using the IN syntax.
     * This method shouldn't be used when it's possible that the iterable contains
     * 1000 or more elements. Favor appending an SQL query instead when possible. 
     * @param list
     * @return
     */
    public SqlBuilder in(Iterable<?> list);

    /**
     * Append a list of enums as constants.
     */
    public SqlBuilder in_k(Iterable<? extends Enum<?>> list);
    
    public SqlBuilder in(SqlFragmentSource sqlFragmentSource);
    
    /**
     * Appends the list of arguments to the current query using the NOT IN syntax.
     * This method shouldn't be used when it's possible that the iterable contains
     * 1000 or more elements. Favor appending an SQL query instead when possible. 
     * @param list
     * @return
     */
    public SqlBuilder notIn(Iterable<?> list);
    
    public SqlBuilder notIn(SqlFragmentSource sqlFragmentSource);
    
    public SqlBuilder startsWith(String argument);
    
    public SqlBuilder endsWith(String argument);
    
    public SqlBuilder contains(String argument);

}