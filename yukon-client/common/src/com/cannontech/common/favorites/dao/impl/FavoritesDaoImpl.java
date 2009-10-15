package com.cannontech.common.favorites.dao.impl;


import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
    private Map<Integer, Set<Integer>> favoritesByUser =
        Collections.synchronizedMap(new HashMap<Integer, Set<Integer>>());

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
        favoritesByUser.remove(user.getUserID());
    }

    @Override
    public void removeFavorite(int paoId, LiteYukonUser user) {
        simpleJdbcTemplate.update("DELETE FROM paoFavorites" +
                                  " WHERE userId = ? AND paobjectId = ?",
                                  user.getUserID(), paoId);
        favoritesByUser.remove(user.getUserID());
    }

    @Override
    public boolean isFavorite(int paoId, LiteYukonUser user) {
        Set<Integer> userFavorites = favoritesByUser.get(user.getUserID());

        if (userFavorites == null) {
            List<Integer> favoritesFromDB =
                simpleJdbcTemplate.query("SELECT paobjectId FROM paoFavorites" +
                                         " WHERE userId = ?",
                                         new IntegerRowMapper(),
                                         user.getUserID());

            userFavorites = Sets.newHashSet();
            userFavorites.addAll(favoritesFromDB);
            favoritesByUser.put(user.getUserID(), userFavorites);
        }

        return userFavorites.contains(paoId);
    }

    @Override
    public Map<Integer, Boolean> favoritesByPao(
            Iterable<? extends YukonPao> paos, LiteYukonUser user) {
        Map<Integer, Boolean> retVal = Maps.newHashMap();
        for (YukonPao pao : paos) {
            int paoId = pao.getPaoIdentifier().getPaoId();
            retVal.put(paoId, isFavorite(paoId, user));
        }
        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
