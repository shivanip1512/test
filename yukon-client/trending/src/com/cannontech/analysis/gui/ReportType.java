/*
 * Created on Nov 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.report;

/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportType {
	

		public static final int LOAD_GROUP = 1;
		public static final int STATISTICS = 2;
		
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
