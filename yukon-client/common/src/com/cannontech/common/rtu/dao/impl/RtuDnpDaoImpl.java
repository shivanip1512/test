package com.cannontech.common.rtu.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.rtu.dao.RtuDnpDao;
import com.cannontech.common.rtu.model.RtuPointDetail;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;

public class RtuDnpDaoImpl implements RtuDnpDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;

    SqlStatementBuilder baseSql = new SqlStatementBuilder();
    {
        baseSql.append("SELECT PointId, PointType, PointName, PointOffset, P.PAObjectID, pao.Type, pao.PAOName");
        baseSql.append("FROM Point P");
        baseSql.append("  JOIN YukonPaobject pao ON pao.PaobjectId = p.PaobjectId");
    }
    
    private final static YukonRowMapper<RtuPointDetail> rtuPointDetailMapper = new YukonRowMapper<RtuPointDetail>() {
        @Override
        public RtuPointDetail mapRow(YukonResultSet rs) throws SQLException {
            RtuPointDetail rtuPointDetail = new RtuPointDetail();
            rtuPointDetail.setPointId(rs.getInt("PointId"));

            rtuPointDetail.setPointName(rs.getString("PointName"));
            rtuPointDetail.setDeviceName(rs.getString("PAOName"));

            PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PAObjectId", "Type");
            PointIdentifier pointIdentifier = rs.getPointIdentifier("PointType", "PointOffset");
            PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, pointIdentifier);
            rtuPointDetail.setPaoPointIdentifier(paoPointIdentifier);

            return rtuPointDetail;
        }
    };

    @Override
    public SearchResults<RtuPointDetail> getRtuPointDetail(List<Integer> paoIds, List<String> pointNames,
            List<PointType> types, Direction direction, SortBy sortBy,
            PagingParameters paging) {

        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();

        SqlStatementBuilder sql = new SqlStatementBuilder(baseSql.getSql());
        sql.append("WHERE P.PAObjectID").in(paoIds);
        if (types != null) {
            sql.append("  AND PointType").in(types);
        }
        if (pointNames != null) {
            sql.append("  AND PointName").in(pointNames);
        }
        sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);

        PagingResultSetExtractor<RtuPointDetail> rse = new PagingResultSetExtractor<>(start, count, rtuPointDetailMapper);
        jdbcTemplate.query(sql, rse);
        SearchResults<RtuPointDetail> retVal = new SearchResults<>();
        retVal.setBounds(start, count, getRTUPointCount(paoIds, pointNames, types));
        retVal.setResultList(rse.getResultList());
        return retVal;
    }

    private int getRTUPointCount(List<Integer> paoIds, List<String> pointNames,
            List<PointType> types) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM Point P");
        sql.append("  JOIN YukonPaobject pao ON pao.PaobjectId = p.PaobjectId");
        sql.append("WHERE P.PAObjectID").in(paoIds);
        if (types != null) {
            sql.append("  AND PointType").in(types);
        }
        if (pointNames != null) {
            sql.append("  AND PointName").in(pointNames);
        }

        return jdbcTemplate.queryForInt(sql);
    }

}
