package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

/**
 * Energy company related convenience funcs
 * @author: alauinger
 */
public final class EnergyCompanyDaoImpl implements EnergyCompanyDao {
    // the following is a duplicate of StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID
    public final int DEFAULT_ENERGY_COMPANY_ID = -1;

    private RolePropertyDao rolePropertyDao;
    private YukonUserDao yukonUserDao;
    private IDatabaseCache databaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;

    private EnergyCompanyDaoImpl() {
        super();
    }

    public class DisplayableServiceCompany {
        int serviceCompanyId;
        String serviceCompanyName;

        public int getServiceCompanyId() {
            return serviceCompanyId;
        }

        public void setServiceCompanyId(int serviceCompanyId) {
            this.serviceCompanyId = serviceCompanyId;
        }

        public String getServiceCompanyName() {
            return serviceCompanyName;
        }

        public void setServiceCompanyName(String serviceCompanyName) {
            this.serviceCompanyName = serviceCompanyName;
        }
    }

    @Override
    public List<DisplayableServiceCompany> getAllInheritedServiceCompanies(
            int energyCompanyId) {
        List<Integer> energyCompanyIds = Lists.newArrayList();
        energyCompanyIds.add(energyCompanyId);
        energyCompanyIds.addAll(getParentEnergyCompanyIds(energyCompanyId));
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select sc.CompanyID companyId, sc.CompanyName companyName from ServiceCompany sc");
        sql.append("join ECToGenericMapping ecgm on ecgm.ItemID = sc.CompanyID");
        sql.append("where ecgm.EnergyCompanyID in (").appendArgumentList(energyCompanyIds).append(")");
        sql.append("and ecgm.MappingCategory = 'ServiceCompany'");

        return yukonJdbcTemplate.query(sql.getSql(),
            new ParameterizedRowMapper<DisplayableServiceCompany>() {
                @Override
                public DisplayableServiceCompany mapRow(
                        ResultSet rs, int rowNum)
                        throws SQLException {
                    DisplayableServiceCompany sc = new DisplayableServiceCompany();
                    sc.setServiceCompanyId(rs.getInt("companyId"));
                    sc.setServiceCompanyName(SqlUtils.convertDbValueToString(rs.getString("companyName")));
                    return sc;
                }
            },
            sql.getArguments());
    }

    @Override
    public List<Integer> getParentEnergyCompanyIds(int energyCompanyId) {
        List<Integer> parentIds = Lists.newArrayList();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select EnergyCompanyId");
        sql.append("from ECToGenericMapping");
        sql.append("where MappingCategory = 'Member'");
        sql.append("and ItemId = ").appendArgument(energyCompanyId);

        try {
            int parentId = yukonJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
            parentIds.add(parentId);

            /* Recursive call to get the whole parent chain */
            parentIds.addAll(getParentEnergyCompanyIds(parentId));
        } catch (EmptyResultDataAccessException e) {
            /* We found the last parent which means we are done */
        }

        return parentIds;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompany(int)
     */
    public LiteEnergyCompany getEnergyCompany(int energyCompanyID) {
        for (Iterator<LiteEnergyCompany> i = databaseCache.getAllEnergyCompanies().iterator(); i.hasNext();) {
            LiteEnergyCompany e = i.next();
            if (e.getEnergyCompanyID() == energyCompanyID) {
                return e;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompany(com.cannontech
     * .database.data.lite.LiteYukonUser)
     */
    public LiteEnergyCompany getEnergyCompany(LiteYukonUser user) {
        LiteEnergyCompany liteEnergyCompany = databaseCache.getALiteEnergyCompanyByUserID(user);
        if (liteEnergyCompany == null) {
            return getEnergyCompany(DEFAULT_ENERGY_COMPANY_ID);
        }
        return liteEnergyCompany;
    }

    @Override
    public LiteEnergyCompany getEnergyCompanyByName(
            final String energyCompanyName) {
        List<LiteEnergyCompany> energyCompanies = databaseCache.getAllEnergyCompanies();
        for (final LiteEnergyCompany energyCompany : energyCompanies) {
            String name = energyCompany.getName();
            if (name.equalsIgnoreCase(energyCompanyName))
                return energyCompany;
        }
        throw new NotFoundException("Energy Company with name: " + energyCompanyName + " not found.");
    }

    /*
     * (non-Javadoc)
     * @see
     * com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompaniesByCustomer
     * (int)
     */
    public LiteEnergyCompany[] getEnergyCompaniesByCustomer(int customerID_) {
        List<LiteEnergyCompany> enrgComps = new ArrayList<LiteEnergyCompany>(16);
        for (int i = 0; i < databaseCache.getAllEnergyCompanies().size(); i++) {
            LiteEnergyCompany e = databaseCache.getAllEnergyCompanies().get(i);

            for (int j = 0; j < e.getCiCustumerIDs().size(); i++) {
                if (e.getCiCustumerIDs().elementAt(j) == customerID_) {
                    enrgComps.add(e);
                    break; // move onto the next energycompany
                }
            }
        }

        LiteEnergyCompany[] cArr = new LiteEnergyCompany[enrgComps.size()];
        return enrgComps.toArray(cArr);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyUser(com.cannontech
     * .database.data.lite.LiteEnergyCompany)
     */
    public LiteYukonUser getEnergyCompanyUser(LiteEnergyCompany company) {
        return yukonUserDao.getLiteYukonUser(company.getUserID());
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyUser(int)
     */
    public LiteYukonUser getEnergyCompanyUser(int energyCompanyID) {
        LiteEnergyCompany ec = getEnergyCompany(energyCompanyID);
        return (ec == null ? null : getEnergyCompanyUser(ec));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyProperty(com
     * .cannontech.database.data.lite.LiteYukonUser, int)
     */
    public String getEnergyCompanyProperty(LiteYukonUser user,
            int rolePropertyID) {
        LiteEnergyCompany ec = getEnergyCompany(user);
        LiteYukonUser ecUser = getEnergyCompanyUser(ec);
        return rolePropertyDao.getPropertyStringValue(YukonRoleProperty.getForId(rolePropertyID),
                                                      ecUser);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.cannontech.core.dao.EnergyCompanyDao#getEnergyCompanyProperty(com
     * .cannontech.database.data.lite.LiteEnergyCompany, int)
     */
    public String getEnergyCompanyProperty(LiteEnergyCompany ec,
            int rolePropertyID) {
        return rolePropertyDao.getPropertyStringValue(YukonRoleProperty.getForId(rolePropertyID),
                                                      getEnergyCompanyUser(ec));
    }

    @Override
    public void addEnergyCompanyCustomerListEntry(int customerId,
            int energyCompanyId) {
        String sql = "INSERT INTO EnergyCompanyCustomerList VALUES (?,?)";
        yukonJdbcTemplate.update(sql, energyCompanyId, customerId);
    }
    
    @Override
    public void updateCompanyName(String name, int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE EnergyCompany");
        sql.append("SET Name =").appendArgument(name);
        sql.append("WHERE EnergyCompanyId").eq_k(energyCompanyId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<LiteEnergyCompany> getAllEnergyCompanies() {
        return databaseCache.getAllEnergyCompanies();
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}
