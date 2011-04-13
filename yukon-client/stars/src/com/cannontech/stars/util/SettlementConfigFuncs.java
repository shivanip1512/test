/*
 * Created on Dec 19, 2005
 */
package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompanyFactory;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

/**
 * SettlementConfigFuncs - deals with lite settlementConfig objects
 * @author snebben
 */
public final class SettlementConfigFuncs {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
		
	/**
	 * Return the LiteSettlementConfig for the given configID
	 * @param configID
	 * @return
	 */
	public static LiteSettlementConfig getLiteSettlementConfig(int configID) {
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			Iterator iter = cache.getAllSettlementConfigs().iterator();
			while(iter.hasNext()) {
				LiteSettlementConfig lsc = (LiteSettlementConfig) iter.next();
				if(configID == lsc.getLiteID()) {
					return lsc;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a Vector of LiteSettlementConfig objects for the yukonDefID.
	 * The yukonDefID is the settlement "type" defined in Yukon.
	 * Valid types found in YukonListEntryTypes.
	 * @param yukonDefID
	 * @return
	 */
	public static List<LiteSettlementConfig> getAllLiteConfigsBySettlementType(int yukonDefID)
	{
		List<LiteSettlementConfig> liteConfigs = Lists.newArrayList();
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized(cache) {
				Iterator iter = cache.getAllSettlementConfigs().iterator();
				while(iter.hasNext()) {
					LiteSettlementConfig lsc = (LiteSettlementConfig) iter.next();
					if(lsc.getConfigID() >= 0  && yukonDefID == lsc.getYukonDefID())
						liteConfigs.add(lsc);
				}
				
				if( liteConfigs.isEmpty())
				{	//load default values if no custom ones exist yet.
					iter = cache.getAllSettlementConfigs().iterator();
					while(iter.hasNext()) {
						LiteSettlementConfig lsc = (LiteSettlementConfig) iter.next();
						if(lsc.getConfigID() < 0  && yukonDefID == lsc.getYukonDefID()) {
							liteConfigs.add(lsc);
						}
					}
				}
		}
		return liteConfigs;
	}
	
	
	/**
	 * Returns a Vector of LiteSettlementConfig objects for the entryID (the YukonListEntry).
	 * @param entryID_
	 * @return
	 */
	public static Vector getAllLiteConfigsByEntryID(int entryID_)
	{
		Vector liteConfigs = new Vector();
		if( entryID_ > 0)
		{
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
				synchronized(cache) {
					Iterator iter = cache.getAllSettlementConfigs().iterator();
					while(iter.hasNext()) {
						LiteSettlementConfig lsc = (LiteSettlementConfig) iter.next();
						if(lsc.getEntryID() == entryID_)
							liteConfigs.add(lsc);
					}
				}
		}
		return liteConfigs;
	}

	
	/**
	 * Returns a Vector of Integer rateScheduleID (YukDefID) objects for the yukonDefID.
	 * The yukonDefID is the settlement "type" defined in Yukon.
	 * Valid types found in YukonListEntryTypes.
	 * @param yukonDefID
	 * @return
	 */
	public static Vector getAllRateScheduleIDsSettlementType(int yukonDefID)
	{
		Vector liteConfigs = new Vector();
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized(cache) {
				Iterator iter = cache.getAllSettlementConfigs().iterator();
				while(iter.hasNext()) {
					LiteSettlementConfig lsc = (LiteSettlementConfig) iter.next();
					if(yukonDefID == lsc.getYukonDefID()) {
						liteConfigs.add(lsc);
				}
			}
		}
		return liteConfigs;
	}
	
	public static List<YukonListEntry> getAllAvailRateSchedules(LiteStarsEnergyCompany liteStarsEC, int yukonDefID)
	{
		List<YukonListEntry> rates = Lists.newArrayList();
		YukonSelectionList rateList = liteStarsEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE);
		List<LiteSettlementConfig> liteConfigs = getAllLiteConfigsBySettlementType(yukonDefID);
		for (int i = 0; i < rateList.getYukonListEntries().size(); i++)
		{
			boolean add = true;
			YukonListEntry rate_listEntry = (YukonListEntry) rateList.getYukonListEntries().get(i);
			LiteSettlementConfig lsc = null;
			
			for (int j = 0; j < liteConfigs.size(); j++)
			{
				lsc = (LiteSettlementConfig)liteConfigs.get(j);
				if( lsc.getEntryID() == rate_listEntry.getEntryID() && 
					lsc.getYukonDefID() != yukonDefID)
				{
					add = false;
					break;
				}
			}
			if( add )
				rates.add(rate_listEntry);
		}
		return rates;
	}
	
	@Deprecated
	public static boolean isEditableConfig(int configID, int entryID)
	{
		//YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO configs
		if( configID == SettlementConfig.HECO_RATE_DEMAND_CHARGE)
//			|| configID == SettlementConfig.HECO_RATE_PENALTY_CHARGE)
			return false;

		if( configID >= 0)
			return false;	//these are the custom table entries for rates assoc with a settlement 

		return true;
	}
	
	public static List<LiteSettlementConfig> getNonEditableConfigs(int energyCompanyId, int yukonDefId){
	    ArrayList<LiteSettlementConfig> nonEditableConfigs = Lists.newArrayList();
	    
	    LiteSettlementConfig demandChargeConfig = 
	        getLiteSettlementConfig(energyCompanyId, yukonDefId, SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING);
	    nonEditableConfigs.add(demandChargeConfig);

	    return nonEditableConfigs;
	}
	
	/**
	 * Returns vector of LiteSettlementConfig values
	 * @param yukDefID Settlement Type ID
	 * @param entryID YukonListEntryTypes (rate schedule ID)
	 * @return
	 */
	public static List<LiteSettlementConfig> getRateScheduleConfigs(int yukDefID, int rateEntryID)
	{
		List<LiteSettlementConfig> rateConfigs = Lists.newArrayList();
		
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		List configs = cache.getAllSettlementConfigs();
		for (int i = 0; i < configs.size(); i++)
		{
			LiteSettlementConfig liteConfig = (LiteSettlementConfig)configs.get(i);
			if( rateEntryID == liteConfig.getRefEntryID() &&
				liteConfig.getYukonDefID() == yukDefID)	//less than zero are Yukon defaults
			{
				rateConfigs.add(liteConfig);
			}
		}
		
		if( rateConfigs.isEmpty())	//fill it with defaults
		{
			if( yukDefID == YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO)
			{
				rateConfigs.add(DefaultDatabaseCache.getInstance().getAllSettlementConfigsMap().get(new Integer(SettlementConfig.HECO_RATE_DEMAND_CHARGE)));
			}
		}
		return rateConfigs;
	}

	public static String buildConfigHTML(LiteStarsEnergyCompany liteEC, int yukDefID)
	{
		List<LiteSettlementConfig> liteConfigs = SettlementConfigFuncs.getAllLiteConfigsBySettlementType(yukDefID);

		String html = "";
		
		html += "<tr height='30'>" + LINE_SEPARATOR;
		html += "  <td width='25%' align='right' class='TableCell'>Settlement:</td>" + LINE_SEPARATOR;
		html += "  <td colspan= '2' class='MainText' width='75%'>" + liteEC.getYukonListEntry(yukDefID).getEntryText()+ "</td>" + LINE_SEPARATOR;
		html += "  <input type='hidden' name='SettlementEntryID' value='" + liteEC.getYukonListEntry(yukDefID).getEntryID() +"'>" + LINE_SEPARATOR;		
		html += "</tr>" + LINE_SEPARATOR;
		html += "<tr align='center'>" + LINE_SEPARATOR;
		html += "  <td colspan='3' class='TableCell' height='2'><hr>" + LINE_SEPARATOR;
		html += "  </td>" + LINE_SEPARATOR;
		html += "</tr>" + LINE_SEPARATOR;
		
		for (int i = 0; i < liteConfigs.size(); i++)
		{
			LiteSettlementConfig lsc = (LiteSettlementConfig)liteConfigs.get(i);
			if( SettlementConfigFuncs.isEditableConfig(lsc.getConfigID(), lsc.getEntryID()) ||
				liteEC.getYukonListEntry(yukDefID).getEntryID()== lsc.getEntryID() && lsc.getRefEntryID() == 0) 
			{
				html += "<tr class='TableCell'>" + LINE_SEPARATOR;
				html += "  <td width='25%' align='right'>"+ lsc.getFieldName() + ":&nbsp;" + LINE_SEPARATOR;
				html += "  </td>" + LINE_SEPARATOR;
				html += "  <td width='25%'>" + LINE_SEPARATOR; 
				html += "    <input type='text' name='ConfigID" + lsc.getConfigID() +"' size='15' value='" + lsc.getFieldValue()+ "'>" + LINE_SEPARATOR;
				html += "  </td>" + LINE_SEPARATOR;
				html += "  <td width='50%' align='left'>&nbsp;"+ lsc.getDescription() + LINE_SEPARATOR;
				html += "  </td>" + LINE_SEPARATOR;
				html += "</tr>" + LINE_SEPARATOR;
			}
		}
		
		html += "<tr align='center'>" + LINE_SEPARATOR;
		html += "  <td colspan='3' class='TableCell' height='2'><hr></td>" + LINE_SEPARATOR;
		html += "</tr>" + LINE_SEPARATOR;
		
		List<YukonListEntry> availRates = SettlementConfigFuncs.getAllAvailRateSchedules(liteEC, yukDefID);
		for (int i = 0; i < availRates.size(); i++)
		{
			YukonListEntry entry = (YukonListEntry) availRates.get(i);
			List<LiteSettlementConfig> rateConfigs = SettlementConfigFuncs.getRateScheduleConfigs(yukDefID, entry.getEntryID());
			if( !rateConfigs.isEmpty())
			{
				LiteSettlementConfig rateConfig = (LiteSettlementConfig)rateConfigs.get(0);	//use the first one for checking if the rate is 'checked'
				boolean enableRate = rateConfig.getRefEntryID() > 0;
				
				html += "<tr class='TableCell'>" + LINE_SEPARATOR;
				html += "  <td width='25%'></td>" + LINE_SEPARATOR;
				html += "  <td colspan='2' width='25%' align='left'>" + LINE_SEPARATOR;
				html += "    <input type='checkbox' name='EntryID" + entry.getEntryID() +"' value='" + entry.getEntryID()+ "' " +
							" " + (enableRate ? "checked ":" ") +  
							" onclick='document.getElementById(this.value).disabled=!this.checked;'>" + entry.getEntryText() + LINE_SEPARATOR;
				html += "  </td>" + LINE_SEPARATOR;
				html += "</tr>" + LINE_SEPARATOR;
	
				html += "<tr id='" + entry.getEntryID() +"' " + (enableRate?"":"disabled") + ">" + LINE_SEPARATOR;
				html += "  <td colspan='3'>" + LINE_SEPARATOR;
				html += "    <table width='100%' border='0' cellspacing='0' cellpadding='0'>" + LINE_SEPARATOR;
				
				for ( int j = 0; j < rateConfigs.size(); j++)
				{
					rateConfig = (LiteSettlementConfig)rateConfigs.get(j);
					html += "<tr class='TableCell'>" + LINE_SEPARATOR;			
					html += "  <td width='25%' align='right'>" + rateConfig.getFieldName()+ ":&nbsp;</td>" + LINE_SEPARATOR;
					html += "  <td width='25%' align='left'>" + LINE_SEPARATOR;
					html += "    <input type='text' name='EntryID" + entry.getEntryID() + "RateID" +rateConfig.getConfigID()+ "' size='15' value='" + (rateConfig.getConfigID() < 0?"0": rateConfig.getFieldValue()) + "' ><br>" + LINE_SEPARATOR;
					html += "  </td>" + LINE_SEPARATOR;
					html += "  <td width='50%' align='left'>" + rateConfig.getDescription() + "</td>" + LINE_SEPARATOR;
					html += "</tr>" + LINE_SEPARATOR;
				}
				html += "    </table><BR>" + LINE_SEPARATOR;
				html += "  </td>" + LINE_SEPARATOR;
				html += "</tr>" + LINE_SEPARATOR;
			}			
		}
		return html;
	}
	
	/**
	 * Returns the String value of yukDefID (from YukonListEntryTypes)
	 * @param yukDefID
	 * @return
	 */
	public static String getCTISettlementStr(int yukDefID) 
	{
		switch (yukDefID)
		{
			case YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO:
				return "HECO";
		}
		return "Unknown";
	}
	
	/**
	 * Retrieves the DBPers SettlementConfig based on LiteSettlementConfig lsc.
	 * Updates the DBPers SettlementConfig FieldValue with value.
	 * @param configID
	 * @param value
	 */
	public static void updateSettlementConfigTrx(LiteSettlementConfig lsc, String value)
	{
	    DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
	    
		try
		{
			SettlementConfig sConf = (SettlementConfig)LiteFactory.convertLiteToDBPers(lsc);
			//Need to retrieve all the fields of the Fatty since the Lite doesn't fully load it. 
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, sConf);
			sConf = (SettlementConfig)t.execute();
				
			sConf.setFieldValue(value);
			dbPersistentDao.performDBChange(sConf,TransactionType.UPDATE);

		}
		catch (TransactionException e)
		{
			e.printStackTrace();
		}
	}
	
	public static LiteSettlementConfig getLiteSettlementConfig( int energyCompanyID, int yukonDefID, String fieldName)
	{
	    LiteStarsEnergyCompanyFactory factory = 
            YukonSpringHook.getBean("liteStarsEnergyCompanyFactory", LiteStarsEnergyCompanyFactory.class);
		LiteStarsEnergyCompany lsec = factory.createEnergyCompany(energyCompanyID);
//		int entryID = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO).getEntryID();	//2260
		int entryID = lsec.getYukonListEntry(yukonDefID).getEntryID();	//2260
		Vector settlementConfigs = SettlementConfigFuncs.getAllLiteConfigsByEntryID(entryID);
		for (int i = 0; i < settlementConfigs.size(); i++)
		{
			LiteSettlementConfig lsc = (LiteSettlementConfig)settlementConfigs.get(i);
			if (lsc.getFieldName().equals(fieldName))
			{
				return lsc;
			}
		}
		return null;
	}
	
	public static Vector getLiteSettlementConfigs( int energyCompanyID, int yukonDefID, String fieldName)
	{
		Vector settleConfigs = new Vector();
		
		LiteStarsEnergyCompanyFactory factory = 
            YukonSpringHook.getBean("liteStarsEnergyCompanyFactory", LiteStarsEnergyCompanyFactory.class);
		LiteStarsEnergyCompany lsec = factory.createEnergyCompany(energyCompanyID);
//			int entryID = lsec.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO).getEntryID();	//2260
		int entryID = lsec.getYukonListEntry(yukonDefID).getEntryID();	//2260
		Vector settlementConfigs = SettlementConfigFuncs.getAllLiteConfigsByEntryID(entryID);
		for (int i = 0; i < settlementConfigs.size(); i++)
		{
			LiteSettlementConfig lsc = (LiteSettlementConfig)settlementConfigs.get(i);
			if (lsc.getFieldName().equals(fieldName))
			{
				settleConfigs.add(lsc);
			}
		}
		return settleConfigs;
	}
	public static int getConfigValue(int ecID, int yukonDefID, String fieldName)
	{
		LiteSettlementConfig lsc = SettlementConfigFuncs.getLiteSettlementConfig(ecID, 
																	YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO, 
																	fieldName);

		return Integer.valueOf(lsc.getFieldValue()).intValue();
	}	
}
