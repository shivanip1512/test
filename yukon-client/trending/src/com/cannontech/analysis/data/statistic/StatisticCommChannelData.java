package com.cannontech.analysis.data.statistic;

import java.sql.ResultSet;

import com.cannontech.analysis.data.ColumnProperties;

/**
 * Created on Dec 15, 2003
 * StatisticCommChannelData TableModel object
 * Innerclass object for row data is CommStat:
 *  String portName			- YukonPaobject.paoName
 *  Integer totalAttempts	- DynamicPaoStatistics.attempts
 *  Integer portFailuers	- DynamicPaoStatistics.commErrors
 *  Double portPercent		- (DynamicPaoStatistics.attempts - DPS.commErrors) / DPS.attempts
 *  Double succCommPerc		- DynamicPaoStatistics.completions / DPS.requests
 * @author snebben
 */
public class StatisticCommChannelData extends StatisticReportDataBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int PORT_NAME_COLUMN = 0;
	public final static int TOTAL_ATTEMPTS_COLUMN = 1;
	public final static int PORT_FAILURES_COLUMN = 2;
	public final static int PORT_PERCENT_COLUMN = 3;
	public final static int SUCC_COMM_PERC_COLUMN = 4;
	
	/** String values for column representation */
	public final static String PORT_NAME_STRING = "Port Name";
	public final static String TOTAL_ATTEMPTS_STRING = "Total Attempts";
	public final static String PORT_FAILURES_STRING = "Port Failures";
	public final static String PORT_PERCENT_STRING = "Port Percent";
	public final static String SUCC_COMM_PERC_STRING= "Successful Communication%";
	
	/** Inner class container of table model data*/
	private class CommStat
	{
		public String portName = null;
		public Integer totalAttempts = null;
		public Integer portFailures = null;
		public Double portPercent = null;
		public Double succCommPerc = null;
		public CommStat(String portName_, Integer totalAttempts_, Integer portFailures_, Double portPercent_, Double succCommPerc_)
		{
			portName = portName_;
			totalAttempts = totalAttempts_;
			portFailures = portFailures_;
			portPercent = portPercent_;
			succCommPerc = succCommPerc_;			
		}
	}
	
	/**
	 * Default Constructor
	 */
	public StatisticCommChannelData()
	{
		this("PORT", "PORT", "Monthly");
				
	}

	/**
	 * Constructor 
	 * @param category_ - YukonPaobject.category
	 * @param paoClass_ - YukonPaobject.paoClass
	 * @param statType_ - DYNAMICPAOSTATISTICS.statisticType
	 */
	public StatisticCommChannelData(String category_, String paoClass_, String statType_)
	{
		super(category_, paoClass_, statType_);		
	}
	
	/**
	 * Add CommStat objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String paoName = rset.getString(1);
			Integer attempts = new Integer(rset.getInt(2));
			Integer commErrors = new Integer(rset.getInt(3));
			Integer completions = new Integer(rset.getInt(4));
			Integer requests = new Integer(rset.getInt(5));
					
			Double percent = new Double((attempts.doubleValue() - commErrors.doubleValue())/attempts.doubleValue());
			Double success = new Double(completions.doubleValue() / requests.doubleValue());
			CommStat commStat = new CommStat(paoName, attempts, commErrors, percent, success);
			data.add(commStat);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Build the SQL statement to retrieve StatisticCommChannelData data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT PAO.PAOName, DPS.ATTEMPTS, DPS.COMMERRORS, DPS.COMPLETIONS, DPS.REQUESTS " +
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
	
		sql.append(" AND (STARTDATETIME >= ? AND STARTDATETIME < ? ) ORDER BY PAO.PAOName");
		return sql;
	}	
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[5];
			columnNames[0] = PORT_NAME_STRING;
			columnNames[1] = TOTAL_ATTEMPTS_STRING;
			columnNames[2] = PORT_FAILURES_STRING;
			columnNames[3] = PORT_PERCENT_STRING;
			columnNames[4] = SUCC_COMM_PERC_STRING;
		}
		return columnNames;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[5];
			columnTypes[0] = String.class;
			columnTypes[1] = Integer.class;
			columnTypes[2] = Integer.class;
			columnTypes[3] = Double.class;
			columnTypes[4] = Double.class;
		}
			
		return columnTypes;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[5];
			columnProperties[0] = new ColumnProperties(0, 1, 175, 20, null);
			columnProperties[1] = new ColumnProperties(175, 1, 75, 20, "#,##0");
			columnProperties[2] = new ColumnProperties(240, 1, 50, 20, "#,##0");
			columnProperties[3] = new ColumnProperties(305, 1, 50, 20, "##0.00%");
			columnProperties[4] = new ColumnProperties(370, 1, 110, 20, "##0.00%");
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof CommStat)
		{
			CommStat stat = ((CommStat)o);
			switch( columnIndex)
			{
				case PORT_NAME_COLUMN:
					return stat.portName;
				
				case TOTAL_ATTEMPTS_COLUMN:
					return stat.totalAttempts;
	
				case PORT_FAILURES_COLUMN:
					return stat.portFailures;
	
				case PORT_PERCENT_COLUMN:
					return stat.portPercent;
					
				case SUCC_COMM_PERC_COLUMN:
					return stat.succCommPerc;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getTitleString()
	 */
	public String getTitleString()
	{
		return "COMMUNICATION CHANNEL STATISTICS";
	}
}
