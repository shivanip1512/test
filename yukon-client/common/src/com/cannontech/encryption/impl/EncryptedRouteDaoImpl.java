package com.cannontech.encryption.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.pao.StaticPaoInfo;
import com.cannontech.encryption.EncryptedRouteDao;
import com.google.common.collect.Lists;

public class EncryptedRouteDaoImpl implements EncryptedRouteDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private DBPersistentDao dbPersistentDao;
    private PaoDefinitionDao paoDefinitionDao;

    @Override
    public List<EncryptedRoute> getAllEncryptedRoutes() {
        
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.ROUTE_ENCRYPTION);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectid, paoname, type");
        sql.append("FROM yukonpaobject");
        sql.append("WHERE type").in(paoTypes);

        final List<EncryptedRoute> encryptedIDs = Lists.newArrayList();

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                StaticPaoInfo spi = new StaticPaoInfo(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY);
                spi.setPaobjectId(rs.getInt("PaobjectId"));
                try {
                    dbPersistentDao.performDBChange(spi, TransactionType.RETRIEVE);
                } catch (NotFoundException e) {
                    spi.setValue(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY.getDefaultValue());
                    dbPersistentDao.performDBChange(spi, TransactionType.INSERT);
                }

                EncryptedRoute route = new EncryptedRoute();
                route.setPaoName(rs.getString("PaoName"));
                route.setType(rs.getEnum("Type", PaoType.class));
                route.setInfoKey(spi.getPaoInfo().name());
                route.setPaobjectId(spi.getPaobjectId());
                route.setValue(spi.getValue());
                encryptedIDs.add(route);
            }
        });

        return encryptedIDs;
    }

    @Override
    public void saveEncryptedRoute(EncryptedRoute encryptedRoute) {
        StaticPaoInfo spi = new StaticPaoInfo(PaoInfo.CPS_ONE_WAY_ENCRYPTION_KEY);
        spi.setPaobjectId(encryptedRoute.getPaobjectId());
        dbPersistentDao.performDBChange(spi, TransactionType.RETRIEVE);
        if (encryptedRoute.getValue() != "") {
            spi.setValue(encryptedRoute.getValue());
        } else {
            spi.setValue(null);
        }
        dbPersistentDao.performDBChange(spi, TransactionType.UPDATE);
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }

}
