package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public final class AlarmCatDaoImpl implements AlarmCatDao {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private static final Logger log = YukonLogManager.getLogger(AlarmCatDao.class);
    
    private final Map<Integer, LiteAlarmCategory> alarmCategoryCache = new ConcurrentHashMap<>(100);
    
    private void createDatabaseChangeListener() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDatabase() == DBChangeMsg.CHANGE_ALARM_CATEGORY_DB) {
                    alarmCategoryCache.clear();
                }
            }
        });
    }

    private YukonRowMapper<LiteAlarmCategory> liteAlarmCategoryMapper = new YukonRowMapper<LiteAlarmCategory>() {
        @Override
        public LiteAlarmCategory mapRow(YukonResultSet rs) throws SQLException {
            
            int alarmCategoryId = rs.getInt("AlarmCategoryId");
            String categoryName = rs.getString("CategoryName");
            int notificationGroupId = rs.getInt("NotificationGroupID");
            
            LiteAlarmCategory liteAlarmCategory = new LiteAlarmCategory(alarmCategoryId);
            liteAlarmCategory.setCategoryName(categoryName);
            liteAlarmCategory.setNotificationGroupID(notificationGroupId);
            return liteAlarmCategory;
        }
    };

    @Override
    public LiteAlarmCategory getAlarmCategory(int alarmCategoryId) {
        return getAlarmCategoryCache().get(alarmCategoryId);
    }

    @Override
    public int getAlarmCategoryId(String categoryName) {
        
        for (LiteAlarmCategory liteAlarmCategory : getAlarmCategoryCache().values()) {
            if (liteAlarmCategory.getCategoryName().equals(categoryName)) {
                return liteAlarmCategory.getAlarmCategoryId();
            }
        }
        return PointAlarming.NONE_NOTIFICATIONID;
    }

    @Override
    public List<LiteAlarmCategory> getAlarmCategories() {
        List<LiteAlarmCategory> sortedAlarmCategories = new ArrayList<LiteAlarmCategory>(getAlarmCategoryCache().values());
        Collections.sort(sortedAlarmCategories);
        return sortedAlarmCategories;
    }

    private Map<Integer, LiteAlarmCategory> getAlarmCategoryCache() {
        if (alarmCategoryCache.isEmpty()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT AlarmCategoryId, CategoryName, NotificationGroupId");
            sql.append("FROM AlarmCategory");
            sql.append("WHERE AlarmCategoryId > 0 ORDER BY AlarmCategoryId");
            
            List<LiteAlarmCategory> liteAlarmCategories = jdbcTemplate.query(sql, liteAlarmCategoryMapper);
            
            for (LiteAlarmCategory liteAlarmCategory : liteAlarmCategories) {
                alarmCategoryCache.put(liteAlarmCategory.getAlarmCategoryId(), liteAlarmCategory);
            }
        }
        return alarmCategoryCache;
    }
    
    @PostConstruct
    public void init() throws Exception {
        createDatabaseChangeListener();
        getAlarmCategoryCache();
        log.info("Initialized alarm category cache.");
    }
}