package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.google.common.collect.Lists;

public class ChunkingSqlTemplate<E> {
    public static final int DEFAULT_SIZE = 1000;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private int chunkSize;
    
    public ChunkingSqlTemplate(final SimpleJdbcOperations simpleJdbcTemplate) {
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
    
    public <R> List<R> query(final SqlFragmentGenerator<E> sqlGenerator, final Collection<E> input, 
    		final ParameterizedRowMapper<R> rowMapper) {
    	
    	final List<E> tempInputList = new ArrayList<E>(input);
    	final List<R> resultList = new ArrayList<R>(tempInputList.size());
    	final List<SqlFragmentSource> queryList = new ArrayList<SqlFragmentSource>();
    	
    	int inputSize = tempInputList.size();
    	for (int start = 0; start < inputSize; start += chunkSize ) {
    		int nextToIndex = start + chunkSize;
    		int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
    		
    		List<E> subList = tempInputList.subList(start, toIndex);
    		SqlFragmentSource sqlFragmentSource = sqlGenerator.generate(subList);
    		queryList.add(sqlFragmentSource);
    	}
    	
    	for (final SqlFragmentSource sql : queryList) {
    		List<R> list = simpleJdbcTemplate.query(sql.getSql(), rowMapper, sql.getArguments());
    		resultList.addAll(list);
    	}
    	
    	return resultList;
    }

    public <R> List<R> query(final SqlFragmentGenerator<E> sqlGenerator, final Collection<E> input, 
                             final RowCallbackHandler rch) {
        
        final List<E> tempInputList = Lists.newArrayList(input);
        final List<R> resultList = Lists.newArrayList();
        final List<SqlFragmentSource> queryList = Lists.newArrayList();
        
        int inputSize = tempInputList.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<E> subList = tempInputList.subList(start, toIndex);
            SqlFragmentSource sqlFragmentSource = sqlGenerator.generate(subList);
            queryList.add(sqlFragmentSource);
        }
        
        for (final SqlFragmentSource sql : queryList) {
            simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rch);
        }
        
        return resultList;
    }
    
    public void update(final SqlGenerator<E> sqlGenerator, final Collection<E> input, final Object... args) {
                    
        final List<E> tempInputList = new ArrayList<E>(input);
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
            simpleJdbcTemplate.update(sql, args);
        }
    }

    public void update(final SqlFragmentGenerator<E> sqlGenerator, final Collection<E> input) {
    	
    	final List<E> tempInputList = new ArrayList<E>(input);
    	final List<SqlFragmentSource> sqlFragmentList = new ArrayList<SqlFragmentSource>();
    	
    	int inputSize = tempInputList.size();
    	for (int start = 0; start < inputSize; start += chunkSize ) {
    		int nextToIndex = start + chunkSize;
    		int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
    		
    		List<E> subList = tempInputList.subList(start, toIndex);
    		
    		SqlFragmentSource sqlFragmentSource = sqlGenerator.generate(subList);
    		sqlFragmentList.add(sqlFragmentSource);
    	}
    	
    	for (final SqlFragmentSource sql : sqlFragmentList) {
    		simpleJdbcTemplate.update(sql.getSql(), sql.getArguments());
    	}
    }
    
    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
