package com.cannontech.stars.dr.program.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.stars.dr.program.model.ChanceOfControl;
import com.cannontech.stars.dr.program.model.Program;

/**
 * Row mapper which maps a resultset row into a Program object.
 * NOTE: This mapper *MUST* be used inside of a transaction otherwise there is a
 * possibility of running out of db connections 
 */
public class ProgramRowMapper implements ParameterizedRowMapper<Program> {
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	public ProgramRowMapper(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	
    @Override
    public Program mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Program program = new Program();
        program.setProgramId(rs.getInt("ProgramID"));
        
        String chanceOfControlString = rs.getString("ChanceOfControl");
        ChanceOfControl chanceOfControl = ChanceOfControl.valueOfName(chanceOfControlString);
        program.setChanceOfControl(chanceOfControl);
        
        program.setDescription(rs.getString("Description"));
        program.setProgramOrder(rs.getInt("ProgramOrder"));
        
        String programName = getProgramName(rs);
        program.setProgramName(programName);

        String logoLocation = getLogoLocation(rs);
        program.setLogoLocation(logoLocation);
        
        int applianceCategoryId = rs.getInt("ApplianceCategoryID");
        program.setApplianceCategoryId(applianceCategoryId);
        
        String url = rs.getString("url");
        program.setDescriptionUrl(url);
        
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
    
    /**
     * Returns the logoLocation of the program, if the program's logoLocation
     * is empty the logoLocation of the ApplianceCategory that the program 
     * belongs to is returned instead.
     * @param rs 
     * @return logoLocation
     * @throws SQLException
     */
    private String getLogoLocation(final ResultSet rs) throws SQLException {
        String logoLocation = rs.getString("LogoLocation");
       
        boolean isEmpty = isEmptyString(logoLocation);
        if (!isEmpty) return logoLocation;
        
        int applianceCategoryId = rs.getInt("ApplianceCategoryID");
        
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT LogoLocation ");
        sb.append("FROM YukonWebConfiguration ywc,ApplianceCategory ac ");
        sb.append("WHERE ywc.ConfigurationID = ac.WebConfigurationID ");
        sb.append("AND ac.ApplianceCategoryID = ?");
        
        String sql = sb.toString();
        String applianceCategoryLogoLocation = 
        	simpleJdbcTemplate.queryForObject(sql, String.class, applianceCategoryId);
        return applianceCategoryLogoLocation;
    }
    
    /**
     * Checks for null or empty Strings "", empty database varchars "(none)",
     * and empty csv varchars stored in STARS ",,"
     */
    private boolean isEmptyString(final String value) {
        if (StringUtils.isEmpty(value)) return true;
        if (value.equals(CtiUtilities.STRING_NONE)) return true;
        if (value.matches("^,+$")) return true;
        return false;
    }
}