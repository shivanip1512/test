package com.cannontech.database.data.lite;

import com.cannontech.database.db.customer.CICustomerBase;

/**
 * @author alauinger
 */
public class LiteCICustomer extends LiteCustomer 
{
	private int mainAddressID = 0;
	private String companyName = null;
	private double demandLevel = 0.0;
	private double curtailAmount = 0.0;
	
	
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
		super.retrieve( dbalias );
		
		try
		{
			com.cannontech.database.SqlStatement stat = 
				new com.cannontech.database.SqlStatement(
					"SELECT MainAddressID, CompanyName, " +
					" CustomerDemandLevel, CurtailAmount " +
					" FROM " + CICustomerBase.TABLE_NAME +
					" WHERE CustomerID = " + getLiteID(),
					dbalias );
			
			stat.execute();
			
			if( stat.getRowCount() <= 0 )
				throw new IllegalStateException("Unable to find the Customer with CustomerID = " + getLiteID() );
			
			Object[] objs = stat.getRow(0);
			
			setMainAddressID( ((java.math.BigDecimal) objs[0]).intValue() ); 			
			setCompanyName( objs[1].toString() );	
			setDemandLevel( Double.valueOf(objs[2].toString()).doubleValue() );
			setCurtailAmount( Double.valueOf(objs[3].toString()).doubleValue() );
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}

}
