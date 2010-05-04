package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.google.common.collect.Multimap;

/**
 * RowCallbackHandler to process pao permission authorizations 
 */
public class PaoPermissionRowCallbackHandler implements RowCallbackHandler {

    private final Multimap<Integer, YukonPao> paoLookup;
    private final Multimap<AuthorizationResponse, YukonPao> result;

    public PaoPermissionRowCallbackHandler(Multimap<Integer, YukonPao> paoLookup, 
                                           Multimap<AuthorizationResponse, YukonPao> result){
        this.paoLookup = paoLookup;
        this.result = result;

    }
    
    @Override
    public void processRow(ResultSet rs) throws SQLException {
        int paoId = rs.getInt("paoId");
        String allow = rs.getString("allow");
        AllowDeny allowDeny = AllowDeny.valueOf(allow);

        Collection<YukonPao> collection = paoLookup.removeAll(paoId);
        if(AllowDeny.ALLOW.equals(allowDeny)) {
            // Pao is authorized
            result.putAll(AuthorizationResponse.AUTHORIZED, collection);
        } else {
            // Pao is unauthorized
            result.putAll(AuthorizationResponse.UNAUTHORIZED, collection);
        }
    }

}
