package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPageDao {

    static final int MAX_HISTORY = 10;
    static final int MAX_DR_RECENT_VIEWED = 20;

    /**
     * @return true IFF a page with that path exists in database for the user and is favorited
     */
    boolean isFavorite(UserPage page);

    /**
     * Toggles favorite boolean in database for page with path and userId.
     * If the page is not in the database, it is added with favorite=true
     */
    boolean toggleFavorite(UserPage page);

    /**
     * Adds page to history. Removes all but the MAX_HISTORY most recently viewed non-favorite pages for that page's userId.
     */
    void updateHistory(UserPage page);

    /**
     * @return All UserPages that have the userId of the user, sorted chronologically with most recent elements first.
     */
    List<UserPage> getPagesForUser(LiteYukonUser user);

    /**
     * Delete all references to pao in page history/favorites for all users. To be called after a Pao is deleted.
     */
    void deletePagesForPao(PaoIdentifier paoIdentifier);

    /**
     * Update all references to pao in page history/favorites for all users to use the new PAO name. To be called after a Pao is updated.
     */
    void updatePagesForPao(PaoIdentifier paoIdentifier, String paoName);

    /**
     * @return List of all favorite DR items (Areas, Programs, Groups, Scenarios) for use on the DR Dashboard 
     */
    List<DisplayablePao> getDrFavorites(LiteYukonUser user);

    /**
     * @return List of all recent DR items (Areas, Programs, Groups, Scenarios) for use on the DR Dashboard 
     */
    List<DisplayablePao> getDrRecentViewed(LiteYukonUser user);

}