package com.cannontech.common.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.mock.MockDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.google.common.collect.Lists;

public class ChunkingSqlTemplateTest {
    private final int customChunkSize = 5;
    private final List<Integer> chunkedList1 = Lists.newArrayList(0,1,2,3,4);
    private final List<Integer> chunkedList2 = Lists.newArrayList(5,6,7,8,9);
    private final List<Integer> chunkedList3 = Lists.newArrayList(10);
    private final int initSize = chunkedList1.size() + chunkedList2.size() + chunkedList3.size();

    List<Integer> fullSizeList = new ArrayList<Integer>(initSize);
    {
        fullSizeList.addAll(chunkedList1);
        fullSizeList.addAll(chunkedList2);
        fullSizeList.addAll(chunkedList3);
    }
    
    @Test
    public void test_no_elements_missed_in_chunking() {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(new CustomerSimpleJdbcTemplate());
        template.setChunkSize(customChunkSize);
        
        List<String> resultList = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                return Arrays.toString(subList.toArray());
            }
        
        }, fullSizeList, new RowMapper<String>() {

            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return null;
            }});
        
        int expectedChunkListSize = 3;
        int resultChunkListSize = resultList.size();
        Assert.assertEquals("incorrect sized chunk List", expectedChunkListSize, resultChunkListSize);
        
        String chunkedList1String = Arrays.toString(chunkedList1.toArray());
        String resultString1 = resultList.get(0);
        Assert.assertEquals("incorrect subList elements in first chunk", chunkedList1String, resultString1);
        
        String chunkedList2String = Arrays.toString(chunkedList2.toArray());
        String resultString2 = resultList.get(1);
        Assert.assertEquals("incorrect subList elements in second chunk", chunkedList2String, resultString2);

        String chunkedList3String = Arrays.toString(chunkedList3.toArray());
        String resultString3 = resultList.get(2);
        Assert.assertEquals("incorrect subList elements in third chunk", chunkedList3String, resultString3);
    }
    
    @Test
    public void test_queryForSum_Chunking() {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(new CustomerSimpleJdbcTemplate());
        template.setChunkSize(customChunkSize);
        
        int result = template.queryForSum(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                return new SqlStatementBuilder().append(subList);
            }
        }, fullSizeList);
        
        Assert.assertEquals(11, result);
    }

    /**
     * Creates a mock database connection that can be used for the chunking tests.
     */
    private class CustomerSimpleJdbcTemplate extends YukonJdbcTemplate {
        public CustomerSimpleJdbcTemplate() {
            super(new MockDataSource());
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> query(String sql, RowMapper<T> rm, Object... args) throws DataAccessException {
            return (List<T>) Arrays.asList(new String[]{sql});
        }

        @Override
        public int queryForInt(SqlFragmentSource sql) throws DataAccessException {
            return Arrays.asList(sql.getSql().split(",")).size();
        }
    }
}
