package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class CollectionRowCallbackHandler<T> extends AbstractRowCallbackHandler {
    
    private ParameterizedRowMapper<? extends T> rowMapper;
    private Collection<? super T> collection;

    public CollectionRowCallbackHandler(ParameterizedRowMapper<? extends T> rowMapper,
            Collection<? super T> collection) {
        super();
        this.rowMapper = rowMapper;
        this.collection = collection;
    }

    public <F extends T> CollectionRowCallbackHandler(YukonRowMapper<F> rowMapper,
                                        Collection<? super T> collection) {
        super();
        this.rowMapper = new YukonRowMapperAdapter<F>(rowMapper);
        this.collection = collection;
    }
    
    @Override
    public void processRow(ResultSet rs, int rowNum) throws SQLException {
        T mapRow = rowMapper.mapRow(rs, rowNum);
        collection.add(mapRow);
    }

}
