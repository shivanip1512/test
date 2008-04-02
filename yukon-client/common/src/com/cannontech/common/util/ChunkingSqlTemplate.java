package com.cannontech.common.util;

import java.util.ArrayList;
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
    
    public <R> List<R> query(SqlGenerator<E> sqlGenerator, List<E> input, ParameterizedRowMapper<R> rowMapper, Object... args) {
        final List<R> resultList = new ArrayList<R>(input.size());
        final List<String> queryList = new ArrayList<String>();
        
        int inputSize = input.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<E> subList = input.subList(start, toIndex);
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
