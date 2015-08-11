package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Key;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPageDao {
    int MAX_HISTORY = 10;
    int MAX_DR_RECENT_VIEWED = 20;

    /**
     * @return true IFF a page with that path exists in database for the user and is a favorite.
     */
    boolean isFavorite(Key pageKey);

    /**
     * Toggles favorite boolean in database for page with path and userId.
     * If the page is not in the database, it is added with favorite=true
     */
    boolean toggleFavorite(Key pageKey, SiteModule module, String name, List<String> arguments);

    /**
     * Adds page to history. Removes all but the MAX_HISTORY most recently viewed non-favorite pages for that
     * page's userId.
     */
    void updateHistory(Key pageKey, SiteModule module, String name, List<String> arguments);

    /**
     * @return All UserPages that have the userId of the user, sorted chronologically with most recent elements first.
     */
    List<UserPage> getPagesForUser(LiteYukonUser user);

    /**
     * Delete all references to pao in page history/favorites for all users. To be called after a Pao is deleted.
     */
    void deletePagesForPao(YukonPao pao);

    /**
     * Update all references to pao in page history/favorites for all users to use the new PAO name. To be
     * called after a Pao is updated.
     */
    void updatePagesForPao(YukonPao pao, String paoName);

    /**
     * @return List of all favorite DR items (Areas, Programs, Groups, Scenarios) for use on the DR dashboard 
     */
    List<DisplayablePao> getDrFavorites(LiteYukonUser user);

    /**
     * @return List of all recent DR items (Areas, Programs, Groups, Scenarios) for use on the DR dashboard 
     */
    List<DisplayablePao> getDrRecentViewed(LiteYukonUser user);

    void deletePagesForPoint(int pointId);
}
