package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.yukon.IDatabaseCache;

public final class AlarmCatDaoImpl implements AlarmCatDao {
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
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

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AlarmCategoryId, CategoryName, NotificationGroupId");
        sql.append("FROM AlarmCategory");
        sql.append("WHERE AlarmCategoryId").eq(alarmCategoryId);
        
        return jdbcTemplate.queryForObject(sql, liteAlarmCategoryMapper);
    }
    
    @Override
    public LiteAlarmCategory getAlarmCategoryFromCache(int id) {
        synchronized (databaseCache) {
            List<LiteAlarmCategory> categories = databaseCache.getAllAlarmCategories();

            for (int j = 0; j < categories.size(); j++) {
                if (id == categories.get(j).getAlarmCategoryId()) {
                    return categories.get(j);
                }
            }
            return null;
        }
    }

    @Override
    public int getAlarmCategoryIdFromCache(String categoryName) {
        synchronized (databaseCache) {
            List<LiteAlarmCategory> categories = databaseCache.getAllAlarmCategories();

            for (int j = 0; j < categories.size(); j++) {
                LiteAlarmCategory alCat = categories.get(j);
                if (alCat.getCategoryName().equals(categoryName)) {
                    return alCat.getAlarmCategoryId();
                }
            }
            return PointAlarming.NONE_NOTIFICATIONID;
        }
    }

    @Override
    public List<LiteAlarmCategory> getAlarmCategories() {
        return databaseCache.getAllAlarmCategories();
    }
}