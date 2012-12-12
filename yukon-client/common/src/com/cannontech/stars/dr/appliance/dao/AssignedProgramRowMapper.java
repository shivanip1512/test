/**
 * 
 */
package com.cannontech.stars.dr.appliance.dao;

import java.sql.SQLException;
import java.util.Map;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public class AssignedProgramRowMapper extends
        AbstractRowMapperWithBaseQuery<AssignedProgram> {
    public enum SortBy {
        PROGRAM_NAME {
            @Override
            protected String getOrderBy(boolean sortDescending) {
                return "LOWER(pao.paoName)" + (sortDescending ? " DESC" : "")
                    + ", LOWER(wc.alternateDisplayName)" + (sortDescending ? " DESC" : "");
            }},
        PROGRAM_ORDER {
            @Override
            protected String getOrderBy(boolean sortDescending) {
                return "p.programOrder" + (sortDescending ? " DESC" : "");
            }},
        APPLIANCE_CATEGORY_NAME {
            @Override
            protected String getOrderBy(boolean sortDescending) {
                return "LOWER(ac.description)" + (sortDescending ? " DESC" : "");
            }};

        protected abstract String getOrderBy(boolean sortDescending);
    };

    private SortBy sortBy;
    private boolean sortDescending;
    private Map<Integer, WebConfiguration> webConfigurations;
    private Integer highestProgramOrder;

    public AssignedProgramRowMapper() {
        this(SortBy.PROGRAM_ORDER, false, -1, null);
    }

    public AssignedProgramRowMapper(SortBy sortBy, boolean sortDescending,
                                    Integer highestProgramOrder,
            Map<Integer, WebConfiguration> webConfigurations) {
        this.sortBy = sortBy;
        this.sortDescending = sortDescending;
        this.highestProgramOrder = highestProgramOrder;
        this.webConfigurations = webConfigurations;
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT p.applianceCategoryId,");
        retVal.append(    "p.deviceId, p.webSettingsId,");
        retVal.append(    "p.chanceOfControlId, p.programOrder,");
        retVal.append(    "p.programId, pao.paoName,");
        retVal.append(    "ptsp.seasonalProgramId");
        retVal.append("FROM lmProgramWebPublishing p");
        retVal.append(    "JOIN yukonWebConfiguration wc ON p.webSettingsId = wc.configurationId");
        retVal.append(    "JOIN yukonPaobject pao ON pao.paobjectId = p.deviceId");
        retVal.append(    "JOIN applianceCategory ac ON ac.applianceCategoryId = p.applianceCategoryId");
        retVal.append(    "LEFT JOIN ProgramToSeasonalProgram ptsp ON ptsp.assignedProgramId = p.programId");
        // there is a "blank" row in lmProgramWebPublishing (all zeros) we have to ignore
        retVal.append("WHERE p.applianceCategoryId").neq(0);
        return retVal;
    }

    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder("ORDER BY");
        retVal.append(sortBy.getOrderBy(sortDescending));
        return retVal;
    }

    @Override
    public AssignedProgram mapRow(YukonResultSet rs) throws SQLException {
        
        int applianceCategoryId = rs.getInt("applianceCategoryId");
        int assignedProgramId = rs.getInt("programId");
        int programId = rs.getInt("deviceId");
        String programName = programId == 0 ? null : rs.getString("paoName");
        int chanceOfControlId = rs.getInt("chanceOfControlId");
        int programOrder = rs.getInt("programOrder");

        Integer webConfigurationId = rs.getInt("webSettingsId");
        WebConfiguration webConfiguration = null;
        if (webConfigurations != null) {
            webConfiguration = webConfigurations.get(webConfigurationId);
        }
        // isLast and highestProgramOrder only make sense when looking at a full appliance category.
        boolean isLast = highestProgramOrder != null && programOrder == highestProgramOrder;
        
        Integer alternateProgramId = rs.getInt("seasonalProgramId");

        AssignedProgram assignedProgram = new AssignedProgram(applianceCategoryId, 
                                                              assignedProgramId, 
                                                              programId, 
                                                              alternateProgramId,
                                                              programName,
                                                              chanceOfControlId, 
                                                              programOrder, 
                                                              isLast, 
                                                              webConfigurationId, 
                                                              webConfiguration);

        return assignedProgram;
    }
}
