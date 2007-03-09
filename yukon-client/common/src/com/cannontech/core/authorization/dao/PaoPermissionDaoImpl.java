package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.model.GroupPaoPermission;
import com.cannontech.core.authorization.model.UserPaoPermission;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * Implementation for PaoPermissionDao
 */
public class PaoPermissionDaoImpl implements PaoPermissionDao {

    private JdbcOperations jdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;

    public void setJdbcOps(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public List<UserPaoPermission> getUserPermissions(LiteYukonUser user) {
        return this.getUserPermissions(user.getUserID());
    }

    public List<UserPaoPermission> getUserPermissionsForPao(LiteYukonUser user,
            LiteYukonPAObject pao) {
        return this.getUserPermissionsForPao(user.getUserID(), pao.getYukonID());
    }

    public boolean isUserHasPermissionForPao(LiteYukonUser user, LiteYukonPAObject pao,
            Permission permission) {
        return this.isUserHasPermissionForPao(user.getUserID(), pao.getYukonID(), permission);
    }

    public void addUserPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        this.addUserPermission(user.getUserID(), pao.getYukonID(), permission);
    }

    public void removeUserPermission(LiteYukonUser user, LiteYukonPAObject pao,
            Permission permission) {
        this.removeUserPermission(user.getUserID(), pao.getYukonID(), permission);
    }

    public List<GroupPaoPermission> getGroupPermissions(LiteYukonGroup group) {
        return this.getGroupPermissions(String.valueOf(group.getGroupID()));
    }

    public List<GroupPaoPermission> getGroupPermissions(List<LiteYukonGroup> groupList) {

        List<Integer> idList = new ArrayList<Integer>();
        for (LiteYukonGroup group : groupList) {
            idList.add(group.getGroupID());
        }

        String groupIds = SqlStatementBuilder.convertToSqlLikeList(idList);

        return this.getGroupPermissions(groupIds);
    }

    public List<GroupPaoPermission> getGroupPermissionsForPao(LiteYukonGroup group,
            LiteYukonPAObject pao) {
        return this.getGroupPermissionsForPao(group.getGroupID(), pao.getYukonID());
    }

    public boolean isGroupHasPermissionForPao(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        return this.isGroupHasPermissionForPao(String.valueOf(group.getGroupID()),
                                               pao.getYukonID(),
                                               permission);
    }

    public boolean isGroupHasPermissionForPao(List<LiteYukonGroup> groupList,
            LiteYukonPAObject pao, Permission permission) {

        List<Integer> idList = new ArrayList<Integer>();
        for (LiteYukonGroup group : groupList) {
            idList.add(group.getGroupID());
        }

        String groupIds = SqlStatementBuilder.convertToSqlLikeList(idList);

        return this.isGroupHasPermissionForPao(groupIds, pao.getYukonID(), permission);
    }

