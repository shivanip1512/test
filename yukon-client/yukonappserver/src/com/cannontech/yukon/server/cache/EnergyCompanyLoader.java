package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.db.company.EnergyCompany;

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
	@Override
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

            SqlUtils.close(rset, stmt);

      	}
      	catch(SQLException e ) 
      	{
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
      	    SqlUtils.close(rset, stmt, conn);
            
      	    CTILogger.info( 
      	                   (System.currentTimeMillis() - timerStart)*.001 + 
      	                   " Secs for EnergyCompanyLoader (" + allCompanies.size() + " loaded)" );   
      }
   
   }

}
