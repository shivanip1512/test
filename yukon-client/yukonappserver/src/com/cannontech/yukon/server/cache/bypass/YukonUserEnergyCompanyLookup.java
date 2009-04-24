package com.cannontech.yukon.server.cache.bypass;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserEnergyCompanyLookup {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static String sqlForContact; 
    static String sqlForAdditionalContact;
    static String sqlForOperatorLogin;
    static {
		sqlForContact = "SELECT EnergyCompanyCustomerList.EnergyCompanyID " +
			  " FROM EnergyCompanyCustomerList,Customer,Contact " +
			  " WHERE EnergyCompanyCustomerList.CustomerID=Customer.CustomerID " +
			  " AND Customer.PrimaryContactID=Contact.ContactID"; 
			  
		sqlForAdditionalContact = "SELECT EnergyCompanyCustomerList.EnergyCompanyID " + 
				  " FROM EnergyCompanyCustomerList,CustomerAdditionalContact,Contact " + 
				  " WHERE EnergyCompanyCustomerList.CustomerID=CustomerAdditionalContact.CustomerID " +
				  " AND CustomerAdditionalContact.ContactID=Contact.ContactID ";
				     					
		sqlForOperatorLogin = "SELECT EnergyCompanyID FROM EnergyCompanyOperatorLoginList";
    }
    
	public LiteEnergyCompany loadLiteEnergyCompanyByUser(LiteYukonUser liteYukonUser, List<LiteEnergyCompany> allEnergyCompanies) {

		// Load LiteEnergyCompany by (primary) contact
   		try {
   			String sql = sqlForContact + " AND Contact.LoginId = ?";
   			int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, liteYukonUser.getUserID());

   			//Load the LiteEnergyCompany object
   	   		for (LiteEnergyCompany liteEnergyCompany: allEnergyCompanies) {
   				if(liteEnergyCompany.getEnergyCompanyID() == energyCompanyId) {
   					return  liteEnergyCompany;
   				}
   			}	
   		} catch(IncorrectResultSizeDataAccessException e) {
   			CTILogger.debug("LiteEnergyCompany By Contact ("+liteYukonUser.toString()+") Not Found: " + e);
   		}

   		// If by (primary) Contact did not produce a LiteEnergyCompany, load by AdditionalContact
   		try {
   			String sql = sqlForAdditionalContact + " AND Contact.LoginId = ?";
   			int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, liteYukonUser.getUserID());

   			//Load the LiteEnergyCompany object
   	   		for (LiteEnergyCompany liteEnergyCompany: allEnergyCompanies) {
   				if(liteEnergyCompany.getEnergyCompanyID() == energyCompanyId) {
   					return  liteEnergyCompany;
   				}
   			}	
   		} catch(IncorrectResultSizeDataAccessException e) {
   			CTILogger.debug("LiteEnergyCompany By AdditionalContact ("+liteYukonUser.toString()+") Not Found: " + e);
   		}

		// If by AdditionalContact did not produce a LiteEnergyCompany, load by Operator Login
   		try {
   			String sql = sqlForOperatorLogin + " WHERE OperatorLoginID = ?";
   			int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, liteYukonUser.getUserID());

   			//Load the LiteEnergyCompany object
   	   		for (LiteEnergyCompany liteEnergyCompany: allEnergyCompanies) {
   				if(liteEnergyCompany.getEnergyCompanyID() == energyCompanyId) {
   					return  liteEnergyCompany;
   				}
   			}	
   		} catch(IncorrectResultSizeDataAccessException e) {
   			CTILogger.debug("LiteEnergyCompany By OperatorLogin Not Found: " + e);
   		}
   		return null;
    }
	
	@Required
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
}