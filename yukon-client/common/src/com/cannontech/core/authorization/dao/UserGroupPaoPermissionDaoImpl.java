package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.model.GroupPaoPermission;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.StringRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * User Group implementation for PaoPermissionDao
 */
public class UserGroupPaoPermissionDaoImpl implements PaoPermissionDao<LiteUserGroup> {

    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    public List<PaoPermission> getPermissions(LiteUserGroup userGroup) {
        return this.getPermissionsByUserGroupIds(Collections.singletonList(userGroup.getUserGroupId()));
    }

    @Override
    public List<PaoPermission> getPermissions(List<LiteUserGroup> userGroups) {
        List<Integer> userGroupIds = Lists.transform(userGroups, new Function<LiteUserGroup, Integer>() {
            @Override
            public Integer apply(LiteUserGroup userGroup) {
                return userGroup.getUserGroupId();
            }
        });
        
        return this.getPermissionsByUserGroupIds(userGroupIds);
    }

    @Override
    public List<PaoPermission> getPermissionsForPao(LiteUserGroup userGroup, YukonPao pao) {
        return this.getPermissionsForPao(userGroup.getUserGroupId(), pao.getPaoIdentifier().getPaoId());
    }

    @Override
    public AuthorizationResponse hasPermissionForPao(LiteUserGroup userGroup, YukonPao pao, Permission permission) {
        return this.isHasPermissionForPao(Collections.singletonList(userGroup.getUserGroupId()), pao.getPaoIdentifier().getPaoId(), permission);
    }

    @Override
    public AuthorizationResponse hasPermissionForPao(List<LiteUserGroup> userGroups, YukonPao pao, Permission permission) {
        List<Integer> userGroupIdList = Lists.transform(userGroups, new Function<LiteUserGroup, Integer>() {
            @Override
            public Integer apply(LiteUserGroup userGroup) {
                return userGroup.getUserGroupId();
            }
        });

        return this.isHasPermissionForPao(userGroupIdList, pao.getPaoIdentifier().getPaoId(), permission);
    }

    @Override
    public void addPermission(LiteUserGroup userGroup, int paoId, Permission permission, boolean allow) {
        this.addPermission(userGroup.getUserGroupId(), paoId, permission, allow);
    }

    @Override
    public void removePermission(LiteUserGroup userGroup, YukonPao pao, Permission permission) {
        this.removePermission(userGroup.getUserGroupId(), pao.getPaoIdentifier().getPaoId(), permission);
    }

