package com.cannontech.analysis.tablemodel;

import java.util.Collections;
import java.util.HashMap;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.activity.ActivityLog;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Created on Dec 15, 2003
 * StatisticReportDatabase TableModel object
 * Abstract class for all Statistical report tableModels to extend.
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * Contains the start and stop times for query information.
 * Contains the paoClass - YukonPaobject.paoClass
 * 				category - YukonPaobject.category
 * 				statType - DynamicPaoStatistics.statisticType
 * @author snebben
 */
public class ActivityModel extends ReportModelBase
{
	/** A string for the title of the data */
	private static String title = "ENERGY COMPANY ACTIVITY LOG";
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int ENERGY_COMPANY_COLUMN = 0;
	public final static int CONTACT_COLUMN = 1;
	public final static int USERNAME_COLUMN = 2;	
	public final static int ACCOUNT_NUMBER_COLUMN = 3;
	public final static int ACTION_COLUMN = 4;
	public final static int ACTION_COUNT_COLUMN = 5;
	
//	public final static int DATE_TIME_COLUMN = 6;
//	public final static int ACTION_COLUMN = 7;
//	public final static int DESCRIPTION_COLUMN = 8;	
	
	/** String values for column representation */
	public final static String ENERGY_COMPANY_STRING = "Energy Company";
	public final static String USERNAME_STRING = "Username";
	public final static String CONTACT_STRING = "Contact";
	public final static String ACCOUNT_NUMBER_STRING = "Account Number";
	public final static String ACTION_STRING = "Action";
	public final static String ACTION_COUNT_STRING = "Count";
//	public final static String DATE_TIME_STRING = "Date/Time";
//	public final static String ACTION_STRING = "Action";
//	public final static String DESCRIPTION_STRING = "Description";

	public final static String TOTALS_HEADER_STRING = "TOTALS";

	/** Class fields */
	private HashMap totals = null;

