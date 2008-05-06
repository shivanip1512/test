/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteAccountSite;
import com.cannontech.database.data.lite.stars.LiteCustomerAccount;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoadCustAccountsTask extends TimeConsumingTask {
    private static final JdbcTemplate jdbcTemplate;
    private static final PlatformTransactionManager transactionManager;
    private static final String selectSql;
    private static final String selectSql2;
    private static final String selectSql3;
	private final LiteStarsEnergyCompany energyCompany;
    private final int energyCompanyId;
	private int numAcctLoaded;
    private Map<Integer,List<Integer>> acctAppIDMap;
    private Map<Integer,List<Integer>> acctInvIDMap;
	
    static {
        
        jdbcTemplate = new JdbcTemplate(PoolManager.getYukonDataSource());
        
        transactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        
        selectSql = "SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber,ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, " +
                    "ba.LocationAddress1 as BAddr1, ba.LocationAddress2 as BAddr2, ba.CityName as BCity, ba.StateCode as BState, ba.ZipCode as BZip, ba.County as BCounty, " +
                    "acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, " +
                    "sa.LocationAddress1 as SAddr1, sa.LocationAddress2 as SAddr2, sa.CityName as SCity, sa.StateCode as SState, sa.ZipCode as SZip, sa.County as SCounty, " +
                    "si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID, sub.SubstationName, sub.LMRouteID " +
                    "FROM CustomerAccount ac, Address ba, AccountSite acs, Address sa, SiteInformation si, Substation sub, ECToAccountMapping map " +
                    "WHERE map.EnergyCompanyID = ? AND map.AccountID = ac.AccountID " +
                    "AND ac.BillingAddressID = ba.AddressID AND ac.AccountSiteID = acs.AccountSiteID AND acs.StreetAddressID = sa.AddressID " +
                    "AND acs.SiteInformationID = si.SiteID AND si.SubstationID = sub.SubstationID";
        
        selectSql2 = "SELECT totals.total,app.ApplianceID,app.AccountID " + 
                     "FROM ApplianceBase app,ECToAccountMapping map, " +
                         "(SELECT COUNT(*) as total FROM ApplianceBase app,ECToAccountMapping map WHERE map.EnergyCompanyID = ? AND map.AccountID = app.AccountID) totals " +
                     "WHERE map.EnergyCompanyID = ? " + 
                     "AND map.AccountID = app.AccountID";
        
        selectSql3 = "SELECT totals.total,inv.InventoryID,inv.AccountID " +
                     "FROM InventoryBase inv, ECToAccountMapping map, " +
                         "(SELECT COUNT(*) as total FROM InventoryBase inv,ECToAccountMapping map WHERE map.EnergyCompanyID = ? AND map.AccountID = inv.AccountID) totals " +
                     "WHERE map.EnergyCompanyID = ? " +
                     "AND map.AccountID = inv.AccountID";
                         
    }
    
	public LoadCustAccountsTask(final LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
        this.energyCompanyId = energyCompany.getEnergyCompanyID();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
    @Override
	public String getProgressMsg() {
        if (status == STATUS_RUNNING) {
            if (numAcctLoaded > 0) {
                return numAcctLoaded + " customer accounts loaded";
            }    
            return "Preparing for loading customer accounts...";
        }
		return null;
	}

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        template.setReadOnly(true);
        template.execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                doAction();
                return null;
            }
        });
    }
	
	private void doAction() {
	    if (energyCompany == null) {
	        status = STATUS_ERROR;
	        errorMsg = "Energy company cannot be null";
	        return;
	    }

	    status = STATUS_RUNNING;

	    try {
	        jdbcTemplate.query(selectSql2,
	                           new Object[]{energyCompanyId, energyCompanyId},
	                           new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                if (acctAppIDMap == null) {
	                    int rows = rs.getInt("total");
	                    int initialCap = (int) (rows / 0.75f);
	                    acctAppIDMap = new HashMap<Integer,List<Integer>>(initialCap);
	                }

	                final Integer applianceId = rs.getInt("ApplianceID");
	                final Integer accountId = rs.getInt("AccountID");

	                List<Integer> appIdList = acctAppIDMap.get(accountId);
	                if (appIdList == null) {
	                    appIdList = new ArrayList<Integer>();
	                    acctAppIDMap.put(accountId, appIdList);
	                }
	                appIdList.add(applianceId);
	            }
	        });

	        jdbcTemplate.query(selectSql3,
	                           new Object[]{energyCompanyId, energyCompanyId},
	                           new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                if (acctInvIDMap == null) {
	                    int rows = rs.getInt("total");
	                    int initialCap = (int) (rows / 0.75f);
	                    acctInvIDMap = new HashMap<Integer,List<Integer>>(initialCap);
	                }

	                final Integer inventoryId = rs.getInt("InventoryID");
	                final Integer accountId = rs.getInt("AccountID");

	                List<Integer> inventoryIdList = acctInvIDMap.get(accountId);
	                if (inventoryIdList == null) {
	                    inventoryIdList = new ArrayList<Integer>();
	                    acctInvIDMap.put(accountId, inventoryIdList);
	                }
	                inventoryIdList.add(inventoryId);
	            }
	        });

	        jdbcTemplate.query(selectSql,
	                           new Object[]{energyCompanyId},
	                           new RowCallbackHandler() {
	            public void processRow(ResultSet rs) throws SQLException {
	                if (isCanceled) {
	                    status = STATUS_CANCELED;
	                    return;
	                }

	                loadCustomerAccount(rs);
	                numAcctLoaded++;
	            }
	        });

	        energyCompany.setAccountsLoaded( true );
	        energyCompany.getEnergyCompanyLatch().countDownAccounts();
	        status = STATUS_FINISHED;
	        CTILogger.info( "All customer accounts loaded for energy company #" + energyCompany.getEnergyCompanyID() );
	    } catch (Exception e) {
	        CTILogger.error(e.getMessage(), e);
	        status = STATUS_ERROR;

	        if (e instanceof WebClientException)
	            errorMsg = e.getMessage();
	        else
	            errorMsg = "Failed to load customer accounts";
	    }
	}
	
	private void loadCustomerAccount(final ResultSet rs) throws SQLException {
		int accountID = rs.getInt("AccountID");
		
		if (energyCompany.getBriefCustAccountInfo(accountID, false) != null)
			return;	// customer account already loaded
		
		final LiteStarsCustAccountInformation liteAcctInfo = new LiteStarsCustAccountInformation( accountID );
		
		final LiteCustomerAccount liteAccount = new LiteCustomerAccount();
		liteAccount.setAccountID( accountID );
		liteAccount.setAccountSiteID( rs.getInt("AccountSiteID") );
		liteAccount.setAccountNumber( rs.getString("AccountNumber") );
		liteAccount.setCustomerID( rs.getInt("CustomerID") );
		liteAccount.setBillingAddressID( rs.getInt("BillingAddressID") );
		liteAccount.setAccountNotes( rs.getString("AccountNotes") );
		liteAcctInfo.setCustomerAccount( liteAccount );
		
		final LiteAccountSite liteAcctSite = new LiteAccountSite();
		liteAcctSite.setAccountSiteID( liteAccount.getAccountSiteID() );
		liteAcctSite.setSiteInformationID( rs.getInt("SiteInformationID") );
		liteAcctSite.setSiteNumber( rs.getString("SiteNumber") );
		liteAcctSite.setStreetAddressID( rs.getInt("StreetAddressID") );
		liteAcctSite.setPropertyNotes( rs.getString("PropertyNotes") );
        liteAcctSite.setCustAtHome(rs.getString("CustAtHome"));
		liteAcctInfo.setAccountSite( liteAcctSite );
		
		final LiteSiteInformation liteSiteInfo = new LiteSiteInformation();
		liteSiteInfo.setSiteID( liteAcctSite.getSiteInformationID() );
		liteSiteInfo.setFeeder( rs.getString("Feeder") );
		liteSiteInfo.setPole( rs.getString("Pole") );
		liteSiteInfo.setTransformerSize( rs.getString("TransformerSize") );
		liteSiteInfo.setServiceVoltage( rs.getString("ServiceVoltage") );
		liteSiteInfo.setSubstationID( rs.getInt("SubstationID") );
		liteAcctInfo.setSiteInformation( liteSiteInfo );
		
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized (cache) {
			liteAcctInfo.setCustomer(cache.getACustomerByCustomerID(liteAccount.getCustomerID()));
		}
        
		if (acctAppIDMap != null) {
		    final List<Integer> appIDs = acctAppIDMap.get(accountID);
		    if (appIDs != null) {
		        final List<LiteStarsAppliance> appliances = new ArrayList<LiteStarsAppliance>();
		        for (final Integer applianceId : appIDs) {
		            final LiteStarsAppliance liteApp = new LiteStarsAppliance();
		            liteApp.setApplianceID(applianceId);
		            appliances.add(liteApp);
		        }
		        liteAcctInfo.setAppliances(appliances);
		    }
		}
        
		if (acctInvIDMap != null) {
		    final List<Integer> invIDs = acctInvIDMap.get(accountID);
		    if (invIDs != null) {
		        liteAcctInfo.setInventories( invIDs );
		    }
		}
        
		energyCompany.addCustAccountInformation(liteAcctInfo);
	}

}
