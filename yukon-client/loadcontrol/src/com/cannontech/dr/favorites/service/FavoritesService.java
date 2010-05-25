package com.cannontech.dr.favorites.service;

import java.util.List;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ControllablePao;

public interface FavoritesService {
    public List<ControllablePao> getRecentlyViewed(LiteYukonUser user, int max,
                                                    UiFilter<ControllablePao> filter);

    public List<ControllablePao> getFavorites(LiteYukonUser user,
                                               UiFilter<ControllablePao> filter);
}
