

package com.cannontech.stars.core.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.InUseException;
import com.cannontech.core.dao.impl.LiteYukonUserMapper;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public class ECMappingDaoImpl implements ECMappingDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DbChangeManager dbChangeManager;

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
    public List<Integer> getSubstationIdsForEnergyCompanyId(int energycompanyId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SubstationId");
        sql.append("FROM ECToSubstationMapping");
        sql.append("WHERE EnergyCompanyId").eq(energycompanyId);

        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
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
    public void addEnergyCompanyOperatorLoginListMapping(LiteYukonUser liteYukonUser, LiteStarsEnergyCompany liteStarsEnergyCompany) {
        addEnergyCompanyOperatorLoginListMapping(liteYukonUser.getUserID(), liteStarsEnergyCompany.getEnergyCompanyId());
    }

    @Override
    public void addEnergyCompanyOperatorLoginListMapping(int userId, int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO EnergyCompanyOperatorLoginList");
        sql.values(energyCompanyId, userId);
        
        yukonJdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER, 
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.ADD);
    }
    
    @Override
    public void deleteEnergyCompanyOperatorLoginListMapping(int userId, int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE EnergyCompanyOperatorLoginList");
        sql.append("WHERE OperatorLoginID").eq(userId);
        sql.append("AND EnergyCompanyID").eq(energyCompanyId);
        
        yukonJdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER, 
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.DELETE);
    }

    
    @Override
    @Transactional    
    public void deleteECToAccountMapping(Integer accountId) {
        String sql = "DELETE FROM ECToAccountMapping WHERE AccountId = ?";
        yukonJdbcTemplate.update(sql, accountId);
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
    public void addEcToRouteMapping(int energyCompanyId, int routeId){

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ECToRouteMapping");
        sql.values(energyCompanyId, routeId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int deleteECToRouteMapping(int energyCompanyId, int routeId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToRouteMapping");
        sql.append("WHERE EnergyCompanyId").eq(energyCompanyId);
        sql.append("AND RouteId").eq(routeId);
        
        return yukonJdbcTemplate.update(sql);
    }

    @Override
    public void addECToSubstationMapping(int energyCompanyId, int substationId){

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ECToSubstationMapping");
        sql.values(energyCompanyId, substationId);
        
        yukonJdbcTemplate.update(sql);
    }
   
    @Override
    public int deleteECToSubstationMapping(int energyCompanyId, int substationId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToSubstationMapping");
        sql.append("WHERE EnergyCompanyId").eq(energyCompanyId);
        sql.append("AND SubstationId").eq(substationId);
        
        return yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<Integer> getAccountIds(int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AccountId");
        sql.append("FROM ECToAccountMapping");
        sql.append("WHERE EnergyCompanyId").eq(ecId);
        
        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }
    
    @Override
    public List<LiteUserGroup> getResidentialUserGroups(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UG.*");
        sql.append("FROM ECToResidentialGroupMapping ECTGM");
        sql.append(  "JOIN UserGroup UG ON UG.UserGroupId = ECTGM.UserGroupId");
        sql.append("WHERE ECTGM.EnergyCompanyId").eq(energyCompanyId);
        
        return yukonJdbcTemplate.query(sql, new UserGroupDao.LiteUserGroupRowMapper());
    }
    
    @Override
    public List<LiteUserGroup> getOperatorUserGroups(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UG.*");
        sql.append("FROM ECToOperatorGroupMapping ECTRM");
        sql.append(  "JOIN UserGroup UG ON UG.UserGroupId = ECTRM.UserGroupId");
        sql.append("WHERE ECTRM.EnergyCompanyId").eq(energyCompanyId);
        
        return yukonJdbcTemplate.query(sql, new UserGroupDao.LiteUserGroupRowMapper());
    }
    
    @Override
    public void addECToOperatorUserGroupMapping(int ecId, Iterable<Integer> userGroupIds) {
        for (int userGroupId : userGroupIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder("INSERT INTO ECToOperatorGroupMapping");
            sql.values(ecId, userGroupId);
            yukonJdbcTemplate.update(sql);
        }
    }
    
    @Override
    public void addECToResidentialUserGroupMapping(int ecId, List<Integer> userGroupIds) {
        for (int userGroupId : userGroupIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder("INSERT INTO ECToResidentialGroupMapping");
            sql.values(ecId, userGroupId);
            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    public void deleteECToOperatorUserGroupMapping(int ecId, int userGroupId) throws InUseException {
        SqlStatementBuilder opLoginIdsSql = new SqlStatementBuilder();
        opLoginIdsSql.append("SELECT OperatorLoginId");
        opLoginIdsSql.append("FROM EnergyCompanyOperatorLoginList");
        opLoginIdsSql.append("WHERE EnergyCompanyId").eq(ecId);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonUser YU");
        sql.append("WHERE YU.UserId").in(opLoginIdsSql);
        sql.append("  AND YU.UserGroupId").eq(userGroupId);
        int numUses = yukonJdbcTemplate.queryForInt(sql);
        if (numUses > 0) {
            throw new InUseException();
        }

        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToOperatorGroupMapping");
        sql.append("WHERE EnergyCompanyId").eq(ecId);
        sql.append(  "AND UserGroupId").eq(userGroupId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteECToResidentialUserGroupMapping(int ecId, int userGroupId) throws InUseException {
        SqlStatementBuilder customerIdQuery = new SqlStatementBuilder();
        customerIdQuery.append("SELECT CustomerId FROM CustomerAccount");
        customerIdQuery.append("WHERE AccountId IN (");
        customerIdQuery.append(    "SELECT AccountId FROM EcToAccountMapping");
        customerIdQuery.append(    "WHERE EnergyCompanyId").eq(ecId);
        customerIdQuery.append(")");

        SqlFragmentCollection contactsClause = SqlFragmentCollection.newOrCollection();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("ContactId IN (");
        sql.append(    "SELECT PrimaryContactId FROM Customer");
        sql.append(    "WHERE CustomerId IN (").append(customerIdQuery).append(")");
        sql.append(")");
        contactsClause.add(sql);

        sql = new SqlStatementBuilder();
        sql.append("ContactId IN (");
        sql.append(    "SELECT ContactId FROM CustomerAdditionalContact");
        sql.append(    "WHERE CustomerId IN (").append(customerIdQuery).append(")");
        sql.append(")");
        contactsClause.add(sql);

        SqlStatementBuilder loginIdsByContactSql = new SqlStatementBuilder();
        loginIdsByContactSql.append("SELECT LoginId");
        loginIdsByContactSql.append("FROM Contact");
        loginIdsByContactSql.append("WHERE").append(contactsClause);
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonUser YU"); 
        sql.append("WHERE YU.UserId").in(loginIdsByContactSql);
        sql.append("  AND YU.UserGroupId").eq(userGroupId);
        int numUses = yukonJdbcTemplate.queryForInt(sql);
        if (numUses > 0) {
            throw new InUseException();
        }

        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToResidentialGroupMapping");
        sql.append("WHERE EnergyCompanyId").eq(ecId);
        sql.append(  "AND UserGroupId").eq(userGroupId);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public LiteYukonUser findParentLogin(int childEnergyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM ECToGenericMapping gm");
        sql.append(  "JOIN EnergyCompanyOperatorLoginList ll ON ll.OperatorLoginId = gm.ItemId");
        sql.append(  "JOIN YukonUser yu ON yu.UserId = gm.ItemId");
        sql.append("WHERE gm.MappingCategory").eq_k(EcMappingCategory.MEMBER_LOGIN);
        sql.append(  "AND ll.EnergyCompanyId").eq(childEnergyCompanyId);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql, new LiteYukonUserMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public void saveParentLogin(int parentEcId, int childEcId, Integer parentLogin) {
        /* Remove existing mapping */
        removeParentLogin(parentEcId, childEcId);
        
        /* Add member login mapping */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ECToGenericMapping");
        sql.values(parentEcId, parentLogin, EcMappingCategory.MEMBER_LOGIN);
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void removeParentLogin(int parentEcId, int childEcId) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(childEcId);
        List<Integer> childOperatorLoginIds = ecDao.getOperatorUserIds(energyCompany);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ECToGenericMapping");
        sql.append("WHERE ItemId").in(childOperatorLoginIds);
        sql.append(  "AND MappingCategory").eq_k(EcMappingCategory.MEMBER_LOGIN);
        sql.append(  "AND EnergyCompanyId").eq(parentEcId);
        yukonJdbcTemplate.update(sql);
    }
 
    @Override
    public List<Integer> getItemIdsForEnergyCompanyAndCategory(int energycompanyId, EcMappingCategory category) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItemId ");
        sql.append("FROM ECToGenericMapping");
        sql.append("WHERE EnergyCompanyId").eq(energycompanyId);
        sql.append("AND MappingCategory").eq_k(category);

        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    @PostConstruct
    public void init() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(yukonJdbcTemplate);
    }
    
    @Override
    public boolean isOperatorInOperatorUserGroup(int operatorLoginId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(YU.UserID)");
        sql.append("FROM ECToOperatorGroupMapping ECTRM");
        sql.append(  "JOIN YukonUser YU ON YU.UserGroupId = ECTRM.UserGroupId");
        sql.append("WHERE YU.UserID").eq(operatorLoginId);
        
        int count = yukonJdbcTemplate.queryForInt(sql);
        return count > 0;
    }

}