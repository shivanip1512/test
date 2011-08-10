package com.cannontech.encryption.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.db.pao.StaticPaoInfo;
import com.cannontech.encryption.EncryptedRoute;
import com.cannontech.encryption.EncryptedRouteDao;

public class EncryptedRouteDaoImpl implements EncryptedRouteDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private DBPersistentDao dbPersistentDao;

    @Override
    public List<EncryptedRoute> getAllEncryptedRoutes() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paobjectid, paoname, type");
        sql.append("FROM yukonpaobject");
        sql.append("WHERE paoclass").eq(PaoClass.ROUTE);
        sql.append("    AND (Type ").eq(PaoType.ROUTE_RDS_TERMINAL);
        sql.append("    OR Type ").eq(PaoType.ROUTE_TAP_PAGING);
        sql.append("    OR Type ").eq(PaoType.ROUTE_SNPP_TERMINAL);
        sql.append("    OR Type ").eq(PaoType.ROUTE_WCTP_TERMINAL).append(")");

        final List<EncryptedRoute> encryptedIDs = new ArrayList<EncryptedRoute>();

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
                    dbPersistentDao.performDBChange(spi, TransactionType.RETRIEVE);
                }

                EncryptedRoute route = new EncryptedRoute();
                route.setPaoName(rs.getString("PaoName"));
                route.setType(rs.getString("Type"));
                route.setInfoKey(spi.getPaoInfo().name());
                route.setPaobjectId(spi.getPaobjectId());
                route.setValue(spi.getValue());
                encryptedIDs.add(route);
            }
        });

        return encryptedIDs;
    }

    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
