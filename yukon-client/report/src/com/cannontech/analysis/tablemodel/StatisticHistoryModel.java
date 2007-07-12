package com.cannontech.analysis.tablemodel;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.statistic.StatisticData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

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
public class StatisticHistoryModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public static final int PAO_NAME_COLUMN = 0;
	public static final int TIME_COLUMN = 1;
	public static final int TOTAL_ATTEMPTS_COLUMN = 2;
	public static final int DLC_ATTEMPTS_OR_ERRORS_COLUMN = 3;
	public static final int DLC_OR_PORT_OR_COMMERR_PERCENT_COLUMN = 4;
	public static final int SUCC_COMM_PERC_COLUMN = 5;

	/** String values for column representation */
	public final static String DATE_STRING = "Date";
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
	public static final int STAT_CARRIER_COMM_DATA = 0;
	public static final int STAT_COMM_CHANNEL_DATA = 1;
	public static final int STAT_DEVICE_COMM_DATA = 2;
	public static final int STAT_TRANS_COMM_DATA = 3;
	private int statType = STAT_CARRIER_COMM_DATA;
	
	private int [] statTypes = new int[]{
		STAT_CARRIER_COMM_DATA,
		STAT_COMM_CHANNEL_DATA,
		STAT_DEVICE_COMM_DATA,
		STAT_TRANS_COMM_DATA
	};

	/** DynamicPaoStatistics.statisticalType value critera, null results in all? */
	/** valid types are: Daily | Yesterday | Monthlyf| LastMonth | HourXX  */
	public static String DAILY_STAT_PERIOD_TYPE_STRING = "Daily";
	private String statPeriodType = null;	

	private String[] statPeriodTypes = new String[]{
		DAILY_STAT_PERIOD_TYPE_STRING
	};
	
	private static final String ATT_STAT_TYPE = "statType";
	private static final String ATT_STAT_PERIOD_TYPE = "statPeriodType";	
	/**
	 * Constructor class
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	
	public StatisticHistoryModel()
	{
		this(DAILY_STAT_PERIOD_TYPE_STRING, STAT_CARRIER_COMM_DATA);//default type		
	}

	public StatisticHistoryModel(String statPeriodType_)
	{
		this(statPeriodType_, STAT_CARRIER_COMM_DATA);//default type		
	}
	
	public StatisticHistoryModel(int statType_)
	{
		this(DAILY_STAT_PERIOD_TYPE_STRING, statType_);//default type		
	}
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticHistoryModel(String statPeriodType_, int statType_)
	{
		super();
		statTypes = new int[]{statType_};
		setStatPeriodType(statPeriodType_);
		setStatType(statType_);
	}

	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
				
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
				CTILogger.info("START DATE > " + getStartDate());
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
			SqlUtils.close(rset, pstmt, conn );
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
		StringBuffer sql = new StringBuffer("SELECT PAO.PAOName, DPSH.ATTEMPTS, DPSH.COMMERRORS, DPSH.COMPLETIONS, DPSH.REQUESTS, " +
		" DPSH.SYSTEMERRORS, DPSH.PROTOCOLERRORS, DPSH.DATEOFFSET " + 
		" FROM DYNAMICPAOSTATISTICSHISTORY DPSH, YUKONPAOBJECT PAO " +
		" WHERE DPSH.PAOBJECTID = PAO.PAOBJECTID ");
		if(getPaoIDs() != null)
		{
			sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
	
			for (int i = 1; i < getPaoIDs().length; i++)
			{
				sql.append("," + getPaoIDs()[i]);
			}
			sql.append(")");
		}

		int stopCount = daysFrom1970(getStopDate());
		int startCount = daysFrom1970(getStartDate());
		
		if( stopCount < startCount )
		{
			int tempCount = stopCount;
			stopCount = startCount;
			startCount = tempCount;
		}
		
		sql.append(" AND DPSH.DATEOFFSET >= " + startCount + " AND DPSH.DATEOFFSET <= " + stopCount + " ORDER BY PAO.PAOName, DPSH.DATEOFFSET");
		
		return sql;
		
	}

	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	@SuppressWarnings("unchecked")
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
			Integer dateOffset = new Integer(rset.getInt(8));
			
			StatisticData stat = new StatisticData(paoName, attempts, commErrors, systemErrs, protocolErrs, completions, requests, dateOffset);
			getData().add(stat);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Return the statPeriodType (DynamicPaoStatistics.statisticType)
	 * @return String statPeriodType
	 */
	public String getStatPeriodType()
	{
		return statPeriodType;
	}

	/**
	 * Set the statPeriodType
	 * Also sets the startTime and stopTime based on the statisticType.
	 * @param String statPeriodType_
	 */
	public void setStatPeriodType(String statPeriodType_)
	{
		statPeriodType = statPeriodType_;
	}
	
	@Override
	public String getDateRangeString()
	{
		return getStatPeriodType() + ": " + getDateFormat().format(getStartDate()) + " - " + getDateFormat().format(getStopDate());
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
			
				case TIME_COLUMN:
					return statData.getDate();
					
				case TOTAL_ATTEMPTS_COLUMN:
					return statData.getAttempts();

				case DLC_ATTEMPTS_OR_ERRORS_COLUMN:
					if( getStatType() == STAT_CARRIER_COMM_DATA)
						return statData.getDlcAttempts();
						
					else if( getStatType() == STAT_COMM_CHANNEL_DATA ||  
							getStatType() == STAT_TRANS_COMM_DATA)
						return statData.getCommErrors();
						
					else if( getStatType() == STAT_DEVICE_COMM_DATA )
						return statData.getTotalErrs();
					break;
				case DLC_OR_PORT_OR_COMMERR_PERCENT_COLUMN:
					if( getStatType() == STAT_CARRIER_COMM_DATA)
						return statData.getDlcPercent();
		
					else if( getStatType() == STAT_COMM_CHANNEL_DATA ||  
							getStatType() == STAT_TRANS_COMM_DATA)
						return statData.getPortPercent();
		
					else if( getStatType() == STAT_DEVICE_COMM_DATA )
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
			switch (getStatType())
			{
				case STAT_CARRIER_COMM_DATA:
					columnNames = new String[]{
						MCT_NAME_STRING,
						DATE_STRING,
						TOTAL_ATTEMPTS_STRING,
						DLC_ATTEMPTS_STRING,
						DLC_PERCENT_STRING,
						SUCC_COMM_PERC_STRING
					};				
					break;
				case STAT_COMM_CHANNEL_DATA:
					columnNames = new String[]{
					   PORT_NAME_STRING,
					   DATE_STRING,
					   TOTAL_ATTEMPTS_STRING,
					   PORT_FAILURES_STRING,
					   PORT_PERCENT_STRING,
					   SUCC_COMM_PERC_STRING
					};
					break;
				case STAT_DEVICE_COMM_DATA:
					columnNames = new String[]{
						DEVICE_NAME_STRING,
						DATE_STRING,
						TOTAL_ATTEMPTS_STRING,
						TOTAL_ERRORS_STRING,
						COMM_ERROR_PERCENT_STRING,
						SUCC_COMM_PERC_STRING
					};
					break;
				case STAT_TRANS_COMM_DATA:
					columnNames = new String[]{
						TRANSMITTER_NAME_STRING,
						DATE_STRING,
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
			switch (getStatType())
			{
				case STAT_CARRIER_COMM_DATA:
				case STAT_DEVICE_COMM_DATA:
				case STAT_COMM_CHANNEL_DATA:
				case STAT_TRANS_COMM_DATA:
					columnTypes = new Class[]{
						String.class,
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
			switch (getStatType())
			{
				case STAT_CARRIER_COMM_DATA:
				case STAT_DEVICE_COMM_DATA:
				case STAT_COMM_CHANNEL_DATA:
				case STAT_TRANS_COMM_DATA:
					columnProperties = new ColumnProperties[]{
						new ColumnProperties(0, 1, 120, null),
						new ColumnProperties(120, 1, 80, "#,##0"),
						new ColumnProperties(200, 1, 80, "#,##0"),
						new ColumnProperties(280, 1, 80, "#,##0"),
						new ColumnProperties(360, 1, 90, "##0.00%"),
						new ColumnProperties(450, 1, 100, "##0.00%")
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
		title = getStatTypeString(getStatType()) + " Statistics";
		return title;
	}
	
	public String getStatTypeString(int type)
	{
		switch (type)
		{
			case STAT_CARRIER_COMM_DATA:
				return "Carrier Communication";
			case STAT_COMM_CHANNEL_DATA:
				return "Communication Channel";
			case STAT_DEVICE_COMM_DATA:
				return "Device Communication";
			case STAT_TRANS_COMM_DATA:
				return "Transmitter Communication";
			default :
				return "";
		}
	}
	
	/**
	 * @return
	 */
	public int getStatType()
	{
		return statType;
	}

	/**
	 * @param i
	 */
	public void setStatType(int i)
	{
	    if( statType != i)
	    {
	        statType = i;
	        columnNames = null;
	        columnProperties = null;
	        columnTypes = null;
	    }
	}
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0'cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>&nbsp;Statistic Type</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < statTypes.length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" + ATT_STAT_TYPE +"' value='" + statTypes[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getStatTypeString(statTypes[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Period</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < statPeriodTypes.length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_STAT_PERIOD_TYPE +"' value='" + statPeriodTypes[i] + "' " +  
			 (i==0? "checked" : "") + ">" + statPeriodTypes[i]+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	@Override
	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_STAT_TYPE);
			if( param != null)
				setStatType(Integer.valueOf(param).intValue());
			else
				setStatType(STAT_CARRIER_COMM_DATA);
			
			param = req.getParameter(ATT_STAT_PERIOD_TYPE);
			if( param != null)
				setStatPeriodType(param);
			else
				setStatPeriodType(DAILY_STAT_PERIOD_TYPE_STRING);
		}
	}
	
	//FIX ME JESS MAKE ME PRIVATE
	/*
	 * This was taken from a forum and tested.
	 */
	public int daysFrom1970(Date toDate)
	{
	    int tempDifference = 0;
	    int difference = 0;
	    Calendar earlier = Calendar.getInstance();
	    Calendar later = Calendar.getInstance();
	    
	    earlier.set(1970, 0, 1, 0, 0); //Begin from 1970

	    later.setTime(toDate);
	 
	    while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR))
	    {
	        tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
	        difference += tempDifference;
	 
	        earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
	    }
	 
	    if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR))
	    {
	        tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
	        difference += tempDifference;
	    }
	 
	    return difference;
	}

}
