package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPageDao {

    public static final int MAX_HISTORY = 10;

    public boolean isFavorite(UserPage page);
    public boolean toggleFavorite(UserPage page);
    public void updateHistory(UserPage page);

    public List<UserPage> getPagesForUser(LiteYukonUser user);

    public void deletePagesForPao(PaoIdentifier paoIdentifier);
}