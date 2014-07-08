package com.cannontech.cc.daojdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.ProgramNotificationGroupDao;
import com.cannontech.cc.model.Program;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.spring.CollectionResultSetExtractor;

public class ProgramNotificationGroupDaoImpl implements ProgramNotificationGroupDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NotificationGroupDao notificationGroupDao;
    
    @Override
    public Set<LiteNotificationGroup> getNotificationGroupsForProgram(Program program) {
        SqlStatementBuilder query = new SqlStatementBuilder();
        query.append("select NotificationGroupId");
        query.append("from CCurtProgramNotifGroup");
        query.append("where CCurtProgramId = ?");
        Object[] args = new Object[] {program.getId()};
        RowMapper rowMapper = new RowMapper() {
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                int notificationGroupId = rs.getInt("NotificationGroupId");
                LiteNotificationGroup lng = 
                    notificationGroupDao.getLiteNotificationGroup(notificationGroupId);
                return lng;
            }
        };
        Set<LiteNotificationGroup> result = new HashSet<LiteNotificationGroup>();
        CollectionResultSetExtractor extractor = new CollectionResultSetExtractor(result,rowMapper);
        jdbcTemplate.query(query.getSql(), args, extractor);
        return result;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void setNotificationGroupsForProgram(Program program, Set<LiteNotificationGroup> notificationGroups) {
        Set<LiteNotificationGroup> currentDbSet = getNotificationGroupsForProgram(program);
        // find ids that are to be removed
        Set<LiteNotificationGroup> toAdd = new HashSet<LiteNotificationGroup>(notificationGroups);
        toAdd.removeAll(currentDbSet);
        SqlStatementBuilder addSql = new SqlStatementBuilder();
        addSql.append("insert into CCurtProgramNotifGroup (NotificationGroupId, CCurtProgramId)");
        addSql.append("values (?, ?)");
        for (LiteNotificationGroup notifGroup : toAdd) {
            final int notifGroupId = notifGroup.getLiteID();
            final int programId = program.getId();
            PreparedStatementSetter pss = new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, notifGroupId);
                    ps.setInt(2, programId);
                }
            };
            jdbcTemplate.update(addSql.getSql(), pss);
        }
        
        // find ids that are to be added
        Set<LiteNotificationGroup> toDelete = new HashSet<LiteNotificationGroup>(currentDbSet);
        toDelete.removeAll(notificationGroups);
        Set<Integer> toDeleteInts = new HashSet<Integer>();
        for (LiteNotificationGroup lng : toDelete) {
            toDeleteInts.add(lng.getLiteID());
        }
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("delete");
        deleteSql.append("from CCurtProgramNotifGroup");
        deleteSql.append("where NotificationGroupId in (", toDeleteInts, ")");
        deleteSql.append("and CCurtProgramId = ", program.getId());
        jdbcTemplate.update(deleteSql);
    }
    
    @Override
    public void deleteForProgram(Program program) {
        Object[] args = new Object[] {program.getId()};
        SqlStatementBuilder query = new SqlStatementBuilder();
        query.append("delete");
        query.append("from CCurtProgramNotifGroup");
        query.append("where CCurtProgramId = ?");
        
        jdbcTemplate.update(query.getSql(), args);
    }
}
