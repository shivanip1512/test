package com.cannontech.analysis.tablemodel;



import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.statistic.StatisticData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

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
public class StatisticModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	private static final int PAO_NAME_COLUMN = 0;
	private static final int TOTAL_ATTEMPTS_COLUMN = 1;
	private static final int DLC_ATTEMPTS_OR_ERRORS_COLUMN = 2;
	private static final int DLC_OR_PORT_OR_COMMERR_PERCENT_COLUMN = 3;
	private static final int SUCC_COMM_PERC_COLUMN = 4;

	/** String values for column representation */
	//Carrier
	public final static String MCT_NAME_STRING = "MCT Name";
	public final static String TOTAL_ATTEMPTS_STRING = "Total Attempts";
	public final static String DLC_ATTEMPTS_STRING = "DLC Attempts";
	public final static String DLC_PERCENT_STRING = "DLC Percent";
	public final static String SUCC_COMM_PERC_STRING= "Successful Communication%";
	//CommChannel
	public final static String PORT_NAME_STRING = "Port Name";
	public final static String PORT_FAILURES_STRING = "Port Failures";
	public final static String PORT_PERCENT_STRING = "Port Percent";
	//Device
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String TOTAL_ERRORS_STRING = "Total Errors";
	public final static String COMM_ERROR_PERCENT_STRING = "Communication Error%";
	//Transmitter
	public final static String TRANSMITTER_NAME_STRING = "Transmitter Name";
			
	/** Class fields */
	/** YukonPaobject.paoClass value criteria, null results in all paoClasses */
	private String paoClass = null;
	
	/** YukonPaobject.category value criteria, null results in all categories */
	private String category = null;
	
	/** DynamicPaoStatistics.statisticalType value critera, null results in all? */
	/** valid types are: Daily | Yesterday | Monthly | LastMonth | HourXX  */
	public static String DAILY_STAT_TYPE_STRING = "Daily";
	public static String YESTERDAY_STAT_TYPE_STRING = "Yesterday";
	public static String MONTHLY_STAT_TYPE_STRING = "Monthly";
	public static String LASTMONTH_STAT_TYPE_STRING = "LastMonth";
	private String statType = null;	

	//Statistic model types.  These are how we narrow down the paobjects and such~!
	public static final int CARRIER_COMM_DATA = 0;
	public static final int COMM_CHANNEL_DATA = 1;
	public static final int DEVICE_COMM_DATA = 2;
	public static final int TRANS_COMM_DATA = 3;
	//value for storing the type of stat model we want.
	//Valid values are: CARRIER_COMM_DATA, COMM_CHANNEL_DATA, DEVICE_COMM_DATA, TRANS_COMM_DATA
	private int statModelType = CARRIER_COMM_DATA;	
	
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	
	public StatisticModel()
	{
		this(DAILY_STAT_TYPE_STRING, ReportTypes.STATISTIC_DATA);//default type		
	}

	public StatisticModel(String statType_)
	{
		this(statType_, ReportTypes.STATISTIC_DATA);//default type		
	}
	
	public StatisticModel(int reportType_)
	{
		this(DAILY_STAT_TYPE_STRING, reportType_);//default type		
	}
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticModel(String statType_, int reportType_)
	{
		super();
		setStatType(statType_);
		setReportType(reportType_);		
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
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
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
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT PAO.PAOName, DPS.ATTEMPTS, DPS.COMMERRORS, DPS.COMPLETIONS, DPS.REQUESTS, " +
		" DPS.SYSTEMERRORS, DPS.PROTOCOLERRORS " + 
		" FROM DYNAMICPAOSTATISTICS DPS, YUKONPAOBJECT PAO " +
		" WHERE DPS.PAOBJECTID = PAO.PAOBJECTID ");
		if(getPaoIDs() != null)
		{
			sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
	
			for (int i = 1; i < getPaoIDs().length; i++)
			{
				sql.append("," + getPaoIDs()[i]);
			}
			sql.append(")");
		}

		if( getPaoClass() != null )
			sql.append(" AND PAOCLASS = '" + getPaoClass() +"' ");
		if (getCategory() != null)
			sql.append(" AND PAO.CATEGORY = '" + getCategory() + "' "); 
		if (getStatType() != null )
			sql.append(" AND DPS.STATISTICTYPE='" + getStatType() + "' ");
			
		
		sql.append(" AND (STARTDATETIME >= ? ) ORDER BY PAO.PAOName");
		return sql;
		
	}

	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(java.sql.ResultSet rset)
	{
		try
		{
			String paoName = rset.getString(1);
			Integer attempts = new Integer(rset.getInt(2));
			Integer commErrors = new Integer(rset.getInt(3));
			Integer completions = new Integer(rset.getInt(4));
			Integer requests = new Integer(rset.getInt(5));
			Integer systemErrs = new Integer(rset.getInt(6));
			Integer protocolErrs = new Integer(rset.getInt(7));
			
			StatisticData stat = new StatisticData(paoName, attempts, commErrors, systemErrs, protocolErrs, completions, requests);
			getData().add(stat);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Return the category (YukonPaobject.Category)
	 * @return String category.
	 */
	public String getCategory()
	{
		if( category == null )
		{
			switch (getReportType())
			{
				case CARRIER_COMM_DATA:
					category = PAOGroups.STRING_CAT_DEVICE;
					break;
				case COMM_CHANNEL_DATA:
					category = PAOGroups.STRING_CAT_PORT;
					break;
				case DEVICE_COMM_DATA:
					category = PAOGroups.STRING_CAT_DEVICE;
					break;
				case TRANS_COMM_DATA:
					category = PAOGroups.STRING_CAT_DEVICE;
					break;
				default:
					category = null;	
			}
		}
		return category;
	}

	/**
	 * Return the paoClass (YukonPaobject.PaoClass)
	 * @return String paoClass
	 */
	public String getPaoClass()
	{
		if (paoClass == null)
		{
			switch (getReportType())
			{
				case CARRIER_COMM_DATA:
					paoClass = DeviceClasses.STRING_CLASS_CARRIER;
					break;
				case COMM_CHANNEL_DATA:
					paoClass = PAOGroups.STRING_CAT_PORT;
					break;
				case DEVICE_COMM_DATA:
					paoClass = null;
					break;					
				case TRANS_COMM_DATA:
					paoClass = DeviceClasses.STRING_CLASS_TRANSMITTER;
					break;
				default:
					paoClass = null;	
			}
		}
		return paoClass;
	}

	/**
	 * Return the statType (DynamicPaoStatistics.statisticType)
	 * @return String statType
	 */
	public String getStatType()
	{
		return statType;
	}

	/**
	 * Set the category
	 * @param String category_
	 */
	private void setCategory(String category_)
	{
		category = category_;
	}

	/**
	 * Set the paoClass
	 * @param String paoClass_
	 */
	private void setPaoClass(String paoClass_)
	{
		paoClass = paoClass_;
	}

	/**
	 * Set the statType
	 * Also sets the startTime and stopTime based on the statisticType.
	 * @param String statType_
	 */
	public void setStatType(String statType_)
	{
		if( statType_.equalsIgnoreCase(DAILY_STAT_TYPE_STRING))
		{
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);

			setStartTime(cal.getTime().getTime());
			cal.add(java.util.Calendar.DATE, 1);
			setStopTime(cal.getTime().getTime());
		}
		else if (statType_.equalsIgnoreCase(MONTHLY_STAT_TYPE_STRING))
		{
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			setStartTime(cal.getTime().getTime());
			cal.add(java.util.Calendar.MONTH, 1);
			setStopTime(cal.getTime().getTime());
			
		}
		else if (statType_.equalsIgnoreCase(YESTERDAY_STAT_TYPE_STRING))
		{
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			cal.add(java.util.Calendar.DATE, -1);
			setStartTime(cal.getTime().getTime());
			cal.add(java.util.Calendar.DATE, 1);
			setStopTime(cal.getTime().getTime());
		}
		else if (statType_.equalsIgnoreCase(LASTMONTH_STAT_TYPE_STRING))
		{
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			cal.add(java.util.Calendar.MONTH, -1);
			setStartTime(cal.getTime().getTime());
			
			setStopTime(cal.getTime().getTime());
	
		}		
		statType = statType_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		if( getStatType().equalsIgnoreCase(DAILY_STAT_TYPE_STRING))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");
			return "Daily: " + format.format(new java.util.Date(getStartTime()));
		}
		else if( getStatType().equalsIgnoreCase(YESTERDAY_STAT_TYPE_STRING))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");
			return "Yesterday: " + format.format(new java.util.Date(getStartTime()));
		}
		else if( getStatType().equalsIgnoreCase(MONTHLY_STAT_TYPE_STRING))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMMMM yyyy");
			return "Monthly: " + format.format(new java.util.Date(getStartTime()));
		}
		else if( getStatType().equalsIgnoreCase(LASTMONTH_STAT_TYPE_STRING))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMMMM yyyy");
			return "Previous Month: " + format.format(new java.util.Date(getStartTime()));
		}				
		return String.valueOf(getStartTime());
	}
	/**
	 * @return
	 */
	public int getStatModelType()
	{
		return statModelType;
	}

	/**
	 * @param i
	 */
	public void setStatModelType(int i)
	{
		statModelType = i;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof StatisticData)
		{
			StatisticData statData = (StatisticData)o;
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return statData.getPAOName();
			
				case TOTAL_ATTEMPTS_COLUMN:
					return statData.getAttempts();

				case DLC_ATTEMPTS_OR_ERRORS_COLUMN:
					if( getStatModelType() == CARRIER_COMM_DATA)
						return statData.getDlcAttempts();
						
					else if( getStatModelType() == COMM_CHANNEL_DATA ||  getStatModelType() == TRANS_COMM_DATA)
						return statData.getCommErrors();
						
					else if( getStatModelType() == DEVICE_COMM_DATA)
						return statData.getTotalErrs();
					break;
				case DLC_OR_PORT_OR_COMMERR_PERCENT_COLUMN:
					if( getStatModelType() == CARRIER_COMM_DATA)
						return statData.getDlcPercent();
		
					else if( getStatModelType() == COMM_CHANNEL_DATA ||  getStatModelType() == TRANS_COMM_DATA)
						return statData.getPortPercent();
		
					else if( getStatModelType() == DEVICE_COMM_DATA)
						return statData.getCommErrPercent();
					break;
				case SUCC_COMM_PERC_COLUMN:
					return statData.getSuccessPercent();
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
			switch (getStatModelType())
			{
				case CARRIER_COMM_DATA:
					columnNames = new String[]{
						MCT_NAME_STRING,
						TOTAL_ATTEMPTS_STRING,
						DLC_ATTEMPTS_STRING,
						DLC_PERCENT_STRING,
						SUCC_COMM_PERC_STRING
					};				
					break;
				case COMM_CHANNEL_DATA:
					columnNames = new String[]{
					   PORT_NAME_STRING,
					   TOTAL_ATTEMPTS_STRING,
					   PORT_FAILURES_STRING,
					   PORT_PERCENT_STRING,
					   SUCC_COMM_PERC_STRING
					};
					break;
				case DEVICE_COMM_DATA:
					columnNames = new String[]{
						DEVICE_NAME_STRING,
						TOTAL_ATTEMPTS_STRING,
						TOTAL_ERRORS_STRING,
						COMM_ERROR_PERCENT_STRING,
						SUCC_COMM_PERC_STRING
					};
					break;
				case TRANS_COMM_DATA:
					columnNames = new String[]{
						TRANSMITTER_NAME_STRING,
						TOTAL_ATTEMPTS_STRING,
						PORT_FAILURES_STRING,
						PORT_PERCENT_STRING,
						SUCC_COMM_PERC_STRING
					};
					break;					
			}
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
			switch (getStatModelType())
			{
				case CARRIER_COMM_DATA:
				case DEVICE_COMM_DATA:
				case COMM_CHANNEL_DATA:
				case TRANS_COMM_DATA:			
					columnTypes = new Class[]{
						String.class,
						Integer.class,
						Integer.class,
						Double.class,
						Double.class
					};
					break;
			}
		}
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if( columnProperties == null)
		{
			switch (getStatModelType())
			{
				case CARRIER_COMM_DATA:
				case DEVICE_COMM_DATA:
					columnProperties = new ColumnProperties[]{
						//posX, posY, width, height, numberFormatString
						new ColumnProperties(0, 1, 210, 20, null),
						new ColumnProperties(210, 1, 60, 20, "#,##0"),
						new ColumnProperties(270, 1, 60, 20, "#,##0"),
						new ColumnProperties(330, 1, 60, 20, "##0.00%"),
						new ColumnProperties(390, 1, 110, 20, "##0.00%")
					};
					break;
				case COMM_CHANNEL_DATA:
				case TRANS_COMM_DATA:
					columnProperties = new ColumnProperties[]{
						new ColumnProperties(0, 1, 175, 20, null),
						new ColumnProperties(175, 1, 75, 20, "#,##0"),
						new ColumnProperties(240, 1, 50, 20, "#,##0"),
						new ColumnProperties(305, 1, 50, 20, "##0.00%"),
						new ColumnProperties(370, 1, 110, 20, "##0.00%")
					};
					break;
			}
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		switch (getStatModelType())
		{
			case CARRIER_COMM_DATA :
				title = "CARRIER COMMUNICATION STATISTICS";
				break;
			case COMM_CHANNEL_DATA:
				title = "COMMUNICATION CHANNEL STATISTICS";
				break;
			case DEVICE_COMM_DATA:
				title = "DEVICE COMMUNICATION STATISTICS";
				break;
			case TRANS_COMM_DATA:
				title = "TRANSMITTER COMMUNICATION STATISTICS";
				break;
			default :
				break;
		}
		return title;
	}
}
