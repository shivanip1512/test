package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * An implementation of ResultSetExtractor that will return subsets of the result set.
 * Useful for paging search results.  Use this when retrieving data for a paged box container.
 * @param <T>
 */
public class PagingExtractor<T> implements ResultSetExtractor {
    
    private int pageCount = 0;
    private int start = 0; 
    private YukonRowMapper<T> mapper;
    
    public PagingExtractor (int start, int pageCount, YukonRowMapper<T> mapper) {
        this.start = start;
        this.pageCount = pageCount;
        this.mapper = mapper;
    }

    @Override
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<T> list = new ArrayList<T>();
        
        // Move the cursor to the correct spot in the result set so we only
        // process the results we want
        for(int i = start; i > 0; i--){
            rs.next();
        }
        
        while(rs.next() && pageCount-- > 0){
            
            T thing = mapper.mapRow(new YukonResultSet(rs));
            list.add(thing);
        }

        return list;
    }

}