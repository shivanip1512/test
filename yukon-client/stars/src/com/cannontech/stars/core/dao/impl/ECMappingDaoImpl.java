package com.cannontech.stars.core.dao.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ECMappingDaoImpl implements ECMappingDao, InitializingBean {
    private YukonJdbcTemplate yukonJdbcTemplate;;
    private StarsDatabaseCache starsDatabaseCache;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    @Override
    public LiteStarsEnergyCompany getCustomerAccountEC(final CustomerAccount account) {
        int accountId = account.getAccountId();
        return this.getCustomerAccountEC(accountId);
    }

    @Override
    public LiteStarsEnergyCompany getCustomerAccountEC(final int accountId) {
        int energyCompanyId = getEnergyCompanyIdForAccountId(accountId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }
    
    @Override
    public int getEnergyCompanyIdForAccountId(int accountId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT EnergyCompanyId");
    	sql.append("FROM ECToAccountMapping");
    	sql.append("WHERE AccountId").eq(accountId);

    	return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getEnergyCompanyIdForInventoryId(int inventoryId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT EnergyCompanyId");
    	sql.append("FROM ECToInventoryMapping");
    	sql.append("WHERE InventoryId").eq(inventoryId);
    	
        return yukonJdbcTemplate.queryForInt(sql);
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
        
        
        int energyCompanyId = yukonJdbcTemplate.queryForInt(sql.toString(), contactId, contactId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }
    
    @Override
    @Transactional    
    public void updateECToAccountMapping(int accountId, int energyCompanyId) {
        String sql = "UPDATE ECToAccountMapping SET EnergyCompanyId = ? WHERE AccountId = ?";
        yukonJdbcTemplate.update(sql, energyCompanyId, accountId);
    }
    
    @Override
    @Transactional    
    public void addECToAccountMapping(ECToAccountMapping ecToAccountMapping) {
        String sql = "INSERT INTO ECToAccountMapping VALUES (?,?)";
        yukonJdbcTemplate.update(sql, ecToAccountMapping.getEnergyCompanyId(), ecToAccountMapping.getAccountId());
    }
    
    @Override
    @Transactional    
    public void deleteECToAccountMapping(Integer accountId) {
        String sql = "DELETE FROM ECToAccountMapping WHERE AccountId = ?";
        yukonJdbcTemplate.update(sql, accountId);
    }
    

    /**
     * Method to delete energy company to inventory mapping 
     * @param inventoryId
     */    
    @Override    
    @Transactional    
    public void deleteECToInventoryMapping(int inventoryId) {
        String sql = "DELETE FROM ECToInventoryMapping WHERE InventoryID = ?";
        yukonJdbcTemplate.update(sql, inventoryId);
    }
    
    @Override
    @Transactional    
    public void deleteECToCustomerEventMapping(List<Integer> eventIds) {
        if(!eventIds.isEmpty()) {
            chunkyJdbcTemplate.update(new ECToCustomerEventMappingDeleteSqlGenerator(), eventIds);
        }
    }
    
    /**
     * Sql generator for deleting energy company to event mappings, useful for bulk deleteing
     * with chunking sql template.
     */
    private class ECToCustomerEventMappingDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ECToLMCustomerEventMapping WHERE EventId IN (", eventIds, ")");
            return sql.toString();
        }
    }

    @Override
    public void addECToWorkOrderMapping(int energyCompanyId, int workOrderId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ECToWorkOrderMapping");
        sql.values(energyCompanyId, workOrderId);
        
        yukonJdbcTemplate.update(sql);
    }

    
    @Override
    @Transactional    
    public void deleteECToWorkOrderMapping(List<Integer> workOrderIds) {
        if(!workOrderIds.isEmpty()) {
            chunkyJdbcTemplate.update(new ECToWorkOrderMappingDeleteSqlGenerator(), workOrderIds);
        }
    }
    
    /**
     * Sql generator for deleting energy company to work order mappings, useful for bulk deleteing
     * with chunking sql template.
     */
    private class ECToWorkOrderMappingDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> workOrderIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ECToWorkOrderMapping WHERE WorkOrderId IN (", workOrderIds, ")");
            return sql.toString();
        }
    }
    
    @Override
    @Transactional    
    public void addECToCallReportMapping(int energyCompanyId, int callId) {
        String sql = "INSERT INTO ECToCallReportMapping VALUES (?,?)";
        yukonJdbcTemplate.update(sql, energyCompanyId, callId);
    }
    
    @Override
    @Transactional    
    public void deleteECToCallReportMapping(List<Integer> callReportIds) {
        if(!callReportIds.isEmpty()) {
            chunkyJdbcTemplate.update(new ECToCallReportMappingDeleteSqlGenerator(), callReportIds);
        }
    }
    
    /**
     * Sql generator for deleting energy company to call report mappings, useful for bulk deleteing
     * with chunking sql template.
     */
    private class ECToCallReportMappingDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> callReportIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM ECToCallReportMapping WHERE CallReportId IN (", callReportIds, ")");
            return sql.toString();
        }
    }
    
    @Override
    public Set<YukonEnergyCompany> getChildEnergyCompanies(int energyCompanyId) {
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);

        // Get all of the child energy companies including itself for the supplied energy company id.
        List<Integer> childEnergyCompanyIds = energyCompany.getAllEnergyCompaniesDownward();
        Set<YukonEnergyCompany> availableChildEnergyCompanies = Sets.newHashSetWithExpectedSize(childEnergyCompanyIds.size());
        for (int childEnergyCompanyId : childEnergyCompanyIds) {
            YukonEnergyCompany childEnergyCompany = starsDatabaseCache.getEnergyCompany(childEnergyCompanyId);
            availableChildEnergyCompanies.add(childEnergyCompany);
        }
        
        return availableChildEnergyCompanies;

    }

    @Override
    public Set<Integer> getChildEnergyCompanyIds(int energyCompanyId) {
        Set<YukonEnergyCompany> childEnergyCompanies = getChildEnergyCompanies(energyCompanyId);
        
        Collection<Integer> childEnergyCompanyIds = 
            Collections2.transform(childEnergyCompanies, LiteStarsEnergyCompany.getEnergyCompanyToEnergyCompanyIdsFunction());
        
        return Sets.newHashSet(childEnergyCompanyIds);
        
    }
    
    @Override
    public LinkedHashSet<YukonEnergyCompany> getParentEnergyCompanies(int energyCompanyId) {

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        //  Get all the energy companies to the supplied energy company id.
        List<LiteStarsEnergyCompany> allParentEnergyCompanies = ECUtils.getAllAscendants(energyCompany);

        LinkedHashSet<YukonEnergyCompany> availableEnergyCompanies = Sets.newLinkedHashSet();
        availableEnergyCompanies.addAll(allParentEnergyCompanies);
        
        return availableEnergyCompanies;

    }
    
    @Override
    public LinkedHashSet<Integer> getParentEnergyCompanyIds(int energyCompanyId) {

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        //  Get all the energy companies to the supplied energy company id.
        List<LiteStarsEnergyCompany> allParentEnergyCompanies = ECUtils.getAllAscendants(energyCompany);
        List<Integer> energyCompaniesIdList = 
            Lists.transform(allParentEnergyCompanies, LiteStarsEnergyCompany.getEnergyCompanyToEnergyCompanyIdsFunction());
        
        return Sets.newLinkedHashSet(energyCompaniesIdList);

    }
    
    // DI Setters
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(yukonJdbcTemplate);
    }

}
