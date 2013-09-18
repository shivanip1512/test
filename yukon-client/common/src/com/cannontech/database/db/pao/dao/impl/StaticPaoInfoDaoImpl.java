package com.cannontech.database.db.pao.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.pao.dao.StaticPaoInfoDao;
import com.cannontech.database.incrementer.NextValueHelper;

public class StaticPaoInfoDaoImpl implements StaticPaoInfoDao {

    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    private static final String tableName = "StaticPaoInfo";

    @Override
    public String getValue(PaoInfo paoInfoKey, int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Value FROM").append(tableName);
        sql.append("WHERE InfoKey").eq(paoInfoKey);
        sql.append("AND PAObjectId").eq(paoId);

        return yukonJdbcTemplate.queryForString(sql);
    }

    @Override
    public void saveValue(PaoInfo paoInfoKey, int paoId, String value) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        try {
            String previousValue = getValue(paoInfoKey, paoId);
            if (value.equals(previousValue)) {
                return;
            }
            sql.append("UPDATE").append(tableName);
            sql.append("SET Value").eq(value);
            sql.append("WHERE InfoKey").eq(paoInfoKey);
            sql.append("AND PAObjectId").eq(paoId);
        } catch(EmptyResultDataAccessException e) {
            sql.append("INSERT INTO").append(tableName);
            sql.append("(StaticPaoInfoId, PAObjectId, InfoKey, Value)");
            sql.values(nextValueHelper.getNextValue("StaticPaoInfo"), paoId, paoInfoKey, value);
        }
        yukonJdbcTemplate.update(sql);
    }
}
