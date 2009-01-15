package com.cannontech.common.util;

import java.util.List;

/**
 * This represents a piece of an SQL statement. It provides access to the
 * actual SQL and any object arguments that correspond to '?' placeholders
 * in the SQL.
 * 
 * It is assumed that the number of '?' in the String returned by getSql()
 * is equal to the size of the List returned by getArgumentList().
 *
 */
public interface SqlFragmentSource {
    /**
     * Returns the SQL String for this fragment.
     * @return
     */
    public String getSql();
    
    /**
     * Returns the arguments as a List.
     * @return
     */
    public List<Object> getArgumentList();
    
    /**
     * Returns the arguments as an Object array.
     * @return
     */
    public Object[] getArguments();
}
