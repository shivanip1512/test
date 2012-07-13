package com.cannontech.analysis.tablemodel;

import java.io.IOException;
import java.io.OutputStream;
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
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.lm.LMEvent;
import com.cannontech.analysis.data.lm.LMEventCustomer;
import com.cannontech.analysis.data.lm.SettlementCustomer;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.database.db.customer.CICustomerPointData;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompanyFactory;
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
	/*public static final int LM_EVENT_SUMMARY_SETTLEMENT = 0;
	public static final int MONTHLY_BILLING_SETTLEMENT = 1;
	public static final int CUSTOMER_MONTHLY_BILLING_SETTLEMENT = 2;
	public static final int DSMIS_SETTLEMENT = 3;
	private int settlementReportType = LM_EVENT_SUMMARY_SETTLEMENT;
	*/
	private Vector dataObjects = new Vector();
	
	/** Map of Integer(customerID) to CICustomerBase object*/
	public HashMap<Integer, SettlementCustomer> settlementCustomerMap = null;
	
	/** An eri amount for each customer, index of eriAmounts are the same as the customerIDs index*/
	private Double[] eriAmounts = null;
	private Double[] efslEmergencyCharges = null;
	private Double[] efslDispatchedCharges = null;
	private Double[] efslUnderfrequecyCharges = null;
	private Double[] controlledDemandIncentive = null;
	
	private Integer[] customerIDS = null;
	private Double[] custDemandLevels = null;
	private Double[] custCurtailLoads = null;
	
	private LiteStarsEnergyCompany liteStarsEC = null;
	
	/** A string for the title of the data */
	private static String title = "Settlement";
	
	protected String pointIDCSV = null;
	
	
