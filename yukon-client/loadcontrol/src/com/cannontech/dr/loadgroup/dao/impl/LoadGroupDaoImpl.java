package com.cannontech.dr.loadgroup.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;

public class LoadGroupDaoImpl implements LoadGroupDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String baseLoadGroupQuery = 
        "SELECT paObjectId, paoName, type FROM yukonPAObject " 
        + "WHERE category = 'DEVICE' AND paoClass = 'GROUP'";
    private final static String loadGroupsByProgramIdQuery = 
        "SELECT paObjectId, paoName, type FROM yukonPAObject " 
        + "WHERE category = 'DEVICE' AND paoClass = 'GROUP' " 
        + "AND paObjectId IN (SELECT lmGroupDeviceId FROM lmProgramDirectGroup WHERE deviceId = ?)";
    private final static String singleLoadGroupByIdQuery = 
        "SELECT paObjectId, paoName, type FROM yukonPAObject " 
        + "WHERE category = 'DEVICE' AND paoClass = 'GROUP' AND paObjectId = ?";

    private final static ParameterizedRowMapper<DisplayablePao> loadGroupRowMapper =
        new ParameterizedRowMapper<DisplayablePao>() {
        @Override
        public DisplayablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            String paoTypeStr = rs.getString("type");
            int deviceTypeId = PAOGroups.getPAOType("DEVICE", paoTypeStr);
            PaoType paoType = PaoType.getForId(deviceTypeId);
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    paoType);
            DisplayablePao retVal = new DisplayableDevice(paoId,
                                                          rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<DisplayablePao> getLoadGroups() {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(baseLoadGroupQuery,
                                                               loadGroupRowMapper);
        return retVal;
    }
    
    @Override
    public List<DisplayablePao> getLoadGroupsForProgram(int programId) {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(loadGroupsByProgramIdQuery,
                                                               loadGroupRowMapper,
                                                               programId);
        return retVal;
    }

    @Override
    public DisplayablePao getLoadGroup(int loadGroupId) {
        return simpleJdbcTemplate.queryForObject(singleLoadGroupByIdQuery,
                                                 loadGroupRowMapper,
                                                 loadGroupId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
