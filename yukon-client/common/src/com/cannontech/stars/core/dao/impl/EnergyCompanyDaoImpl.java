package com.cannontech.stars.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class EnergyCompanyDaoImpl implements EnergyCompanyDao {
    private final Logger log = YukonLogManager.getLogger(EnergyCompanyDaoImpl.class);

    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private PaoDao paoDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private ConfigurationSource configSource;
    
    private Map<Integer, List<Integer>> cachedRouteIds = new ConcurrentHashMap<>();
    private Map<Integer, EnergyCompany> energyCompanies;
    private Map<Integer, Integer> operatorIdToEcId = new HashMap<Integer, Integer>(); //<UserId, EcId>

    @Autowired
    public EnergyCompanyDaoImpl(AsyncDynamicDataSource asyncDynamicDataSource){
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_ROUTE,
                new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                cachedRouteIds.clear();
            }
        });

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY,
                new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                synchronized (EnergyCompanyDaoImpl.this) {
                    energyCompanies = null;
                    operatorIdToEcId.clear();
                }
            }
        });

        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB) {
                    synchronized (EnergyCompanyDaoImpl.this) {
                        if (energyCompanies != null) {
                            for (EnergyCompany energyCompany : energyCompanies.values()) {
                                if (energyCompany.getUser().getUserID() == dbChange.getId()) {
                                    energyCompanies = null;
                                    return;
                                }
                            }
                        }

                        operatorIdToEcId.remove(dbChange.getId());
                    }
                }
            }
        });
    }

    @Override
    public EnergyCompany getEnergyCompanyByAccountId(int accountId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
        EnergyCompany energyCompany = getEnergyCompany(energyCompanyId);
        return energyCompany;
    }

    @Override
    public EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
        Integer energyCompanyId = operatorIdToEcId.get(operator.getUserID());
        if (energyCompanyId == null) { // populate cache
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ECOLL.EnergyCompanyId");
            sql.append("FROM EnergyCompanyOperatorLoginList ECOLL");
            sql.append("WHERE ECOLL.OperatorLoginId").eq(operator.getUserID());
    
            try {
                energyCompanyId = jdbcTemplate.queryForInt(sql);
                operatorIdToEcId.put(operator.getUserID(), energyCompanyId);
            } catch (EmptyResultDataAccessException e) {
                throw new EnergyCompanyNotFoundException("No energy company found for user id: " + operator.getUserID(), e);
            }
        }
        return getEnergyCompany(energyCompanyId);
    }

    @Override
    public EnergyCompany getEnergyCompany(LiteYukonUser user) {
        try {
            return getEnergyCompanyByOperator(user);
        } catch (EnergyCompanyNotFoundException e) {
            // either not an operator or no energy company associated
            log.debug("EnergyCompany By Operator (" + user + ") Not Found: " + e);
        }

        return getEnergyCompany(getEnergyCompanyIdForUser(user));
    }

    @Override
    public boolean isEnergyCompanyOperator(LiteYukonUser operator) {
        try {
            getEnergyCompanyByOperator(operator);
        } catch (EnergyCompanyNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public EnergyCompany getEnergyCompanyByInventoryId(int inventoryId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForInventoryId(inventoryId);
        EnergyCompany energyCompany = getEnergyCompany(energyCompanyId);
        return energyCompany;
    }

    @Override
    public Collection<EnergyCompany> getAllEnergyCompanies() {
        return getEnergyCompanies().values();
    }

    @Override
    public boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany) {
        return energyCompany.getEnergyCompanyId() == DEFAULT_ENERGY_COMPANY_ID;
    }

    @Deprecated
    @Override
    public List<Integer> getDirectChildEnergyCompanies(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyID");
        sql.append("FROM EnergyCompany");
        sql.append("WHERE ParentEnergyCompanyId").eq(energyCompanyId);

        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    @Deprecated
    @Override
    public Integer getParentEnergyCompany(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ParentEnergyCompanyId");
        sql.append("FROM EnergyCompany");
        sql.append("WHERE EnergyCompanyID").eq(energyCompanyId);
        return jdbcTemplate.queryForObject(sql, TypeRowMapper.INTEGER_NULLABLE);
    }

    @Deprecated
    @Override
    public Integer findParentEnergyCompany(int energyCompanyId) {
        try {
            return getParentEnergyCompany(energyCompanyId);
        } catch (IncorrectResultSizeDataAccessException e) {
            // find
        }

        return null;
    }

    @Override
    public boolean isPrimaryOperator(int operatorLoginId){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(EnergyCompanyId)");
        sql.append("FROM EnergyCompany");
        sql.append("WHERE UserId").eq(operatorLoginId);

        int count = jdbcTemplate.queryForInt(sql);
        return count > 0;
    }

    @Override
    public EnergyCompany getEnergyCompany(int ecId) {
        EnergyCompany energyCompany = getEnergyCompanies().get(ecId);
        if (energyCompany == null) {
            throw new EnergyCompanyNotFoundException("Energy company id = " + ecId + " does not exist.");
        }
        return energyCompany;
    }

    @Override
    public EnergyCompany findEnergyCompany(int ecId) {
        EnergyCompany energyCompany = getEnergyCompanies().get(ecId);
        return energyCompany;
    }

    @Override
    public EnergyCompany getEnergyCompany(String energyCompanyName) {
        EnergyCompany energyCompany = findEnergyCompany(energyCompanyName);
        if (energyCompany == null) {
            throw new NotFoundException("Energy Company with name: " + energyCompanyName + " not found.");
        }
        return energyCompany;
    }

    @Override
    public EnergyCompany findEnergyCompany(String energyCompanyName) {
        for (EnergyCompany energyCompany : getEnergyCompanies().values()) {
            if (energyCompanyName.equalsIgnoreCase(energyCompany.getName())) {
                return energyCompany;
            }
        }
        return null;
    }

    @Override
    public List<Integer> getRouteIds(int ecId) {
        if (cachedRouteIds.containsKey(ecId)) {
            return cachedRouteIds.get(ecId);
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RouteId");
        sql.append("FROM ECToRouteMapping");
        sql.append("WHERE EnergyCompanyId").eq(ecId);

        List<Integer> routeIds = ImmutableList.copyOf(jdbcTemplate.query(sql, TypeRowMapper.INTEGER));
        cachedRouteIds.put(ecId, routeIds);
        return routeIds;
    }

    @Override
    public List<LiteYukonPAObject> getAllRoutes(EnergyCompany energyCompany) {
        if (ecSettingDao.getBoolean(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, energyCompany.getId())) {
            List<LiteYukonPAObject> result = IterableUtils.safeList(paoDao.getAllLiteRoutes());
            return result;
        }

        List<LiteYukonPAObject> inheritedRoutes = ImmutableList.of();
        if (energyCompany.getParent() != null) {
            inheritedRoutes = getAllRoutes(energyCompany.getParent());
        }
        List<LiteYukonPAObject> routeList = new ArrayList<>(inheritedRoutes);
        routeList.addAll(getRoutes(energyCompany));

        Collections.sort(routeList, LiteComparators.liteStringComparator);
        return Collections.unmodifiableList(routeList);
    }

    @Override
    public void addCiCustomer(int customerId, EnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO EnergyCompanyCustomerList");
        sql.values(energyCompany.getId(), customerId);
        jdbcTemplate.update(sql);
    }

    @Override
    public List<Integer> getCiCustomerIds(EnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CustomerID FROM EnergyCompanyCustomerList");
        sql.append("WHERE EnergyCompanyID").eq(energyCompany.getId());
        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    @Override
    public List<EnergyCompany> getEnergyCompaniesByCiCustomer(int ciCustomerId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyID FROM EnergyCompanyCustomerList");
        sql.append("WHERE CustomerID").eq(ciCustomerId);
        List<Integer> ecIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);

        List<EnergyCompany> customerEnergyCompanies = new ArrayList<>();
        for (Integer ecId : ecIds) {
            customerEnergyCompanies.add(getEnergyCompany(ecId));
        }
        return customerEnergyCompanies;
    }

    @Override
    public int createEnergyCompany(String name, int contactId, LiteYukonUser ecUser) {
        int energyCompanyId = nextValueHelper.getNextValue("EnergyCompany");

        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        SqlParameterSink insertParams = insertSql.insertInto("EnergyCompany");
        insertParams.addValue("EnergyCompanyId", energyCompanyId);
        insertParams.addValue("Name", name);
        insertParams.addValue("PrimaryContactId", contactId);
        insertParams.addValue("UserId", ecUser.getUserID());
        jdbcTemplate.update(insertSql);

        return energyCompanyId;
    }

    @Override
    public void updateCompanyName(String name, int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE EnergyCompany");
        sql.append("SET Name").eq(name);
        sql.append("WHERE EnergyCompanyId").eq_k(ecId);

        jdbcTemplate.update(sql);

        dbChangeManager.processDbChange(ecId, DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,  DBChangeMsg.CAT_ENERGY_COMPANY,
                                        DBChangeMsg.CAT_ENERGY_COMPANY, DbChangeType.UPDATE);
    }

    @Override
    public List<Integer> getOperatorUserIds(EnergyCompany energyCompany) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT OperatorLoginID");
        sql.append("FROM EnergyCompanyOperatorLoginList");
        sql.append("WHERE EnergyCompanyID").eq(energyCompany.getId());
        List<Integer> list = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        return list;
    }

    private List<LiteYukonPAObject> getRoutes(EnergyCompany energyCompany) {
        List<Integer> routeIDs = getRouteIds(energyCompany.getId());
        List<LiteYukonPAObject> routeList =  Lists.newArrayListWithCapacity(routeIDs.size());
        for (Integer routeId : routeIDs) {
            LiteYukonPAObject liteRoute = paoDao.getLiteYukonPAO(routeId);
            routeList.add(liteRoute);
        }

        return Collections.unmodifiableList(routeList);
    }

    /**
     * Return energy company id for customer account or default energy company's id
     */
    private int getEnergyCompanyIdForUser(LiteYukonUser user) {
        // primary contact
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ectam.EnergyCompanyId");
            sql.append("FROM ECToAccountMapping ectam, CustomerAccount ca, Customer cu, Contact c");
            sql.append("WHERE ectam.AccountId = ca.AccountId");
            sql.append("    AND ca.CustomerId = cu.CustomerId");
            sql.append("    AND cu.PrimaryContactID = c.ContactID");
            sql.append("    AND c.LoginId").eq(user.getUserID());

            return jdbcTemplate.queryForInt(sql);
        } catch(IncorrectResultSizeDataAccessException e) {
            log.debug("EnergyCompany By Contact (" + user + ") Not Found: " + e);
        }

        // additional contact
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ectam.EnergyCompanyId");
            sql.append("FROM ECToAccountMapping ectam, CustomerAccount ca,");
            sql.append("    CustomerAdditionalContact cac, Contact c");
            sql.append("WHERE ectam.AccountId = ca.AccountId");
            sql.append("    AND ca.CustomerId = cac.CustomerID");
            sql.append("    AND cac.ContactID = c.ContactID");
            sql.append("    AND c.LoginId").eq(user.getUserID());

            return jdbcTemplate.queryForInt(sql);
        } catch(IncorrectResultSizeDataAccessException e) {
            log.debug("EnergyCompany By AdditionalContact (" + user + ") Not Found: " + e);
        }

        return DEFAULT_ENERGY_COMPANY_ID;
    }

    private synchronized Map<Integer, EnergyCompany> getEnergyCompanies() {
        if (energyCompanies == null) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT EC.EnergyCompanyId as EcId, EC.ParentEnergyCompanyId as ParentEcId,");
            sql.append(    "Name, PrimaryContactID, YU.UserId, UserName, Status, ForceReset,UserGroupId ");
            sql.append("FROM EnergyCompany EC");
            sql.append("JOIN YukonUser YU on EC.UserId = YU.UserId");

            EnergyCompanyRowCallbackHandler energyCompanyRowCallbackHandler = new EnergyCompanyRowCallbackHandler();
            jdbcTemplate.query(sql, energyCompanyRowCallbackHandler);
            energyCompanies = energyCompanyRowCallbackHandler.getEnergyCompanies();
        }
        return energyCompanies;
    }

    private static class EnergyCompanyRowCallbackHandler implements YukonRowCallbackHandler {
        private EnergyCompany.Builder builder = new EnergyCompany.Builder();

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            LiteYukonUser user = new LiteYukonUser(rs.getInt("UserID"));
            user.setUsername(rs.getString("UserName"));
            user.setLoginStatus(LoginStatusEnum.retrieveLoginStatus(rs.getString("Status")));
            user.setForceReset(rs.getBoolean("ForceReset"));
            user.setUserGroupId(rs.getNullableInt("UserGroupId"));

            builder.addEnergyCompany(rs.getInt("EcId"), rs.getString("Name"), user, rs.getInt("PrimaryContactId"),
                                     rs.getNullableInt("ParentEcId"));
        }

        public Map<Integer, EnergyCompany> getEnergyCompanies() {
            return builder.build();
        }
    }
    
    @Override
    public EnergyCompany getDefaultEnergyCompanyForThirdPartyApiOrSystemUsage() {
        Optional<String> energyCompanyName = configSource.getOptionalString(MasterConfigString.RFN_ENERGY_COMPANY_NAME);
        EnergyCompany energyCompany = null;
        if (energyCompanyName.isPresent()) {
            energyCompany = findEnergyCompany(energyCompanyName.get());
        }
        if (energyCompany == null) {
            List<EnergyCompany> energyCompanies = (List<EnergyCompany>) getAllEnergyCompanies();
            energyCompanies.removeIf(e -> e.getId() == -1);
            if (energyCompanies.size() == 1) {
                return energyCompanies.get(0);
            } else {
                throw new EnergyCompanyNotFoundException("No value is specified for the RFN_ENERGY_COMPANY_NAME configuration property in master.cfg");
            }
        }
        return energyCompany;
        
    }
}
