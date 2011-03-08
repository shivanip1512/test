package com.cannontech.stars.dr.account.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.energyCompany.EcMappingCategory;

public class ApplianceAndProgramDaoImpl implements ApplianceAndProgramDao {

    private static final ParameterizedRowMapper<ProgramLoadGroup> programGroupRowMapper;
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDefinitionDao paoDefinitionDao;
    
    static {
        programGroupRowMapper = ApplianceAndProgramDaoImpl.createProgramGroupRowMapper();
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ProgramLoadGroup> getProgramsByLMGroupId(final int lmGroupId) throws DataAccessException {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT yp.PAObjectId, MAX(lmwp.ProgramId) AS ProgramId, ywc.AlternateDisplayName, yp.PAOName, lmpdg.LMGroupDeviceId");
    	sql.append("FROM YukonPAObject yp");
    	sql.append(		"JOIN LMProgramWebPublishing lmwp ON yp.PAObjectId = lmwp.DeviceId");
    	sql.append(		"JOIN YukonWebConfiguration ywc ON lmwp.WebSettingsId = ywc.ConfigurationId");
    	sql.append(		"JOIN LMProgramDirectGroup lmpdg ON lmwp.DeviceId = lmpdg.DeviceId");
    	sql.append("WHERE lmpdg.LMGroupDeviceId").eq(lmGroupId);
    	sql.append("GROUP BY yp.PAObjectId, ywc.AlternateDisplayName, yp.PAOName, lmpdg.LMGroupDeviceId");
        List<ProgramLoadGroup> progs = yukonJdbcTemplate.query(sql, programGroupRowMapper);
        return progs;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ProgramLoadGroup> getAllProgramsForAnEC(final int energyCompanyId) throws DataAccessException {
    	
    	Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT yp.PAObjectId, lmwp.ProgramId, ywc.AlternateDisplayName, yp.PAOName, -1 AS LMGroupDeviceId");
    	sql.append("FROM YukonPAObject yp");
    	sql.append(		"JOIN LMProgramWebPublishing lmwp ON lmwp.DeviceId = yp.PAObjectId");
    	sql.append(		"JOIN YukonWebConfiguration ywc ON lmwp.WebSettingsId = ywc.ConfigurationId");
    	sql.append("WHERE yp.Type").in(paoTypes);
    	sql.append(		"AND lmwp.ApplianceCategoryId IN (");
    	sql.append(			"SELECT ItemId");
    	sql.append(			"FROM ECToGenericMapping");
    	sql.append(			"WHERE MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
    	sql.append(				"AND EnergyCompanyId").eq(energyCompanyId);
    	sql.append(		")");
    	
        List<ProgramLoadGroup> prog = yukonJdbcTemplate.query(sql, programGroupRowMapper);
        return prog;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ProgramLoadGroup> getAllProgramsForAnECAndParentEC(final int energyCompanyId) throws DataAccessException {
    	
    	Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT yp.PAObjectId, MAX(lmwp.ProgramId) AS ProgramId, ywc.AlternateDisplayName, yp.PAOName, -1 AS LMGroupDeviceId");
    	sql.append("FROM YukonPAObject yp");
    	sql.append(		"JOIN LMProgramWebPublishing lmwp ON lmwp.DeviceId = yp.PAObjectId");
    	sql.append(		"JOIN YukonWebConfiguration ywc ON lmwp.WebSettingsId = ywc.ConfigurationId");
    	sql.append("WHERE yp.Type").in(paoTypes);
    	sql.append(		"AND lmwp.ApplianceCategoryId IN (");
    	sql.append(			"SELECT ItemId");
    	sql.append(			"FROM ECToGenericMapping");
    	sql.append(			"WHERE MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
    	sql.append(				"AND (");
    	sql.append(					"EnergyCompanyId").eq(energyCompanyId);
    	sql.append(					"OR EnergyCompanyId IN (");
    	sql.append(						"SELECT EnergyCompanyId");
    	sql.append(						"FROM ECToGenericMapping");
    	sql.append(						"WHERE ItemId").eq(energyCompanyId);
    	sql.append(							"AND MappingCategory").eq_k(EcMappingCategory.MEMBER);
    	sql.append(					")");
    	sql.append(				")");
    	sql.append(		")");
    	sql.append("GROUP BY yp.PAObjectId, ywc.AlternateDisplayName, yp.PAOName");
    	
        List<ProgramLoadGroup> prog = yukonJdbcTemplate.query(sql, programGroupRowMapper);
        return prog;
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

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
}
