package com.cannontech.analysis.tablemodel;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.lm.LMEvent;
import com.cannontech.analysis.data.lm.LMEventCustomer;
import com.cannontech.analysis.data.lm.SettlementCustomer;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.customer.CICustomerPointData;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.stars.util.SettlementConfigFuncs;

/**
 * Created on Nov 15, 2005
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * @author snebben
 */
public class HECO_SettlementModelBase extends ReportModelBase
{
	private Vector dataObjects = new Vector();
	
	/** Map of Integer(customerID) to CICustomerBase object*/
	public HashMap settlementCustomerMap = null;
	
	/** An eri amount for each customer, index of eriAmounts are the same as the customerIDs index*/
	private Double[] eriAmounts = null;
	private Double[] efslEmergencyCharges = null;
	private Double[] efslDispatchedCharges = null;
	private Double[] efslUnderfrequecyCharges = null;
	
	private Integer[] customerIDS = null;
	private Double[] custDemandLevels = null;
	private Double[] custCurtailLoads = null;
	
	private Vector settlementConfigs = null;
	private LiteStarsEnergyCompany liteStarsEC = null;
	
	/** A string for the title of the data */
	private static String title = "Settlement";
	
	protected String pointIDCSV = null;
	
	
//	private static final String ATT_ACTION_GROUP_TYPE = "actionGroupType";
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
	protected SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	protected DecimalFormat decimalFormat = new DecimalFormat("0.00");
	protected DecimalFormat intFormat = new DecimalFormat("0");
	protected DecimalFormat moneyFormat = new DecimalFormat("$0.00");
	public static void main(String[] args)
	{
		HECO_SettlementModelBase model = new HECO_SettlementModelBase();
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTime(model.getStartDate());
		tempCal.set(Calendar.DAY_OF_MONTH, 15);
		model.setStartDate(tempCal.getTime());
//		model.collectData();
		model.getColumnNames();
		
	}
	
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public HECO_SettlementModelBase(Date start_, Date stop_)
	{
		super(start_, stop_);//default type
		setEnergyCompanyID(new Integer(0));
		moneyFormat.setNegativePrefix("(");
		moneyFormat.setNegativeSuffix(")");
	}

	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public HECO_SettlementModelBase()
	{
		this(null, null);		
	}

	/**
	 * Constructor class
	 * Only ONE energycompanyID is used 
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public HECO_SettlementModelBase(Integer ecID_)
	{
		this();//default type
		setEnergyCompanyID(ecID_);
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		//Reset all objects, new data being collected!
		getData().clear();
		setData(null);
		settlementCustomerMap = null;	//dump old data
		customerIDS = null;	//dump old data
		custDemandLevels = null;
		custCurtailLoads = null;
		settlementConfigs = null;	//dump old configs
		pointIDCSV = null;
		
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));
				CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());
				rset = pstmt.executeQuery();

				while( rset.next())
				{
					addDataRow(rset);
				}
				loadDataVector();
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
				if( rset != null)
					rset.close();
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
			
			//Sort the data!
//			Collections.sort(getData(), actLogComparator);
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
		 
	/**
	 * Load the customer RPH data for each LMEvent
	 */
	private void collectCustomerBillingData(LMEvent event)
	{
		int rowCount = 0;
		if( getSettlementCustomerMap().isEmpty())
			return;
			
		StringBuffer sql = new StringBuffer("SELECT POINTID, VALUE, TIMESTAMP "+
			" FROM " + RawPointHistory.TABLE_NAME +  
			" WHERE TIMESTAMP >= ? " +
			" AND TIMESTAMP <= ? ");
			
			String pointList = getPointIDCSV();
			if( pointList.length() > 0)
				sql.append(" AND POINTID IN (" + pointList + ")"); 

			sql.append(" ORDER BY POINTID, TIMESTAMP");
		
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( event.getAdjustedStartDateTime().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( event.getAdjustedStopDateTime().getTime() ));
				CTILogger.info("START DATE >= " + event.getAdjustedStartDateTime() + "  -  STOP DATE <= " + event.getAdjustedStopDateTime());
				rset = pstmt.executeQuery();
				Vector vectorOfLiteRPH = null;
				int currentPointID = -1;
				while( rset.next())
				{
					int pointID = rset.getInt(1);
					if( currentPointID != pointID)
					{
						if( currentPointID > -1)	//not the first time!
						{
							Iterator iter = getSettlementCustomerMap().entrySet().iterator();
							while (iter.hasNext())
							{
								SettlementCustomer settleCust = (SettlementCustomer)((Map.Entry)iter.next()).getValue();
								if (settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT) != null && 
									settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT).getPointID().intValue() == pointID)
								{	//Found the matching pointid to customer!
									LMEventCustomer eventCust = (LMEventCustomer)event.getLmEventCustomerMap().get(settleCust.getCustomerID());
									if( eventCust != null)
										eventCust.setLiteRPHVector(vectorOfLiteRPH);
									CTILogger.info("For customer: " + eventCust.getCustomerID().intValue() + "  RPH Collected: " + vectorOfLiteRPH.size() + " Records");
								}
							}
						}
						currentPointID = pointID;
						vectorOfLiteRPH = new Vector();
					}
						
