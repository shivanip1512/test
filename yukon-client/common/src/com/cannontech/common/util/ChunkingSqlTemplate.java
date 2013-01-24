package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.database.CollectionRowCallbackHandler;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowCallbackHandlerAdapter;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.YukonRowMapperAdapter;
import com.google.common.collect.Lists;

public class ChunkingSqlTemplate {
    public static final int DEFAULT_SIZE = 1000;

    private YukonJdbcTemplate yukonJdbcTemplate;
    private int chunkSize = DEFAULT_SIZE;
    
    /**
     * @deprecated - instead of using this constructor use the YukonJdbTemplate constructor.
     */
    @Deprecated
    public ChunkingSqlTemplate(final SimpleJdbcOperations simpleJdbcTemplate) {
        this.yukonJdbcTemplate = new YukonJdbcTemplate(simpleJdbcTemplate.getJdbcOperations());
    }
    
    public ChunkingSqlTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    /**
     * @deprecated - args should not be passed into this method.  We should include them in the sqlGenerator.
     */
    @Deprecated
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
            List<R> list = yukonJdbcTemplate.query(sql, rowMapper, args);
            resultList.addAll(list);
        }

        return resultList;
    }
    
    public <I, R> List<R> query(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, final ParameterizedRowMapper<R> rowMapper) {
    	List<R> resultList = Lists.newArrayList();

    	CollectionRowCallbackHandler<R> rch = new CollectionRowCallbackHandler<R>(rowMapper, resultList);
    	query(sqlGenerator, input, rch);

    	return resultList;
    }

    public <I, R> void queryInto(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, 
                                final YukonRowMapper<R> rowMapper, Collection<? super R> resultSink) {
        
        CollectionRowCallbackHandler<R> rch = new CollectionRowCallbackHandler<R>(rowMapper, resultSink);
        query(sqlGenerator, input, rch);
    }

    public <I> void query(SqlFragmentGenerator<I> sqlGenerator, Iterable<I> input, YukonRowCallbackHandler rch) {
        YukonRowCallbackHandlerAdapter yrch = new YukonRowCallbackHandlerAdapter(rch);
        query(sqlGenerator, input, yrch);
    }

    public <I> void query(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, final RowCallbackHandler rch) {
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
            yukonJdbcTemplate.query(sql, rch);
        }
    }

    /**
     * This is a chunking version of the queryForInt jdbc call.  This should be used when ever more than 1000 ids need to be used in a query
     */
    public <I> int queryForSum(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input) {
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
        
        int result = 0;
        for (final SqlFragmentSource sql : queryList) {
            result += yukonJdbcTemplate.queryForInt(sql);
        }
        return result;
    }
    
    /**
     * @deprecated - args should not be passed into this method.  We should include them in the sqlGenerator.
     */
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
            yukonJdbcTemplate.update(sql, args);
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
    	    yukonJdbcTemplate.update(sql);
    	}
    }
    
    public <I, R> List<R> query(final SqlFragmentGenerator<I> sqlGenerator, final Iterable<I> input, final YukonRowMapper<R> rowMapper) {
        return query(sqlGenerator, input, new YukonRowMapperAdapter<R>(rowMapper));
    }

    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
