package com.cannontech.analysis.data.statistic;

import java.sql.ResultSet;

import com.cannontech.analysis.data.ColumnProperties;


/**
 * Created on Dec 15, 2003
 * StatisticCarrierCommData TableModel object
 * Innerclass object for row data is CarrierCommStat:
 *  String mctName			- YukonPaobject.paoName
 *  Integer totalAttempts	- DynamicPaoStatistics.attempts
 *  Integer dlcAttempts		- DynamicPaoStatistics.attempts - DPS.commErrors - DPS.systemErrors
 *  Double dlcPercent		- (DynamicPaoStatistics.attempts - DPS.protocolErrors) / DPS.attempts
 *  Double succCommPerc		- DynamicPaoStatistics.completions / DPS.requests
 * @author snebben
 */
public class StatisticCarrierCommData extends StatisticReportDataBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int MCT_NAME_COLUMN = 0;
	public final static int TOTAL_ATTEMPTS_COLUMN = 1;
	public final static int DLC_ATTEMPTS_COLUMN = 2;
	public final static int DLC_PERCENT_COLUMN = 3;
	public final static int SUCC_COMM_PERC_COLUMN = 4;

	/** String values for column representation */
	public final static String MCT_NAME_STRING = "MCT Name";
	public final static String TOTAL_ATTEMPTS_STRING = "Total Attempts";
	public final static String DLC_ATTEMPTS_STRING = "DLC Attempts";
	public final static String DLC_PERCENT_STRING = "DLC Percent";
	public final static String SUCC_COMM_PERC_STRING= "Successful Communication%";

	/** Inner class container of table model data*/
	private class CarrierCommStat
	{
		public String mctName = null;
		public Integer totalAttempts = null;
		public Integer dlcAttempts = null;
		public Double dlcPercent = null;
		public Double succCommPerc = null;
		public CarrierCommStat(String mctName_, Integer totalAttempts_, Integer dlcAttempts_, Double dlcPercent_, Double succCommPerc_)
		{
			mctName = mctName_;
			totalAttempts = totalAttempts_;
			dlcAttempts = dlcAttempts_;
			dlcPercent = dlcPercent_;
			succCommPerc = succCommPerc_;			
		}
	}
	
	/**
	 * Default Constructor.
	 */
	public StatisticCarrierCommData()
	{
		this("DEVICE", "CARRIER", "Monthly");		
	}

	/**
	 * Constructor 
	 * @param category_ - YukonPaobject.category
	 * @param paoClass_ - YukonPaobject.paoClass
	 * @param statType_ - DYNAMICPAOSTATISTICS.statisticType
	 */
	public StatisticCarrierCommData(String category_, String paoClass_, String statType_)
	{
		super(category_, paoClass_, statType_);		
	}
	
	/**
	 * Add CarrierCommStat objects to data, retrieved from rset.
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
			
			Integer dlcAttempts = new Integer( attempts.intValue() - commErrors.intValue() - systemErrs.intValue());
			Double dlcPercent = new Double( (attempts.doubleValue() - protocolErrs.doubleValue()) / attempts.doubleValue());
			Double success = new Double(completions.doubleValue() / requests.doubleValue());
			
			CarrierCommStat carrierCommStat = new CarrierCommStat(paoName, attempts, dlcAttempts, dlcPercent, success);
			data.add(carrierCommStat);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Build the SQL statement to retrieve StatisticCarrierCommData data.
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
			columnNames[0] = MCT_NAME_STRING;
			columnNames[1] = TOTAL_ATTEMPTS_STRING;
			columnNames[2] = DLC_ATTEMPTS_STRING;
			columnNames[3] = DLC_PERCENT_STRING;
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
		if( o instanceof CarrierCommStat)
		{
			CarrierCommStat stat = ((CarrierCommStat)o);
			switch( columnIndex)
			{
				case MCT_NAME_COLUMN:
					return stat.mctName;
				
				case TOTAL_ATTEMPTS_COLUMN:
					return stat.totalAttempts;
	
				case DLC_ATTEMPTS_COLUMN:
					return stat.dlcAttempts;
	
				case DLC_PERCENT_COLUMN:
					return stat.dlcPercent;
					
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
		return "CARRIER COMMUNICATION STATISTICS";
	}
	
}
