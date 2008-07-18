package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class ChunkingSqlTemplate<E> {
    public static final int DEFAULT_SIZE = 1000;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private int chunkSize;
    
    public ChunkingSqlTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.chunkSize = DEFAULT_SIZE;
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public <R> List<R> query(final SqlGenerator<E> sqlGenerator, final Collection<E> input, 
                 final ParameterizedRowMapper<R> rowMapper, final Object... args) {
        
        final List<E> tempInputList = new ArrayList<E>(input);
        final List<R> resultList = new ArrayList<R>(tempInputList.size());
        final List<String> queryList = new ArrayList<String>();
        
        int inputSize = tempInputList.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<E> subList = tempInputList.subList(start, toIndex);
            String query = sqlGenerator.generate(subList);
            queryList.add(query);
        }

        for (final String sql : queryList) {
            List<R> list = simpleJdbcTemplate.query(sql, rowMapper, args);
            resultList.addAll(list);
        }
        
        return resultList;
    }
    
    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
