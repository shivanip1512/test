package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.LiteYukonGroup;

/**
 * Builds up maps
 * Map<LiteYukonUser,List<LiteYukonGroup>> and
 * Map<LiteYukonGroup,List<LiteYukonUser>>
 * @author alauinger
 */

public class UserEnergyCompanyLoader implements Runnable
{
		
	private Map allUserEnergyCompanies;
	private List allUsers;
	private List allEnergyCompanies;	
	private String dbAlias = null;

	public UserEnergyCompanyLoader(Map allUserEnergyCompanies, List allUsers, List allEnergyCompanies, String dbAlias) {
		this.allUserEnergyCompanies = allUserEnergyCompanies;
		this.allUsers = allUsers;
		this.allEnergyCompanies = allEnergyCompanies;
		this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		// build up some hashtables to avoid 
   		// exponential algorithm   		
   		HashMap userMap = new HashMap(allUsers.size()*2);
   		HashMap companyMap = new HashMap(allEnergyCompanies.size()*2);
   		
   		Iterator i = allUsers.iterator();
   		while(i.hasNext()) {   			
   			LiteYukonUser u = (LiteYukonUser) i.next();
   			userMap.put(new Integer(u.getUserID()), u);
   		}
   		
   		i = allEnergyCompanies.iterator();
   		while(i.hasNext()) {
   			LiteEnergyCompany c = (LiteEnergyCompany) i.next();
   			companyMap.put(new Integer(c.getLiteID()), c);
   		}
   		
   		String sql1 = "SELECT Contact.LoginID,EnergyCompanyCustomerList.EnergyCompanyID " +
   							  " FROM EnergyCompanyCustomerList,Customer,Contact " +
   							  " WHERE EnergyCompanyCustomerList.CustomerID=Customer.CustomerID " +
   							  " AND Customer.PrimaryContactID=Contact.ContactID"; 
   							  
	    String sql2 = "SELECT Contact.LoginID,EnergyCompanyCustomerList.EnergyCompanyID " + 
	    					  " FROM EnergyCompanyCustomerList,CustomerAdditionalContact,Contact " + 
	    					  " WHERE EnergyCompanyCustomerList.CustomerID=CustomerAdditionalContact.CustomerID " +
	    					  " AND CustomerAdditionalContact.ContactID=Contact.ContactID " + 
	    					  " ORDER by CustomerAdditionalContact.Ordering";
	    					     					
   	    String sql3 = "SELECT OperatorLoginID,EnergyCompanyID FROM EnergyCompanyOperatorLoginList";
   	         		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();

         	rset = stmt.executeQuery(sql1);
   			populateMap(userMap,companyMap,rset);
      		rset.close();
      		
      		rset = stmt.executeQuery(sql2);
      		populateMap(userMap,companyMap,rset);
   			rset.close();
   			
   			rset = stmt.executeQuery(sql3);   			
   			populateMap(userMap, companyMap, rset);            		   			      		   			
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
       " Secs for UserEnergyCompanyLoader (" + allUserEnergyCompanies.size() + " loaded)" );   
      }
   
   }

public void populateMap(HashMap userMap, HashMap companyMap, ResultSet rset)
	throws SQLException {
	while(rset.next()) {
		Integer userID = new Integer(rset.getInt(1));
		Integer companyID = new Integer(rset.getInt(2));
		
		LiteYukonUser user = (LiteYukonUser) userMap.get(userID);
		LiteEnergyCompany company = (LiteEnergyCompany) companyMap.get(companyID);
		
		allUserEnergyCompanies.put(user,company);
	}   		
}
   
   

}
