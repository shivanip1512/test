package com.cannontech.analysis.data.activity;

import java.util.Date;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;


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
public class ActivityLog implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int ENERGY_COMPANY_COLUMN = 0;
	public final static int CONTACT_COLUMN = 1;
	public final static int ACCOUNT_NUMBER_COLUMN = 2;
	public final static int ACTION_COLUMN = 3;
	public final static int ACTION_COUNT_COLUMN = 4;
//	public final static int DATE_TIME_COLUMN = 5;
//	public final static int ACTION_COLUMN = 6;
//	public final static int DESCRIPTION_COLUMN = 7;	
	
	/** String values for column representation */
	public final static String ENERGY_COMPANY_STRING = "Energy Company";
	public final static String CONTACT_STRING = "Contact";
	public final static String ACCOUNT_NUMBER_STRING = "Account Number";
	public final static String ACTION_STRING = "Action";
	public final static String ACTION_COUNT_STRING = "Count";
//	public final static String DATE_TIME_STRING = "Date/Time";
//	public final static String ACTION_STRING = "Action";
//	public final static String DESCRIPTION_STRING = "Description";
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "ENERGY COMPANY ACTIVITY LOG";

	public Integer ecID = null;
	public Integer custID = null;	//used to get the primary contact
	public String acctNumber = null;
	public Integer actionCount = new Integer(0);			
//	public Date dateTime = null;
	public String action = null;
//	public String description = null;
	
	/**
	 * Default Constructor
	 */
	public ActivityLog()
	{
		super();
	}
	/**
	 * Default Constructor
	 */
	public ActivityLog(Integer ecID_, Integer custID_, String acctNum_, Integer actionCount_, String action_)
	{
		super();
		ecID = ecID_;
		custID = custID_;
		acctNumber = acctNum_;
		actionCount = actionCount_;
		action = action_;
	}
	/**
	 * Default Constructor
	 */
	public ActivityLog(Integer ecID_, Integer custID_, Integer acctID_, Integer actionCount_, 
						Date dateTime_, String action_, String description_)
	{
		super();
		ecID = ecID_;
		custID = custID_;
		actionCount = actionCount_;
//		dateTime = dateTime_;
		action = action_;
//		description = description_;		
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
				CONTACT_STRING,
				ACCOUNT_NUMBER_STRING,
				ACTION_STRING,
				ACTION_COUNT_STRING
//				DATE_TIME_STRING,
//				ACTION_STRING,
//				DESCRIPTION_STRING
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
				Integer.class
//				java.util.Date.class,
//				String.class,
//				String.class
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
				new ColumnProperties(0, 1, 150, 18, null),
				new ColumnProperties(150, 1, 100, 18, null),
				new ColumnProperties(250, 1, 200, 18, null),
				new ColumnProperties(450, 1, 25, 18, null)
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
				{
					LiteEnergyCompany lec = EnergyCompanyFuncs.getEnergyCompany(ecID.intValue());
					if( lec == null)
						return "(deleted)";
					return lec.getName();
				}
				case CONTACT_COLUMN:
				{
					LiteContact contact = CustomerFuncs.getPrimaryContact(custID.intValue());
					if (contact == null)
						return "(deleted)";
					
					return (contact.getContLastName() + ", " + contact.getContFirstName());
				}
				case ACCOUNT_NUMBER_COLUMN:
				{
					if (acctNumber == null)
						return "(deleted)";
					return acctNumber;
				}
				case ACTION_COUNT_COLUMN:
					return actionCount;
//				case DATE_TIME_COLUMN:
//					return dateTime;
				case ACTION_COLUMN:
					return action;					
//				case DESCRIPTION_COLUMN:
//					return description;
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
