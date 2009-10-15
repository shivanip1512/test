package com.cannontech.common.favorites.service;

import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface FavoritesService {
    public List<DisplayablePao> getRecentlyViewed(LiteYukonUser user, int max,
                                                  UiFilter<DisplayablePao> filter);

    public List<DisplayablePao> getFavorites(LiteYukonUser user,
            UiFilter<DisplayablePao> filter);
}
