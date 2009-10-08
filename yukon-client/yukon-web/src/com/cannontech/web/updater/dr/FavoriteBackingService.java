package com.cannontech.web.updater.dr;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.dao.DemandResponseFavoritesDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class FavoriteBackingService implements UpdateBackingService {
    private DemandResponseFavoritesDao favoritesDao;

    @Override
    public String getLatestValue(String identifier, long afterDate,
            YukonUserContext userContext) {
        int paoId = Integer.parseInt(identifier);
        Boolean latestValue = favoritesDao.isFavorite(paoId,
                                                      userContext.getYukonUser(),
                                                      new Date(afterDate));
        return latestValue == null ? null : Boolean.toString(latestValue);
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
            long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Autowired
    public void setFavoritesDao(DemandResponseFavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
}
