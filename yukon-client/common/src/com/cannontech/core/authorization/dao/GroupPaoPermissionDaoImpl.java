package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.model.GroupPaoPermission;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;

/**
 * Group implementation for PaoPermissionDao
 */
public class GroupPaoPermissionDaoImpl implements PaoPermissionDao<LiteYukonGroup> {

    private JdbcOperations jdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;

    public void setJdbcOps(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public List<PaoPermission> getPermissions(LiteYukonGroup group) {
        return this.getPermissions(String.valueOf(group.getGroupID()));
    }

    public List<PaoPermission> getPermissions(List<LiteYukonGroup> groupList) {

        List<Integer> idList = new ArrayList<Integer>();
        for (LiteYukonGroup group : groupList) {
            idList.add(group.getGroupID());
        }

        String groupIds = SqlStatementBuilder.convertToSqlLikeList(idList);

        return this.getPermissions(groupIds);
    }

    public List<PaoPermission> getPermissionsForPao(LiteYukonGroup group, LiteYukonPAObject pao) {
        return this.getPermissionsForPao(group.getGroupID(), pao.getYukonID());
    }

    public AuthorizationResponse hasPermissionForPao(LiteYukonGroup group, LiteYukonPAObject pao,
            Permission permission) {
        List<Integer> idList = new ArrayList<Integer>();
        idList.add(new Integer(group.getGroupID()));
        return this.isHasPermissionForPao(idList,
                                          pao.getYukonID(),
                                          permission);
    }

    public AuthorizationResponse hasPermissionForPao(List<LiteYukonGroup> groupList, LiteYukonPAObject pao,
            Permission permission) {
        //TODO
        List<Integer> idList = new ArrayList<Integer>();
        for (LiteYukonGroup group : groupList) {
            
            idList.add(group.getGroupID());
        }

        return this.isHasPermissionForPao(idList, pao.getYukonID(), permission);
    }

    public void addPermission(LiteYukonGroup group, LiteYukonPAObject pao, Permission permission) {
        this.addPermission(group.getGroupID(), pao.getYukonID(), permission);
    }

    public void removePermission(LiteYukonGroup group, LiteYukonPAObject pao, Permission permission) {
        this.removePermission(group.getGroupID(), pao.getYukonID(), permission);
    }

    public void removeAllPaoPermissions(int paoId) {
        String sql = "delete from GroupPaoPermission where paoid = ?";
        jdbcTemplate.update(sql, new Object[] { paoId });
    }

    public void removeAllPermissions(LiteYukonGroup it) {
        this.removeAllPermissions(it.getGroupID());
    }

    public void removeAllPermissions(int groupId) {

        String sql = "delete from GroupPaoPermission where groupid = ?";
        jdbcTemplate.update(sql, new Object[] { groupId });

    }

    public List<Integer> getPaosForPermission(LiteYukonGroup it, Permission permission) {
        return this.getPaosForPermission(Collections.singletonList(it), permission);
    }

    public List<Integer> getPaosForPermission(List<LiteYukonGroup> groupList, Permission permission) {

        List<Integer> idList = new ArrayList<Integer>();
        for (LiteYukonGroup group : groupList) {
            idList.add(group.getGroupID());
        }

        String groupIds = SqlStatementBuilder.convertToSqlLikeList(idList);

        return this.getPaosForPermission(groupIds, permission);
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getPaosForPermission(String groupIds, Permission permission) {

        String sql = "select paoid from grouppaopermission where groupid in (" + groupIds
                + ") and permission = ?";

        List<Integer> paoIdList = jdbcTemplate.queryForList(sql,
                                                            new Object[] { permission.toString() },
                                                            Integer.class);
        return paoIdList;
    }

    @SuppressWarnings("unchecked")
    private List<PaoPermission> getPermissions(String groupIds) {

        String sql = "select groupPaoPermissionId, groupid, paoid, permission, allow from GroupPaoPermission "
                + "where groupid in (" + groupIds + ")";
        List<PaoPermission> gppList = jdbcTemplate.query(sql, new GroupPaoPermissionMapper());
        return gppList;
    }

    @SuppressWarnings("unchecked")
    private List<PaoPermission> getPermissionsForPao(int groupId, int paoId) {

        String sql = "select groupPaoPermissionId, groupid, paoid, permission, allow from GroupPaoPermission "
                + "where groupid = ? and paoid = ?";
        List<PaoPermission> gppList = jdbcTemplate.query(sql,
                                                         new Object[] { groupId, paoId },
                                                         new GroupPaoPermissionMapper());
        return gppList;
    }

    private AuthorizationResponse isHasPermissionForPao(List<Integer> groupIdList, int paoId, Permission permission) {

        String groupIds = SqlStatementBuilder.convertToSqlLikeList(groupIdList);
        
        String sql = "select allow from GroupPaoPermission where groupid in ( " + groupIds
                + ") and paoid = ? " + "and permission = ?";
       
        List<String> count = jdbcTemplate.queryForList(sql, new Object[] { paoId, permission.toString() }, String.class);
        
        if ( count.size() == 0 )
            return AuthorizationResponse.UNKNOWN;
        //TODO
        if( count.contains( new String("false")) )
            return AuthorizationResponse.UNAUTHORIZED;
        else{ 
            for( String s : count )
            {   //Not tested, need LM test
                if( s.compareTo("true") != 0 )
                    return AuthorizationResponse.UNKNOWN;
            }
            return AuthorizationResponse.AUTHORIZED;
        }
    }

    private void addPermission(int groupId, int paoId, Permission permission) {

        int id = nextValueHelper.getNextValue("grouppaopermission");
        String sql = "insert into GroupPaoPermission values (?,?,?,?,?)";
        jdbcTemplate.update(sql, new Object[] { id, groupId, paoId, permission.toString(), new Boolean( !permission.getNotInTableResponse() ).toString() });

    }

    private void removePermission(int groupId, int paoId, Permission permission) {

        String sql = "delete from GroupPaoPermission where groupid = ? and paoid = ? "
                + "and permission = ?";
        jdbcTemplate.update(sql, new Object[] { groupId, paoId, permission.toString() });

    }

    /**
     * Mapping class to process a result set row into a GroupPaoPermission
     */
    private class GroupPaoPermissionMapper implements RowMapper {

        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

            GroupPaoPermission gpp = new GroupPaoPermission();
            gpp.setId(rs.getInt("groupPaoPermissionId"));
            gpp.setGroupId(rs.getInt("groupId"));
            gpp.setPaoId(rs.getInt("paoId"));
            gpp.setPermission(Permission.valueOf(rs.getString("permission")));
            gpp.setAllow( rs.getString("allow"));
            return gpp;
        }
    }
}
