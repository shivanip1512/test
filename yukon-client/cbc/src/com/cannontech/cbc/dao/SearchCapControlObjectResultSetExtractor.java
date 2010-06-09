package com.cannontech.cbc.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.cbc.model.LiteCapControlObject;
import com.google.common.collect.Lists;

public class SearchCapControlObjectResultSetExtractor implements ResultSetExtractor {
    
    private int pageCount = 0;
    private int start = 0; 
    private ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper;

    public SearchCapControlObjectResultSetExtractor(ParameterizedRowMapper<LiteCapControlObject> liteCapControlObjectRowMapper, int start, int pageCount) {
        this.pageCount = pageCount;
        this.start = start;
        this.liteCapControlObjectRowMapper = liteCapControlObjectRowMapper;
    }

    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        
        List<LiteCapControlObject> ccObjectList = Lists.newArrayList();
        
        // Move the cursor to the correct spot in the result set so we only
        // process the results we want
        for(int i = start; i > 0; i--){
            rs.next();
        }
        
        while(rs.next() && pageCount-- > 0){
            
            LiteCapControlObject ccObject = liteCapControlObjectRowMapper.mapRow(rs, rs.getRow());
            ccObjectList.add(ccObject);
        }

        return ccObjectList;
    }
}