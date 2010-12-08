package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.google.common.collect.Lists;

public class ChunkingSqlTemplate {
    public static final int DEFAULT_SIZE = 1000;
    private SimpleJdbcOperations simpleJdbcTemplate;
    private int chunkSize;
    
    public ChunkingSqlTemplate(final SimpleJdbcOperations simpleJdbcTemplate) {
        this.chunkSize = DEFAULT_SIZE;
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public <I, R> List<R> query(final SqlGenerator<I> sqlGenerator, final Iterable<I> input, 
                 final ParameterizedRowMapper<R> rowMapper, final Object... args) {
        
        final List<I> tempInputList = Lists.newArrayList(input);
        final List<R> resultList = new ArrayList<R>(tempInputList.size());
        final List<String> queryList = new ArrayList<String>();
        
        int inputSize = tempInputList.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<I> subList = tempInputList.subList(start, toIndex);
            String query = sqlGenerator.generate(subList);
            queryList.add(query);
        }

        for (final String sql : queryList) {
            List<R> list = simpleJdbcTemplate.query(sql, rowMapper, args);
            resultList.addAll(list);
        }
        
        return resultList;
    }
    
    public <I, R> List<R> query(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, 
    		final ParameterizedRowMapper<R> rowMapper) {
    	
    	List<R> resultList = Lists.newArrayList();
    	CollectionRowCallbackHandler<R> rch = 
    	    new CollectionRowCallbackHandler<R>(rowMapper, resultList);
    	
    	query(sqlGenerator, input, rch);
    	
    	return resultList;
    }

    public <I, R> void queryInto(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, 
                                final YukonRowMapper<R> rowMapper, Collection<? super R> resultSink) {
        
        CollectionRowCallbackHandler<R> rch = 
            new CollectionRowCallbackHandler<R>(rowMapper, resultSink);
        
        query(sqlGenerator, input, rch);
    }
    
    public <I> void query(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, 
                             final RowCallbackHandler rch) {
        
        final List<I> tempInputList = Lists.newArrayList(input);
        final List<SqlFragmentSource> queryList = Lists.newArrayList();
        
        int inputSize = tempInputList.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<I> subList = tempInputList.subList(start, toIndex);
            SqlFragmentSource sqlFragmentSource = sqlGenerator.generate(subList);
            queryList.add(sqlFragmentSource);
        }
        
        for (final SqlFragmentSource sql : queryList) {
            simpleJdbcTemplate.getJdbcOperations().query(sql.getSql(), sql.getArguments(), rch);
        }
        
    }
    
    public <I> void update(final SqlGenerator<I> sqlGenerator, final Iterable<I> input, final Object... args) {
                    
        final List<I> tempInputList = Lists.newArrayList(input);
        final List<String> queryList = new ArrayList<String>();
        
        int inputSize = tempInputList.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<I> subList = tempInputList.subList(start, toIndex);
            String query = sqlGenerator.generate(subList);
            queryList.add(query);
        }

        for (final String sql : queryList) {
            simpleJdbcTemplate.update(sql, args);
        }
    }

    public <I> void update(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input) {
    	
    	final List<I> tempInputList = Lists.newArrayList(input);
    	final List<SqlFragmentSource> sqlFragmentList = new ArrayList<SqlFragmentSource>();
    	
    	int inputSize = tempInputList.size();
    	for (int start = 0; start < inputSize; start += chunkSize ) {
    		int nextToIndex = start + chunkSize;
    		int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
    		
    		List<I> subList = tempInputList.subList(start, toIndex);
    		
    		SqlFragmentSource sqlFragmentSource = sqlGenerator.generate(subList);
    		sqlFragmentList.add(sqlFragmentSource);
    	}
    	
    	for (final SqlFragmentSource sql : sqlFragmentList) {
    		simpleJdbcTemplate.update(sql.getSql(), sql.getArguments());
    	}
    }
    
    public <I, R> List<R> query(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, 
                                final YukonRowMapper<R> rowMapper) {
        
        return query(sqlGenerator, input, new YukonRowMapperAdapter<R>(rowMapper));
    }

    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
