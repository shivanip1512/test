package com.cannontech.dr.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResponseFavoritesDao {
    public void detailPageViewed(int paoId);
    public List<DisplayablePao> getRecentlyViewed(LiteYukonUser user, int max);

    public void addFavorite(int paoId, LiteYukonUser user);
    public void removeFavorite(int paoId, LiteYukonUser user);

    /**
     * Check to see if the specified PAO is a favorite for the given user.
     * @param paoId The id of the PAO.
     * @param user The user.
     * @param afterDate If the paoId has not changed since this date, this
     *            method may return null.
     * @return
     */
    public Boolean isFavorite(int paoId, LiteYukonUser user, Date afterDate);
    public List<DisplayablePao> getFavorites(LiteYukonUser user);
}
