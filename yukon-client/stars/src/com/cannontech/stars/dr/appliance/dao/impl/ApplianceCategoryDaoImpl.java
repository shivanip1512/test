package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceType;
import com.cannontech.stars.util.ECUtils;

public class ApplianceCategoryDaoImpl implements ApplianceCategoryDao {
    private final ParameterizedRowMapper<Integer> idRowMapper = createIdRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private AuthDao authDao;
    private ECMappingDao ecMappingDao;
    private YukonUserDao yukonUserDao;
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ApplianceCategory> getApplianceCategories(final CustomerAccount customerAccount) {

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(energyCompany.getUserID());
        List<Integer> idList;
        if (authDao.checkRoleProperty(liteYukonUser, EnergyCompanyRole.INHERIT_PARENT_APP_CATS)){
            List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
            idList = ECUtils.toIdList(allAscendants);
        } else {
            idList = Collections.singletonList(energyCompany.getLiteID());
        }
        
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT ApplianceCategoryID");
        sqlBuilder.append("FROM ApplianceCategory");
        sqlBuilder.append("WHERE ApplianceCategoryID IN (");
        sqlBuilder.append("     SELECT ItemID ");
        sqlBuilder.append("		FROM ECToGenericMapping ");
        sqlBuilder.append("     WHERE MappingCategory = ? ");
        sqlBuilder.append("     AND EnergyCompanyID in ( ");
        sqlBuilder.append(idList);
        sqlBuilder.append("))");
        String sql = sqlBuilder.toString();
        
        List<Integer> applianceCategoryIdList = simpleJdbcTemplate.query(sql,
                                                                         idRowMapper,
                                                                         YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY);
        
        final Set<ApplianceCategory> set = new HashSet<ApplianceCategory>(applianceCategoryIdList.size());
        
        for (final Integer applianceCategoryId : applianceCategoryIdList) {
            final ApplianceCategory applianceCategory = getById(applianceCategoryId);
            set.add(applianceCategory);
        }
        return new ArrayList<ApplianceCategory>(set);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ApplianceCategory getById(int applianceCategoryId) {
        final SqlStatementBuilder sb = new SqlStatementBuilder();
        sb.append("SELECT EntryText FROM YukonListEntry WHERE YukonListEntry.ListID = (");
        sb.append("SELECT ListID FROM YukonSelectionList WHERE ListName = ?) ");
        sb.append("AND EntryID = (SELECT CategoryID FROM ApplianceCategory WHERE ApplianceCategoryID = ?)");
        String sql = sb.toString();
        
        final SqlStatementBuilder sqlBuilder2 = new SqlStatementBuilder();
        sqlBuilder2.append("SELECT LogoLocation");
        sqlBuilder2.append("FROM YukonWebConfiguration ywc,ApplianceCategory ac");
        sqlBuilder2.append("WHERE ywc.ConfigurationID = ac.WebConfigurationID");
        sqlBuilder2.append("AND ac.ApplianceCategoryID = ?");
        String sql2 = sqlBuilder2.toString();
        
        String textValue = simpleJdbcTemplate.queryForObject(sql,
                                                             String.class, 
                                                             YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
                                                             applianceCategoryId);
        
        String logoPath = simpleJdbcTemplate.queryForObject(sql2, 
                                                        String.class,
                                                        applianceCategoryId);
        
        ApplianceType applianceType = getApplianceTypeEnumFromTextValue(textValue);
        ApplianceCategory applianceCategory = new ApplianceCategory(applianceCategoryId, applianceType, logoPath);
        return applianceCategory;
    }
    
    private ApplianceType getApplianceTypeEnumFromTextValue(final String textValue) {
        String result = textValue;
        result = result.replaceAll("[(|)]", "");
        result = result.replaceAll("\\s+", "_");
        result = result.toUpperCase();
        return ApplianceType.valueOf(result);
    }
    
    private ParameterizedRowMapper<Integer> createIdRowMapper() {
        final ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        };
        return mapper;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}
