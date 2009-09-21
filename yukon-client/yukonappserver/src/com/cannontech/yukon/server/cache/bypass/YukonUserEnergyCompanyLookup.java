package com.cannontech.yukon.server.cache.bypass;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserEnergyCompanyLookup {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    
	public LiteEnergyCompany loadLiteEnergyCompanyByUser(LiteYukonUser liteYukonUser, List<LiteEnergyCompany> allEnergyCompanies) {

		// Load LiteEnergyCompany by (primary) contact
   		try {
   			SqlStatementBuilder sql = new SqlStatementBuilder();
   			sql.append("SELECT ectam.EnergyCompanyId");
   			sql.append("FROM ECToAccountMapping ectam, CustomerAccount ca, Customer cu, Contact c");
   			sql.append("WHERE ectam.AccountId = ca.AccountId");
   			sql.append("	AND ca.CustomerId = cu.CustomerId");
   			sql.append("	AND cu.PrimaryContactID = c.ContactID");
   			sql.append("	AND c.LoginId = ").appendArgument(liteYukonUser.getUserID());
   			
   			int energyCompanyId = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());

   			//Load the LiteEnergyCompany object
   	   		for (LiteEnergyCompany liteEnergyCompany : allEnergyCompanies) {
   				if(liteEnergyCompany.getEnergyCompanyID() == energyCompanyId) {
   					return  liteEnergyCompany;
   				}
   			}	
   		} catch(IncorrectResultSizeDataAccessException e) {
   			CTILogger.debug("LiteEnergyCompany By Contact ("+liteYukonUser.toString()+") Not Found: " + e);
   		}

   		// If by (primary) Contact did not produce a LiteEnergyCompany, load by AdditionalContact
   		try {

   			SqlStatementBuilder sql = new SqlStatementBuilder();
   			sql.append("SELECT ectam.EnergyCompanyId");
   			sql.append("FROM ECToAccountMapping ectam, CustomerAccount ca,");
   			sql.append("	CustomerAdditionalContact cac, Contact c");
   			sql.append("WHERE ectam.AccountId = ca.AccountId");
   			sql.append("	AND ca.CustomerId = cac.CustomerID");
   			sql.append(" 	AND cac.ContactID = c.ContactID");
   			sql.append("	AND c.LoginId = ").appendArgument(liteYukonUser.getUserID());
   			
   			int energyCompanyId = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());

   			//Load the LiteEnergyCompany object
   	   		for (LiteEnergyCompany liteEnergyCompany : allEnergyCompanies) {
   				if(liteEnergyCompany.getEnergyCompanyID() == energyCompanyId) {
   					return  liteEnergyCompany;
   				}
   			}	
   		} catch(IncorrectResultSizeDataAccessException e) {
   			CTILogger.debug("LiteEnergyCompany By AdditionalContact ("+liteYukonUser.toString()+") Not Found: " + e);
   		}

		// If by AdditionalContact did not produce a LiteEnergyCompany, load by Operator Login
   		try {
   			SqlStatementBuilder sql = new SqlStatementBuilder();
   			sql.append("SELECT EnergyCompanyID");
   			sql.append("FROM EnergyCompanyOperatorLoginList");
   			sql.append("	WHERE OperatorLoginID = ").appendArgument(liteYukonUser.getUserID());
   			
   			int energyCompanyId = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());

   			//Load the LiteEnergyCompany object
   	   		for (LiteEnergyCompany liteEnergyCompany : allEnergyCompanies) {
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