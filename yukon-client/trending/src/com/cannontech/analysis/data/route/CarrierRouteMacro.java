/*
 * Created on Mar 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.route;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;

/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/** Inner class container of table model data*/
public class CarrierRouteMacro implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	public final static int MACRO_ROUTE_NAME_COLUMN = 0;
	public final static int ROUTE_NAME_COLUMN = 1;
	public final static int TRANSMITTER_NAME_COLUMN = 2;
	public final static int CCU_BUS_NUMBER_COLUMN = 3;
	public final static int AMP_USE_COLUMN = 4;
	public final static int FIXED_BITS_COLUMN = 5;
	public final static int VARIABLE_BITS_COLUMN = 6;
	public final static int DEFAULT_ROUTE_COLUMN = 7;
	
	/** String values for column representation */
	public final static String MACRO_ROUTE_NAME_STRING = "Route Macro Name";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String TRANSMITTER_NAME_STRING  = "Transmitter Name";
	public final static String CCU_BUS_NUMBER_STRING = "CCU Bus Number";
	public final static String AMP_USE_STRING = "AMP Use";
	public final static String FIXED_BITS_STRING = "Fixed Bits";
	public final static String VARIABLE_BITS_STRING = "Variable Bits";
	public final static String DEFAULT_ROUTE_STRING = "Default Route";
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "Database Report - Route Macro";

	public String routeMacroName = null;
	public String routeName = null;
	public String transmitterName = null;			
	public String ccuBusNumber = null;
	public String ampUse = null;
	public String fixedBits = null;
	public String variableBits = null;
	public String defaultRoute = null;
	
	
	/**
	 * 
	 */
	public CarrierRouteMacro()
	{
		super();
	}

	/**
	 * @param routeMacroName_
	 * @param routeName_
	 * @param transmitterName_
	 * @param ccuBusNumber_
	 * @param ampUse_
	 * @param fixedBits_
	 * @param variableBits_
	 * @param defaultRoute_
	 */
	public CarrierRouteMacro(String routeMacroName_, String routeName_, String transmitterName_, String ccuBusNumber_, String ampUse_, String fixedBits_, String variableBits_, String defaultRoute_)
	{
		routeMacroName = routeMacroName_;
		routeName = routeName_;
		transmitterName = transmitterName_;
		ccuBusNumber = ccuBusNumber_;
		ampUse = ampUse_;
		fixedBits = fixedBits_;
		variableBits = variableBits_;
		defaultRoute = defaultRoute_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof CarrierRouteMacro)
		{
			CarrierRouteMacro CarrierRouteMacro = ((CarrierRouteMacro)o); 
			switch( columnIndex)
			{
				case MACRO_ROUTE_NAME_COLUMN:
					return CarrierRouteMacro.routeMacroName;
		
				case ROUTE_NAME_COLUMN:
					return CarrierRouteMacro.routeName;
	
				case TRANSMITTER_NAME_COLUMN:
					return CarrierRouteMacro.transmitterName;
	
				case CCU_BUS_NUMBER_COLUMN:
					return CarrierRouteMacro.ccuBusNumber;
				
				case AMP_USE_COLUMN:
					return CarrierRouteMacro.ampUse;
				
				case FIXED_BITS_COLUMN:
					return CarrierRouteMacro.fixedBits;
					
				case VARIABLE_BITS_COLUMN:
					return CarrierRouteMacro.variableBits;
					
				case DEFAULT_ROUTE_COLUMN:
					return CarrierRouteMacro.defaultRoute;
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				MACRO_ROUTE_NAME_STRING,
				ROUTE_NAME_STRING,
				TRANSMITTER_NAME_STRING,
				CCU_BUS_NUMBER_STRING,
				AMP_USE_STRING,
				FIXED_BITS_STRING,
				VARIABLE_BITS_STRING,
				DEFAULT_ROUTE_STRING,
			};
		}
		return columnNames;
	}
	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class
			};
		}
		return columnTypes;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{

		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]
			{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 20, 20, null),
				new ColumnProperties(20, 1, 85, 20, "#,##0"),
				new ColumnProperties(110, 1, 85, 20, "#,##0"),
				new ColumnProperties(205, 1, 35, 20, "##0.00%"),
				new ColumnProperties(250, 1, 50, 20, "##0.00%"),
				new ColumnProperties(310, 1, 50, 20, "##0.00%"),
				new ColumnProperties(365, 1, 50, 20, "##0.00%"),
				new ColumnProperties(430, 1, 50, 20, "##0.00%")
			};
		}
		return columnProperties;
	}	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}	
}