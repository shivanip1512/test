package com.cannontech.stars.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;

public class ECMappingDaoImpl implements ECMappingDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    public LiteStarsEnergyCompany getCustomerAccountEC(final CustomerAccount account) {
        int accountId = account.getAccountId();
        return this.getCustomerAccountEC(accountId);
    }

    @Override
    public LiteStarsEnergyCompany getCustomerAccountEC(final int accountId) {
        String sql = "SELECT EnergyCompanyID FROM ECToAccountMapping WHERE AccountID = ?";
        int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, accountId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }
    
    @Override
    public LiteStarsEnergyCompany getInventoryEC(int inventoryId) {
        String sql = "SELECT EnergyCompanyID FROM ECToInventoryMapping WHERE InventoryID = ?";
        int energyCompanyId = simpleJdbcTemplate.queryForInt(sql, inventoryId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }
    
    @Override
    public LiteStarsEnergyCompany getContactEC(int contactId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyId");
        sql.append("FROM ECToAccountMapping");
        sql.append("WHERE AccountId = ");
        sql.append("    (SELECT AccountId");
        sql.append("     FROM CustomerAccount");
        sql.append("     WHERE CustomerId = ");
        sql.append("        (SELECT CustomerId");
        sql.append("         FROM Customer");
        sql.append("         WHERE PrimaryContactId = ?");
        sql.append("         OR CustomerId = ");
        sql.append("            (SELECT CustomerId");
        sql.append("             FROM CustomerAdditionalContact");
        sql.append("             WHERE ContactId = ?)))");
        
        
        int energyCompanyId = simpleJdbcTemplate.queryForInt(sql.toString(), contactId, contactId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }
    
    @Override
    public void updateECToAccountMapping(int accountId, int energyCompanyId) {
        String sql = "UPDATE ECToAccountMapping SET EnergyCompanyId = ? WHERE AccountId = ?";
        simpleJdbcTemplate.update(sql, energyCompanyId, accountId);
    }
    
    @Override
    public void addECToAccountMapping(ECToAccountMapping ecToAccountMapping) {
        String sql = "INSERT INTO ECToAccountMapping VALUES (?,?)";
        simpleJdbcTemplate.update(sql, ecToAccountMapping.getEnergyCompanyId(), ecToAccountMapping.getAccountId());
    }
    
    @Override
    public void deleteECToAccountMapping(Integer accountId) {
        String sql = "DELETE FROM ECToAccountMapping WHERE AccountId = ?";
        simpleJdbcTemplate.update(sql, accountId);
    }
    
    @Override
    public void deleteECToCustomerEventMapping(List<Integer> eventIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToLMCustomerEventMapping WHERE EventId IN (", eventIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }
    
    @Override
    public void deleteECToWorkOrderMapping(List<Integer> workOrderIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToWorkOrderMapping WHERE EventId IN (", workOrderIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }
    
    @Override
    public void deleteECToCallReportMapping(List<Integer> callReportIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToCallReportMapping WHERE EventId IN (", callReportIds, ")");
        simpleJdbcTemplate.update(sql.toString());
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
}
