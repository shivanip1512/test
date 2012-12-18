package com.cannontech.common.validation.dao.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.database.YukonJdbcTemplate;

public class RphTagDaoImpl implements RphTagDao {
    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public boolean insertTag(long changeId, RphTag value) {
        try {
            SqlStatementBuilder sql =  new SqlStatementBuilder();
            sql.append("insert into RphTag values (");
            sql.appendArgument(changeId).append(",");
            sql.appendArgument(value).append(")");
            yukonJdbcTemplate.update(sql);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
    
    public int clearTagsAfter(long lastChangeId, Set<RphTag> tags) {
        SqlStatementBuilder sql =  new SqlStatementBuilder();
        sql.append("delete from RphTag");
        sql.append("where ChangeId").gt(lastChangeId);
        sql.append("  and TagName").in(tags);
        
        return yukonJdbcTemplate.update(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
