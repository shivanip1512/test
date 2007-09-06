package com.cannontech.database.data.lite.stars;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
import com.cannontech.database.db.stars.hardware.InventoryBase;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsCustAccountInformation extends LiteBase {

	private LiteCustomerAccount customerAccount = null;
	private LiteCustomer customer = null;
	private LiteAccountSite accountSite = null;
	private LiteSiteInformation siteInformation = null;
	private LiteCustomerResidence customerResidence = null;
	private List<LiteStarsLMProgram> programs = null;		// List of LiteStarsLMProgram
	private List<LiteStarsAppliance> appliances = null;	// List of LiteStarsAppliance
	private List<Integer> inventories = null;	// List of IDs of LiteInventoryBase
	private List<LiteLMProgramEvent> programHistory = null;	// List of LiteLMProgramEvent
	private List<StarsCallReport> callReportHistory = null;	// List of StarsCallReport
	private List<Integer> serviceRequestHistory = null;	// List of IDs of LiteWorkOrderBase
	private List<LiteLMThermostatSchedule> thermostatSchedules = null;	// List of LiteLMThermostatSchedule
	
	private boolean extended = false;
	
	public LiteStarsCustAccountInformation() {
		super();
		setLiteType( LiteTypes.STARS_CUST_ACCOUNT_INFO );
	}
	
	public LiteStarsCustAccountInformation(int accountID) {
		super();
		setAccountID( accountID );
		setLiteType( LiteTypes.STARS_CUST_ACCOUNT_INFO );
	}
	
	public int getAccountID() {
		return getLiteID();
	}
	
	public void setAccountID(int accountID) {
		setLiteID( accountID );
	}
	
	public boolean hasTwoWayThermostat(LiteStarsEnergyCompany energyCompany) {
		for (int i = 0; i < getInventories().size(); i++) {
			int invID = getInventories().get(i).intValue();
			LiteInventoryBase liteInv = energyCompany.getInventory( invID, true );
			
			if (liteInv instanceof LiteStarsLMHardware &&
				((LiteStarsLMHardware) liteInv).isTwoWayThermostat())
				return true;
		}
		
		return false;
	}
    
    public static String getCompanyName(int customerID)
    {
        return CICustomerBase.getCompanyNameFromDB(customerID);
    }
	
	/**
	 * Returns the appliances.
	 * @return ArrayList
	 */
	public List<LiteStarsAppliance> getAppliances() {
		if (appliances == null)
        {
            if( getCustomerAccount() != null)   //Must already have at least the base objects loaded
            {
                Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
                try {
                    Vector<Integer> applianceIDS = ApplianceBase.getApplianceIDs( new Integer(getAccountID()), conn );
                    appliances = new ArrayList<LiteStarsAppliance>(applianceIDS.size());
                    for (int i = 0; i < applianceIDS.size(); i++)
                    {
                        LiteStarsAppliance liteStarsApp = new LiteStarsAppliance(applianceIDS.get(i).intValue());
                        appliances.add(liteStarsApp);
                    }
                }
                catch( java.sql.SQLException e ) {
                    CTILogger.error( e.getMessage(), e );
                }
                finally {
                    try {
                        if (conn != null) conn.close();
                    }
                    catch (java.sql.SQLException e) {}
                }
            }
        }
		return appliances;
	}

	/**
	 * Returns the callReportHistory.
	 * @return ArrayList
	 */
	public List<StarsCallReport> getCallReportHistory() {
		if (callReportHistory == null)
			callReportHistory = new ArrayList<StarsCallReport>();
		return callReportHistory;
	}

	/**
	 * Returns the customerAccount.
	 * @return LiteStarsCustomerAccount
	 */
	public LiteCustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * Returns the inventorys.
	 * @return ArrayList
	 */
	public List<Integer> getInventories() {
		if (inventories == null)
        {
            if( getCustomerAccount() != null)   //Must already have at least the base objects loaded
            {
                Connection conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
                try {
                    Vector<Integer> inventoryIDS = InventoryBase.getInventoryIDs(new Integer(getAccountID()), conn);
                    inventories = new ArrayList<Integer>(inventoryIDS.size());
                    for (int i = 0; i < inventoryIDS.size(); i++)
                        inventories.add(inventoryIDS.get(i));
                }
                catch( java.sql.SQLException e ) {
                    CTILogger.error( e.getMessage(), e );
                }
                finally {
                    try {
                        if (conn != null) conn.close();
                    }
                    catch (java.sql.SQLException e) {}
                }
            }
        }
		return inventories;
	}

	/**
	 * Returns the programs.
	 * @return ArrayList
	 */
	public List<LiteStarsLMProgram> getPrograms() {
		if (programs == null)
			programs = new ArrayList<LiteStarsLMProgram>();
		return programs;
	}

	/**
	 * Returns the serviceRequestHistory.
	 * @return ArrayList
	 */
	public List<Integer> getServiceRequestHistory() {
		if (serviceRequestHistory == null)
			serviceRequestHistory = new ArrayList<Integer>();
		return serviceRequestHistory;
	}

	/**
	 * Sets the appliances.
	 * @param appliances The appliances to set
	 */
	public void setAppliances(List<LiteStarsAppliance> appliances) {
		this.appliances = appliances;
	}

	/**
	 * Sets the callReportHistory.
	 * @param callReportHistory The callReportHistory to set
	 */
	public void setCallReportHistory(List<StarsCallReport> callReportHistory) {
		this.callReportHistory = callReportHistory;
	}

	/**
	 * Sets the customerAccount.
	 * @param customerAccount The customerAccount to set
	 */
	public void setCustomerAccount(LiteCustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	/**
	 * Sets the inventorys.
	 * @param inventorys The inventorys to set
	 */
	public void setInventories(List<Integer> inventories) {
		this.inventories = inventories;
	}

	/**
	 * Sets the programs.
	 * @param programs The programs to set
	 */
	public void setPrograms(List<LiteStarsLMProgram> programs) {
		this.programs = programs;
	}

	/**
	 * Sets the serviceRequestHistory.
	 * @param serviceRequestHistory The serviceRequestHistory to set
	 */
	public void setServiceRequestHistory(List<Integer> serviceRequestHistory) {
		this.serviceRequestHistory = serviceRequestHistory;
	}

	/**
	 * Returns the accountSite.
	 * @return LiteAccountSite
	 */
	public LiteAccountSite getAccountSite() {
		return accountSite;
	}

	/**
	 * Returns the customer.
	 * @return LiteCustomer
	 */
	public LiteCustomer getCustomer() {
		return customer;
	}

	/**
	 * Returns the siteInformation.
	 * @return LiteSiteInformation
	 */
	public LiteSiteInformation getSiteInformation() {
		return siteInformation;
	}

	/**
	 * Sets the accountSite.
	 * @param accountSite The accountSite to set
	 */
	public void setAccountSite(LiteAccountSite accountSite) {
		this.accountSite = accountSite;
	}

	/**
	 * Sets the customerBase.
	 * @param customerBase The customerBase to set
	 */
	public void setCustomer(LiteCustomer customer) {
		this.customer = customer;
	}

	/**
	 * Sets the siteInformation.
	 * @param siteInformation The siteInformation to set
	 */
	public void setSiteInformation(LiteSiteInformation siteInformation) {
		this.siteInformation = siteInformation;
	}

	/**
	 * Returns the customerResidence.
	 * @return LiteCustomerResidence
	 */
	public LiteCustomerResidence getCustomerResidence() {
		return customerResidence;
	}

	/**
	 * Sets the customerResidence.
	 * @param customerResidence The customerResidence to set
	 */
	public void setCustomerResidence(LiteCustomerResidence customerResidence) {
		this.customerResidence = customerResidence;
	}

	/**
	 * Returns the extended.
	 * @return boolean
	 */
	public boolean isExtended() {
		return extended;
	}

	/**
	 * Sets the extended.
	 * @param extended The extended to set
	 */
	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	/**
	 * @return
	 */
	public List<LiteLMProgramEvent> getProgramHistory() {
		if (programHistory == null)
			programHistory = new ArrayList<LiteLMProgramEvent>();
		return programHistory;
	}

	/**
	 * @param list
	 */
	public void setProgramHistory(List<LiteLMProgramEvent> list) {
		programHistory = list;
	}

	/**
	 * @return
	 */
	public List<LiteLMThermostatSchedule> getThermostatSchedules() {
		if (thermostatSchedules == null)
			thermostatSchedules = new ArrayList<LiteLMThermostatSchedule>();
		return thermostatSchedules;
	}

	/**
	 * @param list
	 */
	public void setThermostatSchedules(ArrayList<LiteLMThermostatSchedule> list) {
		thermostatSchedules = list;
	}
    
    public void retrieveLiteStarsCustAccountInfo() throws java.sql.SQLException {

        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        java.sql.ResultSet rset = null;
        
        try {
            conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
            
            String sql = "SELECT ac.AccountID, ac.AccountSiteID, ac.AccountNumber, ac.CustomerID, ac.BillingAddressID, ac.AccountNotes, " + //1-6 
                        " acs.SiteInformationID, acs.SiteNumber, acs.StreetAddressID, acs.PropertyNotes, acs.CustAtHome, acs.CustomerStatus, " + //7-12
                        " si.Feeder, si.Pole, si.TransformerSize, si.ServiceVoltage, si.SubstationID " + //13-17
//                        " sub.SubstationName, sub.RouteID, " + //18-19
//                        " ba.LocationAddress1, ba.LocationAddress2, ba.CityName, ba.StateCode, ba.ZipCode, ba.County, " + //20-25
//                        " sa.LocationAddress1, sa.LocationAddress2, sa.CityName, sa.StateCode, sa.ZipCode, sa.County, " + //26-31                        
//                        " c.primarycontactid, c.customertypeid, c.timezone, c.customernumber, c.ratescheduleid, c.alttracknum, c.temperatureunit, " + //32-38 
//                        " cont.contfirstname, cont.contlastname, cont.loginid, cont.addressid " + //39-42
                        " FROM CustomerAccount ac, AccountSite acs, SiteInformation si " +
//                        " , Substation sub customer c, contact cont, Address ba, Address sa, ECToAccountMapping map " +
                        " WHERE ac.accountid = ? " +
                        " AND ac.AccountSiteID = acs.AccountSiteID " + 
                        " AND acs.SiteInformationID = si.SiteID";
//                        " AND si.SubstationID = sub.SubstationID ";
//                        " AND map.AccountID = ac.AccountID " + 
//                        " and ac.customerid = c.customerid " + 
//                        " and c.primarycontactid = cont.contactid " + 
//                        " AND ac.BillingAddressID = ba.AddressID " +
//                        " AND acs.StreetAddressID = sa.AddressID " +  

            
            stmt = conn.prepareStatement(sql);
            stmt.setInt( 1, getAccountID() );
            rset = stmt.executeQuery();
            
            while (rset.next())
            {
                customerAccount = new LiteCustomerAccount();
                customerAccount.setAccountID( getAccountID() );
                customerAccount.setAccountSiteID( rset.getInt(2));
                customerAccount.setAccountNumber( rset.getString(3));
                customerAccount.setCustomerID( rset.getInt(4) );
                customerAccount.setBillingAddressID( rset.getInt(5));
                customerAccount.setAccountNotes( rset.getString(6) );

                accountSite = new LiteAccountSite();
                accountSite.setAccountSiteID( customerAccount.getAccountSiteID());
                accountSite.setSiteInformationID( rset.getInt(7) );
                accountSite.setSiteNumber( rset.getString(8) );
                accountSite.setStreetAddressID( rset.getInt(9) );
                accountSite.setPropertyNotes( rset.getString(10));
                accountSite.setCustAtHome( rset.getString(11));
                accountSite.setCustStatus( rset.getString(12) );

                siteInformation = new LiteSiteInformation();
                siteInformation.setSiteID( accountSite.getAccountSiteID() );
                siteInformation.setFeeder( rset.getString(13) );
                siteInformation.setPole( rset.getString(14));
                siteInformation.setTransformerSize( rset.getString(15) );
                siteInformation.setServiceVoltage( rset.getString(16) );
                siteInformation.setSubstationID( rset.getInt(17) );
             
                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    customer =  ( (LiteCustomer)cache.getACustomerByCustomerID(customerAccount.getCustomerID()) );
                }
            }
        }
        catch( java.sql.SQLException e ) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }
    }
}
