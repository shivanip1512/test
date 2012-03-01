package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RPHServiceTagDao;
import com.cannontech.core.dynamic.RphServiceTag;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class RPHServiceTagDaoImpl implements RPHServiceTagDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<RphServiceTag> dbTemplate;
    private final static FieldMapper<RphServiceTag> rphServiceTagFieldMapper = new FieldMapper<RphServiceTag>() {
        @Override
        public void setPrimaryKey(RphServiceTag rphServiceTag, int rphServiceTagId) {
            rphServiceTag.setRphServiceTagId(rphServiceTagId);
        }

        @Override
        public Number getPrimaryKey(RphServiceTag rphServiceTag) {
            return rphServiceTag.getRphServiceTagId();
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, RphServiceTag rphServiceTag) {
            parameterHolder.addValue("ChangeId", rphServiceTag.getChangeId());
            parameterHolder.addValue("ServiceName", rphServiceTag.getServiceName());
            parameterHolder.addValue("ServiceNameRef", rphServiceTag.getServiceNameRef());
        }
    };

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<RphServiceTag>(yukonJdbcTemplate, nextValueHelper);
        dbTemplate.setTableName("RPHServiceTag");
        dbTemplate.setFieldMapper(rphServiceTagFieldMapper);
        dbTemplate.setPrimaryKeyField("RPHServiceTagId");
        dbTemplate.setPrimaryKeyValidOver(0);
    }
    
    YukonRowMapper<RphServiceTag> rphServiceTagRowMapper = new YukonRowMapper<RphServiceTag>() {
        @Override
        public RphServiceTag mapRow(YukonResultSet rs) throws SQLException {
            RphServiceTag rphServiceTag = new RphServiceTag();
            rphServiceTag.setRphServiceTagId(rs.getInt("RphServiceTagId"));
            rphServiceTag.setChangeId(rs.getInt("ChangeId"));
            rphServiceTag.setServiceName(rs.getString("ServiceName"));
            rphServiceTag.setServiceNameRef(rs.getStringSafe("ServiceNameRef"));
            return rphServiceTag;
        }};
    
    @Override
    public void insert(RphServiceTag rphServiceTag) {
        dbTemplate.insert(rphServiceTag);
    }
    
    @Override
    public void save(RphServiceTag rphServiceTag) {
        dbTemplate.save(rphServiceTag);
    }

    @Override
    public void update(RphServiceTag rphServiceTag) {
        dbTemplate.update(rphServiceTag);
    }

    @Override
    public RphServiceTag getRphServiceTag(String serviceName, String serviceNameRef) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM RPHServiceTag");
        sql.append("WHERE ServiceName").eq(serviceName);
        if (serviceNameRef != null) {
            sql.append("  AND ServiceNameRef").eq(serviceNameRef);
        }
        
        return yukonJdbcTemplate.query(sql, rphServiceTagRowMapper).get(0);
    }
}