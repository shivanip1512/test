package com.cannontech.analysis.tablemodel;



import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.statistic.StatisticData;
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
	/** Class fields */
	/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;
	
	/** YukonPaobject.paoClass value criteria, null results in all paoClasses */
	private String paoClass = null;
	
	/** YukonPaobject.category value criteria, null results in all categories */
	private String category = null;
	
	/** DynamicPaoStatistics.statisticalType value critera, null results in all? */
	/** valid types are: Daily | Yesterday | Monthly | LastMonth | HourXX  */
	private String statType = null;

	private String title = null;
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	
	public StatisticModel()
		{
			this("Daily", ReportTypes.CARRIER_COMM_DATA);//default type		
		}
	
	
	public StatisticModel(String statType_)
	{
		this(statType_, ReportTypes.CARRIER_COMM_DATA);//default type		
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
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				//pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				/*java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
				tempCal.add(java.util.Calendar.DATE, -90);
				stmt.setTimestamp(1, new java.sql.Timestamp(tempCal.getTime().getTime()));
				System.out.println( "DATE > "+ tempCal.getTime());*/
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + data.size());
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
			
			StatisticData stat = (StatisticData)ReportTypes.create(getReportType());
			
			stat.setPAOName(paoName);
			stat.setAttempts(attempts);
			stat.setCommErrors(commErrors);
			stat.setSystemErrors(systemErrs);
			stat.setProtocolErrs(protocolErrs);
			stat.setCompletions(completions);
			stat.setRequests(requests);

			data.add(stat);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Return the startTime in millis.
	 * @return long startTime
	 */
//	public long getStartTime()
	//{
	//	return startTime;
	//}
	/**
	 * Reuturn the stopTime in millis.
	 * @return long stopTime
	 */
	//public long getStopTime()
	//{
	//	return stopTime;
	//}
	/**
	 * Set the startTime
	 * @param long time
	 */
	//public void setStartTime(long time)
	//{
	//	startTime = time;
	//}
	/**
	 * Set the stopTime
	 * @param long time
	 */
	//public void setStopTime(long time)
	//{
	//	stopTime = time;
	//}
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
				case ReportTypes.CARRIER_COMM_DATA:
					category = PAOGroups.STRING_CAT_DEVICE;
					break;
				case ReportTypes.COMM_CHANNEL_DATA:
					category = PAOGroups.STRING_CAT_PORT;
					break;
				case ReportTypes.DEVICE_COMM_DATA:
					category = PAOGroups.STRING_CAT_DEVICE;
					break;
				case ReportTypes.TRANS_COMM_DATA:
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
				case ReportTypes.CARRIER_COMM_DATA:
					paoClass = DeviceClasses.STRING_CLASS_CARRIER;
					break;
				case ReportTypes.COMM_CHANNEL_DATA:
					paoClass = PAOGroups.STRING_CAT_PORT;
					break;
				case ReportTypes.DEVICE_COMM_DATA:
					paoClass = null;
					break;					
				case ReportTypes.TRANS_COMM_DATA:
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
		if( statType_.equalsIgnoreCase("Daily"))
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
		else if (statType_.equalsIgnoreCase("Monthly"))
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
		else if (statType_.equalsIgnoreCase("Yesterday"))
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
		else if (statType_.equalsIgnoreCase("LastMonth"))
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
		if( getStatType().equalsIgnoreCase("Daily"))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");
			return "Daily: " + format.format(new java.util.Date(getStartTime()));
		}
		else if( getStatType().equalsIgnoreCase("Yesterday"))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");
			return "Yesterday: " + format.format(new java.util.Date(getStartTime()));
		}
		else if( getStatType().equalsIgnoreCase("Monthly"))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMMMM yyyy");
			return "Monthly: " + format.format(new java.util.Date(getStartTime()));
		}
		else if( getStatType().equalsIgnoreCase("LastMonth"))
		{
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMMMM yyyy");
			return "Previous Month: " + format.format(new java.util.Date(getStartTime()));
		}				
		return String.valueOf(getStartTime());
	}
}
