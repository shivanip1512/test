package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.cbc.dao.CapbankCommentDao;
import com.cannontech.cbc.model.CapbankComment;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.incrementer.NextValueHelper;


public class CapbankCommentDaoImpl implements CapbankCommentDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<CapbankComment> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
            insertSql = "INSERT INTO capbankcomment (commentid,paoid,userid," + 
            "commentTime,comment,altered) VALUES (?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM capbankcomment WHERE commentid = ?";
            
            updateSql = "UPDATE capbankcomment SET paoid = ?," + 
            "userid = ?,commentTime = ?,comment = ?, " + 
            "altered = ? WHERE commentid = ?";
            
            selectAllSql = "SELECT commentid,paoid,userid,commentTime," + 
            "comment,altered FROM capbankcomment";
            
            selectByIdSql = selectAllSql + " WHERE commentid = ?";
            
            rowMapper = CapbankCommentDaoImpl.createRowMapper();
        }

    private static final ParameterizedRowMapper<CapbankComment> createRowMapper() {
        ParameterizedRowMapper<CapbankComment> rowMapper = new ParameterizedRowMapper<CapbankComment>() {
            public CapbankComment mapRow(ResultSet rs, int rowNum) throws SQLException {
                CapbankComment comment = new CapbankComment();
                comment.setId(rs.getInt("CommentID"));
                comment.setPaoId(rs.getInt("PaoId"));
                comment.setUserId(rs.getInt("UserId"));
                comment.setTime(rs.getTimestamp("commentTime"));
                comment.setComment(rs.getString("comment"));
                String str = rs.getString("Altered");
                boolean b = ((str.charAt(0) == 'Y') || (str.charAt(0) == 'y') || (str.charAt(0) == '1'))? true:false;
                comment.setAltered(b);
                return comment;
            }
        };
        return rowMapper;
    }
    
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public boolean add( CapbankComment comment ){
        int id = nextValueHelper.getNextValue("CapbankComment");
        int rowsAffected = simpleJdbcTemplate.update(insertSql, id,
                                                     comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getTime(),
                                                     comment.getComment(),
                                                     comment.isAltered());
        return (rowsAffected == 1);
    }
    
    public boolean remove( CapbankComment comment ){
        int rowsAffected = simpleJdbcTemplate.update(removeSql, comment.getId());
        return (rowsAffected == 1);
    }
    
    public boolean update( CapbankComment comment ){
        int rowsAffected = simpleJdbcTemplate.update(updateSql, comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getTime(),
                                                     comment.getComment(),
                                                     comment.isAltered(),
                                                     comment.getId());
        return (rowsAffected == 1);
    }
    
    public CapbankComment getById( int id )
    {
        CapbankComment c = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return c;
    }
    
    public List<String> getLastFiveByPaoId( int paoId ){       
        List<CapbankComment> list = simpleJdbcTemplate.query(selectAllSql + " WHERE paoId = ? ORDER BY commentTime desc ", rowMapper, paoId);
        List<String> tsTemp = new ArrayList<String>();
        for( int i = 0; i < list.size(); i++ )
        {        
            tsTemp.add(new String( list.get(i).getComment() ) );
            if( i == 4 )
                break;
        }
        return tsTemp;
    }
    
    public List<CapbankComment> getAllCommentsByPao( int paoId ) { 
        String sql = selectAllSql + " WHERE paoId = ? ORDER BY commentTime desc";
        
        List<CapbankComment> list = simpleJdbcTemplate.query(sql, rowMapper, paoId);
        
        return list;
    }
}
