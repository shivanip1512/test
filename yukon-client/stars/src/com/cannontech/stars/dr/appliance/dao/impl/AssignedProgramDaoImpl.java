package com.cannontech.stars.dr.appliance.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public class AssignedProgramDaoImpl implements AssignedProgramDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private WebConfigurationDao webConfigurationDao;

    @Override
    public AssignedProgram getById(int assignedProgramId) {
        AssignedProgramRowMapper rowMapper =
            new AssignedProgramRowMapper();
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE p.programId").eq(assignedProgramId);

        AssignedProgram assignedProgram =
            simpleJdbcTemplate.queryForObject(sql.getSql(), rowMapper,
                                              sql.getArguments());
        WebConfiguration webConfiguration =
            webConfigurationDao.getForAssignedProgram(assignedProgramId);
        assignedProgram.setWebConfiguration(webConfiguration);

        return assignedProgram;
    }

    @Override
    public int getHighestProgramOrderForApplianceCategory(
            int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX(programOrder) FROM lmProgramWebPublishing");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId);
        return simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setWebConfigurationDao(WebConfigurationDao webConfigurationDao) {
        this.webConfigurationDao = webConfigurationDao;
    }
}
