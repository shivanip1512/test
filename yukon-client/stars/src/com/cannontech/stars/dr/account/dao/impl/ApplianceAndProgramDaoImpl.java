package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;

public class ApplianceAndProgramDaoImpl implements ApplianceAndProgramDao {
    private static final String selectAllProgramsForECSql;
    private static final String selectAllProgramsForECAndParentECSql;
    private static final String selectProgramsByLMGroupId;
    private static final ParameterizedRowMapper<ProgramLoadGroup> programGroupRowMapper;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    static {
        selectAllProgramsForECAndParentECSql = "SELECT yp.PAObjectId, MAX(lmwp.ProgramId) AS ProgramId, ywc.AlternateDisplayName, yp.PAOName, -1 AS LMGroupDeviceId FROM YukonPAObject yp," +
        " LMProgramWebPublishing lmwp, YukonWebConfiguration ywc WHERE lmwp.DeviceId = yp.PAObjectId AND lmwp.WebSettingsId = ywc.ConfigurationId" +
        " AND yp.Type = '" + PAOGroups.STRING_LM_DIRECT_PROGRAM[0] + "' AND lmwp.ApplianceCategoryId in" +
        " (SELECT ItemId FROM ECToGenericMapping WHERE MappingCategory = 'ApplianceCategory' AND (EnergyCompanyId = ?" +
        " OR EnergyCompanyId in (SELECT EnergyCompanyId FROM ECToGenericMapping WHERE ItemId = ? AND MappingCategory = 'Member')))" +
        " GROUP BY yp.PAObjectId, ywc.AlternateDisplayName, yp.PAOName";
        
        selectAllProgramsForECSql = "SELECT yp.PAObjectId,lmwp.ProgramId,ywc.AlternateDisplayName,yp.PAOName,-1 AS LMGroupDeviceId FROM YukonPAObject yp," +
                " LMProgramWebPublishing lmwp, YukonWebConfiguration ywc WHERE lmwp.DeviceId = yp.PAObjectId AND lmwp.WebSettingsId = ywc.ConfigurationId" +
                " AND yp.Type = '" + PAOGroups.STRING_LM_DIRECT_PROGRAM[0] + "' AND lmwp.ApplianceCategoryId in" +
                " (SELECT ItemId FROM ECToGenericMapping WHERE MappingCategory = 'ApplianceCategory' AND EnergyCompanyId = ?)";
        
        selectProgramsByLMGroupId = "SELECT yp.PAObjectId, lmwp.ProgramId, ywc.AlternateDisplayName, yp.PAOName, lmpdg.LMGroupDeviceId FROM " +
                "YukonPAObject yp, LMProgramWebPublishing lmwp, YukonWebConfiguration ywc, LMProgramDirectGroup lmpdg WHERE " +
                "yp.PAObjectId = lmwp.DeviceId AND lmwp.WebSettingsId = ywc.ConfigurationId AND lmwp.DeviceId = lmpdg.DeviceId AND " +
                "lmpdg.LMGroupDeviceId = ?";
        
        programGroupRowMapper = ApplianceAndProgramDaoImpl.createProgramGroupRowMapper();
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ProgramLoadGroup> getProgramsByLMGroupId(final int lmGroupId) throws DataAccessException {
        List<ProgramLoadGroup> progs = simpleJdbcTemplate.query(selectProgramsByLMGroupId, programGroupRowMapper, lmGroupId);
        return progs;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ProgramLoadGroup> getAllProgramsForAnEC(final int energyCompanyId) throws DataAccessException {
        List<ProgramLoadGroup> prog = simpleJdbcTemplate.query(selectAllProgramsForECSql, programGroupRowMapper, energyCompanyId);
        return prog;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ProgramLoadGroup> getAllProgramsForAnECAndParentEC(final int energyCompanyId) throws DataAccessException {
        List<ProgramLoadGroup> prog = simpleJdbcTemplate.query(selectAllProgramsForECAndParentECSql, programGroupRowMapper, energyCompanyId, energyCompanyId);
        return prog;
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    private static final ParameterizedRowMapper<ProgramLoadGroup> createProgramGroupRowMapper() {
        final ParameterizedRowMapper<ProgramLoadGroup> rowMapper = new ParameterizedRowMapper<ProgramLoadGroup>() {
            public ProgramLoadGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                final ProgramLoadGroup program = new ProgramLoadGroup();
                program.setPaobjectId(rs.getInt("PAObjectId"));
                String programName = rs.getString("AlternateDisplayName");
                if(programName != null && programName.length() > 1)
                    program.setProgramName(programName);
                else
                    program.setProgramName(rs.getString("PAOName"));
                program.setProgramId(rs.getInt("ProgramId"));
                program.setLmGroupId(rs.getInt("LMGroupDeviceId"));
                return program;
            }
        };
        return rowMapper;
    }
    
}
