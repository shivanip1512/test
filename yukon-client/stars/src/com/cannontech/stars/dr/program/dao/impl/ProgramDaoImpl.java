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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.dao.ProgramRowMapper;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ProgramDaoImpl implements ProgramDao {
    private static final String selectSql;
    private final ParameterizedRowMapper<Integer> groupIdRowMapper = createGroupIdRowMapper();
    private final ParameterizedRowMapper<Integer> programIdRowMapper = createProgramIdRowMapper();

    private ApplianceAndProgramDao applianceAndProgramDao;
    private ApplianceCategoryDao applianceCategoryDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonJdbcTemplate yukonJdbcTemplate;

    private final String selectSQLHeader =
        "SELECT LMPWP.programId, LMPWP.programOrder, YWC.description, YWC.url, "+
        "       YWC.alternateDisplayName, PAO.paoName, YLE.entryText as ChanceOfControl, "+
        "       LMPWP.applianceCategoryId, YWC.logoLocation, PAO.Type "+
        "FROM LMProgramWebPublishing LMPWP "+
        "INNER JOIN YukonWebConfiguration YWC ON LMPWP.webSettingsId = YWC.configurationId "+
        "INNER JOIN YukonPAObject PAO ON PAO.paobjectId = LMPWP.deviceId "+
        "INNER JOIN YukonListEntry YLE ON YLE.entryId = LMPWP.chanceOfControlId ";

    static {
        selectSql = "SELECT ProgramID,ProgramOrder,ywc.Description,ywc.url,AlternateDisplayName,PAOName,yle.EntryText as ChanceOfControl,ApplianceCategoryID,LogoLocation, ypo.Type " +
                    "FROM LMProgramWebPublishing pwp, YukonWebConfiguration ywc, YukonPAObject ypo, YukonListEntry yle " +
                    "WHERE pwp.WebsettingsID = ywc.ConfigurationID " +
                    "AND ypo.PAObjectID = pwp.DeviceID " +
                    "AND yle.EntryID = pwp.ChanceOfControlID";
        
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Program getByProgramId(final int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("AND pwp.ProgramID").eq(programId);
        Program program = yukonJdbcTemplate.queryForObject(sql, new ProgramRowMapper(yukonJdbcTemplate));
        return program;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Program> getByAppliances(final List<Appliance> applianceList) {
        final List<Integer> assignedProgramIdList = new ArrayList<Integer>(applianceList.size());
        for (final Appliance appliance : applianceList) {
            Integer programId = appliance.getProgramId();
            assignedProgramIdList.add(programId);
        }
        List<Program> programList = getByAssignedProgramIds(assignedProgramIdList);
        return programList;
    }

    @Override
    public List<Program> getByApplianceCategory(int applianceCategoryId) {
        ProgramRowMapper rowMapper = new ProgramRowMapper(yukonJdbcTemplate);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("AND applianceCategoryId").eq(applianceCategoryId);
        List<Program> retVal = yukonJdbcTemplate.query(sql, rowMapper);
        return retVal;
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
        
        final ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
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
        }, idList, new YukonRowMapperAdapter<Program>(new ProgramRowMapper(yukonJdbcTemplate)));
        
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
    public List<Program> getByAssignedProgramIds(final List<Integer> assignedProgramIdList) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append(" AND pwp.ProgramID IN (");
        sql.append(assignedProgramIdList);
        sql.append(")");
        sql.append(" ORDER BY ProgramOrder");

        List<Program> programList = yukonJdbcTemplate.query(sql, new ProgramRowMapper(yukonJdbcTemplate));
        return programList;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Program> getByProgramIds(final Iterable<Integer> programIdList) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append(" AND PWP.DeviceId").in(programIdList);
        sql.append(" ORDER BY ProgramOrder");
        
        List<Program> programList = yukonJdbcTemplate.query(sql, new ProgramRowMapper(yukonJdbcTemplate));
        return programList;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Program getByProgramName(String programName, int energyCompanyId) {
        
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        Set<Integer> appCatEnergyCompanyIds = applianceCategoryDao.getAppCatEnergyCompanyIds(yukonEnergyCompany);
        
        final SqlStatementBuilder programQuery = new SqlStatementBuilder();
        programQuery.append(selectSQLHeader);
        programQuery.append("INNER JOIN ECToGenericMapping ECTGM ON (ECTGM.itemId = LMPWP.applianceCategoryId");
        programQuery.append("                                        AND ECTGM.mappingCategory = 'ApplianceCategory')");
        programQuery.append("WHERE PAO.paoClass").eq_k(PaoClass.LOADMANAGEMENT);
        programQuery.append("AND PAO.category").eq_k(PaoCategory.LOADMANAGEMENT);
        programQuery.append("AND PAO.paoName").eq(programName);
        programQuery.append("AND ECTGM.energyCompanyId").in(appCatEnergyCompanyIds);
        
        try {
            return yukonJdbcTemplate.queryForObject(programQuery, new ProgramRowMapper(yukonJdbcTemplate));
        } catch(IncorrectResultSizeDataAccessException ex){
            throw new ProgramNotFoundException("The program name supplied returned too many results or none at all.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Program getByAlternateProgramName(String alternateProgramName, int energyCompanyId) {
        
        YukonEnergyCompany yukonEnergyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        Set<Integer> appCatEnergyCompanyIds = applianceCategoryDao.getAppCatEnergyCompanyIds(yukonEnergyCompany);
        
        final SqlStatementBuilder programQuery = new SqlStatementBuilder();
        programQuery.append(selectSQLHeader);
        programQuery.append("INNER JOIN ECToGenericMapping ECTGM ON (ECTGM.itemId = LMPWP.applianceCategoryId");
        programQuery.append("                                        AND ECTGM.mappingCategory = 'ApplianceCategory')");
        programQuery.append("WHERE ((PAO.paobjectId > 0 ");
        programQuery.append("        AND PAO.paoClass").eq_k(PaoClass.LOADMANAGEMENT);
        programQuery.append("        AND PAO.category").eq_k(PaoCategory.LOADMANAGEMENT).append(")");
        programQuery.append("    OR (PAO.paobjectId = 0))");
        programQuery.append("AND (YWC.alternateDisplayName").eq(alternateProgramName);
        programQuery.append("     OR YWC.alternateDisplayName like ").appendArgument(alternateProgramName+",%");
        programQuery.append("     OR YWC.alternateDisplayName like ").appendArgument("%,"+alternateProgramName).append(")");
        programQuery.append("AND ECTGM.energyCompanyId").in(appCatEnergyCompanyIds);
        
        try {
            return yukonJdbcTemplate.queryForObject(programQuery, new ProgramRowMapper(yukonJdbcTemplate));
        } catch(IncorrectResultSizeDataAccessException ex){
            throw new ProgramNotFoundException("The alternate program name supplied returned too many results or none at all.");            
        }
    }

    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctGroupIdsByYukonProgramIds(final Set<Integer> programIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(" SELECT Distinct LMPDG.LMGroupDeviceId "); 
        sql.append(" FROM LMProgramDirectGroup LMPDG ");
        sql.append(" WHERE LMPDG.DeviceId in (", programIds, ") ");
        
        List<Integer> list = yukonJdbcTemplate.query(sql.toString(), groupIdRowMapper);
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
        
        List<Integer> list = yukonJdbcTemplate.query(sql.toString(), groupIdRowMapper);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Integer> getDistinctProgramIdsByGroupIds(final Set<Integer> groupIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(" SELECT Distinct LMPDG.DeviceId"); 
        sql.append(" FROM LMProgramDirectGroup LMPDG ");
        sql.append(" WHERE LMPDG.LMGroupDeviceId in (", groupIds, ") ");
        
        List<Integer> list = yukonJdbcTemplate.query(sql.toString(), programIdRowMapper);
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
        
        List<Integer> groupIdList = yukonJdbcTemplate.query(sql, groupIdRowMapper, programId);
        return groupIdList;
    }
    
    @Override
    public String getProgramNames(int loadGroupId) {
        String programNamesStr = "";
        
        List<ProgramLoadGroup> programsByLMGroupId = 
            applianceAndProgramDao.getProgramsByLMGroupId(loadGroupId);
        
        List<String> enrolledProgramNames =
            Lists.transform(programsByLMGroupId, new Function<ProgramLoadGroup, String>() {
   
                @Override
                public String apply(ProgramLoadGroup programLoadGroup) {
                    return programLoadGroup.getProgramName();
                }
            });
        
        if (enrolledProgramNames != null && enrolledProgramNames.size() > 0) {
            
            programNamesStr = enrolledProgramNames.get(0);
            for (int i = 1; i < enrolledProgramNames.size(); i++) {
                if (enrolledProgramNames.size() - 1 < i) {
                    programNamesStr += ", ";
                }
                
                String programName = enrolledProgramNames.get(i);
                programNamesStr += programName;
            }
        }
        
        return programNamesStr;
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

    // DI Setters
    @Autowired
    public void setApplianceAndProgramDao(ApplianceAndProgramDao applianceAndProgramDao) {
        this.applianceAndProgramDao = applianceAndProgramDao;
    }
    
    @Autowired
    public void setApplianceCategoryDao(ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
