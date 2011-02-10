package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
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
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Group implementation for PaoPermissionDao
 */
public class GroupPaoPermissionDaoImpl implements PaoPermissionDao<LiteYukonGroup> {

    private JdbcOperations jdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;
    private YukonJdbcTemplate yukonJdbcTemplate;

    public void setJdbcOps(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
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

    public List<PaoPermission> getPermissionsForPao(LiteYukonGroup group, YukonPao pao) {
        return this.getPermissionsForPao(group.getGroupID(), pao.getPaoIdentifier().getPaoId());
    }

    public AuthorizationResponse hasPermissionForPao(LiteYukonGroup group, YukonPao pao,
            Permission permission) {
        List<Integer> idList = new ArrayList<Integer>();
        idList.add(new Integer(group.getGroupID()));
        return this.isHasPermissionForPao(idList,
                                          pao.getPaoIdentifier().getPaoId(),
                                          permission);
    }

    public AuthorizationResponse hasPermissionForPao(List<LiteYukonGroup> groupList, YukonPao pao,
            Permission permission) {
        List<Integer> idList = new ArrayList<Integer>();
        for (LiteYukonGroup group : groupList) {
            
            idList.add(group.getGroupID());
        }

        return this.isHasPermissionForPao(idList, pao.getPaoIdentifier().getPaoId(), permission);
    }

    public void addPermission(LiteYukonGroup group, int paoId, Permission permission, boolean allow) {
        this.addPermission(group.getGroupID(), paoId, permission, allow);
    }

    public void removePermission(LiteYukonGroup group, YukonPao pao, Permission permission) {
        this.removePermission(group.getGroupID(), pao.getPaoIdentifier().getPaoId(), permission);
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
    
    public void removeAllPermissions(LiteYukonGroup it, Permission permission) {
        String sql = "delete from GroupPaoPermission where groupid = ? and permission = ?";
        jdbcTemplate.update(sql, new Object[] { it.getGroupID(), permission.name() });
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

    private List<Integer> getPaosForPermission(String groupIds, Permission permission) {

        String sql = "select paoid from grouppaopermission where groupid in (" + groupIds
                + ") and permission = ?";

        List<Integer> paoIdList = jdbcTemplate.queryForList(sql,
                                                            new Object[] { permission.name() },
                                                            Integer.class);
        return paoIdList;
    }

    private List<PaoPermission> getPermissions(String groupIds) {

        String sql = "select groupPaoPermissionId, groupid, paoid, permission, allow from GroupPaoPermission "
                + "where groupid in (" + groupIds + ")";
        List<? extends PaoPermission> gppList = jdbcTemplate.query(sql, new GroupPaoPermissionMapper());
        List<PaoPermission> result = Lists.newArrayList(gppList);
        return result;
    }

    private List<PaoPermission> getPermissionsForPao(int groupId, int paoId) {

        String sql = "select groupPaoPermissionId, groupid, paoid, permission, allow from GroupPaoPermission "
                + "where groupid = ? and paoid = ?";
        List<? extends PaoPermission> gppList = jdbcTemplate.query(sql,
                                                         new Object[] { groupId, paoId },
                                                         new GroupPaoPermissionMapper());
        List<PaoPermission> result = Lists.newArrayList(gppList);
        return result;
    }

    private AuthorizationResponse isHasPermissionForPao(List<Integer> groupIdList, int paoId, Permission permission) {

        String groupIds = SqlStatementBuilder.convertToSqlLikeList(groupIdList);
        
        String sql = "select allow from GroupPaoPermission where groupid in ( " + groupIds
                + ") and paoid = ? " + "and permission = ?";

        List<?> allowList = jdbcTemplate.queryForList(sql, new Object[] { paoId, permission.name() }, String.class);

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
                                                                               LiteYukonGroup it,
                                                                               Permission permission) {
        return getPaoAuthorizations(paos, Collections.singletonList(it), permission);
    }
    
    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
                                                                               List<LiteYukonGroup> it,
                                                                               final Permission permission) {
        
        final Multimap<AuthorizationResponse, PaoIdentifier> result = ArrayListMultimap.create();

        final Multimap<Integer, PaoIdentifier> paoLookup = ArrayListMultimap.create();
        for(PaoIdentifier pao : paos) {
            int paoId = pao.getPaoId();
            paoLookup.put(paoId, pao);
        }
        
        final List<Integer> groupIdList = Lists.newArrayList();
        for(LiteYukonGroup group : it) {
            groupIdList.add(group.getGroupID());
        }
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        template.query(new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT allow, paoId ");
                sql.append("FROM GroupPaoPermission ");
                sql.append("WHERE groupId").in(groupIdList);
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

    private void addPermission(int groupId, int paoId, Permission permission, boolean allow) {

        int id = nextValueHelper.getNextValue("grouppaopermission");
        AllowDeny allowDeny = allow ? AllowDeny.ALLOW : AllowDeny.DENY;
        
        String sql = "insert into GroupPaoPermission values (?,?,?,?,?)";
        jdbcTemplate.update(sql, new Object[] { id, groupId, paoId, permission.name(), allowDeny.name() });

    }

    private void removePermission(int groupId, int paoId, Permission permission) {

        String sql = "delete from GroupPaoPermission where groupid = ? and paoid = ? "
                + "and permission = ?";
        jdbcTemplate.update(sql, new Object[] { groupId, paoId, permission.name() });

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
    public AuthorizationResponse hasPermissionForPao(int id, int paoId, Permission permission) {
        List<Integer> idList = new ArrayList<Integer>();
        idList.add(id);

        return this.isHasPermissionForPao(idList, paoId, permission);
    }
}
