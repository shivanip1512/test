package com.cannontech.analysis.tablemodel;

import java.util.HashMap;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.stars.ProgramDetail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.lite.LiteContact;

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
public class ProgramDetailModel extends ReportModelBase
{
	/** A string for the title of the data */
	private static String title = "ENERGY COMPANY ACTIVITY LOG";
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int ENERGY_COMPANY_COLUMN = 0;
	public final static int PROGRAM_NAME_COLUMN = 1;
	public final static int CONTACT_COLUMN = 2;	
	public final static int ACCOUNT_NUMBER_COLUMN = 3;
	public final static int STATUS_COLUMN = 4;
	
	/** String values for column representation */
	public final static String ENERGY_COMPANY_STRING = "Energy Company";
	public final static String PROGRAM_NAME_STRING = "Program";
	public final static String CONTACT_STRING = "Contact";
	public final static String ACCOUNT_NUMBER_STRING = "Account Number";
	public final static String STATUS_STRING = "Status";
	
	/** Class fields */
	private int[] ecIDs = null;	

	//Key = Str("AcctID_VirtProgID")
	//Value = Most recent lmProgramEvent(Integer)
	private HashMap acctProgPairs = null;
	
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ProgramDetailModel(long stopTime_)
	{
		//use the stop (max) time for both date entries.
		super(ReportTypes.EC_ACTIVITY_LOG_DATA, stopTime_, stopTime_);//default type
	}

	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ProgramDetailModel()
	{
		this(ReportTypes.EC_ACTIVITY_LOG_DATA);//default report type
	}
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ProgramDetailModel(int reportType_)
	{
		super();//default type
		setReportType(reportType_);		
	}
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ProgramDetailModel(int [] ecIDs_)
	{
		this(ReportTypes.EC_ACTIVITY_LOG_DATA);//default type
		setECIDs(ecIDs_);
	}
	/**
	 * Constructor class
	 * Only ONE energycompanyID is used, constructor for convenience 
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ProgramDetailModel(Integer ecID_)
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
				rset = pstmt.executeQuery();

				boolean dataExists = false;
				while( rset.next())
				{
					addDataRow(rset);
					dataExists = true;
				}
				if (dataExists)
					buildAcctProgHashMap();
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
		StringBuffer sql = new StringBuffer("SELECT AM.ACCOUNTID, LPWP.PROGRAMID VirtualProgID, LPWP.DEVICEID LMProgID, " +
		" AC.DESCRIPTION, YWC.ALTERNATEDISPLAYNAME, CA.CUSTOMERID, CA.ACCOUNTNUMBER, EC.NAME " +
		" FROM APPLIANCECATEGORY AC, YUKONWEBCONFIGURATION YWC, LMPROGRAMWEBPUBLISHING LPWP, "+
		" ECTOACCOUNTMAPPING AM, ECTOGENERICMAPPING GM, CUSTOMERACCOUNT CA, ENERGYCOMPANY EC " +
		" WHERE LPWP.WEBSETTINGSID = YWC.CONFIGURATIONID "+  
		" AND CA.ACCOUNTID = AM.ACCOUNTID " +
		" AND LPWP.APPLIANCECATEGORYID = AC.APPLIANCECATEGORYID ");
		if( getECIDs() != null)
		{
			sql.append(" AND EC.ENERGYCOMPANYID IN (" + getECIDs()[0]);
			for (int i = 1; i < getECIDs().length; i++)
				sql.append(", " + getECIDs()[i]);
			sql.append(")");
		}
		sql.append(" AND GM.ENERGYCOMPANYID = EC.ENERGYCOMPANYID "+
		" AND GM.ENERGYCOMPANYID = AM.ENERGYCOMPANYID "+
		" AND AC.APPLIANCECATEGORYID = GM.ITEMID "+
		" AND GM.MAPPINGCATEGORY = 'ApplianceCategory' " +
		" ORDER BY EC.NAME, YWC.ALTERNATEDISPLAYNAME, CA.ACCOUNTNUMBER ");

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
			Integer acctID = new Integer(rset.getInt(1));
			Integer virtualProgID = new Integer(rset.getInt(2));
			Integer lmProgID = new Integer(rset.getInt(3));
			String desc = rset.getString(4);
			String altDisplayName = rset.getString(5);
			Integer custID = new Integer(rset.getInt(6));
			String acctNum = rset.getString(7);
			String ecName = rset.getString(8);
			
			//AM.ACCOUNTID, LPWP.PROGRAMID VirtualProgID, LPWP.DEVICEID LMProgID, AC.DESCRIPTION, YWC.ALTERNATEDISPLAYNAME, CA.CUSTOMERID, CA.ACCOUNTNUMBER 
			ProgramDetail pd = new ProgramDetail(ecName, altDisplayName, custID, acctNum, acctID, new Integer(-1) );
			getData().add(pd);

			//KEY = "ACCTID_PROGID"
			String acctProgStr = acctID.toString() + "_" + virtualProgID.toString();
			getAcctProgPairs().put(acctProgStr, pd);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param i
	 */
	public void setECIDs(Integer ecID)
	{
		setECIDs(new int[]{ecID.intValue()});
	}
	/**
	 * @return
	 */
	public int[] getECIDs()
	{
		return ecIDs;
	}

