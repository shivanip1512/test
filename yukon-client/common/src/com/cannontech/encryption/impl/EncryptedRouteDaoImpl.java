package com.cannontech.encryption.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class EncryptedRouteDaoImpl implements EncryptedRouteDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public List<EncryptedRoute> getAllEncryptedRoutes() {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.ROUTE_ENCRYPTION);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.PAOName, ypo.PAObjectID, ypo.Type, ek.Name, ek.EncryptionKeyId");
        sql.append("FROM YukonPAObjectEncryptionKey ypoe");
        sql.append("RIGHT JOIN YukonPAObject ypo ON ypoe.PAObjectId = ypo.PAObjectID");
        sql.append("LEFT JOIN EncryptionKey ek ON ek.EncryptionKeyId = ypoe.EncryptionKeyId");
        sql.append("WHERE ypo.Type").in(paoTypes);

        final List<EncryptedRoute> encryptedRoutes = Lists.newArrayList();

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                EncryptedRoute route = new EncryptedRoute();

                route.setPaoName(rs.getString("PAOName"));
                route.setType(rs.getEnum("Type", PaoType.class));
                route.setPaobjectId(rs.getInt("PAObjectId"));
                route.setEncryptionKeyName(rs.getString("Name"));
                route.setEncryptionKeyId(rs.getNullableInt("EncryptionKeyId"));

                encryptedRoutes.add(route);
            }
        });

        return encryptedRoutes;
    }

    @Override
    public void addEncryptedRoute(EncryptedRoute encryptedRoute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO YukonPAObjectEncryptionKey");
        sql.append("(PAObjectId, EncryptionKeyId)");
        sql.values(encryptedRoute.getPaobjectId(),encryptedRoute.getEncryptionKeyId());

        yukonJdbcTemplate.update(sql);

        dbPersistentDao.processDBChange(new DBChangeMsg(encryptedRoute.getPaobjectId(),
                                                        DBChangeMsg.CHANGE_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                                        PAOGroups.STRING_CAT_ROUTE,
                                                        DbChangeType.UPDATE));
    }

    @Override
    public void deleteEncryptedRoute(EncryptedRoute encryptedRoute) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE");
        sql.append("FROM YukonPAObjectEncryptionKey");
        sql.append("WHERE PAOBjectId").eq(encryptedRoute.getPaobjectId());

        yukonJdbcTemplate.update(sql);
        dbPersistentDao.processDBChange(new DBChangeMsg(encryptedRoute.getPaobjectId(),
                                                        DBChangeMsg.CHANGE_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                                        PAOGroups.STRING_CAT_ROUTE,
                                                        DbChangeType.UPDATE));    
    }

    @Override
    public List<EncryptionKey> getEncryptionKeys() {
        List<EncryptionKey> keyList = Lists.newArrayList();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ek.*, (CASE WHEN COUNT(ypoek.EncryptionKeyId) > 0 THEN 1 ELSE 0 END) as CurrentlyUsed");
        sql.append("FROM EncryptionKey ek");
        sql.append("LEFT JOIN YukonPAObjectEncryptionKey ypoek ON ek.EncryptionKeyId = ypoek.EncryptionKeyId");
        sql.append("GROUP BY ek.EncryptionKeyId, ek.Name, ek.Value");

        keyList = yukonJdbcTemplate.query(sql, new YukonRowMapper<EncryptionKey>() {
            @Override
            public EncryptionKey mapRow(YukonResultSet rs) throws SQLException {
                int id = rs.getInt("EncryptionKeyId");
                String name = rs.getString("Name");
                String value = rs.getString("Value");
                boolean currentlyUsed = BooleanUtils.toBoolean(rs.getInt("CurrentlyUsed"));

                return new EncryptionKey(id, name, value, currentlyUsed);
            }
            
        });
        
        return keyList;
    }

    @Override
    public void saveNewEncyptionKey(String name, String value) {
            int encryptionKeyId = getNextEncryptionKeyId();
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO EncryptionKey");
            sql.append("(EncryptionKeyId, Name, Value)");
            sql.values(encryptionKeyId, name, value);

            yukonJdbcTemplate.update(sql);

            dbPersistentDao.processDBChange(new DBChangeMsg(encryptionKeyId,
                                                            DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB,
                                                            DBChangeMsg.CAT_ENCRYPTION_KEY_DB,
                                                            DbChangeType.ADD));
            
    }

    @Override
    public void deleteEncyptionKey(int encryptionKeyId) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM EncryptionKey");
            sql.append("WHERE");
            sql.append("EncryptionKeyId").eq(encryptionKeyId);

            yukonJdbcTemplate.update(sql);
            
            dbPersistentDao.processDBChange(new DBChangeMsg(encryptionKeyId,
                                                            DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB,
                                                            DBChangeMsg.CAT_ENCRYPTION_KEY_DB,
                                                            DbChangeType.DELETE));   
            
    }

    public final static Integer getNextEncryptionKeyId( ) {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("EncryptionKey");
    }

}
