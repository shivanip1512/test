/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Hashtable;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.WebClientException;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadCustAccountsTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
	
	int numAcctTotal = 0;
	int numAcctLoaded = 0;
	
	public LoadCustAccountsTask(LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (status == STATUS_RUNNING) {
			if (numAcctTotal > 0)
				return numAcctLoaded + " of " + numAcctTotal + " customer accounts loaded";
			else
				return "Preparing for loading customer accounts...";
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (energyCompany == null) {
			status = STATUS_ERROR;
			errorMsg = "Energy company cannot be null";
			return;
		}
		
		status = STATUS_RUNNING;
		
		String sql = "SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber,ac.CustomerID, ac.BillingAddressID, ac.AccountNotes," +
			" ba.LocationAddress1 as BAddr1, ba.LocationAddress2 as BAddr2, ba.CityName as BCity, ba.StateCode as BState, ba.ZipCode as BZip, ba.County as BCounty," +
			" acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes," +
			" sa.LocationAddress1 as SAddr1, sa.LocationAddress2 as SAddr2, sa.CityName as SCity, sa.StateCode as SState, sa.ZipCode as SZip, sa.County as SCounty," +
			" si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, sub.SubstationName, sub.RouteID" +
			" FROM CustomerAccount ac, Address ba, AccountSite acs, Address sa, SiteInformation si, Substation sub, ECToAccountMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() + " AND map.AccountID = ac.AccountID" +
			" AND ac.BillingAddressID = ba.AddressID AND ac.AccountSiteID = acs.AccountSiteID AND acs.StreetAddressID = sa.AddressID" +
			" AND acs.SiteInformationID = si.SiteID AND si.SubstationID = sub.SubstationID";
		
		String sql2 = "SELECT app.ApplianceID, app.AccountID FROM ApplianceBase app, ECToAccountMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() + " AND map.AccountID = app.AccountID";
		
		String sql3 = "SELECT inv.InventoryID, inv.AccountID FROM InventoryBase inv, ECToAccountMapping map" +
			" WHERE map.EnergyCompanyID = " + energyCompany.getEnergyCompanyID() + " AND map.AccountID = inv.AccountID";
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			if (conn == null) {
				CTILogger.error( getClass() + ": failed to get database connection" );
				return;
			}
			
			stmt = conn.createStatement();
			java.sql.ResultSet rset = stmt.executeQuery( sql2 );
			
			Hashtable acctAppIDMap = new Hashtable();
			while (rset.next()) {
				Integer appID = new Integer( rset.getInt(1) );
				Integer acctID = new Integer( rset.getInt(2) );
				
				ArrayList appIDs = (ArrayList) acctAppIDMap.get( acctID );
				if (appIDs == null) {
					appIDs = new ArrayList();
					acctAppIDMap.put( acctID, appIDs );
				}
				appIDs.add( appID );
			}
			
			rset.close();
			rset = stmt.executeQuery( sql3 );
			
			Hashtable acctInvIDMap = new Hashtable();
			while (rset.next()) {
				Integer invID = new Integer( rset.getInt(1) );
				Integer acctID = new Integer( rset.getInt(2) );
				
				ArrayList invIDs = (ArrayList) acctInvIDMap.get( acctID );
				if (invIDs == null) {
					invIDs = new ArrayList();
					acctInvIDMap.put( acctID, invIDs );
				}
				invIDs.add( invID );
			}
			
			rset.close();
			rset = stmt.executeQuery( sql );
			numAcctTotal = rset.getMetaData().getColumnCount();
			
			while (rset.next()) {
				loadCustomerAccount(rset, acctAppIDMap, acctInvIDMap);
				numAcctLoaded++;
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
			
			energyCompany.setAccountsLoaded( true );
			status = STATUS_FINISHED;
			
			CTILogger.info( "All customer accounts loaded for energy company #" + energyCompany.getEnergyCompanyID() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			status = STATUS_ERROR;
			
			if (e instanceof WebClientException)
				errorMsg = e.getMessage();
			else
				errorMsg = "Failed to load customer accounts";
		}
		finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	private void loadCustomerAccount(java.sql.ResultSet rset, Hashtable acctAppIDMap, Hashtable acctInvIDMap)
		throws java.sql.SQLException
	{
		int accountID = rset.getInt("AccountID");
		
		if (energyCompany.getBriefCustAccountInfo(accountID, false) != null)
			return;	// customer account already loaded
		
		LiteStarsCustAccountInformation liteAcctInfo = new LiteStarsCustAccountInformation( accountID );
		
		LiteCustomerAccount liteAccount = new LiteCustomerAccount();
		liteAccount.setAccountID( accountID );
		liteAccount.setAccountSiteID( rset.getInt("AccountSiteID") );
		liteAccount.setAccountNumber( rset.getString("AccountNumber") );
		liteAccount.setCustomerID( rset.getInt("CustomerID") );
		liteAccount.setBillingAddressID( rset.getInt("BillingAddressID") );
		liteAccount.setAccountNotes( rset.getString("AccountNotes") );
		liteAcctInfo.setCustomerAccount( liteAccount );
		
		LiteAccountSite liteAcctSite = new LiteAccountSite();
		liteAcctSite.setAccountSiteID( liteAccount.getAccountSiteID() );
		liteAcctSite.setSiteInformationID( rset.getInt("SiteInformationID") );
		liteAcctSite.setSiteNumber( rset.getString("SiteNumber") );
		liteAcctSite.setStreetAddressID( rset.getInt("StreetAddressID") );
		liteAcctSite.setPropertyNotes( rset.getString("PropertyNotes") );
		liteAcctInfo.setAccountSite( liteAcctSite );
		
		LiteSiteInformation liteSiteInfo = new LiteSiteInformation();
		liteSiteInfo.setSiteID( liteAcctSite.getSiteInformationID() );
		liteSiteInfo.setFeeder( rset.getString("Feeder") );
		liteSiteInfo.setPole( rset.getString("Pole") );
		liteSiteInfo.setTransformerSize( rset.getString("TransformerSize") );
		liteSiteInfo.setServiceVoltage( rset.getString("ServiceVoltage") );
		liteSiteInfo.setSubstationID( rset.getInt("SubstationID") );
		liteAcctInfo.setSiteInformation( liteSiteInfo );
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized (cache) {
			liteAcctInfo.setCustomer( (LiteCustomer)cache.getAllCustomersMap().get(new Integer(liteAccount.getCustomerID())) );
		}
        
        ArrayList appIDs = (ArrayList) acctAppIDMap.get( new Integer(accountID) );
        if (appIDs != null) {
			ArrayList appliances = new ArrayList();
			for (int i = 0; i < appIDs.size(); i++) {
				LiteStarsAppliance liteApp = new LiteStarsAppliance();
				liteApp.setApplianceID( ((Integer)appIDs.get(i)).intValue() );
				appliances.add( liteApp );
			}
			liteAcctInfo.setAppliances( appliances );
        }
        
        ArrayList invIDs = (ArrayList) acctInvIDMap.get( new Integer(accountID) );
        if (invIDs != null)
        	liteAcctInfo.setInventories( invIDs );
        
        LiteAddress liteSAddr = new LiteAddress();
		liteSAddr.setAddressID( liteAcctSite.getStreetAddressID() );
		liteSAddr.setLocationAddress1( rset.getString("SAddr1") );
		liteSAddr.setLocationAddress2( rset.getString("SAddr2") );
		liteSAddr.setCityName( rset.getString("SCity") );
		liteSAddr.setStateCode( rset.getString("SState") );
		liteSAddr.setZipCode( rset.getString("SZip") );
		liteSAddr.setCounty( rset.getString("SCounty") );
		energyCompany.addAddress( liteSAddr );
        
		LiteAddress liteBAddr = new LiteAddress();
		liteBAddr.setAddressID( liteAccount.getBillingAddressID() );
		liteBAddr.setLocationAddress1( rset.getString("BAddr1") );
		liteBAddr.setLocationAddress2( rset.getString("BAddr2") );
		liteBAddr.setCityName( rset.getString("BCity") );
		liteBAddr.setStateCode( rset.getString("BState") );
		liteBAddr.setZipCode( rset.getString("BZip") );
		liteBAddr.setCounty( rset.getString("BCounty") );
		energyCompany.addAddress( liteBAddr );
		
		energyCompany.addCustAccountInformation( liteAcctInfo );
	}

}
