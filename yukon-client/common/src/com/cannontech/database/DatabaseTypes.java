package com.cannontech.database;

/**
 * This type was created in VisualAge.
 */
public class DatabaseTypes {

	public static final int CORE_DB = 1;
	public static final int LM_DB = 2;
	public static final int CAP_CONTROL_DB = 3;
	public static final int SYSTEM_DB = 4;
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public final static Object convertDBToJavaType(Object o) {

	if( o instanceof java.math.BigDecimal )
		return new Integer( ((java.math.BigDecimal)o).intValue() );

	return o;
}
}
