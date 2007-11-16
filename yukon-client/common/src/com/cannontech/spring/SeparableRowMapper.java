package com.cannontech.spring;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public abstract class SeparableRowMapper<T> implements ParameterizedRowMapper<T> {
    
    private final SeparableRowMapper<? super T> parent;

    public SeparableRowMapper(SeparableRowMapper<? super T> parent) {
        this.parent = parent;
    }
    
    public SeparableRowMapper() {
        this.parent = null;
    }

    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T t = createObject(rs);
        return internalMapRow(rs, t);
    }

    private T internalMapRow(ResultSet rs, T t) throws SQLException {
        if (parent != null) {
            parent.internalMapRow(rs, t);
        }
        mapRow(rs, t);
        return t;
    }

    protected abstract void mapRow(ResultSet rs, T t) throws SQLException;

    protected abstract T createObject(ResultSet rs) throws SQLException;

}
