package com.cannontech.common.favorites.dao;

import java.util.Map;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface FavoritesDao {
    public void detailPageViewed(int paoId);

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
    public boolean isFavorite(int paoId, LiteYukonUser user);

    public Map<Integer, Boolean> favoritesByPao(
            Iterable<? extends YukonPao> paos, LiteYukonUser user);
}
