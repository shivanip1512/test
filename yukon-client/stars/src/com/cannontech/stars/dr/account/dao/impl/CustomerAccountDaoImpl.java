package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountRowMapper;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CustomerAccountDaoImpl implements CustomerAccountDao {
	
	private final Logger logger = YukonLogManager.getLogger(CustomerAccountDaoImpl.class);
	
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllUsefulAccountInfoFromECSql;
    private static final ParameterizedRowMapper<CustomerAccount> rowMapper;
    private static final ParameterizedRowMapper<CustomerAccountWithNames> specialAccountInfoRowMapper;
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonUserDao yukonUserDao;
    private ContactDao contactDao;
    
    private SqlStatementBuilder selectSql = new SqlStatementBuilder();
    {
        selectSql.appendFragment(new CustomerAccountRowMapper().getBaseQuery());
    }
    
    private final SqlFragmentSource selectAccountContactInfoSql; 
    {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CA.AccountId, CA.AccountNumber, CUST.AltTrackNum,");
        sql.append(       "CONT.ContLastName, CONT.ContFirstName");
        sql.append("FROM CustomerAccount CA");
        sql.append("JOIN Customer CUST ON CUST.CustomerId = CA.CustomerId");
        sql.append("JOIN Contact CONT ON CONT.ContactId = CUST.PrimaryContactId");
        
        selectAccountContactInfoSql = sql;
    }
    
    static {
        
        insertSql = "INSERT INTO CustomerAccount (AccountId,AccountSiteId,AccountNumber,CustomerId,BillingAddressId,AccountNotes) VALUES (?,?,?,?,?,?)";
        
        removeSql = "DELETE FROM CustomerAccount WHERE AccountId = ?";
        
        updateSql = "UPDATE CustomerAccount SET AccountSiteId = ?, AccountNumber = ?, CustomerId = ?, BillingAddressId = ?, AccountNotes = ? WHERE AccountId = ?";
        
        selectAllUsefulAccountInfoFromECSql = "SELECT ca.AccountId, ca.AccountNumber, cont.ContLastName, cont.ContFirstName" +
                " FROM CustomerAccount ca JOIN Customer cust ON cust.CustomerId = ca.CustomerId " + 
                " JOIN Contact cont ON cont.ContactId = cust.PrimaryContactId " +
                " JOIN ECToAccountMapping ec ON ec.AccountId = ca.AccountId " + 
                " WHERE EnergyCompanyId = ? ";
        
        rowMapper = CustomerAccountDaoImpl.createRowMapper();
        
        specialAccountInfoRowMapper = CustomerAccountDaoImpl.createCustomerAccountWithNamesRowMapper();
    }
    
    @Override
    public LiteYukonUser getYukonUserByAccountId(int accountId) {
        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
        return yukonUserDao.getLiteYukonUser(primaryContact.getLoginID());
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(final CustomerAccount account) {
        final int nextId = nextValueHelper.getNextValue("CustomerAccount");
        account.setAccountId(nextId);

        int rowsAffected = yukonJdbcTemplate.update(insertSql, 
                                                     account.getAccountId(),
                                                     account.getAccountSiteId(),
                                                     account.getAccountNumber(),
                                                     account.getCustomerId(),
                                                     account.getBillingAddressId(),
                                                     SqlUtils.convertStringToDbValue(account.getAccountNotes()));
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final CustomerAccount account) {
        int rowsAffected = yukonJdbcTemplate.update(removeSql, account.getAccountId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(final CustomerAccount account) {
        int rowsAffected = yukonJdbcTemplate.update(updateSql, 
                                                     account.getAccountSiteId(),
                                                     account.getAccountNumber(),
                                                     account.getCustomerId(),
                                                     account.getBillingAddressId(),
                                                     SqlUtils.convertStringToDbValue(account.getAccountNotes()),
                                                     account.getAccountId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getById(final int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("WHERE AccountId").eq(accountId);
        
        CustomerAccount account = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return account;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getByAccountNumber(final String accountNumber, final LiteYukonUser user) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        return getByAccountNumberForDescendentsOfEnergyCompany(accountNumber, energyCompany);
    }
    
    @Override
    public CustomerAccount getByAccountNumber(final String accountNumber, final int energyCompanyId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ca.AccountId,AccountSiteId,AccountNumber,ca.CustomerId,BillingAddressId,AccountNotes");
        sqlBuilder.append("FROM CustomerAccount ca, ECToAccountMapping ecta");
        sqlBuilder.append("WHERE ca.AccountID = ecta.AccountID");
        sqlBuilder.append("AND ca.AccountNumber = ?");
        sqlBuilder.append("AND ecta.EnergyCompanyID = ?");
        final String sql = sqlBuilder.toString();
        
        CustomerAccount account = null;
        try {
            account = yukonJdbcTemplate.queryForObject(sql,
                                                        rowMapper,
                                                        accountNumber,
                                                        energyCompanyId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Account with account number: " + accountNumber + " could not be found.", e);
        }
        return account;
    }
    
    @Override
    public CustomerAccount getByAccountNumberForDescendentsOfEnergyCompany(final String accountNumber,
                                                               YukonEnergyCompany yukonEnergyCompany) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        List<LiteStarsEnergyCompany> allDescendants = ECUtils.getAllDescendants(energyCompany);
        List<Integer> energyCompanyIds = new ArrayList<Integer>();
        for (LiteStarsEnergyCompany liteStarsEnergyCompany : allDescendants) {
            energyCompanyIds.add(liteStarsEnergyCompany.getEnergyCompanyId());
        }
        return getByAccountNumber(accountNumber, energyCompanyIds);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerAccount getByAccountNumber(final String accountNumber, List<Integer> energyCompanyIds) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ca.AccountId,AccountSiteId,AccountNumber,ca.CustomerId,BillingAddressId,AccountNotes");
        sqlBuilder.append("FROM CustomerAccount ca, ECToAccountMapping ecta");
        sqlBuilder.append("WHERE ca.AccountID = ecta.AccountID");
        sqlBuilder.append("AND ca.AccountNumber = ?");
        sqlBuilder.append("AND ecta.EnergyCompanyID in (",energyCompanyIds,")");
        final String sql = sqlBuilder.toString();
        
        CustomerAccount account = null;
        try {
            account = yukonJdbcTemplate.queryForObject(sql, 
                                                        rowMapper,
                                                        accountNumber);
        } catch (EmptyResultDataAccessException e) {
        	throw new NotFoundException("Account with account number: " + accountNumber + " could not be found.", e);
        }
        return account;
    }

    @Override
    public List<CustomerAccount> getByUser(LiteYukonUser user) {

    	// Load all based on user's Primary Contact
    	List<CustomerAccount> primaryAccountList = getByPrimaryContactUser(user);

    	// Load all based on user's Additional Contact
        List<CustomerAccount> additionalAccountList = getAccountByAdditionalContactUser(user);
        List<CustomerAccount> result = Lists.newArrayList(Iterables.concat(primaryAccountList, additionalAccountList));
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccount> getAll() {
        List<CustomerAccount> list = yukonJdbcTemplate.query(selectSql, rowMapper);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByEC(final int ecId) {
        List<CustomerAccountWithNames> list = yukonJdbcTemplate.query(selectAllUsefulAccountInfoFromECSql, specialAccountInfoRowMapper, ecId);
        return list;
    }

    @Override
    public CustomerAccount getAccountByInventoryId(int inventoryId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.*");
        sql.append("FROM CustomerAccount ca, InventoryBase ib");
        sql.append("WHERE ca.AccountId = ib.AccountId"); 
        sql.append("AND ib.InventoryId").eq(inventoryId);
        
        CustomerAccount account = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        
        return account;
    }
            
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(final int ecId, List<Integer> groupIds,
                                                                             Date startDate, Date stopDate){

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.AccountId, ca.AccountNumber, cust.AltTrackNum, cont.ContLastName, cont.ContFirstName ");
        sql.append("FROM CustomerAccount ca JOIN Customer cust ON ca.CustomerId = cust.CustomerId");
        sql.append("  JOIN Contact cont ON cont.ContactId = cust.PrimaryContactId");
        sql.append("  JOIN ECToAccountMapping ec ON ec.AccountId = ca.AccountId");
        sql.append("WHERE EnergyCompanyId").eq(ecId);
        sql.append("  AND ca.AccountId IN");
        sql.append("    (SELECT LMHCG.AccountId");
        sql.append("    FROM LMHardwareControlGroup LMHCG ");
        sql.append("    WHERE LMHCG.LMGroupId").in(groupIds);
        sql.append("      AND (LMHCG.GroupEnrollStart").lte(stopDate).append(") ");
        sql.append("      AND ((LMHCG.GroupEnrollStop IS NULL) ");
        sql.append("      OR (LMHCG.GroupEnrollStop").gte(startDate).append("))) ");
    
        List<CustomerAccountWithNames> list = yukonJdbcTemplate.query(sql, specialAccountInfoRowMapper);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccountWithNames getAccountWithNamesByCustomerId(final int customerId, final int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectAccountContactInfoSql);
        sql.append("WHERE CA.AccountId").in(getAccountIdsByECSql(Collections.singletonList(ecId)));
        sql.append("AND CUST.CustomerId").eq(customerId);
        
        List<CustomerAccountWithNames> rs = yukonJdbcTemplate.query(sql, specialAccountInfoRowMapper);
        return rs.get(0);
    }

    @Override
    public Map<Integer, CustomerAccountWithNames> getAccountsWithNamesByAccountId(
            Iterable<Integer> accountIds) {
        ChunkingMappedSqlTemplate template =
            new ChunkingMappedSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(selectAccountContactInfoSql);
                sql.append("WHERE ca.accountId").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        ParameterizedRowMapper<Map.Entry<Integer, CustomerAccountWithNames>> rowMapper =
            new ParameterizedRowMapper<Entry<Integer,CustomerAccountWithNames>>() {
            @Override
            public Entry<Integer, CustomerAccountWithNames> mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                CustomerAccountWithNames acctInfo = specialAccountInfoRowMapper.mapRow(rs, rowNum);
                Integer accountId = acctInfo.getAccountId();
                return Maps.immutableEntry(accountId, acctInfo);
            }
        };

        Map<Integer, CustomerAccountWithNames> retVal =
            template.mappedQuery(sqlGenerator, accountIds, rowMapper, typeMapper);
        return retVal;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccountWithNames getAcountWithNamesByAccountNumber(String accountNumber, 
                                                                              final int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectAccountContactInfoSql);
        sql.append("WHERE CA.AccountId").in(getAccountIdsByECSql(Collections.singletonList(ecId)));
        sql.append("AND CA.AccountNumber").eq(accountNumber);
        
        CustomerAccountWithNames account = yukonJdbcTemplate.queryForObject(sql, specialAccountInfoRowMapper);
        return account;
    }
    
	@Override
    public CustomerAccount getCustomerAccount(LiteYukonUser user) {
    	List<CustomerAccount> accountList = getByUser(user);

        if (accountList.size() == 0) {
            throw new NotAuthorizedException("The supplied user's contact is not assigned to an account.");
        } else {
            if (accountList.size() > 1) {
                logger.warn("Multiple accounts associated with user" + user.getUsername() + ", returning an arbitrary account.");
            }
            return accountList.get(0);
        }
    }

    private SqlStatementBuilder getAccountIdsByECSql(Iterable<Integer> energyCompanyIds) {
        SqlStatementBuilder accountIdsByECSql = new SqlStatementBuilder();
        accountIdsByECSql.append("SELECT AccountId");
        accountIdsByECSql.append("FROM ECToAccountMapping");
        accountIdsByECSql.append("WHERE EnergyCompanyId").in(energyCompanyIds);
        
        return accountIdsByECSql;
    }

    @Override
    public CustomerAccount getAccountByContactId(int contactId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM CustomerAccount");
        sql.append("WHERE CustomerId = ");
        sql.append("    (SELECT CustomerId");
        sql.append("     FROM Customer");
        sql.append("     WHERE PrimaryContactId").eq(contactId);
        sql.append("     OR CustomerId = ");
        sql.append("        (SELECT CustomerId");
        sql.append("         FROM CustomerAdditionalContact");
        sql.append("         WHERE ContactId").eq(contactId).append("))");
        
        CustomerAccount account = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return account;
    }
    
    @Override
    public int getTotalNumberOfAccounts(LiteStarsEnergyCompany energyCompany) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT COUNT(*)");
    	sql.append("FROM CustomerAccount ca");
    	sql.append("	JOIN ECToAccountMapping ectam ON ca.AccountId = ectam.AccountId");
    	sql.append("WHERE ectam.EnergyCompanyId").eq(energyCompany.getEnergyCompanyId());
    	
    	int totalNumberOfAccounts = yukonJdbcTemplate.queryForInt(sql);
    	
    	return totalNumberOfAccounts;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    private List<CustomerAccount> getByPrimaryContactUser(final LiteYukonUser user) {
        Validate.notNull(user, "user parameter cannot be null.");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AccountId,AccountSiteId,AccountNumber,CustomerAccount.CustomerId,BillingAddressId,AccountNotes ");
        sql.append("FROM CustomerAccount,Customer,Contact,YukonUser ");
        sql.append("WHERE CustomerAccount.CustomerId = Customer.CustomerId "); 
        sql.append("AND Customer.PrimaryContactId = Contact.ContactId ");
        sql.append("AND YukonUser.UserId = Contact.LoginId ");
        sql.append("AND YukonUser.UserId").eq(user.getUserID());
        sql.append("ORDER BY CustomerAccount.AccountId");
        
        List<CustomerAccount> list = yukonJdbcTemplate.query(sql, rowMapper);
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    private List<CustomerAccount> getAccountByAdditionalContactUser(LiteYukonUser user) {
        Validate.notNull(user, "user parameter cannot be null.");

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AccountId, AccountSiteId, AccountNumber, CA.CustomerId,");
        sql.append("       BillingAddressId, AccountNotes ");
        sql.append("FROM CustomerAccount CA ");
        sql.append("JOIN CustomerAdditionalContact CAC ON CA.CustomerId = CAC.CustomerId ");
        sql.append("JOIN Contact Cont ON CAC.ContactId = Cont.ContactId ");
        sql.append("JOIN YukonUser YU ON YU.UserId = Cont.LoginId ");
        sql.append("WHERE YU.UserId").eq(user.getUserID());
        sql.append("ORDER BY CA.AccountId");

        List<CustomerAccount> list = yukonJdbcTemplate.query(sql, rowMapper);
        return list;
    }
    
    private static final ParameterizedRowMapper<CustomerAccount> createRowMapper() {
        final ParameterizedRowMapper<CustomerAccount> rowMapper = new ParameterizedRowMapper<CustomerAccount>() {
            public CustomerAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                final CustomerAccount account = new CustomerAccount();
                account.setAccountId(rs.getInt("AccountId"));
                account.setAccountNotes(SqlUtils.convertDbValueToString(rs, "AccountNotes"));
                account.setAccountNumber(SqlUtils.convertDbValueToString(rs, "AccountNumber"));
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
                account.setAccountNumber(SqlUtils.convertDbValueToString(rs.getString("AccountNumber")));
                account.setAlternateTrackingNumber(SqlUtils.convertDbValueToString(rs.getString("AltTrackNum")));
                account.setLastName(SqlUtils.convertDbValueToString(rs.getString("ContLastName")));
                account.setFirstName(SqlUtils.convertDbValueToString(rs.getString("ContFirstName")));
                return account;
            }
        };
        return rowMapper;
    }

    // DI Setters
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
}