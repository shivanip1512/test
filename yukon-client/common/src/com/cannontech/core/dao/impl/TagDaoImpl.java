package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.TagDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.yukon.IDatabaseCache;

public final class TagDaoImpl implements TagDao {
    
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private final static YukonRowMapper<LiteTag> mapper = new YukonRowMapper<LiteTag>() {
        @Override
        public LiteTag mapRow(YukonResultSet rs) throws SQLException {
            LiteTag tag = new LiteTag(rs.getInt("TagId"));
            tag.setTagName(rs.getString("TagName"));
            tag.setTagLevel(rs.getInt("TagLevel"));
            tag.setInhibit(rs.getBoolean("Inhibit"));
            tag.setColorId(rs.getInt("ColorId"));
            tag.setImageId(rs.getInt("ImageId"));
            return tag;
        }
    };
    
    @Override
    public List<LiteTag> getAllTags() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select TagId, TagName, TagLevel, Inhibit, ColorId, ImageId");
        sql.append("from Tags");
        sql.append("order by TagLevel");
        
        return jdbcTemplate.query(sql, mapper);
    }
    
    @Override
    public LiteTag getLiteTag(int tagId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select TagId, TagName, TagLevel, Inhibit, ColorId, ImageId");
        sql.append("from Tags");
        sql.append("where TagId").eq(tagId);
        
        return jdbcTemplate.queryForObject(sql, mapper);
    }
    
    @Override
    public LiteTag getLiteTag(String tagName) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select TagId, TagName, TagLevel, Inhibit, ColorId, ImageId");
        sql.append("from Tags");
        sql.append("where upper(TagName)").eq(tagName.toUpperCase());
        
        try {
            return jdbcTemplate.queryForObject(sql, mapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
}