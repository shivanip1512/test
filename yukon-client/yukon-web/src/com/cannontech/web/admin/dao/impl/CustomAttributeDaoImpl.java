package com.cannontech.web.admin.dao.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.admin.dao.CustomAttributeDao;
import com.cannontech.web.admin.model.CustomAttributeDetail;

public class CustomAttributeDaoImpl implements CustomAttributeDao {
    
    private YukonRowMapper<CustomAttributeDetail> detailMapper = rs -> {
        CustomAttributeDetail row = new CustomAttributeDetail();
        row.setId(rs.getInt("AttributeAssignmentId"));
        row.setName(rs.getStringSafe("AttributeName"));
        row.setDeviceType(rs.getEnum("DeviceType", PaoType.class));
        row.setPointType(rs.getEnum("PointType", PointType.class));
        row.setPointOffset(rs.getInt("PointOffset"));
        return row;
    };
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public List<CustomAttributeDetail> getCustomAttributeDetails(List<Integer> attributeIds, List<PaoType> deviceTypes,
            SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeAssignmentId, AttributeName, DeviceType, PointType, PointOffset");
        sql.append("FROM AttributeAssignment aa");
        sql.append("JOIN CustomAttribute ca ON aa.AttributeId = ca.AttributeId");

        if (CollectionUtils.isNotEmpty(attributeIds)) {
            sql.append("WHERE aa.AttributeId").in(attributeIds);
        }
        if (CollectionUtils.isNotEmpty(deviceTypes)) {
            if (CollectionUtils.isEmpty(attributeIds)) {
                sql.append("WHERE DeviceType").in_k(deviceTypes);
            } else {
                sql.append("AND DeviceType").in_k(deviceTypes);
            }
        }

        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            if (direction != null) {
                sql.append(direction);
            }
        }
        return jdbcTemplate.query(sql, detailMapper);
    }
}
 