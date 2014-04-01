package com.cannontech.stars.core.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.RowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class YukonEnergyCompanyServiceImpl implements YukonEnergyCompanyService {
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private Map<Integer, List<Integer>> cachedRouteIds = new ConcurrentHashMap<>();
    private Map<Integer, YukonEnergyCompany> cachedEnergyCompanies = new ConcurrentHashMap<>();

    @Autowired
    public YukonEnergyCompanyServiceImpl(AsyncDynamicDataSource asyncDynamicDataSource){
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
                cachedEnergyCompanies.clear();
            }
        });
        
    }
    
    private static YukonRowMapper<EnergyCompany> energyCompanyRowMapper = new YukonRowMapper<EnergyCompany>() {
        @Override
        public EnergyCompany mapRow(YukonResultSet rs) throws SQLException {
            
            LiteYukonUser user = new LiteYukonUser(rs.getInt("UserID"));
            user.setUsername(rs.getString("UserName"));
            user.setLoginStatus(LoginStatusEnum.retrieveLoginStatus(rs.getString("Status")));
            user.setForceReset(rs.getBoolean("ForceReset"));
            user.setUserGroupId(rs.getNullableInt("UserGroupId"));
            
            EnergyCompany company = new EnergyCompany(rs.getInt("EnergyCompanyId"), rs.getString("Name"), 
                                                      user, rs.getInt("PrimaryContactId"));
            return company;
        }
    };
    
    @Override
    public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId); 
        return yukonEnergyCompany;
    }

    @Override
    public YukonEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
        int energyCompanyId = getEnergyCompanyIdByOperator(operator);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        return energyCompany;
    }

    @Override
    public int getEnergyCompanyIdByOperator(LiteYukonUser operator) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ECOLL.EnergyCompanyId");
        sql.append("FROM EnergyCompanyOperatorLoginList ECOLL");
        sql.append("WHERE ECOLL.OperatorLoginId").eq(operator.getUserID());
        
        try {
            int energyCompanyId = jdbcTemplate.queryForInt(sql);
            return energyCompanyId;
        } catch (EmptyResultDataAccessException e) {
            throw new EnergyCompanyNotFoundException("No energy company found for user id: " + operator.getUserID(), e);
        }
    }
    
    @Override
    public boolean isEnergyCompanyOperator(LiteYukonUser operator) {
        try {
            getEnergyCompanyIdByOperator(operator);
        } catch (EnergyCompanyNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public YukonEnergyCompany getEnergyCompanyByInventoryId(int inventoryId) {
        int energyCompanyId = ecMappingDao.getEnergyCompanyIdForInventoryId(inventoryId);
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId); 
        return yukonEnergyCompany;
    }
    
    @Override
    public List<YukonEnergyCompany> getAllEnergyCompanies() {
        List<YukonEnergyCompany> energyCompanies = Lists.<YukonEnergyCompany>newArrayList(starsDatabaseCache.getAllEnergyCompanies());
        return energyCompanies;
    }

    @Override
    public boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany) {
        return energyCompany.getEnergyCompanyId() == StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID;
    }
    
    /**
     * This method returns a map that contains all of the child to parent energy company mappings.
     */
    protected Map<Integer, Integer> getChildToParentEnergyCompanyHierarchy() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyId, ItemId");
        sql.append("FROM ECToGenericMapping");
        sql.append("WHERE MappingCategory").eq_k(EcMappingCategory.MEMBER);
        
        final Map<Integer, Integer> childToParentMap = Maps.newHashMap();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int parentEnergyCompanyId = rs.getInt("EnergyCompanyId");
                int childEnergyCompanyId = rs.getInt("ItemId");
                childToParentMap.put(childEnergyCompanyId, parentEnergyCompanyId);
            }});
        
        return childToParentMap;
    }
    
    @Override
    public List<Integer> getChildEnergyCompanies(int energyCompanyId) {
        Map<Integer, Integer> childToParentMap = getChildToParentEnergyCompanyHierarchy();
        return getChildEnergyCompanies(energyCompanyId, childToParentMap);
    }

    /**
     * This method is a helper method for getChildEnergyCompanies(energyCompanyId).  Using this method allows us to 
     * make recursive calls without having to make repeated calls to the database to get the child to parent energy company map.
     */
    private List<Integer> getChildEnergyCompanies(int energyCompanyId, Map<Integer, Integer> childToParentMap) {
        List<Integer> result = Lists.newArrayList();

        List<Integer> directChildrenEnergyCompanyIds = getDirectChildEnergyCompanies(energyCompanyId, childToParentMap);
        result.addAll(directChildrenEnergyCompanyIds);
        for (int directChildEnergycompanyId : directChildrenEnergyCompanyIds) {
            result.addAll(getChildEnergyCompanies(directChildEnergycompanyId, childToParentMap));
        }

        return result;
    }

    /**
     * This method is a helper method for getChildEnergyCompanies(energyCompanyId, childToParentMap).  Using this method allows us to 
     * make recursive calls without having to make repeated calls to the database to get the child to parent energy company map.
     */
    private List<Integer> getDirectChildEnergyCompanies(int energyCompanyId, Map<Integer, Integer> childToParentMap) {
        // Getting the EnergyCompanyIds for this energy company's direct parent and children.
        List<Integer> childEnergyCompanyIds = Lists.newArrayList();
        for (Entry<Integer, Integer> childToParentEntry : childToParentMap.entrySet()) {
            if (energyCompanyId == childToParentEntry.getValue()) {
                childEnergyCompanyIds.add(childToParentEntry.getKey());
            }
        }
        
        return childEnergyCompanyIds;
    }
    
    @Override
    public List<Integer> getDirectChildEnergyCompanies(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItemId");
        sql.append("FROM ECToGenericMapping");
        sql.append("WHERE MappingCategory").eq_k(EcMappingCategory.MEMBER);
        sql.append(  "AND EnergyCompanyId").eq(energyCompanyId);
        
        return jdbcTemplate.query(sql, RowMapper.INTEGER);
    }

    @Override
    public Integer getParentEnergyCompany(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyId");
        sql.append("FROM ECToGenericMapping");
        sql.append("WHERE MappingCategory").eq_k(EcMappingCategory.MEMBER);
        sql.append(  "AND ItemId").eq(energyCompanyId);

        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public Integer findParentEnergyCompany(int energyCompanyId) {
        try {
            return getParentEnergyCompany(energyCompanyId);
        } catch (IncorrectResultSizeDataAccessException e) {}
        
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
    public YukonEnergyCompany getEnergyCompany(final int ecId) {
        if (cachedEnergyCompanies.containsKey(ecId)) {
            return cachedEnergyCompanies.get(ecId);
        }
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanyID, Name, PrimaryContactID, YU.UserId, UserName, Status, ForceReset,");
        sql.append("       UserGroupId");
        sql.append("FROM EnergyCompany EC, YukonUser YU");
        sql.append("WHERE EC.UserId = YU.UserId");
        sql.append("AND EnergyCompanyId").eq(ecId);

        YukonEnergyCompany energyCompany = jdbcTemplate.queryForObject(sql, energyCompanyRowMapper);
        cachedEnergyCompanies.put(ecId, energyCompany);
        return energyCompany;
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

        List<Integer> routeIds = ImmutableList.copyOf(jdbcTemplate.query(sql, RowMapper.INTEGER));
        cachedRouteIds.put(ecId, routeIds);
        return routeIds;
    }
}