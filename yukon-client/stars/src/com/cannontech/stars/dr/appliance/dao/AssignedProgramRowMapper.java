/**
 * 
 */
package com.cannontech.stars.dr.appliance.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public class AssignedProgramRowMapper extends
        AbstractRowMapperWithBaseQuery<AssignedProgram> {
    private boolean sortByName;
    private boolean sortDescending;
    private Map<Integer, WebConfiguration> webConfigurations;
    private int highestProgramOrder;

    public AssignedProgramRowMapper() {
        this(false, false, -1, null);
    }

    public AssignedProgramRowMapper(boolean sortByName, boolean sortDescending,
            int highestProgramOrder,
            Map<Integer, WebConfiguration> webConfigurations) {
        this.sortByName = sortByName;
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
        retVal.append(    "p.programId, pao.paoName");
        retVal.append("FROM lmProgramWebPublishing p");
        retVal.append(    "JOIN yukonWebConfiguration wc ON p.webSettingsId = wc.configurationId");
        retVal.append(    "JOIN yukonPaobject pao ON pao.paobjectId = p.deviceId");
        return retVal;
    }

    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder("ORDER BY");
        retVal.append(sortByName ? "LOWER(pao.paoName)" : "p.programOrder");
        if (sortDescending) {
            retVal.append("DESC");
        }
        return retVal;
    }

    @Override
    public AssignedProgram mapRow(ResultSet rs, int rowNum)
            throws SQLException {
        int applianceCategoryId = rs.getInt("applianceCategoryId");
        int assignedProgramId = rs.getInt("programId");
        int programId = rs.getInt("deviceId");
        String programName = rs.getString("paoName");
        int chanceOfControlId = rs.getInt("chanceOfControlId");
        int programOrder = rs.getInt("programOrder");

        Integer webConfigurationId = rs.getInt("webSettingsId");
        WebConfiguration webConfiguration = null;
        if (webConfigurations != null) {
            webConfiguration = webConfigurations.get(webConfigurationId);
        }
        boolean isLast = programOrder == highestProgramOrder;

        AssignedProgram assignedProgram =
            new AssignedProgram(applianceCategoryId, assignedProgramId,
                                programId, programName, chanceOfControlId,
                                programOrder, isLast, webConfiguration);

        return assignedProgram;
    }
}
