package com.cannontech.database;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.collect.Lists;

public class SqlParameterChildHelper extends SqlParameterBase implements SqlParameterChildSink {
    private List<ChildPair<?>> pairList = Lists.newArrayList();
    private MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

    public <T> void addChildren(SimpleTableAccessTemplate<? super T> template,
                            List<? extends T> children) {
        ChildPair<T> childPair = new ChildPair<T>();
        childPair.children = children;
        childPair.template = template;
        
        pairList.add(childPair);
    }
    
    public static class ChildPair<T> {
        SimpleTableAccessTemplate<? super T> template;
        List<? extends T> children;
    }
    
    public List<ChildPair<?>> getPairList() {
        return pairList;
    }

    public void addValueRaw(String paramName, Object value) {
        mapSqlParameterSource.addValue(paramName, value);
    }
    
    public MapSqlParameterSource getMapSqlParameterSource() {
        return mapSqlParameterSource;
    }

}
