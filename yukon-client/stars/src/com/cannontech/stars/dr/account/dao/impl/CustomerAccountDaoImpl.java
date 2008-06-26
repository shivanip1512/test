package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;

public class CustomerAccountDaoImpl implements CustomerAccountDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectSql;
    private static final String selectByIdSql;
    private static final String selectAllUsefulAccountInfoFromECSql;
    private static final String selectAllUsefulAccountInfoFromCustomerIdSql;
    private static final ParameterizedRowMapper<CustomerAccount> rowMapper;
    private static final ParameterizedRowMapper<CustomerAccountWithNames> specialAccountInfoRowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private StarsDatabaseCache starsDatabaseCache;
    
    static {
        
        insertSql = "INSERT INTO CustomerAccount (AccountId,AccountSiteId,AccountNumber,CustomerId,BillingAddressId,AccountNotes) VALUES (?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM CustomerAccount WHERE AccountId = ?";
        
        updateSql = "UPDATE CustomerAccount SET AccountSiteId = ?, AccountNumber = ?, CustomerId = ?, BillingAddressId = ?, AccountNotes = ? WHERE AccountId = ?";
        
        selectSql = "SELECT AccountId,AccountSiteId,AccountNumber,CustomerAccount.CustomerId,BillingAddressId,AccountNotes FROM CustomerAccount";
        
        selectByIdSql = selectSql + " WHERE AccountId = ?";
        
        selectAllUsefulAccountInfoFromECSql = "SELECT ca.AccountId, ca.AccountNumber, cont.ContLastName, cont.ContFirstName" +
                " FROM CustomerAccount ca, Contact cont, Customer cust WHERE AccountId IN" +
                " (SELECT AccountId FROM ECToAccountMapping WHERE EnergyCompanyId = ?) " +
                " AND cust.CustomerId = ca.CustomerId AND cont.ContactId = cust.PrimaryContactId";
        
        selectAllUsefulAccountInfoFromCustomerIdSql = "SELECT ca.AccountId, ca.AccountNumber, cont.ContLastName, cont.ContFirstName" +
                " FROM CustomerAccount ca, Contact cont, Customer cust WHERE AccountId IN" +
                " (SELECT AccountId FROM ECToAccountMapping WHERE EnergyCompanyId = ?) " +
                " AND cust.CustomerId = ca.CustomerId AND cont.ContactId = cust.PrimaryContactId " +
                " AND cust.CustomerId = ? ";

        
        rowMapper = CustomerAccountDaoImpl.createRowMapper();
        
        specialAccountInfoRowMapper = CustomerAccountDaoImpl.createCustomerAccountWithNamesRowMapper();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final CustomerAccount account) {
        final int nextId = nextValueHelper.getNextValue("CustomerAccount");
        account.setAccountId(nextId);

        int rowsAffected = simpleJdbcTemplate.update(insertSql, 
                                                     account.getAccountId(),
                                                     account.getAccountSiteId(),
                                                     account.getAccountNumber(),
                                                     account.getCustomerId(),
                                                     account.getBillingAddressId(),
                                                     account.getAccountNotes());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final CustomerAccount account) {
        int rowsAffected = simpleJdbcTemplate.update(removeSql, account.getAccountId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final CustomerAccount account) {
        int rowsAffected = simpleJdbcTemplate.update(updateSql, 
                                                     account.getAccountSiteId(),
                                                     account.getAccountNumber(),
                                                     account.getCustomerId(),
                                                     account.getBillingAddressId(),
                                                     account.getAccountNotes(),
                                                     account.getAccountId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getById(final int accountId) {
        CustomerAccount account = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, accountId);
        return account;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getByAccountNumber(final String accountNumber, final LiteYukonUser user) {
    	final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
    	sqlBuilder.append("SELECT ca.AccountId,AccountSiteId,AccountNumber,ca.CustomerId,BillingAddressId,AccountNotes");
    	sqlBuilder.append("FROM CustomerAccount ca, ECToAccountMapping ecta");
    	sqlBuilder.append("WHERE ca.AccountID = ecta.AccountID");
    	sqlBuilder.append("AND ca.AccountNumber = ?");
    	sqlBuilder.append("AND ecta.EnergyCompanyID = ?");
    	final String sql = sqlBuilder.toString();
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	int engergyCompanyId = energyCompany.getEnergyCompanyID();
    	
        CustomerAccount account = simpleJdbcTemplate.queryForObject(sql, 
        															rowMapper,
        															accountNumber,
        															engergyCompanyId);
        return account;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccount> getByUser(final LiteYukonUser user) {
        Validate.notNull(user, "user parameter cannot be null.");
        
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT AccountId,AccountSiteId,AccountNumber,CustomerAccount.CustomerId,BillingAddressId,AccountNotes ");
        sb.append("FROM CustomerAccount,Customer,Contact,YukonUser ");
        sb.append("WHERE CustomerAccount.CustomerId = Customer.CustomerId "); 
        sb.append("AND Customer.PrimaryContactId = Contact.ContactId ");
        sb.append("AND YukonUser.UserId = Contact.LoginId ");
        sb.append("AND YukonUser.UserId = ? ");
        sb.append("ORDER BY CustomerAccount.AccountId");
        
        String sql = sb.toString();
        List<CustomerAccount> list = simpleJdbcTemplate.query(sql, rowMapper, user.getUserID());
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccount> getAll() {
        List<CustomerAccount> list = simpleJdbcTemplate.query(selectSql, rowMapper, new Object[]{});
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByEC(final int ecId) {
        List<CustomerAccountWithNames> list = simpleJdbcTemplate.query(selectAllUsefulAccountInfoFromECSql, specialAccountInfoRowMapper, ecId);
        return list;
    }
    

    @Override
    public CustomerAccount getAccountByInventoryId(int inventoryId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.*");
        sql.append("FROM CustomerAccount ca, InventoryBase ib");
        sql.append("WHERE ca.AccountId = ib.AccountId"); 
        sql.append("AND ib.InventoryId = ?");
        
        CustomerAccount account = simpleJdbcTemplate.queryForObject(sql.toString(), rowMapper, inventoryId);
        
        return account;
    }
            
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(final int ecId, List<Integer> groupIds,
                                                                             Date startDate, Date stopDate){

        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(" SELECT ca.AccountId, ca.AccountNumber, cont.ContLastName, cont.ContFirstName ");
            sql.append(" FROM CustomerAccount ca, Contact cont, Customer cust ");
            sql.append(" WHERE AccountId IN (SELECT AccountId ");
            sql.append("                     FROM ECToAccountMapping ");
            sql.append("                     WHERE EnergyCompanyId = ?) ");
            sql.append(" AND cust.CustomerId = ca.CustomerId ");
            sql.append(" AND cont.ContactId = cust.PrimaryContactId ");
            sql.append(" AND ca.AccountId in (SELECT LMHCG.AccountId ");
            sql.append("                         FROM LMHardwareControlGroup LMHCG ");
            sql.append("                         WHERE LMHCG.LMGroupId in (", groupIds, ") ");
            sql.append("                         AND (LMHCG.GroupEnrollStart < ?) ");
            sql.append("                         AND ((LMHCG.GroupEnrollStop IS NULL) ");
            sql.append("                               OR (LMHCG.GroupEnrollStop > ?))) ");
            
            List<CustomerAccountWithNames> list = simpleJdbcTemplate.query(sql.toString(), specialAccountInfoRowMapper, ecId, stopDate, startDate);
            return list;        
        } catch (DataAccessException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccountWithNames getAccountWithNamesByCustomerId(final int customerId, final int ecId) {
        List<CustomerAccountWithNames> rs = simpleJdbcTemplate.query(selectAllUsefulAccountInfoFromCustomerIdSql, specialAccountInfoRowMapper, ecId, customerId);
        return rs.get(0);
    }

    @Override
    public CustomerAccount getAccountByContactId(int contactId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM CustomerAccount");
        sql.append("WHERE CustomerId = ");
        sql.append("    (SELECT CustomerId");
        sql.append("     FROM Customer");
        sql.append("     WHERE PrimaryContactId = ?");
        sql.append("     OR CustomerId = ");
        sql.append("        (SELECT CustomerId");
        sql.append("         FROM CustomerAdditionalContact");
        sql.append("         WHERE ContactId = ?))");
        
        CustomerAccount account = simpleJdbcTemplate.queryForObject(sql.toString(), rowMapper, contactId, contactId);
        
        return account;
    }

    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
    
    private static final ParameterizedRowMapper<CustomerAccount> createRowMapper() {
        final ParameterizedRowMapper<CustomerAccount> rowMapper = new ParameterizedRowMapper<CustomerAccount>() {
            public CustomerAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                final CustomerAccount account = new CustomerAccount();
                account.setAccountId(rs.getInt("AccountId"));
                account.setAccountNotes(rs.getString("AccountNotes"));
                account.setAccountNumber(rs.getString("AccountNumber"));
                account.setAccountSiteId(rs.getInt("AccountSiteId"));
                account.setBillingAddressId(rs.getInt("BillingAddressId"));
                account.setCustomerId(rs.getInt("CustomerId"));
                return account;
            }
        };
        return rowMapper;
    }
    
    private static final ParameterizedRowMapper<CustomerAccountWithNames> createCustomerAccountWithNamesRowMapper() {
        final ParameterizedRowMapper<CustomerAccountWithNames> rowMapper = new ParameterizedRowMapper<CustomerAccountWithNames>() {
            public CustomerAccountWithNames mapRow(ResultSet rs, int rowNum) throws SQLException {
                final CustomerAccountWithNames account = new CustomerAccountWithNames();
                account.setAccountId(rs.getInt("AccountId"));
                account.setAccountNumber(rs.getString("AccountNumber"));
                account.setLastName(rs.getString("ContLastName"));
                account.setFirstName(rs.getString("ContFirstName"));
                return account;
            }
        };
        return rowMapper;
    }
    
}