    @Override
    public void removeAllPaoPermissions(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM GroupPaoPermission");
        sql.append("WHERE PaoId").eq(paoId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void removeAllPermissions(LiteUserGroup userGroup) {
        this.removeAllPermissions(userGroup.getUserGroupId());
    }

    @Override
    public void removeAllPermissions(int userGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM GroupPaoPermission");
        sql.append("WHERE GroupId").eq(userGroupId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void removeAllPermissions(LiteUserGroup userGroup, Permission permission) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM GroupPaoPermission");
        sql.append("WHERE GroupId").eq(userGroup.getUserGroupId());
        sql.append("  AND Permission").eq(permission);

        yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<Integer> getPaosForPermission(LiteUserGroup userGroup, Permission permission) {
        return this.getPaosForPermission(Collections.singletonList(userGroup), permission);
    }

    @Override
    public List<Integer> getPaosForPermission(List<LiteUserGroup> userGroupList, Permission permission) {
        List<Integer> userGroupIds = Lists.transform(userGroupList, new Function<LiteUserGroup, Integer>() {
            @Override
            public Integer apply(LiteUserGroup userGroup) {
                return userGroup.getUserGroupId();
            }
        });
        
        return this.getPaosForUserGroupIdsAndPermission(userGroupIds, permission);
    }

    private List<Integer> getPaosForUserGroupIdsAndPermission(List<Integer> userGroupIds, Permission permission) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaoId");
        sql.append("FROM GroupPaoPermission");
        sql.append("WHERE GroupId").in(userGroupIds);
        sql.append("  AND Permission").eq(permission);
        
        List<Integer> paoIdList = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return paoIdList;
    }

    private List<PaoPermission> getPermissionsByUserGroupIds(List<Integer> userGroupIds) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GroupPaoPermissionId, GroupId, PaoId, Permission, Allow");
        sql.append("FROM GroupPaoPermission");
        sql.append("WHERE GroupId").in(userGroupIds);

        List<? extends PaoPermission> gppList = yukonJdbcTemplate.query(sql, new GroupPaoPermissionMapper());
        List<PaoPermission> result = Lists.newArrayList(gppList);
        return result;
    }

    private List<PaoPermission> getPermissionsForPao(int userGroupId, int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GroupPaoPermissionId, GroupId, PaoId, Permission, Allow");
        sql.append("FROM GroupPaoPermission");
        sql.append("WHERE GroupId").eq(userGroupId);
        sql.append("  AND PaoId").eq(paoId);
        
        List<? extends PaoPermission> gppList = yukonJdbcTemplate.query(sql, new GroupPaoPermissionMapper());
        List<PaoPermission> result = Lists.newArrayList(gppList);
        return result;
    }

    private AuthorizationResponse isHasPermissionForPao(List<Integer> userGroupIds, int paoId, Permission permission) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Allow");
        sql.append("FROM GroupPaoPermission");
        sql.append("WHERE GroupId").in(userGroupIds);
        sql.append("  AND PaoId").eq(paoId);
        sql.append("  AND Permission").eq(permission);

        List<String> allowList = yukonJdbcTemplate.query(sql, new StringRowMapper());
        
        if (allowList.size() == 0) {
            return AuthorizationResponse.UNKNOWN;
        } else {
            if(permission.getDefault()){
                for(Object entry : allowList){
                    if(entry.equals(AllowDeny.DENY.name())){
                        return AuthorizationResponse.UNAUTHORIZED;
                    }
                }
                return AuthorizationResponse.AUTHORIZED;
            } else {
                for(Object entry : allowList){
                    if(entry.equals(AllowDeny.ALLOW.name())){
                        return AuthorizationResponse.AUTHORIZED;
                    }
                }
                return AuthorizationResponse.UNAUTHORIZED;
            }
        }
    }
    
    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               LiteUserGroup lightUserGroup, Permission permission) {
        return getPaoAuthorizations(paos, Collections.singletonList(lightUserGroup), permission);
    }
    
    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               List<LiteUserGroup> userGroups, final Permission permission) {
        
        final Multimap<AuthorizationResponse, PaoIdentifier> result = ArrayListMultimap.create();

        final Multimap<Integer, PaoIdentifier> paoLookup = ArrayListMultimap.create();
        for(PaoIdentifier pao : paos) {
            int paoId = pao.getPaoId();
            paoLookup.put(paoId, pao);
        }
        
        final List<Integer> userGroupIds = Lists.transform(userGroups, new Function<LiteUserGroup, Integer>() {
            @Override
            public Integer apply(LiteUserGroup userGroup) {
                return userGroup.getUserGroupId();
            }
        });
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        template.query(new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT allow, paoId ");
                sql.append("FROM GroupPaoPermission ");
                sql.append("WHERE groupId").in(userGroupIds);
                sql.append("    AND paoid").in(subList);
                sql.append("    AND permission").eq(permission);
                
                return sql;
            }
        },
        paoLookup.keySet(),
        new PaoPermissionRowCallbackHandler(paoLookup, result));
        
        // Add any leftover paos to the unknown list - there was no row in the paopermission table
        // for these paos
        result.putAll(AuthorizationResponse.UNKNOWN, paoLookup.values());
        
        return result;
        
    }

    private void addPermission(int userGroupId, int paoId, Permission permission, boolean allow) {

        int id = nextValueHelper.getNextValue("GroupPaoPermission");
        AllowDeny allowDeny = allow ? AllowDeny.ALLOW : AllowDeny.DENY;
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO GroupPaoPermission");
        sql.values(id, userGroupId, paoId, permission, allowDeny.name());
        
        yukonJdbcTemplate.update(sql);
    }

    private void removePermission(int userGroupId, int paoId, Permission permission) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM GroupPaoPermission");
        sql.append("WHERE GroupId").eq(userGroupId);
        sql.append("  AND PaoId").eq(paoId);
        sql.append("  AND Permission").eq(permission);

        yukonJdbcTemplate.update(sql);
    }

    /**
     * Mapping class to process a result set row into a GroupPaoPermission
     */
    private class GroupPaoPermissionMapper implements ParameterizedRowMapper<GroupPaoPermission> {

        public GroupPaoPermission mapRow(ResultSet rs, int rowNum) throws SQLException {

            GroupPaoPermission gpp = new GroupPaoPermission();
            gpp.setId(rs.getInt("groupPaoPermissionId"));
            gpp.setGroupId(rs.getInt("groupId"));
            gpp.setPaoId(rs.getInt("paoId"));
            String permissionStr = rs.getString("permission");
            gpp.setPermission(Permission.valueOf(permissionStr));
            String allowStr = rs.getString("allow");
            AllowDeny allowDeny = AllowDeny.valueOf(allowStr);
            gpp.setAllow(allowDeny.getAllowValue());
            return gpp;
        }
    }

    @Override
    public AuthorizationResponse hasPermissionForPao(int userGroupId, int paoId, Permission permission) {
        return this.isHasPermissionForPao(Collections.singletonList(userGroupId), paoId, permission);
    }
}