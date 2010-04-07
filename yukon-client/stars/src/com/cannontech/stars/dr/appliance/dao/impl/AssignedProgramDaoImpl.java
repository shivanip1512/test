package com.cannontech.stars.dr.appliance.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramRowMapper;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.webconfiguration.dao.WebConfigurationDao;
import com.cannontech.stars.webconfiguration.model.WebConfiguration;

public class AssignedProgramDaoImpl implements AssignedProgramDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private WebConfigurationDao webConfigurationDao;

    @Override
    public AssignedProgram getById(int assignedProgramId) {
        AssignedProgramRowMapper rowMapper = new AssignedProgramRowMapper();
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("WHERE p.programId").eq(assignedProgramId);

        AssignedProgram assignedProgram =
            yukonJdbcTemplate.queryForObject(sql, rowMapper);
        WebConfiguration webConfiguration =
            webConfigurationDao.getForAssignedProgram(assignedProgramId);
        assignedProgram.setWebConfiguration(webConfiguration);

        return assignedProgram;
    }

    @Override
    public List<AssignedProgram> getByIds(Collection<Integer> assignedProgramIds) {
        Map<Integer, WebConfiguration> webConfigurations =
            webConfigurationDao.getForAssignedPrograms(assignedProgramIds);

        ChunkingSqlTemplate<Integer> template =
            new ChunkingSqlTemplate<Integer>(yukonJdbcTemplate);

        final AssignedProgramRowMapper rowMapper =
            new AssignedProgramRowMapper(true, false, -1, webConfigurations);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append(rowMapper.getBaseQuery());
                sql.append("WHERE p.programId IN (").appendList(subList).append(")");
                return sql;
            }
        };

        List<AssignedProgram> retVal =
            template.query(sqlGenerator, assignedProgramIds, rowMapper);

        return retVal;
    }

    @Override
    public int getHighestProgramOrderForApplianceCategory(
            int applianceCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX(programOrder) FROM lmProgramWebPublishing");
        sql.append("WHERE applianceCategoryId").eq(applianceCategoryId);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setWebConfigurationDao(WebConfigurationDao webConfigurationDao) {
        this.webConfigurationDao = webConfigurationDao;
    }
}
