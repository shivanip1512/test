package com.cannontech.stars.dr.program.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.ChanceOfControl;
import com.cannontech.stars.dr.program.model.Program;

public class ProgramDaoImpl implements ProgramDao {
    private static final String selectSql;
    private final ParameterizedRowMapper<Program> rowMapper = createRowMapper();
    private final ParameterizedRowMapper<Integer> groupIdRowMapper = createGroupIdRowMapper();
    private final ParameterizedRowMapper<Integer> programIdRowMapper = createProgramIdRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        selectSql = "SELECT ProgramID,ProgramOrder,ywc.Description,ywc.url,AlternateDisplayName,PAOName,yle.EntryText as ChanceOfControl,ApplianceCategoryID,LogoLocation " +
                    "FROM LMProgramWebPublishing pwp, YukonWebConfiguration ywc, YukonPAObject ypo, YukonListEntry yle " +
                    "WHERE pwp.WebsettingsID = ywc.ConfigurationID " +
                    "AND ypo.PAObjectID = pwp.DeviceID " +
                    "AND yle.EntryID = pwp.ChanceOfControlID";
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Program getByProgramId(final int programId) {
        final String sql = selectSql + " AND pwp.ProgramID = ?";
        Program program = simpleJdbcTemplate.queryForObject(sql, rowMapper, programId);
        return program;
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
    public Map<ApplianceCategory, List<Program>> getByApplianceCategories(
        final List<ApplianceCategory> applianceCategories) {

        List<Integer> idList = new ArrayList<Integer>(applianceCategories.size());
        for (final ApplianceCategory applianceCategory : applianceCategories) {
            Integer applianceCategoryId = applianceCategory.getApplianceCategoryId();
            idList.add(applianceCategoryId);
        }
        
        final ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
        List<Program> programList = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append(selectSql);
                sqlBuilder.append("AND ApplianceCategoryID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, idList, rowMapper);
        
        final Map<ApplianceCategory, List<Program>> resultMap =
            new HashMap<ApplianceCategory, List<Program>>(applianceCategories.size());
        
        for (final ApplianceCategory applianceCategory : applianceCategories) {
            List<Program> list = resultMap.get(applianceCategory);
            if (list == null) {
                list = new ArrayList<Program>(1);
                resultMap.put(applianceCategory, list);
            }

            List<Program> programsByApplianceCategory = getProgramsByApplianceCategory(applianceCategory, programList);
            list.addAll(programsByApplianceCategory);
        }
        return resultMap;
    }
    
    private List<Program> getProgramsByApplianceCategory(ApplianceCategory applianceCategory, List<Program> programList) {
        final int applianceCategoryId = applianceCategory.getApplianceCategoryId();
        final List<Program> resultList = new ArrayList<Program>(1);

        for (final Program program : programList) {
            int programsApplianceCategoryId = program.getApplianceCategoryId();
            if (programsApplianceCategoryId == applianceCategoryId) resultList.add(program);
        }
        return resultList;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Program> getByProgramIds(final List<Integer> programIdList) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append(selectSql);
        sqlBuilder.append(" AND pwp.ProgramID IN (");
        sqlBuilder.append(programIdList);
        sqlBuilder.append(")");
        sqlBuilder.append(" ORDER BY ProgramOrder");
        
        String sql = sqlBuilder.toString();
        List<Program> programList = simpleJdbcTemplate.query(sql, rowMapper);
        return programList;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByYukonProgramIds(final Set<Integer> programIds) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(" SELECT Distinct LMPDG.LMGroupDeviceId"); 
            sql.append(" FROM LMProgramDirectGroup LMPDG ");
            sql.append(" WHERE LMPDG.DeviceId in (", programIds, ") ");
            
            List<Integer> list = simpleJdbcTemplate.query(sql.toString(), groupIdRowMapper);
            return list;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctProgramIdsByGroupIds(final Set<Integer> groupIds) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(" SELECT Distinct LMPDG.DeviceId"); 
            sql.append(" FROM LMProgramDirectGroup LMPDG ");
            sql.append(" WHERE LMPDG.LMGroupDeviceId in (", groupIds, ") ");
            
            List<Integer> list = simpleJdbcTemplate.query(sql.toString(), programIdRowMapper);
            return list;
        } catch (IncorrectResultSizeDataAccessException e) {
            return Collections.emptyList();
        } 
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getGroupIdsByProgramId(final int programId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT lmpdg.LMGroupDeviceId");
        sqlBuilder.append("FROM YukonPAObject yp, LMProgramWebPublishing lmwp, YukonWebConfiguration ywc, LMProgramDirectGroup lmpdg"); 
        sqlBuilder.append("WHERE yp.PAObjectId = lmwp.DeviceId");
        sqlBuilder.append("AND lmwp.WebSettingsId = ywc.ConfigurationId");
        sqlBuilder.append("AND lmwp.DeviceId = lmpdg.DeviceId");
        sqlBuilder.append("AND lmwp.ProgramID = ?");
        String sql = sqlBuilder.toString();
        
        List<Integer> groupIdList = simpleJdbcTemplate.query(sql, groupIdRowMapper, programId);
        return groupIdList;
    }
    
    private ParameterizedRowMapper<Program> createRowMapper() {
        final ParameterizedRowMapper<Program> mapper = new ParameterizedRowMapper<Program>() {
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
        };
        return mapper;
    }
    
    private ParameterizedRowMapper<Integer> createGroupIdRowMapper() {
        final ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer groupId = rs.getInt("LMGroupDeviceId");
                return groupId;
            }
        };
        return mapper;
    }
    
    private ParameterizedRowMapper<Integer> createProgramIdRowMapper() {
        final ParameterizedRowMapper<Integer> mapper = new ParameterizedRowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Integer groupId = rs.getInt("DeviceId");
                return groupId;
            }
        };
        return mapper;
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
        String applianceCategoryLogoLocation = simpleJdbcTemplate.queryForObject(sql, String.class, applianceCategoryId);
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
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
