/**
 * 
 */
package com.cannontech.stars.dr.appliance.dao;

import java.sql.SQLException;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.dr.appliance.model.AssignedProgramName;
import com.cannontech.stars.dr.appliance.model.UltraLightAssignedProgram;

public class UltraLightAssignedProgramRowMapper extends
        AbstractRowMapperWithBaseQuery<UltraLightAssignedProgram> {

    public UltraLightAssignedProgramRowMapper() {
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT p.applianceCategoryId,");
        retVal.append(    "ac.description AS applianceCategoryName,");
        retVal.append(    "p.deviceId AS programId, wc.alternateDisplayName,");
        retVal.append(    "p.programId AS assignedProgramId, pao.paoName");
        retVal.append("FROM lmProgramWebPublishing p");
        retVal.append(    "JOIN yukonPaobject pao ON pao.paobjectId = p.deviceId");
        retVal.append(    "JOIN applianceCategory ac");
        retVal.append(        "ON ac.applianceCategoryId = p.applianceCategoryId");
        retVal.append(    "JOIN yukonWebConfiguration wc");
        retVal.append(        "ON wc.configurationId = p.webSettingsId");
        return retVal;
    }

    @Override
    public SqlFragmentSource getOrderBy() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("ORDER BY LOWER(pao.paoName)");
        return retVal;
    }

    @Override
    public UltraLightAssignedProgram mapRow(YukonResultSet rs)
            throws SQLException {
        int assignedProgramId = rs.getInt("assignedProgramId");
        int programId = rs.getInt("programId");
        AssignedProgramName name =
            new AssignedProgramName(rs.getString("paoName"),
                                     rs.getString("alternateDisplayName"));
        int applianceCategoryId = rs.getInt("applianceCategoryId");
        String applianceCategoryName = rs.getString("applianceCategoryName");

        UltraLightAssignedProgram retVal =
            new UltraLightAssignedProgram(assignedProgramId, programId,
                                          name.getProgramName(),
                                          name.getDisplayName(),
                                          applianceCategoryId,
                                          applianceCategoryName);

        return retVal;
    }
}
