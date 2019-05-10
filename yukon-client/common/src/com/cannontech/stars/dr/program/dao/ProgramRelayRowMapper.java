package com.cannontech.stars.dr.program.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.dr.program.model.Program;

/**
 * Row mapper which maps a resultset row into a Program object with Relay value .
 * NOTE: This mapper *MUST* be used inside of a transaction otherwise there is a
 * possibility of running out of db connections 
 */
public class ProgramRelayRowMapper extends AbstractRowMapperWithBaseQuery<Program> {
    
    private final YukonJdbcTemplate jdbcTemplate;
    
    public ProgramRelayRowMapper(YukonJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("SELECT programId, programOrder, ywc.description,");
        retVal.append("ywc.url, alternateDisplayName,");
        retVal.append("paoName, yle.entryText as chanceOfControl,");
        retVal.append("applianceCategoryID, logoLocation,");
        retVal.append("ypo.Type");
        retVal.append("FROM lmProgramWebPublishing pwp,");
        retVal.append("yukonWebConfiguration ywc, yukonPAObject ypo,");
        retVal.append("yukonListEntry yle ");
        retVal.append("WHERE pwp.websettingsId = ywc.configurationId");
        retVal.append("AND ypo.paobjectId = pwp.deviceId");
        retVal.append("AND yle.entryID = pwp.chanceOfControlId");
        return retVal;
    }

    @Override
    public Program mapRow(YukonResultSet rs) throws SQLException {
        final Program program = new Program();
        program.setProgramId(rs.getInt("ProgramID"));
        
        String chanceOfControl = rs.getString("ChanceOfControl");
        chanceOfControl = com.cannontech.common.util.StringUtils.stripNone(chanceOfControl);
        program.setChanceOfControl(chanceOfControl);
        
        program.setDescription(rs.getString("Description"));
        program.setProgramOrder(rs.getInt("ProgramOrder"));
        
        String programName = getProgramName(rs.getResultSet());
        program.setProgramName(programName);
        program.setProgramPaoName(rs.getString("PAOName"));

        String applianceCategoryLogo = getApplianceCategoryLogo(rs.getResultSet());
        program.setApplianceCategoryLogo(applianceCategoryLogo);
        
        program.setRelay(rs.getInt("Relay"));
        
        String descriptionIcons = rs.getString("LogoLocation");
        if (!StringUtils.isEmpty(descriptionIcons) && !descriptionIcons.equals(CtiUtilities.STRING_NONE)) {
            String[] descriptionIconArray = descriptionIcons.split(",");
            if (descriptionIconArray.length > 0) {
                program.setSavingsDescriptionIcon(descriptionIconArray[0].trim());
            }
            if (descriptionIconArray.length > 1) {
                program.setControlPercentDescriptionIcon(descriptionIconArray[1].trim());
            }
            if (descriptionIconArray.length > 2) {
                program.setEnvironmentDescriptionIcon(descriptionIconArray[2].trim());
            }
        }

        int applianceCategoryId = rs.getInt("ApplianceCategoryID");
        program.setApplianceCategoryId(applianceCategoryId);
        
        String url = rs.getString("url");
        program.setDescriptionUrl(url);
        
        program.setPaoType(PaoType.getForDbString(rs.getString("Type")));
        
        return program;
    }
    
    private String getProgramName(final ResultSet rs) throws SQLException {
        String programName;
        String paoName = rs.getString("PAOName");
        
        String alternateDisplayName = rs.getString("AlternateDisplayName");
        if (alternateDisplayName == null) {
            programName = paoName;
        } else {
            String[] split = alternateDisplayName.split(",");
            programName = (split.length > 0) ? split[0] : paoName;
        }
        return programName;
    }

    private String getApplianceCategoryLogo(final ResultSet rs) throws SQLException {
        int applianceCategoryId = rs.getInt("ApplianceCategoryID");
        
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT LogoLocation ");
        sb.append("FROM YukonWebConfiguration ywc,ApplianceCategory ac ");
        sb.append("WHERE ywc.ConfigurationID = ac.WebConfigurationID ");
        sb.append("AND ac.ApplianceCategoryID = ?");
        
        String sql = sb.toString();
        String applianceCategoryLogoLocation = 
            jdbcTemplate.queryForObject(sql, String.class, applianceCategoryId);
        return applianceCategoryLogoLocation;
    }
}
