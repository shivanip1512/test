package com.cannontech.database.data.lite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.web.EnergyCompanyCustomerList;

/**
 * @author alauinger
 */
public class LiteEnergyCompany extends LiteBase 
{
	private String name;
	private int primaryContactID;
	private int userID;
	
	private NativeIntVector ciCustumerIDs = null;
	
	public LiteEnergyCompany() {
		initialize(0,null,0,0);
	}
	
	public LiteEnergyCompany(int id) {
		initialize(id,null,0,0);
	}
	
	public LiteEnergyCompany(int id, String name, int primaryContactID, int userID) {
		initialize(id,name, primaryContactID, userID);		
	}	
	
	private void initialize(int id, String name, int primaryContactID, int userID) {
		setLiteType(LiteTypes.ENERGY_COMPANY);
		setLiteID(id);
		setName(name);		
		setPrimaryContactID(primaryContactID);
		setUserID(userID);
	}	
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the energyCompanyID.
	 * @return int
	 */
	public int getEnergyCompanyID() {
		return getLiteID();
	}

	/**
	 * Sets the energyCompanyID.
	 * @param energyCompanyID The energyCompanyID to set
	 */
	public void setEnergyCompanyID(int energyCompanyID) {
		setLiteID(energyCompanyID);
	}


	public void retrieve( String dbAlias )
	{
		
		String sql = 
			"SELECT Name, PrimaryContactID, UserID " +
			"FROM " + EnergyCompany.TABLE_NAME + " " +
			"where EnergyCompanyID=" + getEnergyCompanyID();
   		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		
		try 
		{
			conn = PoolManager.getInstance().getConnection( dbAlias );

			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);

			
			if( rset.next() ) 
			{
				setName( rset.getString(1).trim() );
				setPrimaryContactID( rset.getInt(2) );
				setUserID( rset.getInt(3) );
			}


			//assign all the customers that belong to each Energycompany
			// NOTE: 1 customer may belong to several EnergyCompanies
			getCiCustumerIDs().clear(); 
			sql = 
				"select CustomerID FROM " + EnergyCompanyCustomerList.tableName + " " +
				"where EnergyCompanyID=" + getEnergyCompanyID();

			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);
			while( rset.next() ) 
			{
				int cstID = rset.getInt(1);
				getCiCustumerIDs().add( cstID );
			}

		}
		catch(SQLException e ) 
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally 
		{
				try {
					if( stmt != null )
						stmt.close();
					if( conn != null )
						conn.close();
				}
				catch( java.sql.SQLException e ) {
					com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
				}
		}
      
	}
	
	/**
	 * @return
	 */
	public int getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * @return
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param i
	 */
	public void setPrimaryContactID(int i) {
		primaryContactID = i;
	}

	/**
	 * @param i
	 */
	public void setUserID(int i) {
		userID = i;
	}

	/**
	 * @return
	 */
	public NativeIntVector getCiCustumerIDs()
	{
		if( ciCustumerIDs == null )
			ciCustumerIDs = new NativeIntVector(16);

		return ciCustumerIDs;
	}

	/**
	 * @param vector
	 */
	public void setCiCustumerIDs(NativeIntVector vector)
	{
		ciCustumerIDs = vector;
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString() 
	{
		return getName();
	}
}
