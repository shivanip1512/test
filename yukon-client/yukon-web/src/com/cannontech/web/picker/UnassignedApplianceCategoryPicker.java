package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceTypeEnum;
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class UnassignedApplianceCategoryPicker extends DatabasePicker<Map<String, Object>> {

    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private EnergyCompanyDao ecDao;

    private final static String[] searchColumnNames = new String[] {
        "ac.applianceCategoryId", "ac.description", "yle.yukonDefinitionId", "ac.averageKwLoad"
    };

    private final static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        
        columns.add(new OutputColumn("name", "yukon.web.picker.unassignedApplianceCategoryPicker.applianceCategoryName"));
        columns.add(new OutputColumn("applianceLoad", "yukon.web.picker.unassignedApplianceCategoryPicker.applianceLoad"));
        columns.add(new OutputColumn("applianceType", "yukon.web.picker.unassignedApplianceCategoryPicker.applianceType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<Map<String, Object>>> postProcessingFilters, String extraArgs,
            final YukonUserContext userContext) {

        postProcessingFilters.add(new PostProcessingFilter<Map<String,Object>>() {
            @Override
            public List<Map<String, Object>> process(List<Map<String, Object>> objectsFromDb) {
                List<Map<String, Object>> validAppliancesForEc = new ArrayList<>();

                YukonEnergyCompany yec;
                try {
                    yec = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
                } catch (EnergyCompanyNotFoundException e) {
                    return validAppliancesForEc;
                }

               List<Integer> applianceCategoryIds
                   = applianceCategoryDao.getApplianceCategoryIdsByEC(yec.getEnergyCompanyId());

                for (Map<String, Object> map : objectsFromDb) {
                    int id = (int) map.get("applianceCategoryId");
                    if (applianceCategoryIds.contains(id)) {
                        validAppliancesForEc.add(map);
                    }
                }
                return validAppliancesForEc;
            }
        });
    }
    
    public UnassignedApplianceCategoryPicker() {
        super(new UnassignedApplianceCategoryRowMapper(), searchColumnNames);
    }

    @Override
    public String getIdFieldName() {
        return "applianceCategoryId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "ac.applianceCategoryId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class UnassignedApplianceCategoryRowMapper extends
        AbstractRowMapperWithBaseQuery<Map<String, Object>> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder retVal = new SqlStatementBuilder();
            retVal.append("SELECT ac.description, ac.applianceCategoryId, yle.yukonDefinitionId, ac.averageKwLoad");
            retVal.append("FROM applianceCategory ac");
            retVal.append(    "JOIN yukonListEntry yle ON ac.categoryId = yle.entryId");
            retVal.append(    "JOIN ecToGenericMapping ecm ON ac.applianceCategoryId = ecm.itemId AND ecm.mappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
            retVal.append(    "WHERE ac.applianceCategoryId NOT IN");
            retVal.append(        "(SELECT ApplianceCategoryId FROM EstimatedLoadFormulaAssignment WHERE ApplianceCategoryId IS NOT NULL)");
            return retVal;
        }

        @Override
        public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
            Map<String, Object> map = new HashMap<>();
            
            map.put("name", rs.getString("description")); // description field is used as name, consistent with ACDao
            map.put("applianceCategoryId", rs.getInt("applianceCategoryId"));
            map.put("applianceLoad", rs.getDouble("averageKwLoad"));
            map.put("applianceType", ApplianceTypeEnum.getByDefinitionId(rs.getInt("yukonDefinitionId")));

            return map;
        }
    }
}
