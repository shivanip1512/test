package com.cannontech.common.favorites.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class FavoritesDaoImpl implements FavoritesDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    @Override
    public void detailPageViewed(int paoId) {
        Date now = new Date();
        int rowsAffected = simpleJdbcTemplate.update("UPDATE paoRecentViews" +
        		" SET whenViewed = ? WHERE paobjectId = ?", now, paoId);
        if (rowsAffected < 1) {
            simpleJdbcTemplate.update("INSERT INTO" +
            		" paoRecentViews (paobjectId, whenViewed) VALUES (?, ?)",
            		paoId, now);
        }
    }

    @Override
    public void addFavorite(int paoId, LiteYukonUser user) {
        if (paoId == 0) {
            throw new IllegalArgumentException();
        }
        simpleJdbcTemplate.update("INSERT INTO" +
                                  " paoFavorites (userId, paobjectId) VALUES (?, ?)",
                                  user.getUserID(), paoId);
    }

    @Override
    public void removeFavorite(int paoId, LiteYukonUser user) {
        simpleJdbcTemplate.update("DELETE FROM paoFavorites" +
                                  " WHERE userId = ? AND paobjectId = ?",
                                  user.getUserID(), paoId);
    }

    @Override
    public boolean isFavorite(int paoId, LiteYukonUser user) {
        int isFavorite =
            simpleJdbcTemplate.queryForInt("SELECT count(*) FROM paoFavorites" +
                                           " WHERE userId = ? AND paobjectId = ?",
                                           user.getUserID(),
                                           paoId);

        return isFavorite > 0;
    }

    @Override
    public Map<Integer, Boolean> favoritesByPao(
            Iterable<? extends YukonPao> paos, LiteYukonUser user) {

        List<Integer> favoritesFromDB =
            simpleJdbcTemplate.query("SELECT paobjectId FROM paoFavorites" +
                                     " WHERE userId = ?",
                                     new IntegerRowMapper(),
                                     user.getUserID());

        Set<Integer> userFavorites = Sets.newHashSet();
        userFavorites.addAll(favoritesFromDB);

        Map<Integer, Boolean> retVal = Maps.newHashMap();
        for (YukonPao pao : paos) {
            int paoId = pao.getPaoIdentifier().getPaoId();
            retVal.put(paoId, userFavorites.contains(paoId));
        }
        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
