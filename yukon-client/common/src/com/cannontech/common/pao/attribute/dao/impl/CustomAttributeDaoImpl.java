package com.cannontech.common.pao.attribute.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.attribute.dao.CustomAttributeDao;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class CustomAttributeDaoImpl implements CustomAttributeDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private YukonRowMapper<CustomAttribute> customAttributeMapper = rs -> {
        CustomAttribute row = new CustomAttribute();
        row.setId(rs.getInt("AttributeId"));
        row.setName(rs.getStringSafe("AttributeName"));
        return row;
    };

    @Override
    public void saveCustomAttribute(CustomAttribute attribute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeId");
        sql.append("FROM CustomAttribute");
        sql.append("WHERE AttributeId").eq(attribute.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("CustomAttribute");
            params.addValue("AttributeName", attribute.getName());
            updateCreateSql.append("WHERE AttributeId").eq(attribute.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("CustomAttribute");
            params.addValue("AttributeId", nextValueHelper.getNextValue("CustomAttribute"));
            params.addValue("AttributeName", attribute.getName());
        }
        try {
            jdbcTemplate.update(updateCreateSql);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Attribute with name " + attribute.getName() + " already exist.", e);
        }
    }

    @Override
    public void deleteCustomAttribute(int attributeId) {
        // Check if a custom attribute is being used elsewhere in Yukon (to provide an error to a user attempting to delete it)
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CustomAttribute");
        sql.append("WHERE AttributeId").eq(attributeId);
        jdbcTemplate.update(sql);
    }

    @Override
    public List<CustomAttribute> getCustomAttributes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AttributeId, AttributeName");
        sql.append("FROM CustomAttribute");
        sql.append("ORDER BY AttributeName");
        return jdbcTemplate.query(sql, customAttributeMapper);
    }
}
