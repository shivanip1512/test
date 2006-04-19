package com.cannontech.database.data.lite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.Customer;

/**
 * @author alauinger
 */
public class LiteCICustomer extends LiteCustomer 
{
	private int mainAddressID = 0;
	private String companyName = null;
	private double demandLevel = 0.0;
	private double curtailAmount = 0.0;
	private int ciCustType = YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL;
	
	
	public LiteCICustomer() 
	{
		this( 0, null );
	}
	
	public LiteCICustomer(int id) {
		this( id, null );
	}
	
	public LiteCICustomer( int id, String companyName_ ) 
	{
		super( id );
		setLiteType(LiteTypes.CUSTOMER_CI);
		setCustomerID( id );
		setCompanyName( companyName_ );
	}

	/**
	 * Returns the companyName.
	 * @return String
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Returns the curtailAmount.
	 * @return double
	 */
	public double getCurtailAmount() {
		return curtailAmount;
	}

	/**
	 * Returns the customerID.
	 * @return int
	 */
	public int getCustomerID() {
		return getLiteID();
	}

	/**
	 * Returns the demandLevel.
	 * @return double
	 */
	public double getDemandLevel() {
		return demandLevel;
	}

	/**
	 * Returns the mainAddressID.
	 * @return int
	 */
	public int getMainAddressID() {
		return mainAddressID;
	}

	/**
	 * Sets the companyName.
	 * @param companyName The companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Sets the curtailAmount.
	 * @param curtailAmount The curtailAmount to set
	 */
	public void setCurtailAmount(double curtailAmount) {
		this.curtailAmount = curtailAmount;
	}

	/**
	 * Sets the customerID.
	 * @param customerID The customerID to set
	 */
	public void setCustomerID(int customerID) {
		setLiteID( customerID );
	}

	/**
	 * Sets the demandLevel.
	 * @param demandLevel The demandLevel to set
	 */
	public void setDemandLevel(double demandLevel) {
		this.demandLevel = demandLevel;
	}

	/**
	 * Sets the mainAddressID.
	 * @param mainAddressID The mainAddressID to set
	 */
	public void setMainAddressID(int mainAddressID) {
		this.mainAddressID = mainAddressID;
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString() 
	{
		return getCompanyName();
	}
	
	public void retrieve( String dbalias )
	{
        PreparedStatement pstmt = null;
        java.sql.Connection conn = null;
        ResultSet rset = null;
        try 
        {
            conn = PoolManager.getInstance().getConnection( dbalias );
            
            String sql = "SELECT ci.MainAddressID, ci.CompanyName, ci.CustomerDemandLevel, " +
                        "ci.CurtailAmount, ci.CICustType, c.PrimaryContactID, c.CustomerTypeID, " +
                        "c.TimeZone, c.CustomerNumber, c.RateScheduleID, c.AltTrackNum, c.TemperatureUnit" +
                        " FROM " + Customer.TABLE_NAME + " c, " + CICustomerBase.TABLE_NAME + " ci" +
                        " WHERE ci.CustomerID = ? AND c.CustomerID = ci.CustomerID";
            
            pstmt = conn.prepareStatement( sql );
            pstmt.setInt( 1, getCustomerID());
            rset = pstmt.executeQuery();
            
            if(rset.next())
            {
                setMainAddressID( rset.getInt(1) );            
                setCompanyName( rset.getString(2) );   
                setDemandLevel( rset.getDouble(3) );
                setCurtailAmount( rset.getDouble(4) );
                setCICustType( rset.getInt(5) );
                setPrimaryContactID(rset.getInt(6) );
                setCustomerTypeID( rset.getInt(7) );
                setTimeZone( rset.getString(8) );
                setCustomerNumber( rset.getString(9) );
                setRateScheduleID( rset.getInt(10) );
                setAltTrackingNumber( rset.getString(11) );
                setTemperatureUnit( rset.getString(12) );
            }
            else
                throw new IllegalStateException("Unable to find the Customer with CustomerID = " + getCustomerID() );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }
        
	}

	public int getCICustType() {
		return ciCustType;
	}

	public void setCICustType(int ciCustType) {
		this.ciCustType = ciCustType;
	}
   
}
