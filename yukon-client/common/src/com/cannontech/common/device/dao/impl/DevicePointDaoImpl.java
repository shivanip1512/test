package com.cannontech.common.device.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.dao.DevicePointDao;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.device.model.DevicePointDetail;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;

public class DevicePointDaoImpl implements DevicePointDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private final static YukonRowMapper<DevicePointDetail> devicePointDetailMapper = new YukonRowMapper<DevicePointDetail>() {
        @Override
        public DevicePointDetail mapRow(YukonResultSet rs) throws SQLException {
            DevicePointDetail devicePointDetail = new DevicePointDetail();
            devicePointDetail.setPointId(rs.getInt("PointId"));

            devicePointDetail.setPointName(rs.getString("PointName"));
            devicePointDetail.setDeviceName(rs.getString("PAOName"));
            devicePointDetail.setStateGroupId(rs.getInt("StateGroupId"));

            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
            PointIdentifier pointIdentifier = rs.getPointIdentifier("PointType", "PointOffset");
            PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, pointIdentifier);
            devicePointDetail.setPaoPointIdentifier(paoPointIdentifier);

            return devicePointDetail;
        }
    };

    @Override
    public SearchResults<DevicePointDetail> getDevicePointDetail(List<Integer> paoIds, List<String> pointNames,
            List<PointType> types, Direction direction, SortBy sortBy,
            PagingParameters paging) {

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        SqlStatementBuilder allRowsSql = buildSelectQuery(paoIds, pointNames, types, direction, sortBy);
        SqlStatementBuilder countSql = buildSelectQuery(paoIds, pointNames, types, null, null);

        PagingResultSetExtractor<DevicePointDetail> rse = new PagingResultSetExtractor<>(start, count, devicePointDetailMapper);
        jdbcTemplate.query(allRowsSql, rse);

        SearchResults<DevicePointDetail> retVal = new SearchResults<>();

        int totalCount = jdbcTemplate.queryForInt(countSql);
        retVal.setBounds(start, count, totalCount);
        retVal.setResultList(rse.getResultList());
        return retVal;
    }

    private SqlStatementBuilder buildSelectQuery(List<Integer> paoIds, List<String> pointNames, List<PointType> types,
            Direction direction, SortBy sortBy) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        if (sortBy == null) {
            sql.append("SELECT COUNT(*)");
        } else {
            sql.append("SELECT PointId, PointType, PointName, PointOffset, StateGroupId, P.PAObjectID, pao.Type, pao.PAOName");
        }
        sql.append("FROM Point P");
        sql.append("  JOIN YukonPaobject pao ON pao.PaobjectId = p.PaobjectId");
        sql.append("WHERE P.PAObjectID").in(paoIds);
        if (types != null && !types.isEmpty()) {
            sql.append("  AND PointType").in(types);
        }
        if (pointNames != null && !pointNames.isEmpty()) {
            sql.append("  AND PointName").in(pointNames);
        }
        if (sortBy != null) {
            sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        }
        return sql;
    }

}
