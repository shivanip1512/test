package com.cannontech.core.authorization.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.model.PaoPermission;
import com.cannontech.core.authorization.model.UserPaoPermission;
import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * User implementation for PaoPermissionDao
 */
public class UserPaoPermissionDaoImpl implements PaoPermissionDao<LiteYukonUser> {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    @Override
    public List<PaoPermission> getPermissions(LiteYukonUser user) {
        return getPermissions(user.getUserID());
    }

    @Override
    public AuthorizationResponse hasPermissionForPao(LiteYukonUser user, YukonPao pao, Permission permission) {
        return hasPermissionForPao(user.getUserID(), pao.getPaoIdentifier().getPaoId(), permission);
    }

    @Override
    public void addPermission(LiteYukonUser user, int paoId, Permission permission, boolean allow) {
        addPermission(user.getUserID(), paoId, permission, allow);
    }

    @Override
    public void removePermission(LiteYukonUser user, YukonPao pao, Permission permission) {
        removePermission(user.getUserID(), pao.getPaoIdentifier().getPaoId(), permission);
    }

    @Override
    public void removeAllPaoPermissions(int paoId) {
        String sql = "delete from UserPaoPermission where paoid = ?";
        jdbcTemplate.update(sql, new Object[] { paoId });
    }

    @Override
    public void removeAllPermissions(int userId) {
        String sql = "delete from UserPaoPermission where userid = ?";
        jdbcTemplate.update(sql, new Object[] { userId });
    }

    @Override
    public void removeAllPermissions(LiteYukonUser it, Permission permission) {
        String sql = "delete from UserPaoPermission where userid = ? and permission = ?";
        jdbcTemplate.update(sql, new Object[] { it.getUserID(), permission.name() });
    }

    @Override
    public List<Integer> getPaosForPermission(LiteYukonUser user, Permission permission) {
        return getPaosForPermission(user.getUserID(), permission);
    }

    private List<Integer> getPaosForPermission(int userID, Permission permission) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select paoid from UserPaoPermission where userid").eq(userID);
        sql.append("and permission").eq(permission.name());

        List<Integer> paoIdList = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        return paoIdList;
    }

    @Override
    public List<Integer> getPaosForPermission(List<LiteYukonUser> itList, Permission permission) {
        throw new UnsupportedOperationException("Not implemented for users");
    }

    @Override
    public AuthorizationResponse hasPermissionForPao(int userId, int paoId, Permission permission) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select allow from UserPaoPermission where userid").eq(userId);
        sql.append("and paoId").eq(paoId);
        sql.append("and permission").eq(permission.name());

        List<String> allowList = jdbcTemplate.query(sql, TypeRowMapper.STRING);

        if (allowList.size() == 0) {
            return AuthorizationResponse.UNKNOWN;
        } else if (allowList.size() == 1) {
            if (allowList.get(0).equals(AllowDeny.ALLOW.name())) {
                return AuthorizationResponse.AUTHORIZED;
            }
            return AuthorizationResponse.UNAUTHORIZED;
        } else {
            throw new IncorrectResultSizeDataAccessException(1, allowList.size());
        }
    }

    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
            final LiteYukonUser it, final Permission permission) {

        final Multimap<AuthorizationResponse, PaoIdentifier> result = ArrayListMultimap.create();

        final Multimap<Integer, PaoIdentifier> paoLookup = ArrayListMultimap.create();
        for (PaoIdentifier pao : paos) {
            int paoId = pao.getPaoId();
            paoLookup.put(paoId, pao);
        }

        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);

        template.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT allow, paoId ");
                sql.append("FROM UserPaoPermission ");
                sql.append("WHERE userid").eq(it.getUserID());
                sql.append("    AND paoid").in(subList);
                sql.append("    AND permission").eq(permission);
                return sql;
            }
        }, paoLookup.keySet(), new PaoPermissionRowCallbackHandler(paoLookup, result));

        // Add any leftover paos to the unknown list - there was no row in the paopermission table
        // for these paos
        result.putAll(AuthorizationResponse.UNKNOWN, paoLookup.values());

        return result;
    }

    @Override
    public Multimap<AuthorizationResponse, PaoIdentifier> getPaoAuthorizations(Collection<PaoIdentifier> paos,
            List<LiteYukonUser> it, Permission permission) {
        throw new UnsupportedOperationException("Not implemented for users");
    }

    private List<PaoPermission> getPermissions(int userId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select userPaoPermissionId, userid, paoid, permission, allow from UserPaoPermission where userid").eq(
            userId);
        List<? extends PaoPermission> uppList = jdbcTemplate.query(sql, new UserPaoPermissionMapper());
        List<PaoPermission> result = Lists.newArrayList(uppList);
        return result;
    }

    private void addPermission(int userId, int paoId, Permission permission, boolean allow) {
        int id = nextValueHelper.getNextValue("userpaopermission");
        AllowDeny allowDeny = allow ? AllowDeny.ALLOW : AllowDeny.DENY;

        String sql = "insert into UserPaoPermission values (?,?,?,?,?)";
        jdbcTemplate.update(sql, new Object[] { id, userId, paoId, permission.name(), allowDeny.name() });
    }

    private void removePermission(int userId, int paoId, Permission permission) {
        String sql = "delete from UserPaoPermission where userid = ? and paoid = ? " + "and permission = ?";
        jdbcTemplate.update(sql, new Object[] { userId, paoId, permission.name() }); // use name here
    }

    /**
     * Mapping class to process a result set row into a UserPaoPermission
     */
    private class UserPaoPermissionMapper implements RowMapper<UserPaoPermission> {
        @Override
        public UserPaoPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserPaoPermission upp = new UserPaoPermission();
            upp.setId(rs.getInt("userPaoPermissionId"));
            upp.setUserId(rs.getInt("userId"));
            upp.setPaoId(rs.getInt("paoId"));
            upp.setPermission(Permission.valueOf(rs.getString("permission")));
            String allowStr = rs.getString("allow");
            AllowDeny allowDeny = AllowDeny.valueOf(allowStr);
            upp.setAllow(allowDeny.getAllowValue());

            return upp;
        }
    }
}
