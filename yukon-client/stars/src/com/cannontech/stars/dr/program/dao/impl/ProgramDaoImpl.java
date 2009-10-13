package com.cannontech.stars.dr.program.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.dao.ProgramRowMapper;
import com.cannontech.stars.dr.program.model.Program;

public class ProgramDaoImpl implements ProgramDao {
    private static final String selectSql;
    private final ParameterizedRowMapper<Integer> groupIdRowMapper = createGroupIdRowMapper();
    private final ParameterizedRowMapper<Integer> programIdRowMapper = createProgramIdRowMapper();
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final String selectSQLHeader =
        "SELECT LMPWP.programId, LMPWP.programOrder, YWC.description, YWC.url, "+
        "       YWC.alternateDisplayName, PAO.paoName, YLE.entryText as ChanceOfControl, "+
        "       LMPWP.applianceCategoryId, YWC.logoLocation "+
        "FROM LMProgramWebPublishing LMPWP "+
        "INNER JOIN YukonWebConfiguration YWC ON LMPWP.webSettingsId = YWC.configurationId "+
        "INNER JOIN YukonPAObject PAO ON PAO.paobjectId = LMPWP.deviceId "+
        "INNER JOIN YukonListEntry YLE ON YLE.entryId = LMPWP.chanceOfControlId ";

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
        Program program = simpleJdbcTemplate.queryForObject(sql, new ProgramRowMapper(simpleJdbcTemplate), programId);
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
    @Transactional
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
        }, idList, new ProgramRowMapper(simpleJdbcTemplate));
        
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
        List<Program> programList = simpleJdbcTemplate.query(sql, new ProgramRowMapper(simpleJdbcTemplate));
        return programList;
    }

    @Override
    @Transactional(readOnly = true)
    public Program getByProgramName(String programName,
                                          List<Integer> energyCompanyIds) {
        
        final SqlStatementBuilder programQuery = new SqlStatementBuilder();
        programQuery.append(selectSQLHeader);
        programQuery.append("INNER JOIN ECToGenericMapping ECTGM ON (ECTGM.itemId = LMPWP.applianceCategoryId");
        programQuery.append("                                        AND ECTGM.mappingCategory = 'ApplianceCategory')");
        programQuery.append("WHERE PAO.paoClass = ?");
        programQuery.append("AND PAO.category = ?");
        programQuery.append("AND PAO.paoName = ?");
        programQuery.append("AND ECTGM.energyCompanyId in (", energyCompanyIds, ")");
        
        try {
            return simpleJdbcTemplate.queryForObject(programQuery.toString(), 
            										 new ProgramRowMapper(simpleJdbcTemplate),
                                                     DeviceClasses.STRING_CLASS_LOADMANAGER,
                                                     PAOGroups.STRING_CAT_LOADMANAGEMENT,
                                                     programName);
        } catch(IncorrectResultSizeDataAccessException ex){
            throw new IllegalArgumentException("The program name supplied returned too many results or none at all.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Program getByAlternateProgramName(String alternateProgramName,
                                             List<Integer> energyCompanyIds) {
        
        final SqlStatementBuilder programQuery = new SqlStatementBuilder();
        programQuery.append(selectSQLHeader);
        programQuery.append("INNER JOIN ECToGenericMapping ECTGM ON (ECTGM.itemId = LMPWP.applianceCategoryId");
        programQuery.append("                                        AND ECTGM.mappingCategory = 'ApplianceCategory')");
        programQuery.append("WHERE ((PAO.paobjectId > 0 AND PAO.paoClass = ? AND PAO.category = ?)");
        programQuery.append("    OR (PAO.paobjectId = 0))");
        programQuery.append("AND (YWC.alternateDisplayName = ?");
        programQuery.append("     OR YWC.alternateDisplayName like ?");
        programQuery.append("     OR YWC.alternateDisplayName like ?)");
        programQuery.append("AND ECTGM.energyCompanyId in (", energyCompanyIds, ")");
        
        try {
            return simpleJdbcTemplate.queryForObject(programQuery.toString(), 
            										 new ProgramRowMapper(simpleJdbcTemplate),
                                                     DeviceClasses.STRING_CLASS_LOADMANAGER,
                                                     PAOGroups.STRING_CAT_LOADMANAGEMENT,
                                                     alternateProgramName,
                                                     alternateProgramName+",%",
                                                     "%,"+alternateProgramName);
        } catch(IncorrectResultSizeDataAccessException ex){
            throw new IllegalArgumentException("The alternate program name supplied returned too many results or none at all.");            
        }
    }

    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByYukonProgramIds(final Set<Integer> programIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(" SELECT Distinct LMPDG.LMGroupDeviceId "); 
        sql.append(" FROM LMProgramDirectGroup LMPDG ");
        sql.append(" WHERE LMPDG.DeviceId in (", programIds, ") ");
        
        List<Integer> list = simpleJdbcTemplate.query(sql.toString(), groupIdRowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByProgramIds(final Set<Integer> programIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(" SELECT Distinct LMPDG.LMGroupDeviceId "); 
        sql.append(" FROM LMProgramDirectGroup LMPDG ");
        sql.append(" WHERE LMPDG.DeviceId in (SELECT LMPWP.DeviceId ");
        sql.append("                          FROM LMProgramWebPublishing LMPWP ");
        sql.append("                          WHERE LMPWP.ProgramId in (", programIds, ")) ");
        
        List<Integer> list = simpleJdbcTemplate.query(sql.toString(), groupIdRowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctProgramIdsByGroupIds(final Set<Integer> groupIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(" SELECT Distinct LMPDG.DeviceId"); 
        sql.append(" FROM LMProgramDirectGroup LMPDG ");
        sql.append(" WHERE LMPDG.LMGroupDeviceId in (", groupIds, ") ");
        
        List<Integer> list = simpleJdbcTemplate.query(sql.toString(), programIdRowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getGroupIdsByProgramId(final int programId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT lmpdg.LMGroupDeviceId");
        sqlBuilder.append("FROM LMProgramWebPublishing lmwp, LMProgramDirectGroup lmpdg"); 
        sqlBuilder.append("WHERE lmwp.DeviceId = lmpdg.DeviceId");
        sqlBuilder.append("AND lmwp.ProgramID = ?");
        String sql = sqlBuilder.toString();
        
        List<Integer> groupIdList = simpleJdbcTemplate.query(sql, groupIdRowMapper, programId);
        return groupIdList;
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
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
