package com.cannontech.common.point.alarm.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.database.YukonJdbcTemplate;

public class PointPropertyValueDaoImpl implements PointPropertyValueDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final String insertSql;
    private static final String removeSql;
    private static final String removeByPointIdSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByIdAndAttributeSql;

    private static final RowMapper<PointPropertyValue> rowMapper;

    static {
        insertSql = "INSERT INTO PointPropertyValue (pointid, PointPropertyCode,fltvalue) VALUES (?,?,?)";

        removeSql = "DELETE FROM PointPropertyValue WHERE pointid = ? AND PointPropertyCode = ?";

        removeByPointIdSql = "DELETE FROM PointPropertyValue WHERE pointid = ?";

        updateSql = "UPDATE PointProperty SET fltvalue = ? " +
                    "WHERE pointid = ? AND PointPropertyCode = ?";

        selectAllSql = "SELECT pointid, PointPropertyCode, fltvalue FROM PointPropertyValue";

        selectByIdSql = selectAllSql + " WHERE pointid = ?";

        selectByIdAndAttributeSql = selectAllSql + " WHERE pointid = ? AND PointPropertyCode = ?";

        rowMapper = new RowMapper<PointPropertyValue>() {
            @Override
            public PointPropertyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
                PointPropertyValue attribute = new PointPropertyValue();
                attribute.setPointId(rs.getInt("PointID"));
                attribute.setPointPropertyCode(rs.getInt("PointPropertyCode"));
                attribute.setFloatValue(rs.getFloat("fltvalue"));
                return attribute;
            }
        };

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(PointPropertyValue attrib) {
        int rowsAffected =
            jdbcTemplate.update(insertSql, attrib.getPointId(), attrib.getPointPropertyCode(), attrib.getFloatValue());
        return (rowsAffected == 1);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<PointPropertyValue> getById(int id) {
        return jdbcTemplate.query(selectByIdSql, rowMapper, id);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public PointPropertyValue getByIdAndPropertyId(int id, int attributeId) {
        return jdbcTemplate.queryForObject(selectByIdAndAttributeSql, rowMapper, new Object[] { id, attributeId });
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove(PointPropertyValue attrib) {
        int rowsAffected = jdbcTemplate.update(removeSql, attrib.getPointId(), attrib.getPointPropertyCode());

        return (rowsAffected > 0);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean removeByPointId(int id) {
        int rowsAffected = jdbcTemplate.update(removeByPointIdSql, id);

        return (rowsAffected > 0);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(PointPropertyValue attrib) {
        int rowsAffected =
            jdbcTemplate.update(updateSql, attrib.getFloatValue(), attrib.getPointId(), attrib.getPointPropertyCode());
        return (rowsAffected == 1);
    }
}