	public static java.util.Comparator actLogComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;
			thisVal = ((ActivityLog)o1).getECName();
			anotherVal = ((ActivityLog)o2).getECName();
			if( thisVal.equalsIgnoreCase(anotherVal) )
			{				
				//if the Timestamps are equal, we need to sort by accountNumber
				thisVal = ((ActivityLog)o1).getAcctNumber();
				anotherVal = ((ActivityLog)o2).getAcctNumber();
				Object acct1 = null, acct2 = null;
				try
				{
					acct1 = Integer.valueOf(thisVal);
				}
				catch (NumberFormatException nfe1)
				{
					try
					{
						acct2 = Integer.valueOf(anotherVal);		
					}
					catch (NumberFormatException nfe2)
					{
						if( thisVal.equalsIgnoreCase(anotherVal))
						{				
							//if the Timestamps are equal, we need to sort by username
							thisVal = ((ActivityLog)o1).getUserName();
							anotherVal = ((ActivityLog)o2).getUserName();
							if( thisVal.equalsIgnoreCase(anotherVal))
							{
								thisVal = ((ActivityLog)o1).getAction();
								anotherVal = ((ActivityLog)o2).getAction();
							}
							return ( thisVal.compareToIgnoreCase(anotherVal) );
						}				
						//both are strings
						return ( thisVal.compareToIgnoreCase(anotherVal) );
					}
					//first one is string, second one is number
					return -1;
				}
				try
				{
					acct2 = Integer.valueOf(anotherVal);
				}
				catch (Exception e)
				{
					//first one is number, second one is string
					return 1;
				}
				
				if( ((Integer)acct1).intValue() == ((Integer)acct2).intValue() )
				{				
					//if the Timestamps are equal, we need to sort by username
					thisVal = ((ActivityLog)o1).getUserName();
					anotherVal = ((ActivityLog)o2).getUserName();
					if( thisVal.equalsIgnoreCase(anotherVal))
					{
						thisVal = ((ActivityLog)o1).getAction();
						anotherVal = ((ActivityLog)o2).getAction();
					}
					return ( thisVal.compareToIgnoreCase(anotherVal) );
				}				
				return ( ((Integer)acct1).compareTo((Integer)acct2));
			}				

			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
	};
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel(long startTime_, long stopTime_)
	{
		super(ReportTypes.EC_ACTIVITY_LOG_DATA, startTime_, stopTime_);//default type
	}

	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel()
	{
		this(ReportTypes.EC_ACTIVITY_LOG_DATA);//default report type		
	}
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel(int reportType_)
	{
		super();//default type
		setReportType(reportType_);		
	}
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel(int [] ecIDs_)
	{
		this(ReportTypes.EC_ACTIVITY_LOG_DATA);//default type
		setECIDs(ecIDs_);
	}
	/**
	 * Constructor class
	 * Only ONE energycompanyID is used, constructor for convenience 
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel(Integer ecID_)
	{
		this(ReportTypes.EC_ACTIVITY_LOG_DATA);//default type
		setECIDs(ecID_);
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		int rowCount = 0;
		StringBuffer sql = buildSQLStatement();
		CTILogger.info(sql.toString());
				
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
				}				
			}
		}
				
		catch( java.sql.SQLException e )
		{
			CTILogger.error(" DB : Standard SQL Builder did not work, trying with a non SQL-92 query");
			//try using a nonw SQL-92 method, will be slower
			//  Oracle 8.1.X and less will use this
			runNonSQL92Statement();
		}
		finally
		{
			try
			{
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
			Collections.sort(getData(), actLogComparator);
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT AL.ENERGYCOMPANYID, AL.USERID, AL.CUSTOMERID, AL.ACCOUNTID, CA.ACCOUNTNUMBER, ACTION, " + 
		" COUNT(ACTIVITYLOGID) AS ACTIONCOUNT " + 
		" FROM ACTIVITYLOG AL LEFT OUTER JOIN CUSTOMERACCOUNT CA " +
		" ON CA.ACCOUNTID = AL.ACCOUNTID " + 
		" WHERE AL.TIMESTAMP >= ?");
		if( getECIDs() != null )
		{
			sql.append(" AND AL.ENERGYCOMPANYID IN (" + getECIDs()[0]);
			for (int i = 1; i < getECIDs().length; i++)
				sql.append(", " + getECIDs()[i]);
			sql.append(")");
		}		
		sql.append(" GROUP BY AL.ENERGYCOMPANYID, AL.CUSTOMERID, AL.USERID, AL.ACCOUNTID, CA.ACCOUNTNUMBER, ACTION " +
					" ORDER BY AL.ENERGYCOMPANYID, AL.CUSTOMERID, AL.USERID, CA.ACCOUNTNUMBER, ACTION");
		return sql;
		
	}
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public void runNonSQL92Statement()
	{
		
		int rowCount = 0;
			
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				StringBuffer sql = new StringBuffer("SELECT DISTINCT ACCOUNTID, ACCOUNTNUMBER FROM CUSTOMERACCOUNT");
				CTILogger.info(sql.toString());
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				HashMap acctNumHash = new HashMap();
				while( rset.next())
				{
					Integer acctID = new Integer(rset.getInt(1));
					String acctNum = rset.getString(2);
					acctNumHash.put(acctID, acctNum);
				}
					
				sql = new StringBuffer("SELECT ENERGYCOMPANYID, USERID, CUSTOMERID, ACCOUNTID, ACTION, " + 
					" COUNT(ACTIVITYLOGID) AS ACTIONCOUNT " +
					" FROM ACTIVITYLOG " +
					" WHERE TIMESTAMP >= ? ");

				if( getECIDs() != null )
				{
					sql.append(" AND AL.ENERGYCOMPANYID IN (" + getECIDs()[0]);
					for (int i = 1; i < getECIDs().length; i++)
						sql.append(", " + getECIDs()[i]);
					sql.append(")");
				}							

				sql.append(" GROUP BY ENERGYCOMPANYID, CUSTOMERID, USERID, ACCOUNTID, ACTION " +
							" ORDER BY ENERGYCOMPANYID, CUSTOMERID, USERID, ACCOUNTID, ACTION");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					Integer ecID = new Integer(rset.getInt(1));
					LiteEnergyCompany lec = EnergyCompanyFuncs.getEnergyCompany(ecID.intValue());
					String ecName = "(delete)";
					if( lec != null)
						ecName = lec.getName();
					
					Integer userID = new Integer(rset.getInt(2));
					String userName = "";
					LiteYukonUser user = YukonUserFuncs.getLiteYukonUser(userID.intValue());
					if (user == null)
					{
						if( userID.intValue() == -1)
							userName = "(n/a)";
						else
							userName = "(deleted)";
					}
					else
						userName = (user.getUsername());

					Integer custID = new Integer(rset.getInt(3));
					Integer acctID = new Integer(rset.getInt(4));
					String action = rset.getString(5);
					Integer count = new Integer(rset.getInt(6));
					//ENERGYCOMPANYID, CUSTOMERID, ACCOUNTID, ACTION, COUNT(ACTIVITYLOGID) AS ACTIONCOUNT 

					String acctNum = (String)acctNumHash.get(acctID);
					if (acctNum == null)
					{
						if( acctID.intValue() == -1)
							acctNum = "(n/a)";
						else
							acctNum =  "(deleted)";
					}

					ActivityLog al = new ActivityLog(ecName, userName, custID, acctNum, acctID, count, action);
					getData().add(al); 
				}
			}
		}
		
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		return;
	}
	
	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(java.sql.ResultSet rset)
	{
		try
		{
			Integer ecID = new Integer(rset.getInt(1));
			LiteEnergyCompany lec = EnergyCompanyFuncs.getEnergyCompany(ecID.intValue());
			String ecName = "(delete)";
			if( lec != null)
				ecName = lec.getName();

			Integer userID = new Integer(rset.getInt(2));
			String userName = "";
			LiteYukonUser user = YukonUserFuncs.getLiteYukonUser(userID.intValue());
			if (user == null)
			{
				if( userID.intValue() == -1)
					userName = "(n/a)";
				else
					userName = "(deleted)";
			}
			else
				userName = (user.getUsername());

			Integer custID = new Integer(rset.getInt(3));
			Integer acctID = new Integer(rset.getInt(4));
			String acctNum = rset.getString(5);
			if (acctNum == null)
			{
				if( acctID.intValue() == -1)
					acctNum = "(n/a)";
				else
					acctNum =  "(deleted)";
			}
			
			String action = rset.getString(6);
			Integer count = new Integer(rset.getInt(7));
			//AL.ENERGYCOMPANYID, AL.USERID, AL.CUSTOMERID, AL.ACCOUNTID, CA.ACCOUNTNUMBER, ACTION, COUNT(ACTIVITYLOGID) AS ACTIONCOUNT
	
			ActivityLog al = new ActivityLog(ecName, userName, custID, acctNum, acctID, count, action);
			getData().add(al);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
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
					return al.getECName();
				case USERNAME_COLUMN:
					return al.getUserName();
				case CONTACT_COLUMN:
				{
					LiteContact contact = CustomerFuncs.getPrimaryContact(al.getCustID().intValue());
					if (contact == null)
						return "(n/a)";

					return (contact.getContLastName() + ", " + contact.getContFirstName());
				}
				case ACCOUNT_NUMBER_COLUMN:
				{
					if (al.getAcctNumber() == null)
					{
						if( al.getAcctID().intValue() == -1)
							return "(n/a)";
						else
							return "(deleted)";
					}
					return al.getAcctNumber();
				}
				case ACTION_COUNT_COLUMN:
					return al.getActionCount();
//					case DATE_TIME_COLUMN:
//						return dateTime;
				case ACTION_COLUMN:
					return al.getAction();					
//					case DESCRIPTION_COLUMN:
//						return description;
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
				ENERGY_COMPANY_STRING,
				CONTACT_STRING,
				USERNAME_STRING,
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
				new ColumnProperties(250, 1, 100, 18, null),
				new ColumnProperties(350, 1, 150, 18, null),
				new ColumnProperties(500, 1, 25, 18, null)
			};				
		}
		return columnProperties;
	}

	public HashMap getTotals()
	{
		if (totals == null)
		{
			totals = new HashMap();
			for(int i = 0; i < getData().size(); i++)
			{
				String key = ((ActivityLog)getData().get(i)).getAction();
				Integer initValue = (Integer)totals.get(key);
				if( initValue == null)
					initValue = new Integer(0);
	
				Integer newValue = ((ActivityLog)getData().get(i)).getActionCount();
				newValue = new Integer(newValue.intValue() + initValue.intValue());
				
				totals.put(key, newValue);		
			}
		}
		return totals;
	}

}
