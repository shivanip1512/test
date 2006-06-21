/*
 * Created on Nov 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.data.lm;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.stars.util.SettlementConfigFuncs;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LMEventCustomer
{
	/** Place holder for the number of deviation periods allowed before violation, loaded from DB */
	private Integer deviationPeriods = null;
	/** Place holder for the Energy Reduction Incentive Rate, loaded from DB */
	private Double eriRate = null;
	
	/** The customerID for this LMEvent */
	private Integer customerID = null;	
	/** The customer Demand Level for customerID */
	private Double custDemandLevel = null;
	/** The customer curtailable load for customerID */
	private Double custCurtailLoad = null;
	/** Vector of LiteRawPointHisotory objects for this customer LMEvent*/
	private Vector liteRPHVector = new Vector();
	
	/** The number of "interval" (15minute) violations	 */
	private Integer numIntervalViolations = null;
	
	/** The maximum kW reading for event */
	private Double maxKW = null;

	/** ERI Credit for within acceptible violation count */
	private Double eriCredit = null;	
	
	/** ExcessFirmServiceLevelCharge*/
	private Double excessFirmServiceLevelCharge = null;


	/**
	 * 
	 * @param customerID_
	 * @param demandLevel
	 * @param curtailLoad
	 */
	public LMEventCustomer(Integer customerID_, Double demandLevel, Double curtailLoad)
	{
		customerID = customerID_;
		custDemandLevel = demandLevel;
		custCurtailLoad = curtailLoad;
	}
	
	/**
	 * Returns the number of deviation periods allowed before violation.  
	 * Loaded from DB, based on customerID (EnergyCompanyID for SettlementConfigs)
	 * @return
	 */
	private Integer getDeviationPeriods()
	{
		if( deviationPeriods == null)
		{
			int ecID = DaoFactory.getCustomerDao().getLiteCustomer(getCustomerID().intValue()).getEnergyCompanyID();
			LiteSettlementConfig lsc = SettlementConfigFuncs.getLiteSettlementConfig(ecID, YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO, SettlementConfig.HECO_ALLOWED_VIOLATIONS_STRING);
			if( lsc != null)
				deviationPeriods = Integer.valueOf(lsc.getFieldValue());
			else
				deviationPeriods = new Integer(5);	//default value?  This HOPEFULLY will never happen
		}
		return deviationPeriods;
	}
	/**
	 * Returns the Energy Reduction Incentive Rate.  
	 * Loaded from DB, based on customerID (EnergyCompanyID for SettlementConfigs)
	 * @return
	 */
	private Double getERIRate()
	{
		if( eriRate == null)
		{
			int ecID = DaoFactory.getCustomerDao().getLiteCustomer(getCustomerID().intValue()).getEnergyCompanyID();
			LiteSettlementConfig lsc = SettlementConfigFuncs.getLiteSettlementConfig(ecID, YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO, SettlementConfig.HECO_ERI_RATE_STRING);
			if( lsc != null)
				eriRate = Double.valueOf(lsc.getFieldValue());
			else
				eriRate= new Double(.25);	//default value?  This HOPEFULLY will never happen
		}
		return eriRate;
	}
	
	/**
	 * The number of intervals where the reading exceeded the (demandLevel (FSL) + 1)
	 * @return
	 */
	public Integer getNumIntervalViolations()
	{
		if( numIntervalViolations == null)
		{
			int violations = 0;
			for (int i = 0; i < getLiteRPHVector().size(); i++)
			{
				LiteRawPointHistory lRPH = (LiteRawPointHistory)getLiteRPHVector().get(i);

				if( getCustDemandLevel() != null)
				{
					if (lRPH.getValue() > getCustDemandLevel().doubleValue() + 1d)
						violations++;
				}
			}
			numIntervalViolations = new Integer(violations);
		}
		return numIntervalViolations;
	}
	
	/**
	 * The maximum interval value less the demand level (FSL)
	 * @return
	 */
	public Double getMaxKW()
	{
		if( maxKW == null)
		{
			double max = 0;
			for (int i = 0; i < getLiteRPHVector().size(); i++)
			{
				LiteRawPointHistory lRPH = (LiteRawPointHistory)getLiteRPHVector().get(i);
				if( getCustDemandLevel() != null)
				{
					if ((lRPH.getValue() - getCustDemandLevel().doubleValue()) > max)
						max = lRPH.getValue() - getCustDemandLevel().doubleValue();
				}
			}
			maxKW = new Double(max);
		}
		return maxKW;
	}
	
	/**
	 * The Energy Reduction Incentive (ERI) credit
	 * The $ value of the (eventDuration * curtailableLoad * ERIRate($/kwh)).
	 * Note:  Value is 0 if the number of interval violations exceeds the defined acceptible deviations amount
	 * @param duration
	 * @return
	 */
	public Double getERICredit(Double duration )
	{
		if( eriCredit == null)
		{
			if( getNumIntervalViolations().intValue() <= getDeviationPeriods().intValue())
			{
				if( getCustCurtailLoad() != null)
				{
					eriCredit = new Double(duration.doubleValue() * getCustCurtailLoad().doubleValue() * getERIRate().doubleValue());
				}
			}
			
			if (eriCredit == null)	//we were unsuccessful in loading
			{
				eriCredit = new Double(0);
				CTILogger.info("ERICredit defaulted to 0: Customer:" + getCustomerID().intValue() + 
								" NumViolations:" + getNumIntervalViolations().toString() + 
								" DevPeriods:" + getDeviationPeriods().toString() + " CurtailLoad:"+(getCustCurtailLoad()==null?"null":getCustCurtailLoad().toString()));
			}
		}
		return eriCredit;
	}
	
	/**
	 * The Excess Firm Service Level Charge (EFSLC)
	 * For events with maxKw > 0, the $ value of ( (-maxKw) * 2 * monthlyBillingDemandCharge)
	 * @return
	 */
	public Double getExcessFirmServiceLevelCharge()
	{
		if( excessFirmServiceLevelCharge == null)
		{
			if( getMaxKW().doubleValue() > 0)
			{
				int ecID = DaoFactory.getCustomerDao().getLiteCustomer(getCustomerID().intValue()).getEnergyCompanyID();
				Vector configs = SettlementConfigFuncs.getLiteSettlementConfigs(ecID, YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO, SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING);
				double demandCharge = 0;
				LiteCustomer liteCust = DaoFactory.getCustomerDao().getLiteCustomer(getCustomerID().intValue());
				for (int i = 0; i < configs.size(); i++)
				{
					LiteSettlementConfig lsc = (LiteSettlementConfig)configs.get(i);
					if(lsc.getRefEntryID() == liteCust.getRateScheduleID())
						demandCharge = Double.valueOf(lsc.getFieldValue()).doubleValue();
				}
							
				excessFirmServiceLevelCharge = new Double( -(getMaxKW().doubleValue()) * 2d * demandCharge);
			}
			else
			{
				excessFirmServiceLevelCharge = new Double(0);
				CTILogger.info("EFSL Charge defaulted to 0: Customer:" + getCustomerID().intValue() + 
								" MaxKW:" + getMaxKW().toString() + " DemandLevel:"+(getCustDemandLevel()==null?"null":getCustDemandLevel().toString()));
			}
		}
		return excessFirmServiceLevelCharge;
	}
	/**
	 * Returns a vector of LiteRawPointHistory values for this customer during the date range of this LMEvent.
	 * This vector is NOT loaded in LMEventCustomer, but should be loaded by another source (the tableModel perhaps).
	 * @return
	 */
	public Vector getLiteRPHVector()
	{
		return liteRPHVector;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String retStr = "CustomerID: " + getCustomerID() + 
			"\r\nViolations: " + getNumIntervalViolations() +
			"\r\nMax kW    : " + getMaxKW() +  
			"\r\n";

		return retStr;
	}

	/**
	 * Returns the customerID for this event
	 * @return
	 */
	public Integer getCustomerID()
	{
		return customerID;
	}
	
	/**
	 * Sets the Vector of LiteRawPointHistory values for the lmevent and customer.
	 * @param vector
	 */
	public void setLiteRPHVector(Vector vector)
	{
		liteRPHVector = vector;
	}

	/**
	 * Returns the customer curtailable load.
	 * @return
	 */
	public Double getCustCurtailLoad()
	{
		return custCurtailLoad;
	}

	/**
	 * Returns the customer Demand level.
	 * @return
	 */
	public Double getCustDemandLevel()
	{
		return custDemandLevel;
	}

}