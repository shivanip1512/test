package com.cannontech.stars.util.filter.filterby;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.SqlGenerator;
import com.google.common.base.Join;

public class FilterByChunkingSqlGenerator {
    private int chunkSize = 1000;
    
    public FilterByChunkingSqlGenerator() {
        
    }
    
    public String generate(SqlGenerator<Integer> sqlGenerator, List<Integer> list) {
        List<String> sqlList = new ArrayList<String>();
        
        int inputSize = list.size();
        for (int start = 0; start < inputSize; start += chunkSize ) {
            int nextToIndex = start + chunkSize;
            int toIndex = (inputSize < nextToIndex) ? inputSize : nextToIndex;
            
            List<Integer> subList = list.subList(start, toIndex);
            
            String sqlChunk = sqlGenerator.generate(subList);
            sqlList.add(sqlChunk);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(Join.join(" OR ", sqlList));
        sb.append(")");
        
        String sql = sb.toString();
        return sql;
    }
    
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    };
    
}
