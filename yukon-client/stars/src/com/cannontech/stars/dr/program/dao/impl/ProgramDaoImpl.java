package com.cannontech.stars.dr.program.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;

public class ProgramDaoImpl implements ProgramDao {
    private static final String selectSql;
    private final Comparator<Program> programComparator = createProgramComparator();
    private final ParameterizedRowMapper<Program> rowMapper = createRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        selectSql = "SELECT ProgramID,ProgramOrder,ywc.Description,AlternateDisplayName,PAOName,ChanceOfControlID,ApplianceCategoryID,LogoLocation " +
                    "FROM LMProgramWebPublishing pwp, YukonWebConfiguration ywc, YukonPAObject ypo " + 
                    "WHERE pwp.WebsettingsID = ywc.ConfigurationID " +
                    "AND ypo.PAObjectID = pwp.DeviceID";
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Program> getByAppliances(final List<Appliance> applianceList) {
        final List<Integer> programIdList = new ArrayList<Integer>(applianceList.size());
        for (final Appliance appliance : applianceList) {
            Integer programId = appliance.getProgramId();
            programIdList.add(programId);
        }
        List<Program> programList = getByProgramIds(programIdList);
        return programList;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Program> getByProgramIds(final List<Integer> programIdList) {
        final StringBuilder sb = new StringBuilder();
        sb.append(selectSql);
        sb.append(" AND pwp.ProgramID IN (");
        sb.append(SqlStatementBuilder.convertToSqlLikeList(programIdList));
        sb.append(")");
        
        String sql = sb.toString();
        List<Program> programList = simpleJdbcTemplate.query(sql, rowMapper);
        Collections.sort(programList, programComparator);
        return programList;
    }
    
    private ParameterizedRowMapper<Program> createRowMapper() {
        final ParameterizedRowMapper<Program> mapper = new ParameterizedRowMapper<Program>() {
            @Override
            public Program mapRow(ResultSet rs, int rowNum) throws SQLException {
                final Program program = new Program();
                program.setProgramId(rs.getInt("ProgramID"));
                program.setChanceOfControlId(rs.getInt("ChanceOfControlID"));
                program.setDescription(rs.getString("Description"));
                program.setProgramOrder(rs.getInt("ProgramOrder"));
                
                String programName = getProgramName(rs);
                program.setProgramName(programName);

                String logoLocation = getLogoLocation(rs);
                program.setLogoLocation(logoLocation);
                
                return program;
            }
        };
        return mapper;
    }
    
    private String getProgramName(final ResultSet rs) throws SQLException {
        String programName;
        String paoName = rs.getString("PAOName");
        
        String alternateDisplayName = rs.getString("AlternateDisplayName");
        boolean alternateDisplayNameIsNull = rs.wasNull();
        if (alternateDisplayNameIsNull) {
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
        boolean isNull = rs.wasNull();
        boolean isEmpty = isEmptyString(logoLocation);
        
        if (!(isNull || isEmpty)) return logoLocation;
        
        int applianceCategoryId = rs.getInt("ApplianceCategoryID");
        
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT LogoLocation ");
        sb.append("FROM YukonWebConfiguration ywc,ApplianceCategory ac ");
        sb.append("WHERE ywc.ConfigurationID = ac.WebConfigurationID ");
        sb.append("AND ac.ApplianceCategoryID = ?");
        
        String sql = sb.toString();
        String applianceCategoryLogoLocation = simpleJdbcTemplate.queryForObject(sql, String.class, applianceCategoryId);
        return applianceCategoryLogoLocation;
    }
    
    /**
     * Checks for empty Strings "", empty database varchars "(none)",
     * and empty csv varchars stored in STARS ",,"
     */
    private boolean isEmptyString(final String value) {
        if (value.equals("") || value.equals(CtiUtilities.STRING_NONE)) return true;
        if (value.matches("^,+$")) return true;
        return false;
    }
    
    private static Comparator<Program> createProgramComparator() {
        final Comparator<Program> comparator = new Comparator<Program>() {
            @Override
            public int compare(Program o1, Program o2) {
                Integer order1 = o1.getProgramOrder();
                Integer order2 = o2.getProgramOrder();
                int result = order1.compareTo(order2);
                return result;
            }
        };
        return comparator;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
