package com.cannontech.analysis.data.activity;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;


/**
 * Created on Dec 15, 2003
 * SystemLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class ActivityLog extends com.cannontech.database.db.activity.ActivityLog implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 9;
	
	/** Enum values for column representation */
	public final static int ENERGY_COMPANY_COLUMN = 0;
	public final static int CUSTOMER_COLUMN = 1;
	public final static int USER_COLUMN = 2;
	public final static int ACCOUNT_COLUMN = 3;
	public final static int PAO_NAME_COLUMN = 4;
	public final static int DATE_COLUMN = 5;
	public final static int TIME_COLUMN = 6;
	public final static int ACTION_COLUMN = 7;
	public final static int DESCRIPTION_COLUMN = 8;	
	
	/** String values for column representation */
	public final static String ENERGY_COMPANY_STRING = "Energy Company";
	public final static String CUSTOMER_STRING = "Customer";
	public final static String USER_STRING = "User";
	public final static String ACCOUNT_STRING = "Account";
	public final static String PAO_NAME_STRING = "Pao Name";
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String ACTION_STRING = "Action";
	public final static String DESCRIPTION_STRING = "Description";
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "ENERGY COMPANY ACTIVITY LOG";

	/**
	 * Default Constructor
	 */
	public ActivityLog()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				ENERGY_COMPANY_STRING,
				CUSTOMER_STRING,
				USER_STRING,
				ACCOUNT_STRING,
				PAO_NAME_STRING,
				DATE_STRING,
				TIME_STRING,
				ACTION_STRING,
				DESCRIPTION_STRING
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
				Integer.class,
				Integer.class,
				java.util.Date.class,
				java.util.Date.class,
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
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 150, 18, null),
				new ColumnProperties(0, 1, 50, 18, null),
				new ColumnProperties(50, 1, 50, 18, null),
				new ColumnProperties(100, 1, 50, 18, null),
				new ColumnProperties(150, 1, 50, 18, null),
				new ColumnProperties(200, 1, 50, 18, "MM/dd/yyyy"),
				new ColumnProperties(250, 1, 50, 18, "hh:mm:ss"),
				new ColumnProperties(300, 1, 100, 18, null),
				new ColumnProperties(400, 1, 200, 18, null)
			};				
		}
		return columnProperties;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof ActivityLog)
		{
			ActivityLog al = ((ActivityLog)o);			
			switch( columnIndex)
			{
				case ENERGY_COMPANY_COLUMN:
					return EnergyCompanyFuncs.getEnergyCompany(al.getEnergyCompanyID().intValue()).getName();
				case CUSTOMER_COLUMN:
				{
					LiteCICustomer cust = CustomerFuncs.getLiteCICustomer(al.getCustomerID().intValue());
					if (cust == null)
						return " --- ";
					return cust.getCompanyName();
				}
				case USER_COLUMN:
					return YukonUserFuncs.getLiteYukonUser(al.getUserID().intValue()).getUsername();
				case ACCOUNT_COLUMN:
					return al.getAccountID();
				case PAO_NAME_COLUMN:
					return al.getPaoID();
				case DATE_COLUMN:
					return al.getTimestamp();
				case TIME_COLUMN:
					return al.getTimestamp();
				case ACTION_COLUMN:
					return al.getAction();					
				case DESCRIPTION_COLUMN:
					return al.getDescription();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
}