//	private static final String ATT_ACTION_GROUP_TYPE = "actionGroupType";
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
	protected SimpleDateFormat dateFormat_yyyy = new SimpleDateFormat("yyyyMM");
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
	 */
	public HECO_SettlementModelBase()
	{
		this(null, null);		
	}

	/**
	 * Constructor class
	 * Only ONE energycompanyID is used 
	 */
	public HECO_SettlementModelBase(Integer ecID_)
	{
		this();//default type
		setEnergyCompanyID(ecID_);
	}
	
    /*
     * Clear out (re-init) all data storage objects.
     */
    public void clearDataObjects() {
        getData().clear();
        getDataObjects().clear();
        settlementCustomerMap = null;   //dump old data
        pointIDCSV = null;
        
        eriAmounts = null;
        efslEmergencyCharges = null;
        efslDispatchedCharges = null;
        efslUnderfrequecyCharges = null;
        controlledDemandIncentive = null;
        customerIDS = null;
        custDemandLevels = null;
        custCurtailLoads = null;
    }
    
    @Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
        clearDataObjects();
        		
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

        CTILogger.info("LM EVENT: " + event.getEventTypeString() + " (ID:"+event.getGroupID() +")");
        CTILogger.info("ActualStartDateTime     - " + event.getActualStartDateTime()     + "  - ActualStopDateTime     - " + event.getActualStopDateTime());
        CTILogger.info("RoundedStartDateTime    - " + event.getRoundedStartDateTime()    + "  - RoundedStopDateTime    - " + event.getRoundedStopDateTime());
        CTILogger.info("SettlementStartDateTime - " + event.getSettlementStartDateTime() + "  - SettlementStopDateTime - " + event.getSettlementStopDateTime());
        CTILogger.info("AdjustedStartDateTime   - " + event.getAdjustedStartDateTime()   + "  - AdjustedStopDateTime   - " + event.getAdjustedStopDateTime());

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
									settleCust.getPointData(CICustomerPointData.TYPE_SETTLEMENT).getPointID().intValue() == currentPointID)
								{	//Found the matching pointid to customer!
									LMEventCustomer eventCust = (LMEventCustomer)event.getLmEventCustomerMap().get(settleCust.getCustomerID());
									if( eventCust != null)
										eventCust.setLiteRPHVector(vectorOfLiteRPH);
									CTILogger.info("For customer: " + eventCust.getCustomerID().intValue() + " PointID: " +currentPointID+ " RPH Collected: " + vectorOfLiteRPH.size() + " Records");
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
							CTILogger.info("For customer: " + eventCust.getCustomerID().intValue() + " PointID: " +currentPointID+ "  RPH Collected: " + vectorOfLiteRPH.size() + " Records");
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
			SqlUtils.close(rset, pstmt, conn );
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
		StringBuffer sql = new StringBuffer("SELECT GRP.PAONAME, LMCH.PAOBJECTID, STARTDATETIME, STOPDATETIME, ACTIVERESTORE " +
			" FROM " + LMControlHistory.TABLE_NAME +  " LMCH, YUKONPAOBJECT GRP " +
			" WHERE LMCH.PAOBJECTID = GRP.PAOBJECTID " +
            " AND STARTDATETIME > ? and STARTDATETIME <= ? " +
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
            String groupName = rset.getString(1);
            if( groupName.toLowerCase().indexOf("cidlc") > -1)
            {
                Integer groupID = new Integer(rset.getInt(2));
    			Timestamp startTS = rset.getTimestamp(3);
    			Timestamp stopTS = rset.getTimestamp(4);
    			
    			LMEvent lmEvent = new LMEvent(groupName, groupID, new Date(startTS.getTime()), new Date(stopTS.getTime()), 
                                              getEnergyCompanyID(), getCustomerIDS(), getCustDemandLevels(), getCustCurtailLoads());
    			getDataObjects().add(lmEvent);
    			collectCustomerBillingData(lmEvent);
            }
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
	@Override
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
	public HashMap<Integer, SettlementCustomer> getSettlementCustomerMap()
	{
		if( settlementCustomerMap == null)
			loadSettlementCustomerMap();
		return settlementCustomerMap;
	}
	
	private void loadSettlementCustomerMap()
	{
		settlementCustomerMap = new HashMap<Integer, SettlementCustomer>();
		if (getEnergyCompanyID() != null )
		{
			Connection conn = null;
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias());
				
				List<LiteCICustomer> ciCustomers = DefaultDatabaseCache.getInstance().getAllCICustomers();
				for (LiteCICustomer liteCICustomer : ciCustomers) {
					if( liteCICustomer.getRateScheduleID() > 0)
					{
					    List<Pair<Integer, String>> demandCharges = SettlementConfigFuncs.getLiteSettlementConfigs(SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING);
					    for (Pair<Integer, String> pair : demandCharges) {
							if (pair.getFirst() > 0 && liteCICustomer.getRateScheduleID() == pair.getFirst())
							{
								SettlementCustomer settleCust = new SettlementCustomer(new Integer(liteCICustomer.getCustomerID()), getStartDate(), getStopDate());
								settlementCustomerMap.put(settleCust.getCustomerID(), settleCust);
								CTILogger.info("Added Customer: " + settleCust.getCustomerID().intValue() + " " + pair.getFirst());
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
		return getSettlementCustomerMap().get(customerID);
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
	
		Iterator<Entry<Integer, SettlementCustomer>> iter = getSettlementCustomerMap().entrySet().iterator();
		int index = 0;
		while(iter.hasNext())
		{
			SettlementCustomer settleCust = iter.next().getValue();
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
			if (getEnergyCompanyID() != null) {
			    LiteStarsEnergyCompanyFactory factory = 
			        YukonSpringHook.getBean("liteStarsEnergyCompanyFactory", LiteStarsEnergyCompanyFactory.class);
				liteStarsEC = factory.createEnergyCompany(getEnergyCompanyID().intValue());
			}	
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
			loadCustomerEventTotals();

		return eriAmounts;
	}
	
	/**
	 * Returns the total of all entris in the an array of eri Incentive Amounts.  
	 * @return
	 */
	public double getERIIncentiveAmountsTotal()
	{
		double total = 0;
		for (int i = 0; i < getERIIncentiveAmounts().length; i++)
		{
			total += getERIIncentiveAmounts()[i].doubleValue();
		}
		return total;

	}	
	/**
	 * Returns an array of EFSL Dispatched Charges.  
	 * Indexed based on the customerIDs:  Where index 0 contains the efslDispatchedCharges for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getEfslDispatchedCharges()
	{
		if( efslDispatchedCharges == null )
			loadCustomerEventTotals();
		
		return efslDispatchedCharges;
	}
	/**
	 * Returns the total of all entries in the array of EFSL Dispatched Charges.  
	 * @return
	 */
	public double getEfslDispatchedTotal()
	{
		double total = 0;
		for (int i = 0; i < getEfslDispatchedCharges().length; i++)
		{
			total += getEfslDispatchedCharges()[i].doubleValue();
		}
		return total;
	}
	/**
	 * Returns an array of EFSL Emergency Charges.  
	 * Indexed based on the customerIDs:  Where index 0 contains the efslEmergencyCharges for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getEfslEmergencyCharges()
	{
		if( efslEmergencyCharges == null )
			loadCustomerEventTotals();
		
		return efslEmergencyCharges;
	}
	/**
	 * Returns the total of all entries in the array of EFSL Emergency Charges.  
	 * @return
	 */
	public double getEfslEmergencyTotal()
	{	
		double total = 0;
		for (int i = 0; i < getEfslEmergencyCharges().length; i++)
		{
			total += getEfslEmergencyCharges()[i].doubleValue();
		}
		return total;
	}
	/**
	 * Returns an array of EFSL UnderFrequency Charges.  
	 * Indexed based on the customerIDs:  Where index 0 contains the efslUnderfrequencyCharges for the customer at customerIDs[0]
	 * @return
	 */
	public Double[] getEfslUnderfrequecyCharges()
	{
		if( efslUnderfrequecyCharges == null )
			loadCustomerEventTotals();
		
		return efslUnderfrequecyCharges;
	}

	/**
	 * Returns the total of all entries in the array of EFSL UnderFrequency Charges.  
	 * @return
	 */
	public double getEfslUnderfrequecyTotal()
	{		
		double total = 0;
		for (int i = 0; i < getEfslUnderfrequecyCharges().length; i++)
		{
			total += getEfslUnderfrequecyCharges()[i].doubleValue();
		}
		return total;
	}
	/**
	 * Returns an array of settlementCustomer ControlledDemandIncentive values.  
	 * Indexed based on the customerIDs:  Where index 0 contains the controlledDemandIncentive for the settlementCustomer at customerIDs[0]
	 * @return
	 */
	public Double[] getControlledDemandIncentive()
	{
		if( controlledDemandIncentive == null )
			loadAllCustomerTotals();
		
		return controlledDemandIncentive;
	}

	/**
	 * Returns  the total of all entries in the array of ControlledDemandIncentive.  
	 * @return
	 */
	public double getControlledDemandIncentiveTotal()
	{		
		double total = 0;
		for (int i = 0; i < getControlledDemandIncentive().length; i++)
		{
			total += getControlledDemandIncentive()[i].doubleValue();
		}
		return total;
	}
	
	/**
	 * Returns the total EFSL charges (Dispatched, Emergency, UF) for customer at index.
	 * @param index
	 * @return
	 */
	public Double getTotalEFSLCharges(int index)
	{
		return new Double( getEfslDispatchedCharges()[index].doubleValue() + 
					getEfslEmergencyCharges()[index].doubleValue() + 
					getEfslUnderfrequecyCharges()[index].doubleValue());
	}
	
	/**
 	 * Returns the total EFSL charges (Dispatched, Emergency, UF) for All Customers. 
	 * @param index
	 * @return
	 */
	public double getAllTotalEFSLCharges()
	{
		return getEfslDispatchedTotal() + getEfslEmergencyTotal() + getEfslUnderfrequecyTotal();
	}	
	/**
	 * Returns the total CIDLC credits, the sum of the total EFSL charges and total CIDLC incentive for customer at index.
	 * @param index
	 * @return
	 */
	public Double getTotalCIDLCredits(int index)
	{
		return new Double( getTotalEFSLCharges(index).doubleValue() + getTotalCIDLCIncentive(index).doubleValue());
	}
	/**
	 * Returns the total CIDLC credits, the sum of the total EFSL charges and totoal CIDLC incentive for ALL Customers. 
	 * @param index
	 * @return
	 */
	public double getAllTotalCIDLCredits()
	{
		return getAllTotalCIDLCIncentive() + getAllTotalEFSLCharges();
	}
	/**
	 * Returns the total CIDLC incentive, cdi(controlDemandIncentive), and ERI incentive
	 * for customer at index.
	 * @param index
	 * @return
	 */
	public Double getTotalCIDLCIncentive(int index)
	{
		return new Double( getControlledDemandIncentive()[index].doubleValue() + getERIIncentiveAmounts()[index].doubleValue());
	}
	
	/**
	 * Returns the total CIDLC incentive, cdi(controlDemandIncentive), and ERI incentive
	 * for ALL Customers.
	 * @param index
	 * @return
	 */
	public double getAllTotalCIDLCIncentive()
	{
		return getControlledDemandIncentiveTotal() + getERIIncentiveAmountsTotal();
	}	
	/**
	 * Load the efsl charges arrays (Dispatched, Emergency, UF).
	 */
	private void loadCustomerEventTotals()
	{
		// Load all of the ERI Amounts
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
			  		eriAmount += lmEventCust.getERICredit(event.getRoundedDuration(), event.getEventType()).doubleValue();
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
	/**
	 * Load the efsl charges arrays (Dispatched, Emergency, UF).
	 */
	private void loadAllCustomerTotals()
	{
		// Load all of the ERI Amounts
		controlledDemandIncentive = new Double[getCustomerIDS().length];
		double cdi = 0;
		for ( int i = 0; i < getCustomerIDS().length; i++)
		{
			SettlementCustomer settleCust = getSettlementCustomer(getCustomerIDS()[i]);;
			cdi = settleCust.getControlledDemandIncentive().doubleValue();
            controlledDemandIncentive[i] = (cdi < 0 ? new Double(0) : new Double(cdi));
		}
	}
    
    @Override
    public void buildByteStream(OutputStream out) throws IOException
    {
        //Write column headers
        for (int r = 0; r < getColumnCount(); r++) 
        {
            if( r != 0 )
                out.write(getFieldSeparator().getBytes());
                    
            out.write(getColumnName(r).getBytes());
        }
        out.write(LINE_SEPARATOR.getBytes());
        
        //Write data
        for (int r = 0; r < getRowCount(); r++) 
        {
            for (int c = 0; c < getColumnCount(); c++) 
            { 
                if (c != 0) 
                    out.write(getFieldSeparator().getBytes()); 
                
                String str = String.valueOf(getValueAt(r,c));
                //Remove irregular characters to ensure better csv format
                str = str.replaceAll("\r\n", "");
                str = str.replaceAll(",", ";");
                out.write(str.getBytes());
            } 
            out.write(LINE_SEPARATOR.getBytes());
        } 
    }
}