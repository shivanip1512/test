package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.scenario.model.Scenario;

public class PaoNameControllablePaoRowMapper implements
        ParameterizedRowMapper<ControllablePao> {

    public ControllablePao mapRow(ResultSet rs, int rowNum) throws SQLException {
        String paoName = rs.getString("paoName");
        int paoID = rs.getInt("paobjectId");
        String paoTypeStr = rs.getString("type");
        PaoType paoType = PaoType.getForDbString(paoTypeStr);
        
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoID, paoType);
        if (paoType.equals(PaoType.LM_SCENARIO)) {
            int scenarioProgramCount = rs.getInt("ScenarioProgramCount");
            return new Scenario(paoIdentifier, paoName, scenarioProgramCount);
        }
        
        return new ControllablePao(paoIdentifier, paoName);
    }
}
