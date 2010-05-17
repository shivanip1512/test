package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.model.ControllablePao;

public class PaoNameControllablePaoRowMapper implements
        ParameterizedRowMapper<ControllablePao> {

    public ControllablePao mapRow(ResultSet rs, int rowNum) throws SQLException {
        String paoName = rs.getString("paoName");
        int paoID = rs.getInt("paobjectId");
        String paoTypeStr = rs.getString("type");
        PaoType paoType = PaoType.getForDbString(paoTypeStr);
        return new ControllablePao(new PaoIdentifier(paoID, paoType),
                                     paoName);
    }
}
