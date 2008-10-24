package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
        
        final SqlStatementBuilder entryTextQuery = new SqlStatementBuilder();
        entryTextQuery.append("SELECT EntryText FROM YukonListEntry WHERE YukonListEntry.ListID = (");
        entryTextQuery.append("SELECT ListID FROM YukonSelectionList WHERE ListName = ?) ");
        entryTextQuery.append("AND EntryID = (SELECT CategoryID FROM ApplianceCategory WHERE ApplianceCategoryID = ?)");
        String sql = entryTextQuery.toString();
        
        final SqlStatementBuilder categoryNameAndLogoQuery = new SqlStatementBuilder();
        categoryNameAndLogoQuery.append("SELECT ac.description, ywc.alternateDisplayName, ywc.logoLocation ");
        categoryNameAndLogoQuery.append("FROM YukonWebConfiguration ywc,ApplianceCategory ac");
        categoryNameAndLogoQuery.append("WHERE ywc.ConfigurationID = ac.WebConfigurationID");
        categoryNameAndLogoQuery.append("AND ac.ApplianceCategoryID = ?");
        String sql2 = categoryNameAndLogoQuery.toString();
        
        String textValue = simpleJdbcTemplate.queryForObject(sql,
                                                             String.class, 
                                                             YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY,
                                                             applianceCategoryId);
        
        CategoryNameAndLogoContainer catNameAndLogo = 
            simpleJdbcTemplate.queryForObject(sql2,
                                              new CategoryNameAndLogoRowMapper(),
                                              applianceCategoryId);


        String categoryLabel;
        if(!StringUtils.isBlank(catNameAndLogo.getDisplayName())) {
            categoryLabel = catNameAndLogo.getDisplayName();
        } else {
            categoryLabel = catNameAndLogo.getDefaultDisplayName();
        }
        ApplianceType applianceType = getApplianceTypeEnumFromTextValue(textValue);
        
        return new ApplianceCategory(applianceCategoryId, 
                                     categoryLabel, 
                                     applianceType, 
                                     catNameAndLogo.getLogoLocation());
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
    
    protected class CategoryNameAndLogoRowMapper implements ParameterizedRowMapper<CategoryNameAndLogoContainer> {

        public CategoryNameAndLogoContainer mapRow(ResultSet rs, int rowNum) throws SQLException {
            
            String displayName = rs.getString("alternateDisplayName");
            String defaultDisplayName = rs.getString("description");
            String logoLocation = rs.getString("logoLocation");
            
            return new CategoryNameAndLogoContainer(displayName,
                                                    defaultDisplayName,
                                                    logoLocation);
        }
    }

    protected class CategoryNameAndLogoContainer{
        private String displayName;
        private String defaultDisplayName;
        private String logoLocation;
        
        public CategoryNameAndLogoContainer(String displayName, 
                                            String defaultDisplayName,
                                            String logoLocation){
            this.displayName = displayName;
            this.defaultDisplayName = defaultDisplayName;
            this.logoLocation = logoLocation;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDefaultDisplayName() {
            return defaultDisplayName;
        }

        public String getLogoLocation() {
            return logoLocation;
        }
    }
}
