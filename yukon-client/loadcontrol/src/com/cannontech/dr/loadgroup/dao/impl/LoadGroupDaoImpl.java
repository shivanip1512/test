package com.cannontech.dr.loadgroup.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.loadgroup.model.LoadGroup;
import com.cannontech.dr.model.ControllablePao;

public class LoadGroupDaoImpl implements LoadGroupDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String singleLoadGroupByIdQuery = 
        "SELECT paObjectId, paoName, type FROM yukonPAObject " 
        + "WHERE category = 'DEVICE' AND paoClass = 'GROUP' AND paObjectId = ?";

    private final static ParameterizedRowMapper<ControllablePao> loadGroupRowMapper =
        new ParameterizedRowMapper<ControllablePao>() {
        @Override
        public ControllablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            String paoTypeStr = rs.getString("type");
            int deviceTypeId = PAOGroups.getPAOType("DEVICE", paoTypeStr);
            PaoType paoType = PaoType.getForId(deviceTypeId);
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    paoType);
            ControllablePao retVal = new LoadGroup(paoId,
                                                    rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<ControllablePao> getForProgram(int programId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectId, paoName, type FROM yukonPaobject");
        sql.append("WHERE paobjectId IN (");
        sql.append("SELECT lmGroupDeviceId FROM lmProgramDirectGroup");
        sql.append("WHERE deviceId").eq(programId).append(")");

        return simpleJdbcTemplate.query(sql.getSql(),
                                        loadGroupRowMapper,
                                        sql.getArguments());
    }

    @Override
    public ControllablePao getLoadGroup(int loadGroupId) {
        return simpleJdbcTemplate.queryForObject(singleLoadGroupByIdQuery,
                                                 loadGroupRowMapper,
                                                 loadGroupId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
