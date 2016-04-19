package com.cannontech.web.support.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.support.SystemHealthMetricIdentifier;

/**
 * Dao to set and retrieve system metric favorites for users.
 */
public interface UserSystemMetricDao {
    
    void addFavorite(LiteYukonUser user, SystemHealthMetricIdentifier metric);
    
    void removeFavorite(LiteYukonUser user, SystemHealthMetricIdentifier metric);
    
    List<SystemHealthMetricIdentifier> getFavorites(LiteYukonUser user);

}
