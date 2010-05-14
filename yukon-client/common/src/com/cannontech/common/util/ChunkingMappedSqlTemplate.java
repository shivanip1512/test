package com.cannontech.common.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

public class ChunkingMappedSqlTemplate {

	private SimpleJdbcOperations simpleJdbcTemplate;
    
    public ChunkingMappedSqlTemplate(final SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    /**
     * Returns a mapping of inputs to results. Query is run in a chunking manner. This method 
     * features the ability to keep the resulting map keys in the order of the input list.
     * Example: Normally a query with the clause WHERE someId IN (1,2,3,4) may return the results in 
     * an arbitrary order. However, you may want the results to be return in the same order as those 
     * input values used in the IN clause. That is what this method provides. 
     * @param <I> Type of value used by the SqlFragmentGenerator. 
     *        Often Integer or String.
     * @param <C> Type of values in the input list and the result map key type. May be a complex 
     *        type that contains a field of type &lt;I&gt;, see inputTypeToSqlGeneratorTypeMapper 
     *        parameter.
     * @param <R> Type of model object created by the row mapper and the result map value type.
     * @param sqlGenerator An SqlFragmentGenerator that takes objects of type &lt;I&gt; to create 
     *        its IN-Clause  
     * @param inputList A list of input values. The order of this list will determine the result 
     *        map's key ordering.
     * @param rowMapper A typical row mapper, but instead of returning just the model object it 
     *        should return a single Map.Entry that wraps the search value as the key and the model 
     *        object as the value.
     * @param inputTypeToSqlGeneratorTypeMapper A mapper that can convert from the input type to the 
     *        SqlFragmentGenerator type. This allows complex types to be passed as the input and be 
     *        the basis of the result ordering, but the function defines what discrete part of the 
     *        input type to use for the query search value. Tip: If your input type is the same as 
     *        your generator type, use Functions.identity() to create your 
     *        inputTypeToSqlGeneratorTypeMapper.
     * @return
     */
	public <I, R, C> Map<C, R> mappedQuery(final SqlFragmentGenerator<I> sqlGenerator, 
										final Iterable<C> input,
										final ParameterizedRowMapper<Map.Entry<I, R>> rowMapper,
										Function<C, I> inputTypeToSqlGeneratorTypeMapper) {

		Map<C, R> resultMap = Maps.newLinkedHashMap();
		final Map<I, R> intermediaryResult = Maps.newHashMap();

		ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(simpleJdbcTemplate);
		chunkingTemplate.query(sqlGenerator, Lists.transform(Lists.newArrayList(input), inputTypeToSqlGeneratorTypeMapper), new RowCallbackHandler() {
					
			private int row = 0;
					
			public void processRow(ResultSet rs) throws SQLException {
				Entry<I, R> entry = rowMapper.mapRow(rs, row++);
				intermediaryResult.put(entry.getKey(), entry.getValue());
			}
		});

		for (C i : input) {
		    I e = inputTypeToSqlGeneratorTypeMapper.apply(i);
		    if (intermediaryResult.containsKey(e)) {
		        R r = intermediaryResult.get(e);
		        resultMap.put(i, r);
		    }
		}

		return resultMap;
	}
	
    /**
     * Returns a mapping of results to inputs. Query is run in a chunking manner.
     * @param <I> Type of value used by the SqlFragmentGenerator. Often Integer or String.
     * @param <C> Type of values in the input list and the result map value type. 
     *        May be a complex type that contains a field of type &lt;I&gt;, see 
     *        inputTypeToSqlGeneratorTypeMapper parameter.
     * @param <R> Type of model object created by the row mapper and the result map key type.
     * @param sqlGenerator An SqlFragmentGenerator that takes objects of type &lt;I&gt; to create 
     *        its IN-Clause  
     * @param inputList A list of input values.
     * @param rowMapper A typical row mapper, but instead of returning just the model object 
     *        it should return a single Map.Entry that wraps the search value as the key and the 
     *        model object as the value.
     * @param inputTypeToSqlGeneratorTypeMapper A mapper that can convert from the input type to 
     *        the SqlFragmentGenerator type. This allows complex types to be passed as the input 
     *        and be the basis of the result, but the function defines what discrete part of the 
     *        input type to use for the query search value. Tip: If your input type is the same as 
     *        your generator type, use Functions.identity() to create your 
     *        inputTypeToSqlGeneratorTypeMapper.
     * @return
     */
	public <I, R, C> SetMultimap<R, C> reverseMultimappedQuery(final SqlFragmentGenerator<I> sqlGenerator, 
	                                                           final Iterable<C> input,
	                                                           final ParameterizedRowMapper<Map.Entry<I, R>> rowMapper,
	                                                           Function<C, I> inputTypeToSqlGeneratorTypeMapper) {
	    
	    SetMultimap<R, C> resultMap = HashMultimap.create();
	    final Map<I, R> intermediaryResult = Maps.newHashMap();
	    
	    ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(simpleJdbcTemplate);
	    chunkingTemplate.query(sqlGenerator, 
	                           Lists.transform(Lists.newArrayList(input), inputTypeToSqlGeneratorTypeMapper), 
	                           new RowCallbackHandler() {
	        
	        private int row = 0;
	        
	        public void processRow(ResultSet rs) throws SQLException {
	            Entry<I, R> entry = rowMapper.mapRow(rs, row++);
	            intermediaryResult.put(entry.getKey(), entry.getValue());
	        }
	    });
	    
	    for (C i : input) {
	        I e = inputTypeToSqlGeneratorTypeMapper.apply(i);
	        if (intermediaryResult.containsKey(e)) {
	            R r = intermediaryResult.get(e);
	            resultMap.put(r, i);
	        }
	    }
	    
	    return resultMap;
	}
}
