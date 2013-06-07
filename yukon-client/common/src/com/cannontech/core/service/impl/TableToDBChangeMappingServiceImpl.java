package com.cannontech.core.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.service.TableToDBChangeMappingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.dispatch.DbChangeCategory;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.messaging.util.DbChangeManager;

public class TableToDBChangeMappingServiceImpl implements TableToDBChangeMappingService{
    
    @Autowired private DatabaseMigrationService databaseMigrationService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private PointDao pointDao;
    
    @PostConstruct
    public void startup() {
        addDBTableListener("ApplianceCategory", DbChangeCategory.APPLIANCE);
        addDBTableListener("LMGroupExpressCom", 0, PAOGroups.STRING_CAT_DEVICE, PaoType.LM_GROUP_EXPRESSCOMM.getDbString());
        addDBTableListener("LMProgram", 0, PAOGroups.STRING_CAT_LOADMANAGEMENT, PaoType.LM_DIRECT_PROGRAM.getDbString());
        addDBTableListener("LMProgramWebPublishing", DBChangeMessage.CHANGE_STARS_PUBLISHED_PROGRAM_DB, DBChangeMessage.CAT_STARS_PUBLISHED_PROGRAM, DBChangeMessage.CAT_STARS_PUBLISHED_PROGRAM);
        addDBTableListener("YukonGroup", 10, DBChangeMessage.CAT_YUKON_USER_GROUP, DBChangeMessage.CAT_YUKON_USER);
        addDBTableListener("YukonWebConfiguration", DBChangeMessage.CHANGE_WEB_CONFIG_DB, DBChangeMessage.CAT_WEB_CONFIG, DBChangeMessage.CAT_WEB_CONFIG);
        addPointDBTableListener("Point", 1, DBChangeMessage.CAT_POINT);
    }

    private void addDBTableListener(final String tableName, final DbChangeCategory changeCategory) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            @Override
            public void rowInserted(int primaryKey) {
                dbChangeManager.processDbChange(DbChangeType.ADD, changeCategory, primaryKey);
            }

            @Override
            public void rowUpdated(int primaryKey) {
                dbChangeManager.processDbChange(DbChangeType.UPDATE, changeCategory, primaryKey);
            }

            @Override
            public void rowDeleted(int primaryKey) {
                dbChangeManager.processDbChange(DbChangeType.DELETE, changeCategory, primaryKey);
            }
        });
    }

    private void addDBTableListener(final String tableName, final int database, final String category, final String objectType) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            @Override
            public void rowInserted(int primaryKey) {
                dbChangeManager.processDbChange(primaryKey, database, category, objectType, DbChangeType.ADD);
            }

            @Override
            public void rowUpdated(int primaryKey) {
                dbChangeManager.processDbChange(primaryKey, database, category, objectType, DbChangeType.UPDATE);
            }

            @Override
            public void rowDeleted(int primaryKey) {
                dbChangeManager.processDbChange(primaryKey, database, category, objectType, DbChangeType.DELETE);
            }
        });
    }

    private void addPointDBTableListener(final String tableName, final int database, final String category) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            @Override
            public void rowInserted(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                dbChangeManager.processDbChange(primaryKey, database, category, pointType, DbChangeType.ADD);
            }

            @Override
            public void rowUpdated(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                dbChangeManager.processDbChange(primaryKey, database, category, pointType, DbChangeType.UPDATE);
            }

            @Override
            public void rowDeleted(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                dbChangeManager.processDbChange(primaryKey, database, category, pointType, DbChangeType.DELETE);
            }
        });
    }
}