    public void addGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        this.addGroupPermission(group.getGroupID(), pao.getYukonID(), permission);
    }

    public void removeGroupPermission(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        this.removeGroupPermission(group.getGroupID(), pao.getYukonID(), permission);
    }

    public void removeAllPaoPermissions(int paoId) {

        String sql = "delete from GroupPaoPermission where paoid = ?";
        jdbcTemplate.update(sql, new Object[] { paoId });

        sql = "delete from UserPaoPermission where paoid = ?";
        jdbcTemplate.update(sql, new Object[] { paoId });

    }

    @SuppressWarnings("unchecked")
    private List<GroupPaoPermission> getGroupPermissions(String groupIds) {

        String sql = "select groupPaoPermissionId, groupid, paoid, permission from GroupPaoPermission "
                + "where groupid in (" + groupIds + ")";
        List<GroupPaoPermission> gppList = jdbcTemplate.query(sql, new GroupPaoPermissionMapper());
        return gppList;
    }

    @SuppressWarnings("unchecked")
    private List<GroupPaoPermission> getGroupPermissionsForPao(int groupId, int paoId) {

        String sql = "select groupPaoPermissionId, groupid, paoid, permission from GroupPaoPermission "
                + "where groupid = ? and paoid = ?";
        List<GroupPaoPermission> gppList = jdbcTemplate.query(sql,
                                                              new Object[] { groupId, paoId },
                                                              new GroupPaoPermissionMapper());
        return gppList;
    }

    private boolean isGroupHasPermissionForPao(String groupIds, int paoId, Permission permission) {

        String sql = "select count(*) from GroupPaoPermission where groupid in ( " + groupIds
                + ") and paoid = ? " + "and permission = ?";

        int count = jdbcTemplate.queryForInt(sql, new Object[] { paoId, permission.toString() });

        return count > 0;
    }

    private void addGroupPermission(int groupId, int paoId, Permission permission) {

        int id = nextValueHelper.getNextValue("grouppaopermission");

        String sql = "insert into GroupPaoPermission values (?,?,?,?)";
        jdbcTemplate.update(sql, new Object[] { id, groupId, paoId, permission.toString() });

    }

    private void removeGroupPermission(int groupId, int paoId, Permission permission) {

        String sql = "delete from GroupPaoPermission where groupid = ? and paoid = ? "
                + "and permission = ?";
        jdbcTemplate.update(sql, new Object[] { groupId, paoId, permission.toString() });

    }

    private boolean isUserHasPermissionForPao(int userId, int paoId, Permission permission) {

        String sql = "select count(*) from UserPaoPermission where userid = ? and paoid = ? "
                + "and permission = ?";

        int count = jdbcTemplate.queryForInt(sql, new Object[] { userId, paoId,
                permission.toString() });

        return count > 0;
    }

    @SuppressWarnings("unchecked")
    private List<UserPaoPermission> getUserPermissions(int userId) {

        String sql = "select userPaoPermissionId, userid, paoid, permission from UserPaoPermission "
                + "where userid = ?";
        List<UserPaoPermission> uppList = jdbcTemplate.query(sql,
                                                             new Object[] { userId },
                                                             new UserPaoPermissionMapper());
        return uppList;
    }

    @SuppressWarnings("unchecked")
    private List<UserPaoPermission> getUserPermissionsForPao(int userId, int paoId) {

        String sql = "select userPaoPermissionId, userid, paoid, permission from UserPaoPermission "
                + "where userid = ? and paoid = ?";
        List<UserPaoPermission> uppList = jdbcTemplate.query(sql,
                                                             new Object[] { userId, paoId },
                                                             new UserPaoPermissionMapper());
        return uppList;
    }

    private void addUserPermission(int userId, int paoId, Permission permission) {

        int id = nextValueHelper.getNextValue("userpaopermission");

        String sql = "insert into UserPaoPermission values (?,?,?,?)";
        jdbcTemplate.update(sql, new Object[] { id, userId, paoId, permission.toString() });

    }

    private void removeUserPermission(int userId, int paoId, Permission permission) {

        String sql = "delete from UserPaoPermission where userid = ? and paoid = ? "
                + "and permission = ?";
        jdbcTemplate.update(sql, new Object[] { userId, paoId, permission.toString() });

    }

    /**
     * Mapping class to process a result set row into a UserPaoPermission
     */
    private class UserPaoPermissionMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            UserPaoPermission upp = new UserPaoPermission();
            upp.setId(rs.getInt("userPaoPermissionId"));
            upp.setUserId(rs.getInt("userId"));
            upp.setPaoId(rs.getInt("paoId"));
            upp.setPermission(Permission.valueOf(rs.getString("permission")));

            return upp;
        }
    }

    /**
     * Mapping class to process a result set row into a GroupPaoPermission
     */
    private class GroupPaoPermissionMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            GroupPaoPermission gpp = new GroupPaoPermission();
            gpp.setId(rs.getInt("userPaoPermissionId"));
            gpp.setGroupId(rs.getInt("groupId"));
            gpp.setPaoId(rs.getInt("paoId"));
            gpp.setPermission(Permission.valueOf(rs.getString("permission")));

            return gpp;
        }
    }

}
