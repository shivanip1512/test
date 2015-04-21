package com.cannontech.common.validation.dao.impl;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validation.dao.RphTagDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;

public class RphTagDaoImpl implements RphTagDao {
    private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public boolean insertTag(long changeId, Collection<RphTag> rphTags) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            
            SqlParameterSink values = sql.insertInto("RphTag");
            values.addValue("ChangeId",  changeId);
            values.addValue("PeakUp", rphTags.contains(RphTag.PEAKUP));
            values.addValue("PeakDown", rphTags.contains(RphTag.PEAKDOWN));
            values.addValue("UnreasonableUp", rphTags.contains(RphTag.UNREASONABLEUP));
            values.addValue("UnreasonableDown", rphTags.contains(RphTag.UNREASONABLEDOWN));
            values.addValue("ChangeOut", rphTags.contains(RphTag.CHANGEOUT));
            values.addValue("Accepted", 0);
            
            yukonJdbcTemplate.update(sql);
        } catch (DataIntegrityViolationException e) {
            try {
                // a row must already exist, let's update instead
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink values = sql.update("RphTag");
                if (rphTags.contains(RphTag.PEAKUP)) values.addValue("PeakUp", 1);
                if (rphTags.contains(RphTag.PEAKDOWN)) values.addValue("PeakDown", 1);
                if (rphTags.contains(RphTag.UNREASONABLEUP)) values.addValue("UnreasonableUp", 1);
                if (rphTags.contains(RphTag.UNREASONABLEDOWN)) values.addValue("UnreasonableDown", 1);
                if (rphTags.contains(RphTag.CHANGEOUT)) values.addValue("ChangeOut", 1);
                sql.append("WHERE ChangeId").eq(changeId);
                yukonJdbcTemplate.update(sql);
            } catch (DataIntegrityViolationException e2) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void acceptChangeId(long changeId) {
        SqlStatementBuilder acceptSql = new SqlStatementBuilder();
        acceptSql.append("UPDATE RphTag");
        acceptSql.append("SET Accepted").eq_k(1);
        acceptSql.append("WHERE ChangeId").eq(changeId);
        yukonJdbcTemplate.update(acceptSql);
    }
    
    @Override
    public int clearTagsAfter(long lastChangeId, Set<RphTag> tags) {
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("DELETE from RphTag");
        deleteSql.append("WHERE ChangeId").gt(lastChangeId);
        return yukonJdbcTemplate.update(deleteSql);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
