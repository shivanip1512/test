package com.cannontech.spring;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public abstract class SeparableRowMapper<T> implements YukonRowMapper<T> {
    
    private final SeparableRowMapper<? super T> parent;

    public SeparableRowMapper(SeparableRowMapper<? super T> parent) {
        this.parent = parent;
    }
    
    public SeparableRowMapper() {
        this.parent = null;
    }

    public T mapRow(YukonResultSet rs) throws SQLException {
        T t = createObject(rs);
        return internalMapRow(rs, t);
    }

    private T internalMapRow(YukonResultSet rs, T t) throws SQLException {
        if (parent != null) {
            parent.internalMapRow(rs, t);
        }
        mapRow(rs, t);
        return t;
    }

    protected abstract void mapRow(YukonResultSet rs, T t) throws SQLException;

    protected abstract T createObject(YukonResultSet rs) throws SQLException;

}
