package com.cannontech.cbc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.common.util.SqlFragment;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.MaxListResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.pao.CapControlType;
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
    private static final String selectLastTenForPaoAndActon;
    private static final YukonRowMapper<CapControlComment> rowMapper;
    private static final ParameterizedRowMapper<List<Integer>> paoIdRowMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
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
            
            selectLastTenForPaoAndActon = "SELECT CCC.CapComment \"Comment\", MAX(CCC.CommentTime) \"CommentTime\" " +
                                    "FROM CapControlComment CCC " +
                                    "JOIN YukonPAObject PAO ON PAO.PAObjectId = CCC.PaoId " +
                                    "WHERE CCC.Action = ? " +
                                    "AND PAO.Type = (SELECT Type FROM YukonPAObject WHERE PAObjectId = ?) " +
                                    "GROUP BY CCC.CapComment " +
                                    "ORDER BY \"CommentTime\" DESC";

            rowMapper = createRowMapper();
            
            paoIdRowMapper = createPaoIdRowMapper();
        }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(CapControlComment comment) {
        int id = nextValueHelper.getNextValue("CapControlComment");
        comment.setId(id);
        
        int rowsAffected = yukonJdbcTemplate.update(insertSql, id,
                                                     comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getDate(),
                                                     comment.getAction(),
                                                     comment.getComment(),
                                                     comment.isAltered());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean remove( CapControlComment comment ){
        int rowsAffected = yukonJdbcTemplate.update(removeSql, comment.getId());
        boolean result = (rowsAffected == 1); 
        return result;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update( CapControlComment comment ){
        int rowsAffected = yukonJdbcTemplate.update(updateSql, comment.getPaoId(),
                                                     comment.getUserId(),
                                                     comment.getDate(),
                                                     comment.getAction(),
                                                     comment.getComment(),
                                                     comment.isAltered(),
                                                     comment.getId());
        boolean result = (rowsAffected == 1);
        return result;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getById(int id) throws IncorrectResultSizeDataAccessException {
        CapControlComment comment = yukonJdbcTemplate.queryForObject(new SqlFragment(selectByIdSql, id), rowMapper);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getDisabledByPaoId(int paoId) throws IncorrectResultSizeDataAccessException {
        List<CapControlComment> list = yukonJdbcTemplate.query(new SqlFragment(selectByPaoIdActionSql, paoId, CommentAction.DISABLED.name()), rowMapper);
        if (list.size() == 0) throw new IncorrectResultSizeDataAccessException("CapControlComment with PaoID " + paoId + " not found", 1);
        CapControlComment comment = list.get(0);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getDisabledOVUVByPaoId(final int paoId, final CapControlType type) throws DataRetrievalFailureException {
        String paoIdSql;
        
        switch (type) {
            case AREA : {
                paoIdSql = null;
                break;
            }
            case FEEDER : {
                paoIdSql = selectPaoIdsByFeederId;
                break;
            }
            case SPECIAL_AREA : {
                paoIdSql = null;
                break;
            }
            case SUBBUS : {
                paoIdSql = selectPaoIdsBySubBusId;
                break;
            }
            case SUBSTATION : {
                paoIdSql = selectPaoIdBySubstationId;
                break;
            }
            case CAPBANK : {
                paoIdSql = selectPaoIdsByCapBankId;
                break;
            }
            default : throw new DataRetrievalFailureException("invalid type " + type);
        }
        
        final List<Integer> paoIdList = new ArrayList<Integer>();
        paoIdList.add(paoId);
        
        if (paoIdSql != null) {
            List<Integer> list = yukonJdbcTemplate.queryForObject(paoIdSql, paoIdRowMapper, paoId);
            paoIdList.addAll(list);
        }
        
        final StringBuilder sb = new StringBuilder();
        sb.append(selectAllSql);
        sb.append(" WHERE PaoId IN (");
        sb.append(SqlStatementBuilder.convertToSqlLikeList(paoIdList));
        sb.append(") AND Action = ? ORDER BY CommentTime DESC");
        
        String sql = sb.toString();
        List<CapControlComment> list = yukonJdbcTemplate.query(new SqlFragment(sql, CommentAction.DISABLED_OVUV.name()), rowMapper);
        if (list.size() == 0) throw new IncorrectResultSizeDataAccessException("CapControlComment with PaoID " + paoId + " not found", 1);
        CapControlComment comment = list.get(0);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CapControlComment getStandaloneReasonByPaoId(int paoId) throws IncorrectResultSizeDataAccessException {
        List<CapControlComment> list = yukonJdbcTemplate.query(new SqlFragment(selectByPaoIdActionSql, paoId, CommentAction.STANDALONE_REASON.name()), rowMapper);
        if (list.size() == 0) throw new IncorrectResultSizeDataAccessException("CapControlComment with PaoID " + paoId + " not found", 1);
        CapControlComment comment = list.get(0);
        return comment;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CapControlComment> getUserCommentsByPaoId(final int paoId, final int num) {
        List<CapControlComment> list = yukonJdbcTemplate.query(new SqlFragment(selectByPaoIdActionSql, paoId, CommentAction.USER_COMMENT.name()), rowMapper);
        int toIndex = (num > list.size()) ? list.size() : num;
        List<CapControlComment> resultList = list.subList(0, toIndex);
        return resultList; 
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CapControlComment> getAllCommentsByPao( int paoId ) { 
        String sql = selectAllSql + " WHERE paoId = ? ORDER BY commentTime asc";
        List<CapControlComment> list = yukonJdbcTemplate.query(new SqlFragment(sql, paoId), rowMapper);
        return list;
    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<String> getLastTenCommentsByActionAndType(int paoId, CommentAction action ) {
        MaxListResultSetExtractor<String> rse = new MaxListResultSetExtractor<String>(createSimpleCommentRowMapper(), 10);
        String sql = selectLastTenForPaoAndActon;
        JdbcOperations oldTemplate = yukonJdbcTemplate.getJdbcOperations();
        Object[] arguments = new Object[] {action.name(), paoId};
        oldTemplate.query(sql, arguments, rse);
        List<String> result = rse.getResult();
        return result;
    }
    
    private static final ParameterizedRowMapper<String> createSimpleCommentRowMapper() {
        ParameterizedRowMapper<String> rowMapper = new ParameterizedRowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                String comment = rs.getString("Comment");
                return comment;
            }
        };
        return rowMapper;
    }
    
    private static final YukonRowMapper<CapControlComment> createRowMapper() {
    	YukonRowMapper<CapControlComment> rowMapper = new YukonRowMapper<CapControlComment>() {
            public CapControlComment mapRow(YukonResultSet rs) throws SQLException {
                CapControlComment comment = new CapControlComment();
                comment.setId(rs.getInt("CommentID"));
                comment.setPaoId(rs.getInt("PaoId"));
                comment.setUserId(rs.getNullableInt("UserId"));
                comment.setAction(rs.getString("Action"));
                comment.setDate(rs.getDate("commentTime"));
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

    public void setSimpleJdbcTemplate(final YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}