					double value = rset.getDouble(2);
					Timestamp ts = rset.getTimestamp(3);
					LiteRawPointHistory lRPH = new LiteRawPointHistory(pointID);
					lRPH.setValue(value);
					lRPH.setTimeStamp(ts.getTime());
					vectorOfLiteRPH.add(lRPH);
				}
				if( currentPointID > -1)	//Need to store the last pointid too.
				{
					Iterator iter = getSettlementCustomerMap().entrySet().iterator();
					while (iter.hasNext())
					{
						SettlementCustomer settleCust = (SettlementCustomer)((Map.Entry)iter.next()).getValue();
						if (settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT) != null && 
							settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT).getPointID().intValue() == currentPointID)
						{	//Found the matching pointid to customer!
							LMEventCustomer eventCust = (LMEventCustomer)event.getLmEventCustomerMap().get(settleCust.getCustomerID());
							if( eventCust != null)
								eventCust.setLiteRPHVector(vectorOfLiteRPH);
							CTILogger.info("For customer: " + eventCust.getCustomerID().intValue() + "  RPH Collected: " + vectorOfLiteRPH.size() + " Records");
						}
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
		return;
	
		
	}

	private String getPointIDCSV()
	{
		if( pointIDCSV == null)
		{
			pointIDCSV = new String();
			Iterator iter = getSettlementCustomerMap().entrySet().iterator();
			while(iter.hasNext())
			{
				SettlementCustomer settleCust = (SettlementCustomer)((Map.Entry)iter.next()).getValue();
				CICustomerPointData pointData = settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT);
				if( pointData != null)
					pointIDCSV += pointData.getPointID().intValue() + ",";
			}
	
			//We put one too many ',' in the list
			int removeIndex = pointIDCSV.lastIndexOf(",");
			pointIDCSV = pointIDCSV.substring(0,removeIndex);
		}
		return pointIDCSV;		
	}
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT PAOBJECTID, STARTDATETIME, STOPDATETIME, ACTIVERESTORE " +
			" FROM " + LMControlHistory.TABLE_NAME +  
			" WHERE STARTDATETIME > ? and STARTDATETIME <= ? " +
			" AND CONTROLDURATION > 0 " + 
			" AND ACTIVERESTORE IN ('M', 'T', 'O') " + 
			" ORDER BY STARTDATETIME, STOPDATETIME ");
			
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
			Integer programID = new Integer(rset.getInt(1));
			Timestamp startTS = rset.getTimestamp(2);
			Timestamp stopTS = rset.getTimestamp(3);
			
			LMEvent lmEvent = new LMEvent(programID, new Date(startTS.getTime()), new Date(stopTS.getTime()), getEnergyCompanyID(), getCustomerIDS(), getCustDemandLevels(), getCustCurtailLoads());
			getDataObjects().add(lmEvent);
			collectCustomerBillingData(lmEvent);			
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
	 /**
	  * All implementing classes must/should override this method
	  */
	public Object getAttribute(int columnIndex, Object o)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	/**
	 * All implementing classes must/should override this method
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	/**
	 * All implementing classes must/should override this method
	 */
	public Class[] getColumnTypes()
	{
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	/**
	 * All implementing classes must/should override this method
	 */
	public ColumnProperties[] getColumnProperties()
	{
		return columnProperties;
	}
	
	public String getHTMLOptionsTable()
	{
		String html = "";
		
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td align='center'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>NONE</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Display Options</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
//			String[] paramArray = req.getParameterValues(ATT_ACTION_GROUP_TYPE);
		}
	}

	/**
	 * Returns a map of all customers for YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO.
	 * Customers having rateScheduleID equal to the valid rateScheduleIDs attached to the YUK_DEF_ID_SETTLEMENT_HECO 
	 * settlement type are returned. 
	 * @return
	 */
	public HashMap getSettlementCustomerMap()
	{
		if( settlementCustomerMap == null)
			loadSettlementCustomerMap();
		return settlementCustomerMap;
	}
	
	private void loadSettlementCustomerMap()
	{
		settlementCustomerMap = new HashMap();
		if (getEnergyCompanyID() != null )
		{
			Connection conn = null;
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias());
				
				List ciCustomers = DefaultDatabaseCache.getInstance().getAllCICustomers();
				for (int i = 0; i < ciCustomers.size(); i++)
				{
					LiteCICustomer liteCICust = (LiteCICustomer)ciCustomers.get(i);
					if( liteCICust.getRateScheduleID() > 0)
					{
						for (int j = 0; j < getSettlementConfigs().size(); j++)
						{
							if( ((LiteSettlementConfig)getSettlementConfigs().get(j)).getRefEntryID() > 0 && liteCICust.getRateScheduleID() == ((LiteSettlementConfig)getSettlementConfigs().get(j)).getRefEntryID() )
							{
								SettlementCustomer settleCust = new SettlementCustomer(new Integer(liteCICust.getCustomerID()), getStartDate(), getStopDate());
								settlementCustomerMap.put(settleCust.getCustomerID(), settleCust);
								CTILogger.info("Added Customer: " + settleCust.getCustomerID().intValue() + " " + ((LiteSettlementConfig)getSettlementConfigs().get(j)).getRefEntryID());
								break;						
							}
						}
					}
				}
			}
			catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
			finally {
				try {
					if (conn != null) conn.close();
				}
				catch (java.sql.SQLException e) {}
			}
		}
	}
	
	/**
	 * Returns the SettlementCustomer from getSettlementCustomerMap with key value of customerID (Integer) 
	 * @param customerID
	 * @return SettlementCustomer
	 */
	public SettlementCustomer getSettlementCustomer(Integer customerID)
	{
		return (SettlementCustomer)getSettlementCustomerMap().get(customerID);
	}
	
	/**
	 * An array of Integers (CustomerIDs)
	 * @return Integer[] cusotmerIDs
	 */
	public Integer[] getCustomerIDS()
	{
		if( customerIDS == null)
			loadCustomerArrays();

		return customerIDS;
	}

	/**
	 * An array of Double (custDemandLevels)
	 * @return Double[] custDemandLevels
	 */
	public Double[] getCustDemandLevels()
	{
		if( custDemandLevels == null)
			loadCustomerArrays();
		return custDemandLevels;
	}

	/**
	 * An array of Double (custCurtailLoads)
	 * @return Double[] custCurtailLoads
	 */
	public Double[] getCustCurtailLoads()
	{
		if( custCurtailLoads == null)
			loadCustomerArrays();
		return custCurtailLoads;
	}

	/**
	 * An array of Integers (CustomerIDs)
	 * @return Integer[] cusotmerIDs
	 */
	public void loadCustomerArrays()
	{
		customerIDS = new Integer[getSettlementCustomerMap().size()];
		custDemandLevels = new Double[getSettlementCustomerMap().size()];
		custCurtailLoads = new Double[getSettlementCustomerMap().size()];
	
		Iterator iter = getSettlementCustomerMap().entrySet().iterator();
		int index = 0;
		while(iter.hasNext())
		{
			SettlementCustomer settleCust = (SettlementCustomer)((Map.Entry)iter.next()).getValue();
			customerIDS[index] = settleCust.getCustomerID();
			custDemandLevels[index] = settleCust.getDemandLevel();
			custCurtailLoads[index] = settleCust.getCurtailableLoad();
			index++;
		}
	}

	/**
	 * Return the Vector of data objects.  The Data Objects are the actual LMEvents.
	 * Use getData() for the Integer object, it provides the index into the getDataObjects()
	 * @return Vector dataObject
	 */
	public java.util.Vector getDataObjects()
	{
		if (dataObjects == null)
		{
			dataObjects = new Vector();
		}
		
		return dataObjects;
	}
	
	/**
	 * Returns a Vector of LiteSettlementConfigs for the HECO Settlement type assigned to energyCompanyID.
	 * @return Vector
	 */
	public Vector getSettlementConfigs()
	{
		if (settlementConfigs == null)
		{
			int entryID = getLiteStarsEC().getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO).getEntryID();	//2260
			settlementConfigs = SettlementConfigFuncs.getAllLiteConfigsByEntryID(entryID);
		}
		return settlementConfigs;
	}

	/**
	 * This method should be overwritten by all implementing interfaces.
	 * This method should load the getData() vector.
	 */
	protected void loadDataVector()
	{
	}	
	
	/**
	 * Returns a LiteStarsEnergyCompany for energyCompanyID 
	 * @return
	 */
	public LiteStarsEnergyCompany getLiteStarsEC()
	{
		if( liteStarsEC == null)
		{
			if (getEnergyCompanyID() != null)
				liteStarsEC = new LiteStarsEnergyCompany(getEnergyCompanyID().intValue());
		}
		return liteStarsEC;
	}
	
	/**
	 * Returns an array of eri Incentive Amounts.  
	 * Indexed based on the customerIDs:  Where index 0 contains the ERIIncentiveAmount for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getERIIncentiveAmounts()
	{
		if( eriAmounts == null )
			loadCustomerTotals();

		return eriAmounts;
	}
	
	/**
	 * Returns an array of EFSL Dispatched Charges.  
	 * Indexed based on the customerIDs:  Where index 0 contains the efslDispatchedCharges for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getEfslDispatchedCharges()
	{
		if( efslDispatchedCharges == null )
			loadCustomerTotals();
		
		return efslDispatchedCharges;
	}

	/**
	 * Returns an array of EFSL Emergency Charges.  
	 * Indexed based on the customerIDs:  Where index 0 contains the efslEmergencyCharges for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getEfslEmergencyCharges()
	{
		if( efslEmergencyCharges == null )
			loadCustomerTotals();
		
		return efslEmergencyCharges;
	}

	/**
	 * Returns an array of EFSL UnderFrequency Charges.  
	 * Indexed based on the customerIDs:  Where index 0 contains the efslUnderfrequencyCharges for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getEfslUnderfrequecyCharges()
	{
		if( efslUnderfrequecyCharges == null )
			loadCustomerTotals();
		
		return efslUnderfrequecyCharges;
	}

	
	private void loadCustomerTotals()
	{
//		Load all of the ERI Amounts
  		eriAmounts = new Double[getCustomerIDS().length];
  		efslDispatchedCharges = new Double[getCustomerIDS().length];
		efslEmergencyCharges = new Double[getCustomerIDS().length];
  		efslUnderfrequecyCharges = new Double[getCustomerIDS().length];

  		for ( int i = 0; i < getCustomerIDS().length; i++)
  		{
	  		double eriAmount = 0;
	  		double efslDisp = 0;
	  		double efslEmer = 0;
	  		double efslUF = 0;
	  		for(int j = 0; j < getDataObjects().size(); j++)
	  		{
		  		LMEvent event = ((LMEvent)getDataObjects().get(j));
		  		LMEventCustomer lmEventCust = (LMEventCustomer)event.getLmEventCustomerMap().get(customerIDS[i]);
		  		if( lmEventCust != null)
		  		{
			  		eriAmount += lmEventCust.getERICredit(event.getRoundedDuration()).doubleValue();
					double efslCharge = lmEventCust.getExcessFirmServiceLevelCharge().doubleValue();
			  		switch (event.getEventType())
					{
						case LMEvent.DISPATCHED_EVENT:
							efslDisp += efslCharge;
							break;
						case LMEvent.EMERGENCY_EVENT:
							efslEmer += efslCharge;
							break;
						case LMEvent.UF_EVENT:
							if ( efslCharge < efslUF )
								efslUF = efslCharge;
							break;
						default :
							break;
					}
		  		}
	  		}
	  		eriAmounts[i] = new Double(eriAmount);
	  		efslDispatchedCharges[i] = new Double(efslDisp);
	  		efslEmergencyCharges[i] = new Double(efslEmer);
	  		efslUnderfrequecyCharges[i] = new Double(efslUF);
  		}
	}
}