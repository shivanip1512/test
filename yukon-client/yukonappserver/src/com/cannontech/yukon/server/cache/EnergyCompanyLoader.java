package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.web.EnergyCompanyCustomerList;

/**
 * @author alauinger
 */

public class EnergyCompanyLoader implements Runnable
{
   	private List<LiteEnergyCompany> allCompanies;
	private String dbAlias = null;

	public EnergyCompanyLoader(List<LiteEnergyCompany> allCompanies, String dbAlias) {
   		this.allCompanies = allCompanies;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		String sql = 
				"SELECT EnergyCompanyID, Name, PrimaryContactID, UserID FROM " + EnergyCompany.TABLE_NAME;
   		   		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try 
      	{
          	 conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
	         stmt = conn.createStatement();
	         rset = stmt.executeQuery(sql);
   
      		while (rset.next() ) 
      		{
      			LiteEnergyCompany company = new LiteEnergyCompany(
      					rset.getInt(1),
							rset.getString(2).trim(),
							rset.getInt(3),
							rset.getInt(4) );

      			allCompanies.add(company);                                   		
         	}

      		if(stmt != null) 
      			stmt.close();
         	
         	//assign all the customers that belong to each Energycompany
         	// NOTE: 1 customer may belong to several EnergyCompanies 
				sql = 
					"select EnergyCompanyID, CustomerID FROM " + EnergyCompanyCustomerList.tableName + " " +
					"order by EnergyCompanyID";
				stmt = conn.createStatement();
				rset = stmt.executeQuery(sql);
				while( rset.next() ) 
				{
					int engID = rset.getInt(1);
					int cstID = rset.getInt(2);

					for( int i = 0; i < allCompanies.size(); i++ )
					{
						LiteEnergyCompany company = allCompanies.get(i);
						
						if( company.getEnergyCompanyID() == engID )
						{
							company.getCiCustumerIDs().add( cstID );
						}
					}
				}
					

      	}
      	catch(SQLException e ) 
      	{
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
         	try {
            	if( stmt != null )
               		stmt.close();
                if (rset != null)
                    rset.close();
            	if( conn != null )
               	conn.close();
         	}
         	catch( java.sql.SQLException e ) {
            	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
         	}
   
   		CTILogger.info( 
       (System.currentTimeMillis() - timerStart)*.001 + 
       " Secs for EnergyCompanyLoader (" + allCompanies.size() + " loaded)" );   
      }
   
   }

}
