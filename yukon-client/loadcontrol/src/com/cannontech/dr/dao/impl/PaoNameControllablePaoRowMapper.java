package com.cannontech.dr.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.loadgroup.model.LoadGroup;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.program.model.Program;
import com.cannontech.dr.scenario.model.Scenario;

public class PaoNameControllablePaoRowMapper implements
        ParameterizedRowMapper<ControllablePao> {

    public ControllablePao mapRow(ResultSet rs, int rowNum) throws SQLException {
        String paoName = rs.getString("paoName");
        int paoID = rs.getInt("paobjectId");
        String paoTypeStr = rs.getString("type");
        PaoType paoType = PaoType.getForDbString(paoTypeStr);
        
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoID, paoType);
        
        switch (paoType) {
            case LM_SCENARIO:
                int scenarioProgramCount = rs.getInt("ScenarioProgramCount");
                return new Scenario(paoIdentifier, paoName, scenarioProgramCount);

            case LM_CONTROL_AREA:
                return new ControlArea(paoIdentifier, paoName);

            case LM_DIRECT_PROGRAM:
                return new Program(paoIdentifier, paoName);

            case LM_GROUP_EMETCON:
            case LM_GROUP_EXPRESSCOMM:
            case LM_GROUP_GOLAY:
            case LM_GROUP_INTEGRATION:
            case LM_GROUP_MCT:
            case LM_GROUP_POINT:
            case LM_GROUP_RIPPLE:
            case LM_GROUP_SA205:
            case LM_GROUP_SA305:
            case LM_GROUP_SADIGITAL:
            case LM_GROUP_VERSACOM:
            case MACRO_GROUP:
                return new LoadGroup(paoIdentifier, paoName);

            default:
                throw new IllegalArgumentException(paoName +" is not a valid type ("+paoType+")");
        }
    }
}
