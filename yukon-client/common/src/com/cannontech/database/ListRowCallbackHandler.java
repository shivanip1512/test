package com.cannontech.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

public class ListRowCallbackHandler extends AbstractRowCallbackHandler
{
    private List objectList;
    private RowMapper rowMapper;
    public ListRowCallbackHandler(List objectList, RowMapper rowMapper) {
        super();
        this.objectList = objectList;
        this.rowMapper = rowMapper;
    }

    @Override
    public void processRow(ResultSet rs, int rowNum) throws SQLException
    {
        Object object = rowMapper.mapRow(rs, rowNum);
        if( object != null)
            objectList.add(object);
    }

    public List getObjectList()
    {
        return objectList;
    }
}
