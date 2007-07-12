package com.cannontech.database.data.lite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.customer.Customer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteCustomer extends LiteBase {
	
	private int primaryContactID = 0;
	private int customerTypeID = com.cannontech.database.data.customer.CustomerTypes.INVALID;
	private String timeZone = null;
	private String customerNumber = CtiUtilities.STRING_NONE;
	private int rateScheduleID = CtiUtilities.NONE_ZERO_ID;
	private String altTrackNum = CtiUtilities.STRING_NONE;
    private String temperatureUnit = CtiUtilities.FAHRENHEIT_CHARACTER;
//	private LiteContact liteContact = null;
    
    private boolean extended = false;
	//non-persistent data, 
	//contains com.cannontech.database.data.lite.LiteContact
	private Vector<LiteContact> additionalContacts = null;
	
	// contains int ,Used for residential customers only
	private Vector<Integer> accountIDs = null;
	private int energyCompanyID = -1;
	
	public LiteCustomer() {
		super();
		setLiteType( LiteTypes.CUSTOMER );
	}
	
	public LiteCustomer(int customerID) {
		super();
		setCustomerID( customerID );
		setLiteType( LiteTypes.CUSTOMER );
	}
	
	public int getCustomerID() {
		return getLiteID();
	}
	
	public void setCustomerID(int customerID) {
		setLiteID( customerID );
	}
	
    public void retrieve(String dbAlias) {
        PreparedStatement pstmt = null;
        java.sql.Connection conn = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection( dbAlias );
            
            String sql = "SELECT PrimaryContactID, CustomerTypeID, TimeZone, CustomerNumber, RateScheduleID, AltTrackNum, TemperatureUnit" +
                        " FROM " + Customer.TABLE_NAME +
                        " WHERE CustomerID = ?";            
/*            String sql = "SELECT PrimaryContactID, CustomerTypeID, TimeZone, CustomerNumber, RateScheduleID, AltTrackNum, TemperatureUnit, " +
                        " cont.ContFirstName, cont.ContLastName, cont.LoginID, cont.AddressID " +
                        " FROM " + Customer.TABLE_NAME + " cust, " + Contact.TABLE_NAME + " cont " +
                        " WHERE PrimaryContactID = ContactID " + 
                        " and CustomerID = ?";*/
            
            pstmt = conn.prepareStatement( sql );
            pstmt.setInt( 1, getCustomerID());
            rset = pstmt.executeQuery();
            
            if(rset.next())
            {
                setPrimaryContactID(rset.getInt(1) );
                setCustomerTypeID( rset.getInt(2) );
                setTimeZone( rset.getString(3) );
                setCustomerNumber( rset.getString(4) );
                setRateScheduleID( rset.getInt(5) );
                setAltTrackingNumber( rset.getString(6) );
                setTemperatureUnit( rset.getString(7) );
/*                LiteContact liteContact = new LiteContact(rset.getInt(1));
                liteContact.setContFirstName( rset.getString(8));
                liteContact.setContLastName( rset.getString(9));
                liteContact.setLoginID( rset.getInt(10));
                liteContact.setAddressID( rset.getInt(11));
                setLiteContact(liteContact);*/
            }
            else
                throw new IllegalStateException("Unable to find the Customer with CustomerID = " + getCustomerID() );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
        	SqlUtils.close(rset, pstmt, conn );
        }
    }

    private synchronized void retrieveExtended() {
        if(!isExtended()){
                
            PreparedStatement pstmt = null;
            java.sql.Connection conn = null;
            ResultSet rset = null;
            try {
                conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias());
                
                String sql = "SELECT ca.ContactID " + 
                     " FROM CustomerAdditionalContact ca, " + Customer.TABLE_NAME + " c " +
                     " WHERE c.CustomerID = ?" +
                     " AND c.CustomerID=ca.CustomerID " +
                     " ORDER BY ca.Ordering";
                
                pstmt = conn.prepareStatement( sql );
                pstmt.setInt( 1, getCustomerID());
                rset = pstmt.executeQuery();
                
                getAdditionalContacts().removeAllElements();
                
                while(rset.next()) //add the LiteContact to this Customer
                    getAdditionalContacts().add( DaoFactory.getContactDao().getContact( rset.getInt(1)) );
    
                pstmt.close();
                
                if (VersionTools.starsExists())
                {
                    sql = "SELECT acct.AccountID, map.EnergyCompanyID " +
                         " FROM CustomerAccount acct, ECToAccountMapping map " +
                         " WHERE acct.CustomerID = ?" +
                         " AND acct.AccountID = map.AccountID";
                    
                    pstmt = conn.prepareStatement( sql );
                    pstmt.setInt( 1, getCustomerID());
                    rset = pstmt.executeQuery();
                    
                    getAccountIDs().removeAllElements();
                    
                    while(rset.next())
                    {
                        getAccountIDs().add( new Integer(rset.getInt(1)) );
                        energyCompanyID = rset.getInt(2);
                    }
                    pstmt.close();
                }
            }
            catch (Exception e) {
                CTILogger.error( e.getMessage(), e );
            }
            finally {
                try {
                    if (rset != null) rset.close();
                    if (pstmt != null) pstmt.close();
                    if (conn != null) conn.close();
                }
                catch (java.sql.SQLException e) {}
            }
            setExtended(true);
        }
    }

	/**
	 * Returns the customerTypeID.
	 * @return int
	 */
	public int getCustomerTypeID() {
		return customerTypeID;
	}

	/**
	 * Returns the primaryContactID.
	 * @return int
	 */
	public int getPrimaryContactID() {
        return primaryContactID;
/*        if( getLiteContact() != null)
            return getLiteContact().getContactID();
        return 0;*/
	}

	/**
	 * Sets the additionalContacts.
	 * @param additionalContacts The additionalContacts to set
	 */
	public void setAdditionalContacts(Vector<LiteContact> additionalContacts) {
		this.additionalContacts = additionalContacts;
	}

	/**
	 * Sets the customerTypeID.
	 * @param customerTypeID The customerTypeID to set
	 */
	public void setCustomerTypeID(int customerTypeID) {
		this.customerTypeID = customerTypeID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(int primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * Returns the timeZone.
	 * @return String
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Sets the timeZone.
	 * @param timeZone The timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    /**
	 * Returns the additionalContacts.
	 * @return java.util.Vector
	 */
	public Vector<LiteContact> getAdditionalContacts() {
		if (additionalContacts == null)
        {
			additionalContacts = new Vector<LiteContact>(10);
            retrieveExtended();
        }
		return additionalContacts;
	}
	
	/**
	 * Returns the customer account IDs
	 * @return java.util.Vector
	 */
	public Vector<Integer> getAccountIDs() {
		if (accountIDs == null)
        {
			accountIDs = new Vector<Integer>(1);
            retrieveExtended();
        }
		return accountIDs;
	}
	
	/**
	 * Used for residential customers only. A residential customer
	 * should belong to only one energy company.
	 */
	public int getEnergyCompanyID() {
        if( energyCompanyID == -1)
            retrieveExtended();
		return energyCompanyID;
	}

	/**
	 * @param vector
	 */
	public void setAccountIDs(Vector<Integer> vector)
	{
		accountIDs = vector;
	}

	/**
	 * @param i
	 */
	public void setEnergyCompanyID(int i)
	{
		energyCompanyID = i;
	}
	
	public String getCustomerNumber() 
	{
		return customerNumber;
	}

	public void setCustomerNumber(String custNum) 
	{
		this.customerNumber = custNum;
	}
	
	public String getAltTrackingNumber() 
	{
		return altTrackNum;
	}

	public void setAltTrackingNumber(String altNum) 
	{
		this.altTrackNum = altNum;
	}
	
	public int getRateScheduleID() 
	{
		return rateScheduleID;
	}


	public void setRateScheduleID(int rSched) 
	{
		this.rateScheduleID = rSched;
	}

    public boolean isExtended()
    {
        return extended;
    }

    public void setExtended(boolean extended)
    {
        this.extended = extended;
    }

/*    public LiteContact getLiteContact()
    {
        return liteContact;
    }

    public void setLiteContact(LiteContact liteContact)
    {
        this.liteContact = liteContact;
    }*/

}
