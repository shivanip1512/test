package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Builds up maps
 * Map<LiteYukonUser,List<LiteYukonGroup>> and
 * Map<LiteYukonGroup,List<LiteYukonUser>>
 * @author alauinger
 */

public class UserEnergyCompanyLoader implements Runnable
{
		
	private Map<LiteYukonUser, LiteEnergyCompany> allUserEnergyCompanies;
	private List<LiteYukonUser> allUsers;
	private List<LiteEnergyCompany> allEnergyCompanies;	
	private String dbAlias = null;

	public UserEnergyCompanyLoader(Map<LiteYukonUser, LiteEnergyCompany> allUserEnergyCompanies, List<LiteYukonUser> allUsers, List<LiteEnergyCompany> allEnergyCompanies, String dbAlias) {
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
   		HashMap<Integer, LiteYukonUser> userMap = new HashMap<Integer, LiteYukonUser>(allUsers.size()*2);
   		HashMap<Integer, LiteEnergyCompany> companyMap = new HashMap<Integer, LiteEnergyCompany>(allEnergyCompanies.size()*2);
   		
   		Iterator<LiteYukonUser> userIter = allUsers.iterator();
   		while(userIter.hasNext()) {   			
   			LiteYukonUser u = userIter.next();
   			userMap.put(new Integer(u.getUserID()), u);
   		}
   		
   		Iterator<LiteEnergyCompany> ecIter = allEnergyCompanies.iterator();
   		while(ecIter.hasNext()) {
   			LiteEnergyCompany c = ecIter.next();
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
       " Secs for UserEnergyCompanyLoader (" + allUserEnergyCompanies.size() + " loaded)" );   
      }
   
   }

public void populateMap(HashMap<Integer, LiteYukonUser> userMap, HashMap<Integer, LiteEnergyCompany> companyMap, ResultSet rset)
	throws SQLException {
	while(rset.next()) {
		Integer userID = new Integer(rset.getInt(1));
		Integer companyID = new Integer(rset.getInt(2));
		
		LiteYukonUser user = userMap.get(userID);
		LiteEnergyCompany company = companyMap.get(companyID);
		
		allUserEnergyCompanies.put(user,company);
	}   		
}
   
   

}
