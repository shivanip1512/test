package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteEnergyCompany;

/**
 * @author alauinger
 */

public class EnergyCompanyLoader implements Runnable
{
   	private ArrayList allCompanies;
	private String dbAlias = null;

	public EnergyCompanyLoader(ArrayList allCompanies, String dbAlias) {
   		this.allCompanies = allCompanies;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		String sql = "SELECT EnergyCompanyID, Name FROM EnergyCompany";
   		   		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   
      		while (rset.next() ) {
      			int id = rset.getInt(1);
      			String name = rset.getString(2).trim();
      			
      			LiteEnergyCompany company = new LiteEnergyCompany(id,name);
      			allCompanies.add(company);                                   		
         	}
      	}
      	catch(SQLException e ) {
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
         	try {
            	if( stmt != null )
               		stmt.close();
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
