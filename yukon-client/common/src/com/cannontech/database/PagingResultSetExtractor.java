package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class PagingResultSetExtractor<T> implements ResultSetExtractor {
	
	private int start;
	private int pageCount;
	private ParameterizedRowMapper<T> rowMapper;

	public PagingResultSetExtractor(int start, int pageCount, ParameterizedRowMapper<T> rowMapper) {
		this.start = start;
		this.pageCount = pageCount;
		this.rowMapper = rowMapper;
	}
	
	public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        
        List<T> resultList = new ArrayList<T>();
        
        // Move the cursor to the correct spot in the result set so we only
        // process the results we want
        for(int i = start; i > 0; i--){
            rs.next();
        }
        
        while(rs.next() && pageCount-- > 0){
            
            T mappedObject = rowMapper.mapRow(rs, rs.getRow());
            resultList.add(mappedObject);
        }

        return resultList;
    }
}
