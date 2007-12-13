package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.database.incrementer.NextValueHelper;


public class CapControlCommentDaoImpl implements CapControlCommentDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    
    private static final ParameterizedRowMapper<CapControlComment> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
            insertSql = "INSERT INTO CapControlComment (commentid,paoid,userid,Action" + 
            "commentTime,comment,altered) VALUES (?,?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM CapControlComment WHERE commentid = ?";
            
            updateSql = "UPDATE CapControlComment SET paoid = ?," + 
            "userid = ?,commentTime = ?, Action = ?, comment = ?, " + 
            "altered = ? WHERE commentid = ?";
            
            selectAllSql = "SELECT commentid,paoid,userid,Action,commentTime," + 
            "comment,altered FROM CapControlComment";
            
            selectByIdSql = selectAllSql + " WHERE commentid = ?";
            
            rowMapper = CapControlCommentDaoImpl.createRowMapper();
        }

    private static final ParameterizedRowMapper<CapControlComment> createRowMapper() {
        ParameterizedRowMapper<CapControlComment> rowMapper = new ParameterizedRowMapper<CapControlComment>() {
            public CapControlComment mapRow(ResultSet rs, int rowNum) throws SQLException {
                CapControlComment comment = new CapControlComment();
                comment.setId(rs.getInt("CommentID"));
                comment.setPaoId(rs.getInt("PaoId"));
                comment.setUserId(rs.getInt("UserId"));
                comment.setAction(rs.getString("Action"));
                comment.setTime(rs.getTimestamp("commentTime"));
                comment.setComment(rs.getString("comment"));
                String str = rs.getString("Altered");
                if (str != null) {
                    char c = str.charAt(0);
                    boolean altered = (c == 'Y' || c == 'y' || c == '1');
                    comment.setAltered(altered);
                    
                }
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
    
    public boolean add( CapControlComment comment ){
        int id = nextValueHelper.getNextValue("CapbankComment");
        int rowsAffected = simpleJdbcTemplate.update(insertSql, id,
                                                     comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getAction(),
                                                     comment.getTime(),
                                                     comment.getComment(),
                                                     comment.isAltered());
        return (rowsAffected == 1);
    }
    
    public boolean remove( CapControlComment comment ){
        int rowsAffected = simpleJdbcTemplate.update(removeSql, comment.getId());
        return (rowsAffected == 1);
    }
    
    public boolean update( CapControlComment comment ){
        int rowsAffected = simpleJdbcTemplate.update(updateSql, comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getAction(),
                                                     comment.getTime(),
                                                     comment.getComment(),
                                                     comment.isAltered(),
                                                     comment.getId());
        return (rowsAffected == 1);
    }
    
    public CapControlComment getById( int id )
    {
        CapControlComment c = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return c;
    }
    
    public List<String> getLastFiveByPaoId( int paoId ){       
        List<CapControlComment> list = simpleJdbcTemplate.query(selectAllSql + " WHERE paoId = ? ORDER BY commentTime desc ", rowMapper, paoId);
        List<String> tsTemp = new ArrayList<String>();
        for( int i = 0; i < list.size(); i++ )
        {        
            tsTemp.add(new String( list.get(i).getComment() ) );
            if( i == 4 )
                break;
        }
        return tsTemp;
    }
    
    public List<CapControlComment> getAllCommentsByPao( int paoId ) { 
        String sql = selectAllSql + " WHERE paoId = ? ORDER BY commentTime desc";
        
        List<CapControlComment> list = simpleJdbcTemplate.query(sql, rowMapper, paoId);
        
        return list;
    }
}
