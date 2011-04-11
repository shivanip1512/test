package com.cannontech.database;


public interface SqlParameterSink {
    /**
     * Associates the paramName and the value. The value object is processed
     * with the com.cannontech.common.util.SqlStatementBuilder.convertArgumentToJdbcObject(Object)
     * method (like all arguments to the SqlStatementBuilder.
     * 
     * If in doubt, use this method.
     * @param paramName
     * @param value
     */
    public void addValue(String paramName, Object value);
    
    /**
     * Assosciates the paramName and the value. Value must be a String
     * and will be run through the com.cannontech.database.SqlUtils.convertStringToDbValue(String)
     * method.
     * @param paramName
     * @param value
     */
    public void addValueSafe(String paramName, String value);
    
    /**
     * Associatest the paramName and the value. Value will not be processed, it will simply
     * be added to the argument list and passed directly to the JdbcTemplate.
     * @param paramName
     * @param value
     */
    public void addValueRaw(String paramName, Object value);

}
