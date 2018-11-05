package com.cannontech.stars.dr.account.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountRowMapper;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CustomerAccountDaoImpl implements CustomerAccountDao {
    private final static Logger logger = YukonLogManager.getLogger(CustomerAccountDaoImpl.class);

    @Autowired private ContactDao contactDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserDao userDao;

    private static final YukonRowMapper<CustomerAccount> rowMapper = new YukonRowMapper<CustomerAccount>() {
        @Override
        public CustomerAccount mapRow(YukonResultSet rs) throws SQLException {
            CustomerAccount account = new CustomerAccount();
            account.setAccountId(rs.getInt("AccountId"));
            account.setAccountNotes(rs.getStringSafe("AccountNotes"));
            account.setAccountNumber(rs.getStringSafe("AccountNumber"));
            account.setAccountSiteId(rs.getInt("AccountSiteId"));
            account.setBillingAddressId(rs.getInt("BillingAddressId"));
            account.setCustomerId(rs.getInt("CustomerId"));
            return account;
        }
    };
    private static final YukonRowMapper<CustomerAccountWithNames> specialAccountInfoRowMapper =
        new YukonRowMapper<CustomerAccountWithNames>() {
            @Override
            public CustomerAccountWithNames mapRow(YukonResultSet rs) throws SQLException {
                CustomerAccountWithNames account = new CustomerAccountWithNames();
                account.setAccountId(rs.getInt("AccountId"));
                account.setAccountNumber(rs.getStringSafe("AccountNumber"));
                account.setAlternateTrackingNumber(rs.getStringSafe("AltTrackNum"));
                account.setLastName(rs.getStringSafe("ContLastName"));
                account.setFirstName(rs.getStringSafe("ContFirstName"));
                return account;
            }
        };

    private SimpleTableAccessTemplate<CustomerAccount> customerAccountTemplate;

    private final AdvancedFieldMapper<CustomerAccount> customerAccountFieldMapper =
        new AdvancedFieldMapper<CustomerAccount>() {
            @Override
            public void extractValues(SqlParameterChildSink p, CustomerAccount account) {
                p.addValue("AccountSiteId", account.getAccountSiteId());
                p.addValue("AccountNumber", account.getAccountNumber());
                p.addValue("CustomerId", account.getCustomerId());
                p.addValue("BillingAddressId", account.getBillingAddressId());
                p.addValueSafe("AccountNotes", account.getAccountNotes());
            }

            @Override
            public Number getPrimaryKey(CustomerAccount account) {
                return account.getAccountId();
            }

            @Override
            public void setPrimaryKey(CustomerAccount account, int newId) {
                account.setAccountId(newId);
            }
        };

    @PostConstruct
    public void init() throws Exception {
        customerAccountTemplate = new SimpleTableAccessTemplate<>(jdbcTemplate, nextValueHelper);
        customerAccountTemplate.setTableName("CustomerAccount");
        customerAccountTemplate.setPrimaryKeyField("AccountId");
        customerAccountTemplate.setAdvancedFieldMapper(customerAccountFieldMapper);
        customerAccountTemplate.setPrimaryKeyValidOver(0);
    }

    private final SqlStatementBuilder selectSql = new SqlStatementBuilder();
    {
        selectSql.appendFragment(new CustomerAccountRowMapper().getBaseQuery());
    }

    private final SqlFragmentSource selectAccountContactInfoSql;
    {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CA.AccountId, CA.AccountNumber, CUST.AltTrackNum,");
        sql.append("CONT.ContLastName, CONT.ContFirstName");
        sql.append("FROM CustomerAccount CA");
        sql.append("JOIN Customer CUST ON CUST.CustomerId = CA.CustomerId");
        sql.append("JOIN Contact CONT ON CONT.ContactId = CUST.PrimaryContactId");

        selectAccountContactInfoSql = sql;
    }

    @Override
    public LiteYukonUser getYukonUserByAccountId(int accountId) {
        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
        return userDao.getLiteYukonUser(primaryContact.getLoginID());
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void add(CustomerAccount account) {
        customerAccountTemplate.insert(account);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(final CustomerAccount account) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CustomerAccount");
        sql.append("WHERE AccountId").eq(account.getAccountId());

        int rowsAffected = jdbcTemplate.update(sql);
        boolean result = (rowsAffected == 1);
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void update(CustomerAccount account) {
        customerAccountTemplate.update(account);
    }

    @Override
    public List<CustomerAccount> getCustomerAccountsByAccountNumbers(Set<String> accountNumbers, int energyCompanyId) {
        SqlFragmentGenerator<String> sqlGenerator = new SqlFragmentGenerator<String>() {
            @Override
            public SqlFragmentSource generate(List<String> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(selectSql);
                sql.append("JOIN ECToAccountMapping ec ON ec.AccountId = ca.AccountId");
                sql.append("WHERE ec.EnergyCompanyId").eq_k(energyCompanyId);
                sql.append("AND AccountNumber").in(accountNumbers);
                return sql;
                
            }
        };
        ChunkingSqlTemplate chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        return chunkingSqlTemplate.query(sqlGenerator, accountNumbers, rowMapper);
    }
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getById(final int accountId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("WHERE AccountId").eq(accountId);

        CustomerAccount account = jdbcTemplate.queryForObject(sql, rowMapper);
        return account;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccount getByAccountNumber(final String accountNumber, final LiteYukonUser ecOperator) {
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(ecOperator);
        return getByAccountNumber(accountNumber, ec.getDescendants(true));
    }

    @Override
    public CustomerAccount getByAccountNumber(final String accountNumber, final int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.AccountId,AccountSiteId,AccountNumber,ca.CustomerId,BillingAddressId,AccountNotes");
        sql.append("FROM CustomerAccount ca, ECToAccountMapping ecta");
        sql.append("WHERE ca.AccountID = ecta.AccountID");
        sql.append("  AND ca.AccountNumber").eq(accountNumber);
        sql.append("  AND ecta.EnergyCompanyId").eq_k(energyCompanyId);

        CustomerAccount account = null;
        try {
            account = jdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Account with account number: " + accountNumber + " could not be found.", e);
        }
        return account;
    }

    @Override
    public CustomerAccount getByAccountNumber(String accountNumber,
            Iterable<? extends YukonEnergyCompany> energyCompanies) {
        Iterable<Integer> energyCompanyIds = Iterables.transform(energyCompanies, EnergyCompanyDao.TO_ID_FUNCTION);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.AccountId,AccountSiteId,AccountNumber,ca.CustomerId,BillingAddressId,AccountNotes");
        sql.append("FROM CustomerAccount ca, ECToAccountMapping ecta");
        sql.append("WHERE ca.AccountID = ecta.AccountID");
        sql.append("  AND ca.AccountNumber").eq(accountNumber);
        sql.append("  AND ecta.EnergyCompanyId").in(energyCompanyIds);

        CustomerAccount account = null;
        try {
            account = jdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Account with account number: " + accountNumber + " could not be found.", e);
        }
        return account;
    }

    @Override
    public CustomerAccount findByAccountNumber(String accountNumber,
            Iterable<? extends YukonEnergyCompany> energyCompanies) {
        try {
            return getByAccountNumber(accountNumber, energyCompanies);
        } catch (NotFoundException e) {
            return null;
        }
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

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CustomerAccount> getAll() {
        List<CustomerAccount> list = jdbcTemplate.query(selectSql, rowMapper);
        return list;
    }

    @Override
    public CustomerAccount getAccountByInventoryId(int inventoryId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ca.*");
        sql.append("FROM CustomerAccount ca, InventoryBase ib");
        sql.append("WHERE ca.AccountId = ib.AccountId");
        sql.append("AND ib.InventoryId").eq(inventoryId);

        CustomerAccount account = jdbcTemplate.queryForObject(sql, rowMapper);

        return account;
    }

    @Override
    public Map<Integer, CustomerAccount> getInventoryIdsToAccountMap(Collection<Integer> inventoryIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CA.*, IB.InventoryId");
        sql.append("FROM CustomerAccount CA");
        sql.append("  JOIN InventoryBase IB ON IB.AccountId = CA.AccountId");
        sql.append("WHERE IB.InventoryId").in(inventoryIds);

        final Map<Integer, CustomerAccount> inventoryIdToCustomerAccount = Maps.newHashMap();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                CustomerAccount customerAccount = rowMapper.mapRow(rs);
                inventoryIdToCustomerAccount.put(rs.getInt("InventoryId"), customerAccount);
            }
        });

        return inventoryIdToCustomerAccount;
    }

    @Override
    public Map<Integer, Integer> getAccountIdsByInventoryIds(Iterable<Integer> inventoryIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        Function<Integer, Integer> identityFunction = Functions.identity();

        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ib.InventoryId, ca.AccountId");
                sql.append("FROM CustomerAccount ca, InventoryBase ib");
                sql.append("WHERE ca.AccountId = ib.AccountId");
                sql.append("AND ib.InventoryId").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer, Integer>> rowMapper = new YukonRowMapper<Entry<Integer, Integer>>() {
            @Override
            public Entry<Integer, Integer> mapRow(YukonResultSet rs) throws SQLException {
                Integer inventoryId = rs.getInt("InventoryId");
                Integer accountId = rs.getInt("AccountId");
                return Maps.immutableEntry(inventoryId, accountId);
            }
        };
        return template.mappedQuery(sqlFragmentGenerator, inventoryIds, rowMapper, identityFunction);
    }

    @Override
    public Map<Integer, String> getAccountNumbersByAccountIds(Iterable<Integer> accountIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        Function<Integer, Integer> identityFunction = Functions.identity();

        SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ca.AccountId, ca.AccountNumber");
                sql.append("FROM CustomerAccount ca");
                sql.append("WHERE ca.AccountId").in(subList);
                return sql;
            }
        };

        YukonRowMapper<Map.Entry<Integer, String>> rowMapper = new YukonRowMapper<Map.Entry<Integer, String>>() {
            @Override
            public Entry<Integer, String> mapRow(YukonResultSet rs) throws SQLException {
                Integer accountId = rs.getInt("AccountId");
                String accountNumber = rs.getString("AccountNumber");
                return Maps.immutableEntry(accountId, accountNumber);
            }
        };
        return template.mappedQuery(sqlFragmentGenerator, accountIds, rowMapper, identityFunction);
    }

    @Override
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(final int ecId, List<Integer> groupIds,
            Date startDate, Date stopDate) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT ca.AccountId, ca.AccountNumber, cust.AltTrackNum, cont.ContLastName, cont.ContFirstName ");
        sql.append("FROM CustomerAccount ca JOIN Customer cust ON ca.CustomerId = cust.CustomerId");
        sql.append("  JOIN Contact cont ON cont.ContactId = cust.PrimaryContactId");
        sql.append("  JOIN ECToAccountMapping ec ON ec.AccountId = ca.AccountId");
        sql.append("  JOIN LMHardwareControlGroup LMHCG ON LMHCG.AccountId = ca.AccountId");
        sql.append("WHERE EnergyCompanyId").eq(ecId);
        sql.append("  AND LMHCG.LMGroupId").in(groupIds);
        sql.append("  AND (LMHCG.GroupEnrollStart").lte(stopDate).append(")");
        sql.append("  AND ((LMHCG.GroupEnrollStop IS NULL) ");
        sql.append("    OR (LMHCG.GroupEnrollStop").gte(startDate).append("))");

        List<CustomerAccountWithNames> list = jdbcTemplate.query(sql, specialAccountInfoRowMapper);
        return list;
    }

    @Override
    public Map<Integer, CustomerAccountWithNames> getAccountsWithNamesByAccountId(Iterable<Integer> accountIds) {
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(selectAccountContactInfoSql);
                sql.append("WHERE ca.accountId").in(subList);
                return sql;
            }
        };

        Function<Integer, Integer> typeMapper = Functions.identity();
        YukonRowMapper<Map.Entry<Integer, CustomerAccountWithNames>> rowMapper =
            new YukonRowMapper<Entry<Integer, CustomerAccountWithNames>>() {
                @Override
                public Entry<Integer, CustomerAccountWithNames> mapRow(YukonResultSet rs) throws SQLException {
                    CustomerAccountWithNames acctInfo = specialAccountInfoRowMapper.mapRow(rs);
                    Integer accountId = acctInfo.getAccountId();
                    return Maps.immutableEntry(accountId, acctInfo);
                }
            };

        Map<Integer, CustomerAccountWithNames> retVal =
            template.mappedQuery(sqlGenerator, accountIds, rowMapper, typeMapper);
        return retVal;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CustomerAccountWithNames getAcountWithNamesByAccountNumber(String accountNumber, final int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectAccountContactInfoSql);
        sql.append("WHERE CA.AccountId").in(getAccountIdsByECSql(Collections.singletonList(ecId)));
        sql.append("AND CA.AccountNumber").eq(accountNumber);

        CustomerAccountWithNames account = jdbcTemplate.queryForObject(sql, specialAccountInfoRowMapper);
        return account;
    }

    @Override
    public CustomerAccount getCustomerAccount(LiteYukonUser user) {
        List<CustomerAccount> accountList = getByUser(user);

        if (accountList.size() == 0) {
            throw new NotAuthorizedException("The supplied user's contact is not assigned to an account.");
        }
        if (accountList.size() > 1) {
            logger.warn("Multiple accounts associated with user" + user.getUsername()
                + ", returning an arbitrary account.");
        }
        return accountList.get(0);
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

        CustomerAccount account = jdbcTemplate.queryForObject(sql, rowMapper);
        return account;
    }

    @Override
    public CustomerAccount getAccountByCustomerId(int customerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM CustomerAccount");
        sql.append("WHERE CustomerId").eq(customerId);

        CustomerAccount account = jdbcTemplate.queryForObject(sql, rowMapper);
        return account;
    }

    @Override
    public int getTotalNumberOfAccounts(final YukonEnergyCompany ec, List<Integer> assignedProgramIds) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(DISTINCT CA.AccountId)");
        sql.append("FROM CustomerAccount CA");
        sql.append("    JOIN ECToAccountMapping ECTAM ON CA.AccountId = ECTAM.AccountId");

        // If the assigned programs are supplied let's use the active enrollments of those programs to
        // calculate the total number of accounts.
        if (!CollectionUtils.isEmpty(assignedProgramIds)) {
            sql.append("  JOIN LMHardwareControlGroup LMHCG ON (CA.AccountId = LMHCG.AccountId  AND "
                + " LMHCG.GroupEnrollStart IS NOT NULL AND " + " LMHCG.GroupEnrollStop IS NULL)");
        }

        sql.append("WHERE ECTAM.EnergyCompanyId").eq_k(ec.getEnergyCompanyId());
        if (CollectionUtils.isEmpty(assignedProgramIds)) {
            return jdbcTemplate.queryForInt(sql);
        }

        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                return new SqlStatementBuilder().append(sql).append("AND LMHCG.ProgramId").in(subList);
            };
        };

        return template.queryForSum(sqlGenerator, assignedProgramIds);
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

        List<CustomerAccount> list = jdbcTemplate.query(sql, rowMapper);
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

        List<CustomerAccount> list = jdbcTemplate.query(sql, rowMapper);
        return list;
    }
}