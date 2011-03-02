package com.cannontech.core.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.databaseMigration.TableChangeCallback;
import com.cannontech.common.databaseMigration.service.DatabaseMigrationService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.service.TableToDBChangeMappingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class TableToDBChangeMappingServiceImpl implements TableToDBChangeMappingService{
    
    private DatabaseMigrationService databaseMigrationService;
    private DBPersistentDao dbPersistentDao;
    private PointDao pointDao;
    
    @PostConstruct
    public void startup() {
        addDBTableListener("ApplianceCategory", DBChangeMsg.CHANGE_DO_NOT_CARE_DB, DBChangeMsg.CAT_APPLIANCE, DBChangeMsg.CAT_APPLIANCE);
        addDBTableListener("LMGroupExpressCom", 0, PAOGroups.STRING_CAT_DEVICE, PaoType.LM_GROUP_EXPRESSCOMM.getDbString());
        addDBTableListener("LMProgram", 0, PAOGroups.STRING_CAT_LOADMANAGEMENT, PaoType.LM_DIRECT_PROGRAM.getDbString());
        addDBTableListener("LMProgramWebPublishing", DBChangeMsg.CHANGE_STARS_PUBLISHED_PROGRAM_DB, DBChangeMsg.CAT_STARS_PUBLISHED_PROGRAM, DBChangeMsg.CAT_STARS_PUBLISHED_PROGRAM);
        addDBTableListener("YukonGroup", 10, DBChangeMsg.CAT_YUKON_USER_GROUP, DBChangeMsg.CAT_YUKON_USER);
        addDBTableListener("YukonWebConfiguration", DBChangeMsg.CHANGE_WEB_CONFIG_DB, DBChangeMsg.CAT_WEB_CONFIG, DBChangeMsg.CAT_WEB_CONFIG);
        addPointDBTableListener("Point", 1, DBChangeMsg.CAT_POINT);
        
    }
    
    private void addDBTableListener(final String tableName, final int database, final String category, final String objectType) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            public void rowInserted(int primaryKey) {
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, objectType, DbChangeType.ADD);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowUpdated(int primaryKey) {
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, objectType, DbChangeType.UPDATE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowDeleted(int primaryKey) {
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, objectType, DbChangeType.DELETE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }
        });
    }

    private void addPointDBTableListener(final String tableName, final int database, final String category) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            public void rowInserted(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, pointType, DbChangeType.ADD);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowUpdated(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, pointType, DbChangeType.UPDATE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowDeleted(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, pointType, DbChangeType.DELETE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }
        });
    }
    
    @Autowired
    public void setDatabaseMigrationService(DatabaseMigrationService databaseMigrationService) {
        this.databaseMigrationService = databaseMigrationService;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
}