package com.cannontech.database.data.lite;

import java.util.Vector;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.Customer;

/**
 * @author alauinger
 */
public class LiteCICustomer extends LiteBase 
{
	private int mainAddressID = 0;
	private String companyName = null;
	private int primaryContactID = 0;
	private String timeZone = null;
	private double demandLevel = 0.0;
	private double curtailAmount = 0.0;
	
	//non-persistent data, 
	//contains com.cannontech.database.data.lite.LiteContact
	private Vector additionalContacts = null;
	
	
	private LiteCICustomer() 
	{
		this( 0, null );
	}
	
	public LiteCICustomer(int id) {
		this( id, null );
	}
	
	public LiteCICustomer( int id, String companyName_ ) 
	{
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
	 * Returns the primaryContactID.
	 * @return int
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Returns the timeZone.
	 * @return String
	 */
	public String getTimeZone() {
		return timeZone;
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
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(int primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * Sets the timeZone.
	 * @param timeZone The timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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
		
		try
		{
	      com.cannontech.database.SqlStatement stat = 
	            new com.cannontech.database.SqlStatement(
	             "select i.MainAddressID, i.CompanyName, " +
	             "c.PrimaryContactID, c.TimeZone, i.CustomerDemandLevel, " +
	             "i.CurtailAmount " +
	             "from " + CICustomerBase.TABLE_NAME + " i, " +
	             Customer.TABLE_NAME + " c " + 
	             "where i.CustomerID = " + getLiteID() +
	             " and c.CustomerID = " + getLiteID(),
	             dbalias );
	
			stat.execute();
	
	      if( stat.getRowCount() <= 0 )
	         throw new IllegalStateException("Unable to find the Customer with CustomerID = " + getLiteID() );
	
	      Object[] objs = stat.getRow(0);
	      
			setMainAddressID( ((java.math.BigDecimal) objs[0]).intValue() ); 			
			setCompanyName( objs[1].toString() );	
			setPrimaryContactID( ((java.math.BigDecimal) objs[2]).intValue() );
			setTimeZone( objs[3].toString() );
			setDemandLevel( Double.valueOf(objs[4].toString()).doubleValue() );
			setCurtailAmount( Double.valueOf(objs[5].toString()).doubleValue() );
			
			
			
	      stat = new com.cannontech.database.SqlStatement(
						"SELECT ca.ContactID " + 
						"FROM CustomerAdditionalContact ca, " + 
						CICustomerBase.TABLE_NAME + " ci " +
						"WHERE " +
						"ci.CustomerID=" + getCustomerID() + " " +
						"AND ci.CustomerID=ca.CustomerID " +
						"ORDER BY ci.CustomerID",
						dbalias );
			
			stat.execute();
			
			getAdditionalContacts().removeAllElements();
			
			for( int i = 0; i < stat.getRowCount(); i++ )
			{
				//add the LiteContact to this CICustomer
				getAdditionalContacts().add(
					ContactFuncs.getContact(((java.math.BigDecimal) stat.getRow(i)[0]).intValue()) );

			}

		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	
	}
	
	/**
	 * Returns the additionalContacts.
	 * @return Vector
	 */
	public Vector getAdditionalContacts() 
	{
		if( additionalContacts == null )
			additionalContacts = new Vector(10);

		return additionalContacts;
	}


}
