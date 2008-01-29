package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.incrementer.NextValueHelper;


public class CapControlCommentDaoImpl implements CapControlCommentDao {
    private static final String insertSql;
    private static final String removeSql;
    private static final String updateSql;
    private static final String selectAllSql;
    private static final String selectByIdSql;
    private static final String selectByPaoIdActionSql;
    private static final String selectPaoIdsByCapBankId;
    private static final String selectPaoIdsByFeederId;
    private static final String selectPaoIdsBySubBusId;
    private static final String selectPaoIdBySubstationId;
    private static final ParameterizedRowMapper<CapControlComment> rowMapper;
    private static final ParameterizedRowMapper<List<Integer>> paoIdRowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    
    static {
            insertSql = "INSERT INTO CapControlComment (commentid,paoid,userid,commentTime,Action,capComment,altered) VALUES (?,?,?,?,?,?,?)";
            
            removeSql = "DELETE FROM CapControlComment WHERE commentid = ?";
            
            updateSql = "UPDATE CapControlComment SET paoid = ?," + 
            "userid = ?,commentTime = ?, Action = ?, capComment = ?, " + 
            "altered = ? WHERE commentid = ?";
            
            selectAllSql = "SELECT commentid,paoid,userid,Action,commentTime,capComment,altered " +
            		        "FROM CapControlComment";
            
            selectByIdSql = selectAllSql + " WHERE commentid = ?";
            
            selectByPaoIdActionSql = selectAllSql + " WHERE PaoID = ? AND Action = ? ORDER BY commentTime DESC";
            
            selectPaoIdsByCapBankId = "SELECT fb.feederid, fs.substationbusid, ss.substationid, sa.areaid " +
                                      "FROM ccfeederbanklist fb, ccfeedersubassignment fs, ccsubstationsubbuslist ss, ccsubareaassignment sa " +
                                      "WHERE fb.feederid = fs.feederid " +
                                      "AND fs.substationbusid = ss.substationbusid " + 
                                      "AND ss.substationid = sa.substationbusid " +
                                      "AND deviceid = ?";
            
            selectPaoIdsByFeederId = "SELECT fs.substationbusid, ss.substationid, sa.areaid " +
                                     "FROM ccfeedersubassignment fs, ccsubstationsubbuslist ss, ccsubareaassignment sa " +
                                     "WHERE fs.substationbusid = ss.substationbusid " +
                                     "AND ss.substationid = sa.substationbusid " +
                                     "AND feederid = ?";
            
            selectPaoIdsBySubBusId = "SELECT ss.substationid, sa.areaid " +
                                     "FROM ccsubstationsubbuslist ss, ccsubareaassignment sa " +
                                     "WHERE ss.substationid = sa.substationbusid " +
                                     "AND ss.substationbusid = ?";
            
            selectPaoIdBySubstationId = "SELECT sa.areaid " +
                                        "FROM ccsubareaassignment sa " +
                                        "WHERE substationbusid = ?";
            
            rowMapper = createRowMapper();
            
            paoIdRowMapper = createPaoIdRowMapper();
        }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(CapControlComment comment) {
        int id = nextValueHelper.getNextValue("CapControlComment");
        comment.setId(id);
        
        int rowsAffected = simpleJdbcTemplate.update(insertSql, id,
                                                     comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getTime(),
                                                     comment.getAction(),
                                                     comment.getComment(),
                                                     comment.isAltered());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove( CapControlComment comment ){
        int rowsAffected = simpleJdbcTemplate.update(removeSql, comment.getId());
        boolean result = (rowsAffected == 1); 
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update( CapControlComment comment ){
        int rowsAffected = simpleJdbcTemplate.update(updateSql, comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getTime(),
                                                     comment.getAction(),
                                                     comment.getComment(),
                                                     comment.isAltered(),
                                                     comment.getId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getById(int id) throws IncorrectResultSizeDataAccessException {
        CapControlComment comment = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, id);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getDisabledByPaoId(int paoId) throws IncorrectResultSizeDataAccessException {
        List<CapControlComment> list = simpleJdbcTemplate.query(selectByPaoIdActionSql, rowMapper, paoId, CommentAction.DISABLED.name());
        if (list.size() == 0) throw new IncorrectResultSizeDataAccessException("CapControlComment with PaoID " + paoId + " not found", 1);
        CapControlComment comment = list.get(0);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getDisabledOVUVByPaoId(final int paoId, final int type) throws DataRetrievalFailureException {
        String paoIdSql;
        
        switch (type) {
            case CapControlTypes.CAP_CONTROL_AREA : {
                paoIdSql = null;
                break;
            }
            case CapControlTypes.CAP_CONTROL_FEEDER : {
                paoIdSql = selectPaoIdsByFeederId;
                break;
            }
            case CapControlTypes.CAP_CONTROL_SPECIAL_AREA : {
                paoIdSql = null;
                break;
            }
            case CapControlTypes.CAP_CONTROL_SUBBUS : {
                paoIdSql = selectPaoIdsBySubBusId;
                break;
            }
            case CapControlTypes.CAP_CONTROL_SUBSTATION : {
                paoIdSql = selectPaoIdBySubstationId;
                break;
            }
            case CapControlTypes.CAP_CONTROL_CAPBANK : {
                paoIdSql = selectPaoIdsByCapBankId;
                break;
            }
            default : throw new DataRetrievalFailureException("invalid type " + type);
        }
        
        final List<Integer> paoIdList = new ArrayList<Integer>();
        paoIdList.add(paoId);
        
        if (paoIdSql != null) {
            List<Integer> list = simpleJdbcTemplate.queryForObject(paoIdSql, paoIdRowMapper, paoId);
            paoIdList.addAll(list);
        }
        
        final StringBuilder sb = new StringBuilder();
        sb.append(selectAllSql);
        sb.append(" WHERE PaoId IN (");
        sb.append(SqlStatementBuilder.convertToSqlLikeList(paoIdList));
        sb.append(") AND Action = ? ORDER BY CommentTime DESC");
        
        String sql = sb.toString();
        List<CapControlComment> list = simpleJdbcTemplate.query(sql, rowMapper, CommentAction.DISABLED_OVUV.name());
        if (list.size() == 0) throw new IncorrectResultSizeDataAccessException("CapControlComment with PaoID " + paoId + " not found", 1);
        CapControlComment comment = list.get(0);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getStandaloneReasonByPaoId(int paoId) throws IncorrectResultSizeDataAccessException {
        List<CapControlComment> list = simpleJdbcTemplate.query(selectByPaoIdActionSql, rowMapper, paoId, CommentAction.STANDALONE_REASON.name());
        if (list.size() == 0) throw new IncorrectResultSizeDataAccessException("CapControlComment with PaoID " + paoId + " not found", 1);
        CapControlComment comment = list.get(0);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CapControlComment> getUserCommentsByPaoId(final int paoId, final int num) {
        List<CapControlComment> list = simpleJdbcTemplate.query(selectByPaoIdActionSql, rowMapper, paoId, CommentAction.USER_COMMENT.name());
        int toIndex = (num > list.size()) ? list.size() : num;
        List<CapControlComment> resultList = list.subList(0, toIndex);
        return resultList; 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CapControlComment> getAllCommentsByPao( int paoId ) { 
        String sql = selectAllSql + " WHERE paoId = ? ORDER BY commentTime desc";
        List<CapControlComment> list = simpleJdbcTemplate.query(sql, rowMapper, paoId);
        return list;
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
                comment.setComment(rs.getString("capComment"));
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
    
    private static final ParameterizedRowMapper<List<Integer>> createPaoIdRowMapper() {
        final ParameterizedRowMapper<List<Integer>> rowMapper = new ParameterizedRowMapper<List<Integer>>() {
            @Override
            public List<Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                final int count = rs.getMetaData().getColumnCount();
                final List<Integer> paoIdList = new ArrayList<Integer>(count);
                for (int x = 1; x <= count; x++) {
                    Integer paoId = rs.getInt(x);
                    paoIdList.add(paoId);
                }
                return paoIdList;
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
}

