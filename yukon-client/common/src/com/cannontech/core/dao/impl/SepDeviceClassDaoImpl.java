package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.SepDeviceClassDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.lm.SepDeviceClass;

public class SepDeviceClassDaoImpl implements SepDeviceClassDao {

    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    @Transactional
    public Set<SepDeviceClass> getSepDeviceClassesByDeviceId(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SepDeviceClass");
        sql.append("FROM LMGroupSepDeviceClass");
        sql.append("WHERE DeviceId").eq(deviceId);

        Set<SepDeviceClass> result = EnumSet.noneOf(SepDeviceClass.class);
        yukonJdbcTemplate.queryInto(sql, new YukonRowMapper<SepDeviceClass>() {
            @Override
            public SepDeviceClass mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("SepDeviceClass", SepDeviceClass.class);
            }
        }, result);
        return result;
    }

    @Transactional(readOnly = false)
    @Override
    public int deleteByDeviceId(int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMGroupSepDeviceClass");
        sql.append("WHERE DeviceId").eq(deviceId);
        return yukonJdbcTemplate.update(sql);
    }

    @Override
    @Transactional
    public void save(Set<SepDeviceClass> sepDeviceClasses, int deviceId) {
        /* Remove possible existing entries first */
        deleteByDeviceId(deviceId);

        for (SepDeviceClass deviceClass : sepDeviceClasses) {
            SqlStatementBuilder sql = new SqlStatementBuilder("INSERT INTO LMGroupSepDeviceClass");
            sql.values(deviceId, deviceClass);

            yukonJdbcTemplate.update(sql);
        }
    }

    /* Dependencies */
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}