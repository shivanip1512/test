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

public class TableToDBChangeMappingServiceImpl implements TableToDBChangeMappingService{
    
    private DatabaseMigrationService databaseMigrationService;
    private DBPersistentDao dbPersistentDao;
    private PointDao pointDao;
    
    @PostConstruct
    public void startup() {
        addDBTableListener("LMGroupExpressCom", 0, PAOGroups.STRING_CAT_DEVICE, PaoType.LM_GROUP_EXPRESSCOMM.getDbString());
        addDBTableListener("LMProgram", 0, PAOGroups.STRING_CAT_LOADMANAGEMENT, PaoType.LM_DIRECT_PROGRAM.getDbString());
        addDBTableListener("YukonGroup", 10, DBChangeMsg.CAT_YUKON_USER_GROUP, DBChangeMsg.CAT_YUKON_USER);
        addPointDBTableListener("Point", 1, DBChangeMsg.CAT_POINT);
        
    }
    
    private void addDBTableListener(final String tableName, final int database, final String category, final String objectType) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            public void rowInserted(int primaryKey) {
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, objectType, DBChangeMsg.CHANGE_TYPE_ADD);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowUpdated(int primaryKey) {
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, objectType, DBChangeMsg.CHANGE_TYPE_UPDATE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowDeleted(int primaryKey) {
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, objectType, DBChangeMsg.CHANGE_TYPE_DELETE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }
        });
    }

    private void addPointDBTableListener(final String tableName, final int database, final String category) {
        databaseMigrationService.addDBTableListener(tableName, new TableChangeCallback() {
            public void rowInserted(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, pointType, DBChangeMsg.CHANGE_TYPE_ADD);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowUpdated(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, pointType, DBChangeMsg.CHANGE_TYPE_UPDATE);
                dbPersistentDao.processDBChange(dbChangeMsg);
            }

            public void rowDeleted(int primaryKey) {
                LitePoint litePoint = pointDao.getLitePoint(primaryKey);
                String pointType = PointTypes.getType(litePoint.getLiteType());
                DBChangeMsg dbChangeMsg = new DBChangeMsg(primaryKey, database, category, pointType, DBChangeMsg.CHANGE_TYPE_DELETE);
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