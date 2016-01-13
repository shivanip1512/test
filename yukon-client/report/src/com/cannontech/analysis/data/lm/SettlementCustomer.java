/*
 * Created on Nov 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.data.lm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.database.db.customer.CICustomerPointData;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.util.SettlementConfigFuncs;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SettlementCustomer
{
	private Integer customerID = null;
	private CICustomerBase ciCustomerBase = null;
	private Date startDate = null;	//Greater than
	private Date stopDate = null;

	//fields defined by customer
	/** Firm Service Level (FSL) */
	private Double demandLevel = null;
	/** Contracted Interruptible Load (CIL)*/
	private Double curtailableLoad = null;
	/** Controlled Demand Incentive Amount*/
	private Double cdiAmount = null; 
	/** Maximum RPH demand interval value */
	private Double maxMonthlyDemand = null;
	/** Energy Reduction Incentive Amount*/
	private Double eriAmount = null;
			
	/** Control demand incentive Rate, defined by energycompany settlementConfig */
	private Double cdiRate = null;
	
	
	public SettlementCustomer(Integer customerID_, Date startDate_, Date stopDate_)
	{
		customerID = customerID_;
		startDate = (Date)startDate_.clone();
		stopDate = (Date)stopDate_.clone();
	}
	
	public CICustomerBase getCICustomerBase()
	{
		if( ciCustomerBase == null)
		{
			ciCustomerBase = new CICustomerBase();
			ciCustomerBase.setCustomerID(getCustomerID());
			try{
				Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, ciCustomerBase);
				ciCustomerBase = (CICustomerBase)t.execute();
			}
			catch(TransactionException e)
			{
				e.printStackTrace();
			}
		}
		return ciCustomerBase;
	}
	public CICustomerPointData getPointData(String pointType)
	{
		for (int i = 0; i < getCICustomerBase().getCiCustomerPointData().length; i++)
			if( getCICustomerBase().getCiCustomerPointData()[i].getType().equalsIgnoreCase(pointType))
				return getCICustomerBase().getCiCustomerPointData()[i];
		return null;
	}

	
	private static Double retrieveCICustomerPointData(int pointID, Date startDate, Date stopDate )
	{
		Double returnVal = null;

		AsyncDynamicDataSource dds = YukonSpringHook.getBean("asyncDynamicDataSource", AsyncDynamicDataSource.class);
        PointValueHolder pointData = dds.getPointValue(pointID);
		if (pointData != null)
        {
            if( pointData.getPointDataTimeStamp().after(startDate) &&           //after the startDate
                pointData.getPointDataTimeStamp().compareTo(stopDate) <= 0 )    //before or equal stopDate
                    returnVal = new Double(pointData.getValue());
        }
		
		if( returnVal == null)	//we weren't able to get the value from Cache, or it was not in the correct date range.
		{
			String sqlString = "SELECT TIMESTAMP, VALUE FROM " + RawPointHistory.TABLE_NAME +
				" WHERE POINTID = ?" + 
                " AND TIMESTAMP > ? AND TIMESTAMP <= ?" +
				" ORDER BY TIMESTAMP DESC ";
		
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rset = null;
			try
			{
				conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
				pstmt = conn.prepareStatement(sqlString);
                pstmt.setInt(1, pointID);
                pstmt.setTimestamp(2, new java.sql.Timestamp( startDate.getTime() ));
                pstmt.setTimestamp(3, new java.sql.Timestamp( stopDate.getTime() ));
                CTILogger.info("START DATE > " + startDate + "  -  STOP DATE <= " + stopDate);
				rset = pstmt.executeQuery();
		
				while (rset.next())
				{
					Timestamp ts = rset.getTimestamp(1);
					double value = rset.getDouble(2);
		
					returnVal = new Double(value);
					break;
				}
			}
			catch (java.sql.SQLException e)
			{
				CTILogger.error( e.getMessage(), e );
			}
			finally
			{
				try
				{
					if (rset != null)
						rset.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				}
				catch (java.sql.SQLException e)
				{
					CTILogger.error( e.getMessage(), e );
				}
			}
		}
		return returnVal;
	}
	/**
	 * @return
	 */
	public Double getCurtailableLoad()
	{
		if( curtailableLoad == null)
		{
			CICustomerPointData pointData = getPointData(CICustomerPointData.TYPE_CURTAILABLE);
			if (pointData != null)
				curtailableLoad = retrieveCICustomerPointData(pointData.getPointID().intValue(), getStartDate(), getStopDate());
			else
				curtailableLoad = new Double(0);
		}
		return curtailableLoad;
	}

	/**
	 * @return
	 */
	public Double getDemandLevel()
	{
		if( demandLevel == null)
		{
			CICustomerPointData pointData = getPointData(CICustomerPointData.TYPE_DEMAND);
			if (pointData != null)
				demandLevel = retrieveCICustomerPointData(pointData.getPointID().intValue(), getStartDate(), getStopDate());
			if (demandLevel == null)
				demandLevel = new Double(0);
		}
		return demandLevel;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString()
	{
		String retStr = "CustomerID: " + getCustomerID() + "\tCurtailLoad: " + getCurtailableLoad() + "\tDemandLevel: " + getDemandLevel() +
//			"\r\nEvent     : " + getEventStartDateTime() + " - " + getEventStopDateTime() + 
			"\r\n";

		return retStr;
	}
	/**
	 * @return
	 */
	public Integer getCustomerID()
	{
		return customerID;
	}
	/**
	 * 
	 * @param custID
	 * @param pointType
	 * @return
	 */
	/*public static CICustomerPointData getPointData(Integer custID, String pointType)
	{
		SettlementCustomer settleCust = getSettlementCustomer(custID);
		if (settleCust != null)
		{
			for (int i = 0; i < settleCust.getCICustomerBase().getCiCustomerPointData().length; i++)
				if( settleCust.getCICustomerBase().getCiCustomerPointData()[i].getType().equalsIgnoreCase(pointType))
					return settleCust.getCICustomerBase().getCiCustomerPointData()[i];
		}
		return null;
	}*/
	/**
	 * @return
	 */
	public Double getMaxMonthlyDemand()
	{
		if( maxMonthlyDemand == null)
		{
			maxMonthlyDemand = new Double(0);	//default the value to 0
			CICustomerPointData pointData = getPointData(CICustomerPointData.TYPE_SETTLEMENT);
			if (pointData != null)
			{
				String sqlString = "SELECT MAX(VALUE) FROM " + RawPointHistory.TABLE_NAME +
								" WHERE POINTID = " + pointData.getPointID().intValue() +
								" AND TIMESTAMP > ? AND TIMESTAMP <= ?";
			
				java.sql.Connection conn = null;
				java.sql.PreparedStatement pstmt = null;
				java.sql.ResultSet rset = null;
					
				try
				{
					conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
					
					if( conn == null )
					{
						CTILogger.error(getClass() + ":  Error getting database connection.");
					}
					else
					{
						pstmt = conn.prepareStatement(sqlString);
						pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
						pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));
						CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());
						rset = pstmt.executeQuery();
						while( rset.next())
						{
							maxMonthlyDemand = new Double(rset.getDouble(1));
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
				}
			}
		}
		return maxMonthlyDemand;
	}

	/**
	 * @return
	 */
	public Date getStartDate()
	{
		return startDate;
	}
	
	public Date getStopDate()
	{
		if ( stopDate == null)
		{
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(getStartDate());
			cal.add(Calendar.MONTH, 1);
			stopDate = cal.getTime();
		}
		return stopDate;
	}

	/**
	 * @return
	 */
	public Double getControlledDemandIncentive()
	{
		if (cdiAmount == null)
		{
			cdiAmount = new Double((getMaxMonthlyDemand().doubleValue() - getDemandLevel().doubleValue()) * getCDIRate().doubleValue());
            if(cdiAmount < 0.0)
                cdiAmount = 0.0;
		}
		return cdiAmount;
	}
	
	public Double getCDIRate()
	{
		if (cdiRate == null) {
			cdiRate = Double.valueOf(SettlementConfigFuncs.getLiteSettlementConfig(SettlementConfig.HECO_CDI_RATE_STRING));
		}
		return cdiRate;
	}
	/**
	 * Returns the rate schedule demand charge for customerID.
	 * @return
	 */
	public Double getCIDLCDemandCharge()
	{
		List<Pair<Integer, String>> demandCharges = SettlementConfigFuncs.getLiteSettlementConfigs(SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING);
		double demandCharge = 0;
		for (Pair<Integer, String> pair : demandCharges) {
			if(pair.getFirst().intValue() == getCICustomerBase().getCustomer().getRateScheduleID()) {
				demandCharge = Double.valueOf(pair.getSecond());
			}
		}
		return new Double(demandCharge);
	}
}