	/**
	 * @param is
	 */
	public void setECIDs(int[] is)
	{
		ecIDs = is;
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
		if( o instanceof ProgramDetail)
		{
			ProgramDetail pd = ((ProgramDetail)o);			
			switch( columnIndex)
			{
				case ENERGY_COMPANY_COLUMN:
					return pd.getEcName();
				case PROGRAM_NAME_COLUMN:
					return pd.getProgramName();
				case CONTACT_COLUMN:
				{
					LiteContact contact = CustomerFuncs.getPrimaryContact(pd.getCustID().intValue());
					if (contact == null)
						return "(n/a)";

					return (contact.getContLastName() + ", " + contact.getContFirstName());
				}
				case ACCOUNT_NUMBER_COLUMN:
				{
					if (pd.getAcctNumber() == null)
					{
						if( pd.getAcctID().intValue() == -1)
							return "(n/a)";
						else
							return "(deleted)";
					}
					return pd.getAcctNumber();
				}
				case STATUS_COLUMN:
					return pd.getStatus();
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
				PROGRAM_NAME_STRING,
				CONTACT_STRING,
				ACCOUNT_NUMBER_STRING,
				STATUS_STRING
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
			int offset = 0;
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(offset, 1, 150, 18, null),
				new ColumnProperties(offset, 1, offset+=250, 18, null),
				new ColumnProperties(offset, 1, offset+=100, 18, null),
				new ColumnProperties(offset, 1, offset+=100, 18, null),
				new ColumnProperties(offset, 1, offset+=150, 18, null)
			};				
		}
		return columnProperties;
	}
	
	public void buildAcctProgHashMap()
	{
		int rowCount = 0;
		StringBuffer sql = new StringBuffer("SELECT LPE.ACCOUNTID, LPE.PROGRAMID, YLE.YUKONDEFINITIONID, YLE.ENTRYTEXT "+
		" FROM LMPROGRAMEVENT LPE, LMCUSTOMEREVENTBASE CEB, YUKONLISTENTRY YLE "+
		" WHERE LPE.EVENTID = CEB.EVENTID "+
		" AND YLE.ENTRYID = CEB.ACTIONID " +
		" AND LPE.EVENTID IN "+
		" (SELECT MAX(LPE2.EVENTID) FROM LMPROGRAMEVENT LPE2, LMCUSTOMEREVENTBASE CEB2 "+
		" WHERE CEB2.EVENTID = LPE2.EVENTID AND CEB2.EVENTDATETIME <= ? "+
		" GROUP BY ACCOUNTID, PROGRAMID )" +
		" ORDER BY LPE.ACCOUNTID, LPE.PROGRAMID");
		
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStopTime() ));
				CTILogger.info("MAX STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					Integer acctID = new Integer(rset.getInt(1));
					Integer virtualProgID = new Integer(rset.getInt(2));
					Integer actionID = new Integer(rset.getInt(3));
					String entryText = rset.getString(4);
					
					//LPE.ACCOUNTID, LPE.PROGRAMID, LCEB.ACTIONID
					//Key = "AcctID_ProgID"
					String acctProgStr = acctID.toString()+ "_" + virtualProgID.toString(); 
					ProgramDetail pd = (ProgramDetail)getAcctProgPairs().get(acctProgStr);
					if( pd != null)
					{
						pd.setAction(actionID);
						pd.setStatus(entryText);
					}
						
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
	 * @return
	 */
	public HashMap getAcctProgPairs()
	{
		if( acctProgPairs == null)
			acctProgPairs = new HashMap(10);
		return acctProgPairs;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return (format.format(new java.util.Date(getStopTime())));
	}	

}
