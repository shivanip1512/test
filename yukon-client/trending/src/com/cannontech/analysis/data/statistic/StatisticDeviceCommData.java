package com.cannontech.analysis.data.statistic;

import java.sql.ResultSet;

import com.cannontech.analysis.data.ColumnProperties;

/**
 * Created on Dec 15, 2003
 * StatisticDeviceCommData TableModel object
 * Innerclass object for row data is DeviceCommStat:
 *  String deviceName		- YukonPaobject.paoName
 *  Integer totalAttempts	- DynamicPaoStatistics.attempts
 *  Integer totalErrors		- DynamicPaoStatistics.systemErrors + DPS.protocolErrors + commErrors
 *  Double commErrPerc		- totalErrors / DynamicPaoStatistics.attempts
 *  Double succCommPerc		- DynamicPaoStatistics.completions / DPS.requests
 * @author snebben
 */
public class StatisticDeviceCommData extends StatisticReportDataBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int TOTAL_ATTEMPTS_COLUMN = 1;
	public final static int TOTAL_ERRORS_COLUMN = 2;
	public final static int COMM_ERROR_PERCENT_COLUMN = 3;
	public final static int SUCC_COMM_PERC_COLUMN = 4;
	
	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String TOTAL_ATTEMPTS_STRING = "Total Attempts";
	public final static String TOTAL_ERRORS_STRING = "Total Errors";
	public final static String COMM_ERROR_PERCENT_STRING = "Communication Error%";
	public final static String SUCC_COMM_PERC_STRING= "Successful Communication%";
	
	/** Inner class container of table model data*/
	private class DeviceCommStat
	{
		public String deviceName = null;
		public Integer totalAttempts = null;
		public Integer totalErrors = null;
		public Double commErrPerc = null;
		public Double succCommPerc = null;
		public DeviceCommStat(String deviceName_, Integer totalAttempts_, Integer totalErrs_, Double commErrPerc_, Double succCommPerc_)
		{
			deviceName = deviceName_;
			totalAttempts = totalAttempts_;
			totalErrors = totalErrs_;
			commErrPerc = commErrPerc_;
			succCommPerc = succCommPerc_;			
		}		
	}
	
	/**
	 * Default Constructor.
	 */
	public StatisticDeviceCommData()
	{
		this("DEVICE", "Monthly");		
	}
	
	/**
	 * Constructor 
	 * @param category_ - YukonPaobject.category
	 * @param statType_ - DYNAMICPAOSTATISTICS.statisticType
	 */
	public StatisticDeviceCommData(String category_, String statType_)
	{
		super(category_, null, statType_);
	}
	
	/**
	 * Add DeviceCommStat objects to data, retrieved from rset.
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
			Integer systemErrs = new Integer(rset.getInt(6));
			Integer protocolErrs = new Integer(rset.getInt(7));
			
			Integer totalErrs = new Integer( systemErrs.intValue() + protocolErrs.intValue() + commErrors.intValue());
			Double commErrPercent = new Double (totalErrs.doubleValue() / attempts.doubleValue());
			Double succCommPercent = new Double((completions.doubleValue()/ requests.doubleValue()));
			
			DeviceCommStat devCommStat = new DeviceCommStat(paoName, attempts, totalErrs, commErrPercent, succCommPercent);
			data.add(devCommStat);
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
			columnNames = new String[NUMBER_COLUMNS];
			columnNames[0] = DEVICE_NAME_STRING;
			columnNames[1] = TOTAL_ATTEMPTS_STRING;
			columnNames[2] = TOTAL_ERRORS_STRING;
			columnNames[3] = COMM_ERROR_PERCENT_STRING;
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
			columnTypes = new Class[NUMBER_COLUMNS];
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
			columnProperties = new ColumnProperties[NUMBER_COLUMNS];
			//posX, posY, width, height, numberFormatString
			columnProperties[0] = new ColumnProperties(0, 1, 210, 20, null);
			columnProperties[1] = new ColumnProperties(210, 1, 50, 20, "#,##0");
			columnProperties[2] = new ColumnProperties(260, 1, 50, 20, "#,##0");
			columnProperties[3] = new ColumnProperties(310, 1, 80, 20, "##0.00%");
			columnProperties[4] = new ColumnProperties(390, 1, 110, 20, "##0.00%");
		}
		return columnProperties;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof DeviceCommStat)
		{
			DeviceCommStat stat = ((DeviceCommStat)o);
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return stat.deviceName;
				
				case TOTAL_ATTEMPTS_COLUMN:
					return stat.totalAttempts;
	
				case TOTAL_ERRORS_COLUMN:
					return stat.totalErrors;
	
				case COMM_ERROR_PERCENT_COLUMN:
					return stat.commErrPerc;
					
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
		return "DEVICE COMMUNICATION STATISTICS";
	}
}
