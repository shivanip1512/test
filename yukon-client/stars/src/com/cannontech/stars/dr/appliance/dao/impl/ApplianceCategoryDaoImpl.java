package com.cannontech.stars.dr.appliance.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.util.ECUtils;

public class ApplianceCategoryDaoImpl implements ApplianceCategoryDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private AuthDao authDao;
    private ECMappingDao ecMappingDao;
    private YukonUserDao yukonUserDao;
    
    private final String applianceCategorySQLHeader = 
        "SELECT AC.applianceCategoryId, AC.description, YWC.alternateDisplayName, "+
        "       YLE.entryText as applianceType, YWC.logoLocation "+
        "FROM ApplianceCategory AC "+
        "INNER JOIN YukonWebConfiguration YWC ON AC.webConfigurationId = YWC.configurationId "+
        "INNER JOIN YukonListEntry YLE ON AC.categoryId = YLE.entryId ";

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
                                                                         new IntegerRowMapper(),
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
        
        final SqlStatementBuilder applCateQuery = new SqlStatementBuilder();
        applCateQuery.append(applianceCategorySQLHeader);
        applCateQuery.append("WHERE AC.applianceCategoryId = ?");
                     
        try {
            ApplianceCategory applianceCategory = 
                simpleJdbcTemplate.queryForObject(applCateQuery.toString(),
                                                  new ApplianceCategoryRowMapper(),
                                                  applianceCategoryId);
        
            return applianceCategory;
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("The appliance category id supplied does not exist.");
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ApplianceCategory> getByApplianceCategoryName(String applianceCategoryName, 
                                                              List<Integer> energyCompanyIds){

        final SqlStatementBuilder applCateQuery = new SqlStatementBuilder();
        applCateQuery.append(applianceCategorySQLHeader);
        applCateQuery.append("INNER JOIN ECToGenericMapping ECTGM ON (ECTGM.itemId = AC.applianceCategoryId");
        applCateQuery.append("                                        AND ECTGM.mappingCategory = 'ApplianceCategory')");
        applCateQuery.append("WHERE (AC.description = ?");
        applCateQuery.append("       OR YWC.alternateDisplayName = ?)");
        applCateQuery.append("AND ECTGM.energyCompanyId in (?)");

        
        List<ApplianceCategory> applianceCategories = 
            simpleJdbcTemplate.query(applCateQuery.toString(),
                                     new ApplianceCategoryRowMapper(),
                                     applianceCategoryName,
                                     applianceCategoryName,
                                     energyCompanyIds);

        if (applianceCategories.size() > 0) {
            return applianceCategories;
        } else {
            throw new NotFoundException("The appliance category name supplied does not exist.");
        }
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

class ApplianceCategoryRowMapper implements ParameterizedRowMapper<ApplianceCategory> {

    @Override
    public ApplianceCategory mapRow(ResultSet rs, int rowNum)
            throws SQLException {
        ApplianceCategory applianceCategory = null;
        
        int applianceCategoryId = rs.getInt("applianceCategoryId"); 
        String displayName = rs.getString("alternateDisplayName");
        String defaultDisplayName = rs.getString("description");
        String applianceType = rs.getString("applianceType");
        ApplianceTypeEnum applianceTypeEnum = getApplianceTypeEnumFromTextValue(applianceType);
        String logoLocation = rs.getString("logoLocation");
        
        if (displayName == null){
            displayName = defaultDisplayName; 
        }

        applianceCategory = new ApplianceCategory(applianceCategoryId, 
                                                  displayName, 
                                                  applianceTypeEnum, 
                                                  logoLocation);
        
        return applianceCategory;
    }
    
    private ApplianceTypeEnum getApplianceTypeEnumFromTextValue(final String textValue) {
        String result = textValue;
        result = result.replaceAll("[(|)]", "");
        result = result.replaceAll("\\s+", "_");
        result = result.toUpperCase();
        return ApplianceTypeEnum.valueOf(result);
    }
}
