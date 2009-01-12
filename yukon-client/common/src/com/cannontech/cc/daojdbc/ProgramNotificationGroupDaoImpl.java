package com.cannontech.cc.daojdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.ProgramNotificationGroupDao;
import com.cannontech.cc.model.Program;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.core.dao.support.YukonBaseJdbcDao;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.spring.CollectionResultSetExtractor;

public class ProgramNotificationGroupDaoImpl extends YukonBaseJdbcDao implements ProgramNotificationGroupDao {
    private static final String PROGRAM_NOTIF_GROUP_TABLENAME = "CCurtProgramNotifGroup";
    private NotificationGroupDao notificationGroupDao;
    
    public ProgramNotificationGroupDaoImpl() {
        super();
    }

    @SuppressWarnings("unchecked")
    public Set<LiteNotificationGroup> getNotificationGroupsForProgram(Program program) {
        SqlStatementBuilder query = new SqlStatementBuilder();
        query.append("select NotificationGroupId from", PROGRAM_NOTIF_GROUP_TABLENAME);
        query.append("where CCurtProgramId = ?");
        Object[] args = new Object[] {program.getId()};
        RowMapper rowMapper = new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                int notificationGroupId = rs.getInt("NotificationGroupId");
                LiteNotificationGroup lng = 
                    notificationGroupDao.getLiteNotificationGroup(notificationGroupId);
                return lng;
            }
        };
        Set<LiteNotificationGroup> result = new HashSet<LiteNotificationGroup>();
        CollectionResultSetExtractor extractor = new CollectionResultSetExtractor(result,rowMapper);
        getJdbcTemplate().query(query.toString(), args, extractor);
        return result;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void setNotificationGroupsForProgram(Program program, Set<LiteNotificationGroup> notificationGroups) {
        Set<LiteNotificationGroup> currentDbSet = getNotificationGroupsForProgram(program);
        // find ids that are to be removed
        Set<LiteNotificationGroup> toAdd = new HashSet<LiteNotificationGroup>(notificationGroups);
        toAdd.removeAll(currentDbSet);
        SqlStatementBuilder addSql = new SqlStatementBuilder();
        addSql.append("insert into", PROGRAM_NOTIF_GROUP_TABLENAME, "(NotificationGroupId, CCurtProgramId)");
        addSql.append("values (?, ?)");
        for (LiteNotificationGroup notifGroup : toAdd) {
            final int notifGroupId = notifGroup.getLiteID();
            final int programId = program.getId();
            PreparedStatementSetter pss = new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, notifGroupId);
                    ps.setInt(2, programId);
                }
            };
            getJdbcTemplate().update(addSql.toString(), pss);
        }
        
        // find ids that are to be added
        Set<LiteNotificationGroup> toDelete = new HashSet<LiteNotificationGroup>(currentDbSet);
        toDelete.removeAll(notificationGroups);
        Set<Integer> toDeleteInts = new HashSet<Integer>();
        for (LiteNotificationGroup lng : toDelete) {
            toDeleteInts.add(lng.getLiteID());
        }
        SqlStatementBuilder deleteSql = new SqlStatementBuilder();
        deleteSql.append("delete from", PROGRAM_NOTIF_GROUP_TABLENAME);
        deleteSql.append("where NotificationGroupId in (", toDeleteInts, ")");
        deleteSql.append("and CCurtProgramId = ", program.getId());
        getJdbcTemplate().execute(deleteSql.toString());
    }
    
    public void deleteForProgram(Program program) {
        Object[] args = new Object[] {program.getId()};
        SqlStatementBuilder query = new SqlStatementBuilder();
        query.append("delete from", PROGRAM_NOTIF_GROUP_TABLENAME);
        query.append("where CCurtProgramId = ?");
        
        getJdbcTemplate().update(query.toString(), args);
    }

    public void setNotificationGroupDao(NotificationGroupDao notificationGroupDao) {
        this.notificationGroupDao = notificationGroupDao;
    }

}
