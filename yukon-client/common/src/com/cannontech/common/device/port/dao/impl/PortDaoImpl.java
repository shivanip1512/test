package com.cannontech.common.device.port.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.device.port.service.impl.PortServiceImpl.CommChannelSortBy;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.DeviceBaseModelRowMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;

public class PortDaoImpl implements PortDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public Integer findUniquePortTerminalServer(String ipAddress, Integer port) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PortId ");
        sql.append("FROM PortTerminalServer");
        sql.append("WHERE IpAddress").eq(ipAddress);
        sql.append("  AND SocketPortNumber").eq(port);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public SearchResults<DeviceBaseModel> getDevicesAssignedPort(Integer portId, CommChannelSortBy sortBy, PagingParameters paging, Direction direction) {
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        SqlStatementBuilder allRowsSql = buildSelectQuery(portId, sortBy, direction);
        SqlStatementBuilder countSql = buildSelectQuery(portId, null, direction);
        
        PagingResultSetExtractor<DeviceBaseModel> rse = new PagingResultSetExtractor<>(start, count, new DeviceBaseModelRowMapper());
        jdbcTemplate.query(allRowsSql, rse);
        
        SearchResults<DeviceBaseModel> retVal = new SearchResults<>();
        
        int totalCount = jdbcTemplate.queryForInt(countSql);
        retVal.setBounds(start, count, totalCount);
        retVal.setResultList(rse.getResultList());
        return retVal;
    }
    
    private SqlStatementBuilder buildSelectQuery(Integer portId, CommChannelSortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        if (sortBy == null) {
            sql.append("SELECT COUNT(*)");
        } else {
            sql.append("SELECT ddcs.DeviceId, ypo.type, ypo.PaoName, ypo.DisableFlag");
        }
        sql.append("FROM DeviceDirectCommSettings ddcs");
        sql.append("JOIN YukonPAObject ypo");
        sql.append("ON ddcs.DeviceId = ypo.PaObjectId");
        sql.append("WHERE PortId").eq(portId);
        if (sortBy != null) {
            sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        }
        return sql;
    }
}
