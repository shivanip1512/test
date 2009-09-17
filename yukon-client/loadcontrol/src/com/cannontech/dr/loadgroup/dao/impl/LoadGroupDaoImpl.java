package com.cannontech.dr.loadgroup.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;

public class LoadGroupDaoImpl implements LoadGroupDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

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
            DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                           rs.getString("paoName"));
            return retVal;
        }};

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
