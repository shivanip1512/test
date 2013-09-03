package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPageDao {

    public static final int MAX_HISTORY = 10;

    /**
     * @return true IFF a page with that path exists in database for the user and is favorited
     */
    public boolean isFavorite(UserPage page);

    /**
     * Toggles favorite boolean in database for page with path and userId.
     * If the page is not in the database, it is added with favorite=true
     */
    public boolean toggleFavorite(UserPage page);

    /**
     * Adds page to history. Removes all but the MAX_HISTORY most recently viewed non-favorite pages for that page's userId.
     */
    public void updateHistory(UserPage page);

    /**
     * @return All UserPages that have the userId of the user
     */
    public List<UserPage> getPagesForUser(LiteYukonUser user);

    /**
     * Delete all references to pao in page history/favorites for all users. To be called after a Pao is deleted.
     */
    public void deletePagesForPao(PaoIdentifier paoIdentifier);
}