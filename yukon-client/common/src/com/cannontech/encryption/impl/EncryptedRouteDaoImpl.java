package com.cannontech.encryption.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class EncryptedRouteDaoImpl implements EncryptedRouteDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DBPersistentDao dbPersistentDao;

    private Logger log = YukonLogManager.getLogger(EncryptedRouteDaoImpl.class);
    
    private static final String selectEncryptedRouteSql = "SELECT ypo.PAOName, ypo.PAObjectID, ypo.Type, ek.Name, ek.EncryptionKeyId " 
                    + "FROM YukonPAObjectEncryptionKey ypoe " 
                    + "RIGHT JOIN YukonPAObject ypo ON ypoe.PAObjectId = ypo.PAObjectID " 
                    + "LEFT JOIN EncryptionKey ek ON ek.EncryptionKeyId = ypoe.EncryptionKeyId";

    
    @Override
    public List<EncryptedRoute> getAllEncryptedRoutes() {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.ROUTE_ENCRYPTION);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectEncryptedRouteSql);
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
        Object [] args = {encryptedRoute.getPaobjectId(), encryptedRoute.getEncryptionKeyId()};
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO YukonPAObjectEncryptionKey");
        sql.append("(PAObjectId, EncryptionKeyId)");
        sql.append("VALUES(");
        sql.appendList(args);
        sql.append(")");

        yukonJdbcTemplate.update(sql);

        dbPersistentDao.processDBChange(new DBChangeMsg(encryptedRoute.getPaobjectId(),
                                                        DBChangeMsg.CHANGE_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                                        DBChangeMsg.CAT_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                                        DbChangeType.ADD));
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
                                                        DBChangeMsg.CAT_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                                        DbChangeType.DELETE));    
    }

    @Override
    public List<EncryptionKey> getEncryptionKeys() {
        final List<EncryptionKey> map = Lists.newArrayList();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("Select EncryptionKeyId, Name, Value");
        sql.append("FROM EncryptionKey ");

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int id = rs.getInt("EncryptionKeyId");
                //int energyCompanyId = rs.getInt("EnergyCompanyId");
                String name = rs.getString("Name");
                String value = rs.getString("Value");

                EncryptionKey key = new EncryptionKey(id,name,value);
                map.add(key);
            }
        });
        return map;
    }

    @Override
    public void saveNewEncyptionKey(String name, String value) throws CryptoException {
        try {
            int encryptionKeyId = getNextEncryptionKeyId();
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO EncryptionKey");
            sql.append("(EncryptionKeyId, Name, Value)");
            sql.append("VALUES(");
            sql.appendArgument(encryptionKeyId);
            sql.append(",");
            sql.appendArgument(name);
            sql.append(",");
            sql.appendArgument(value);
            sql.append(")");

            yukonJdbcTemplate.update(sql);

            dbPersistentDao.processDBChange(new DBChangeMsg(encryptionKeyId,
                                                            DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB,
                                                            DBChangeMsg.CAT_ENCRYPTION_KEY_DB,
                                                            DbChangeType.ADD));   
            
        } catch (PersistenceException pe) {
            throw pe;
        } catch (Exception e) {
            log.warn("caught exception in saveNewEncyptionKey", e);
        }

    }

    @Override
    public void deleteEncyptionKey(int encryptionKeyId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM EncryptionKey");
            sql.append("WHERE");
            sql.append("EncryptionKeyId").eq(encryptionKeyId);

            yukonJdbcTemplate.update(sql);
            
            dbPersistentDao.processDBChange(new DBChangeMsg(encryptionKeyId,
                                                            DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB,
                                                            DBChangeMsg.CAT_ENCRYPTION_KEY_DB,
                                                            DbChangeType.DELETE));   
            
        } catch (PersistenceException pe) {
            throw pe;
        } catch (Exception e) {
            log.warn("caught exception in saveNewEncyptionKey", e);
        }
    }

    public final static Integer getNextEncryptionKeyId( ) {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("EncryptionKey");
    }

}
