package com.cannontech.stars.energyCompany.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.yukon.IDatabaseCache;

public final class EnergyCompanyDaoImpl implements EnergyCompanyDao {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private YukonUserDao yukonUserDao;

    private SimpleTableAccessTemplate<EnergyCompany> simpleTableTemplate;
    private final static FieldMapper<EnergyCompany> fieldMapper = new FieldMapper<EnergyCompany>() {
        @Override
        public Number getPrimaryKey(EnergyCompany energyCompany) {
            return energyCompany.getEnergyCompanyId();
        }

        @Override
        public void setPrimaryKey(EnergyCompany energyCompany, int energyCompanyId) {
            energyCompany.setEnergyCompanyId(energyCompanyId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, EnergyCompany energyCompany) {
            parameterHolder.addValue("Name", energyCompany.getName());
            parameterHolder.addValue("PrimaryContactId", energyCompany.getPrimaryContactId());
            parameterHolder.addValue("UserId", energyCompany.getUserId());
        }
    };
    
    @PostConstruct
    public void init() {
        simpleTableTemplate = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        simpleTableTemplate.setTableName("EnergyCompany");
        simpleTableTemplate.setFieldMapper(fieldMapper);
        simpleTableTemplate.setPrimaryKeyField("EnergyCompanyId");
        simpleTableTemplate.setPrimaryKeyValidOver(DEFAULT_ENERGY_COMPANY_ID - 1); /*TODO should default ec be editable? */
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
    public List<DisplayableServiceCompany> getAllServiceCompanies(Iterable<Integer> energyCompanyIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select sc.CompanyID companyId, sc.CompanyName companyName from ServiceCompany sc");
        sql.append("join ECToGenericMapping ecgm on ecgm.ItemID = sc.CompanyID and ecgm.MappingCategory").eq_k(EcMappingCategory.SERVICE_COMPANY);
        sql.append("where ecgm.EnergyCompanyID").in(energyCompanyIds);
        
        return yukonJdbcTemplate.query(sql.getSql(),
            new RowMapper<DisplayableServiceCompany>() {
                @Override
                public DisplayableServiceCompany mapRow(ResultSet rs, int rowNum)
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
    public LiteEnergyCompany getEnergyCompany(int energyCompanyID) {
        for (Iterator<LiteEnergyCompany> i = databaseCache.getAllEnergyCompanies().iterator(); i.hasNext();) {
            LiteEnergyCompany e = i.next();
            if (e.getEnergyCompanyID() == energyCompanyID) {
                return e;
            }
        }
        return null;
    }

    @Override
    public LiteEnergyCompany getEnergyCompany(LiteYukonUser user) {
        LiteEnergyCompany liteEnergyCompany = databaseCache.getALiteEnergyCompanyByUserID(user);
        if (liteEnergyCompany == null) {
            return getEnergyCompany(DEFAULT_ENERGY_COMPANY_ID);
        }
        return liteEnergyCompany;
    }

    @Override
    public LiteEnergyCompany getEnergyCompanyByName(final String energyCompanyName) {

        List<LiteEnergyCompany> energyCompanies = databaseCache.getAllEnergyCompanies();
        for (final LiteEnergyCompany energyCompany : energyCompanies) {
            String name = energyCompany.getName();
            if (name.equalsIgnoreCase(energyCompanyName)) {
                return energyCompany;
            }
        }
        throw new NotFoundException("Energy Company with name: " + energyCompanyName + " not found.");
    }

    @Override
    public LiteEnergyCompany findEnergyCompanyByName(final String energyCompanyName) {
        LiteEnergyCompany company = null;
        try {
            company = getEnergyCompanyByName(energyCompanyName);
        } catch (NotFoundException e) {/* Ignore */}
        return company;
    }

    @Override
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
        
        dbChangeManager.processDbChange(energyCompanyId,
                                        DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
                                        DBChangeMsg.CAT_ENERGY_COMPANY,
                                        DBChangeMsg.CAT_ENERGY_COMPANY,
                                        DbChangeType.UPDATE);
    }

    @Override
    @Transactional
    public void save(EnergyCompany energyCompany) {
        boolean create = !simpleTableTemplate.saveWillUpdate(energyCompany);
        simpleTableTemplate.save(energyCompany);
        
        if (create) {
            SqlStatementBuilder sql = new SqlStatementBuilder("INSERT INTO EnergyCompanyOperatorLoginList");
            sql.values(energyCompany.getEnergyCompanyId(), energyCompany.getUserId());
            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    public List<LiteEnergyCompany> getAllEnergyCompanies() {
        return databaseCache.getAllEnergyCompanies();
    }

}