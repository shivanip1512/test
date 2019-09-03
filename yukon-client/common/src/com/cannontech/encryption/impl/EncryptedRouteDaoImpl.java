package com.cannontech.encryption.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class EncryptedRouteDaoImpl implements EncryptedRouteDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;
    
    private final YukonRowMapper<EncryptionKey> encryptionKeyRowMapper = new YukonRowMapper<EncryptionKey>() {
        @Override
        public EncryptionKey mapRow(YukonResultSet rs)
                throws SQLException {
            EncryptionKey encryptionKey = new EncryptionKey();
            encryptionKey.setPrivateKey(rs.getString("PrivateKey"));
            encryptionKey.setPublicKey(rs.getString("PublicKey"));
            encryptionKey.setTimestamp(rs.getInstant("Timestamp"));
            return encryptionKey;
        }
    };

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

        dbChangeManager.processDbChange(encryptedRoute.getPaobjectId(),
                                        DBChangeMsg.CHANGE_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                        PAOGroups.STRING_CAT_ROUTE,
                                        DbChangeType.UPDATE);
    }

    @Override
    public void deleteEncryptedRoute(EncryptedRoute encryptedRoute) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE");
        sql.append("FROM YukonPAObjectEncryptionKey");
        sql.append("WHERE PAOBjectId").eq(encryptedRoute.getPaobjectId());

        yukonJdbcTemplate.update(sql);
        dbChangeManager.processDbChange(encryptedRoute.getPaobjectId(),
                                        DBChangeMsg.CHANGE_YUKON_PAOBJECT_ENCRYPTION_KEY_DB,
                                        PAOGroups.STRING_CAT_ROUTE,
                                        DbChangeType.UPDATE);    
    }

    @Override
    public List<EncryptionKey> getEncryptionKeys() {
        List<EncryptionKey> keyList = Lists.newArrayList();
        DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
        boolean isOracle = databaseVendor.isOracle();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (isOracle) {
            sql.append("SELECT ek.EncryptionKeyId, ek.Name, ek.PublicKey, ek.EncryptionKeyType, TO_CHAR(ek.PrivateKey) as " + 
            "PrivateKey, (CASE WHEN COUNT(ypoek.EncryptionKeyId) > 0 THEN 1 ELSE 0 END) as CurrentlyUsed");
        } else {
            sql.append("SELECT ek.EncryptionKeyId, ek.Name, ek.PublicKey, ek.EncryptionKeyType, CAST(ek.PrivateKey AS NVARCHAR(MAX)) as "
                + "PrivateKey, (CASE WHEN COUNT(ypoek.EncryptionKeyId) > 0 THEN 1 ELSE 0 END) as CurrentlyUsed");
        }
        sql.append("FROM EncryptionKey ek");
        sql.append("LEFT JOIN YukonPAObjectEncryptionKey ypoek ON ek.EncryptionKeyId = ypoek.EncryptionKeyId");
        sql.append("WHERE ek.EncryptionKeyType=").appendArgument(EncryptionKeyType.ExpresscomOneWay);
        if (isOracle) {
            sql.append("GROUP BY ek.EncryptionKeyId, ek.Name, ek.PublicKey, ek.EncryptionKeyType, TO_CHAR(ek.PrivateKey)");
        } else {
        sql.append("GROUP BY ek.EncryptionKeyId, ek.Name, ek.PublicKey, ek.EncryptionKeyType, CAST(ek.PrivateKey AS NVARCHAR(MAX))");
        }

        keyList = yukonJdbcTemplate.query(sql, new YukonRowMapper<EncryptionKey>() {
            @Override
            public EncryptionKey mapRow(YukonResultSet rs) throws SQLException {
                int id = rs.getInt("EncryptionKeyId");
                String name = rs.getString("Name");
                String value = rs.getString("PrivateKey");
                boolean currentlyUsed = BooleanUtils.toBoolean(rs.getInt("CurrentlyUsed"));

                return new EncryptionKey(id, name, value, currentlyUsed);
            }
            
        });
        
        return keyList;
    }

    @Override
    public void saveNewEncryptionKey(String name, String privateKey, String publicKey, EncryptionKeyType encryptionKeyType, Instant timestamp) {
        int encryptionKeyId = getNextEncryptionKeyId();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO EncryptionKey");
        sql.append("(EncryptionKeyId, Name, PrivateKey");
        if (encryptionKeyType == EncryptionKeyType.Honeywell ||
                encryptionKeyType == EncryptionKeyType.Ecobee ||
                encryptionKeyType == EncryptionKeyType.Itron) {
            sql.append(", PublicKey, EncryptionKeyType, Timestamp)");
            sql.values(encryptionKeyId, name, privateKey, publicKey, encryptionKeyType, timestamp);
        } else {
            sql.append(", EncryptionKeyType)");
            sql.values(encryptionKeyId, name, privateKey, encryptionKeyType);
        }

        yukonJdbcTemplate.update(sql);

        dbChangeManager.processDbChange(encryptionKeyId, DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB,
            DBChangeMsg.CAT_ENCRYPTION_KEY_DB, DbChangeType.ADD);

    }
    
    @Override
    public void saveOrUpdateEncryptionKey(String privateKey, String publicKey, EncryptionKeyType encryptionKeyType, Instant timestamp) {
        if (getEncryptionKey(encryptionKeyType).isPresent()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("UPDATE EncryptionKey ");
            sql.append("SET PrivateKey =").appendArgument(privateKey);
            sql.append(", PublicKey =").appendArgument(publicKey);
            sql.append(", Timestamp =").appendArgument(timestamp);
            sql.append("WHERE EncryptionKeyType =").appendArgument(encryptionKeyType);
            yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
        } else {
            saveNewEncryptionKey(encryptionKeyType.name(), privateKey, publicKey, encryptionKeyType, timestamp);
        }
    }
    
    @Override
    public Optional<EncryptionKey> getEncryptionKey(EncryptionKeyType encryptionKeyType) {
        EncryptionKey encryptionKey=null;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT PrivateKey, PublicKey, Timestamp ");
            sql.append("FROM EncryptionKey ");
            sql.append("WHERE EncryptionKeyType =").appendArgument(encryptionKeyType);
            encryptionKey = yukonJdbcTemplate.queryForObject(sql, encryptionKeyRowMapper);
        }catch (EmptyResultDataAccessException ex) {
            // returns Optional.empty() if the encryptionKey was not found
        }
        return Optional.ofNullable(encryptionKey);
    }

    @Override
    public void deleteEncryptionKey(int encryptionKeyId) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM EncryptionKey");
            sql.append("WHERE");
            sql.append("EncryptionKeyId").eq(encryptionKeyId);

            yukonJdbcTemplate.update(sql);
            
            dbChangeManager.processDbChange(encryptionKeyId,
                                            DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB,
                                            DBChangeMsg.CAT_ENCRYPTION_KEY_DB,
                                            DbChangeType.DELETE);   
            
    }

    public final static Integer getNextEncryptionKeyId( ) {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("EncryptionKey");
    }

}
