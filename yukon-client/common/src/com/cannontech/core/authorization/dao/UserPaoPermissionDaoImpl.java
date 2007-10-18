package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserPaoPermission;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * User implementation for PaoPermissionDao
 */
public class UserPaoPermissionDaoImpl implements PaoPermissionDao<LiteYukonUser> {

    private JdbcOperations jdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;

    public void setJdbcOps(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public List<PaoPermission> getPermissions(LiteYukonUser user) {
        return this.getPermissions(user.getUserID());
    }

    public List<PaoPermission> getPermissionsForPao(LiteYukonUser user, LiteYukonPAObject pao) {
        return this.getPermissionsForPao(user.getUserID(), pao.getYukonID());
    }

    public AuthorizationResponse hasPermissionForPao(LiteYukonUser user, LiteYukonPAObject pao,
            Permission permission) {
        return this.isHasPermissionForPao(user.getUserID(), pao.getYukonID(), permission);
    }

    public void addPermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        this.addPermission(user.getUserID(), pao.getYukonID(), permission);
    }

    public void removePermission(LiteYukonUser user, LiteYukonPAObject pao, Permission permission) {
        this.removePermission(user.getUserID(), pao.getYukonID(), permission);
    }

    public void removeAllPermissions(LiteYukonUser it) {
        this.removeAllPermissions(it.getUserID());
    }

    public void removeAllPaoPermissions(int paoId) {
        String sql = "delete from UserPaoPermission where paoid = ?";
        jdbcTemplate.update(sql, new Object[] { paoId });
    }

    public void removeAllPermissions(int userId) {

        String sql = "delete from UserPaoPermission where userid = ?";
        jdbcTemplate.update(sql, new Object[] { userId });

    }

    public List<Integer> getPaosForPermission(LiteYukonUser user, Permission permission) {
        return this.getPaosForPermission(user.getUserID(), permission);
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getPaosForPermission(int userID, Permission permission) {

        String sql = "select paoid from userpaopermission where userid = ? and permission = ?";

        List<Integer> paoIdList = jdbcTemplate.queryForList(sql, new Object[] { userID,
                permission.toString() }, Integer.class);
        return paoIdList;
    }

    public List<Integer> getPaosForPermission(List<LiteYukonUser> itList, Permission permission) {
        throw new UnsupportedOperationException("Not implemented for users");
    }

    public List<PaoPermission> getPermissions(List<LiteYukonUser> it) {
        throw new UnsupportedOperationException("Not implemented for users");
    }

    public AuthorizationResponse hasPermissionForPao(List<LiteYukonUser> itList, LiteYukonPAObject pao,
            Permission permission) {
        throw new UnsupportedOperationException("Not implemented for users");
    }

    private AuthorizationResponse isHasPermissionForPao(int userId, int paoId, Permission permission) {
        String sql;
        sql = "select allow from UserPaoPermission where userid = ? and paoid = ? " + "and permission = ?";

        List count = jdbcTemplate.queryForList(sql, new Object[] { userId, paoId,
                permission.toString() }, String.class);
        
        if ( count.size() == 0 )
            return AuthorizationResponse.UNKNOWN;
        
        if( count.contains( new String("true")) )
            return AuthorizationResponse.AUTHORIZED;
        else
            return AuthorizationResponse.UNAUTHORIZED;
    }

    @SuppressWarnings("unchecked")
    private List<PaoPermission> getPermissions(int userId) {

        String sql = "select userPaoPermissionId, userid, paoid, permission, allow from UserPaoPermission "
                + "where userid = ?";
        List<PaoPermission> uppList = jdbcTemplate.query(sql,
                                                         new Object[] { userId },
                                                         new UserPaoPermissionMapper());
        return uppList;
    }

    @SuppressWarnings("unchecked")
    private List<PaoPermission> getPermissionsForPao(int userId, int paoId) {

        String sql = "select userPaoPermissionId, userid, paoid, permission, allow from UserPaoPermission "
                + "where userid = ? and paoid = ?";
        List<PaoPermission> uppList = jdbcTemplate.query(sql,
                                                         new Object[] { userId, paoId },
                                                         new UserPaoPermissionMapper());
        return uppList;
    }

    private void addPermission(int userId, int paoId, Permission permission) {

        int id = nextValueHelper.getNextValue("userpaopermission");

        String sql = "insert into UserPaoPermission values (?,?,?,?,?)";
        jdbcTemplate.update(sql, new Object[] { id, userId, paoId, permission.toString(), new Boolean( !permission.getNotInTableResponse() ).toString() });
    }
    

    private void removePermission(int userId, int paoId, Permission permission) {

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
            upp.setAllow(rs.getString("allow"));

            return upp;
        }
    }

}
