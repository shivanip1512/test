package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Category;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

public interface UserPageDao {

    public static final int MAX_HISTORY = 10;

    public boolean contains(UserPage page);
    public UserPage save(UserPage page);
    public void delete(UserPage page);
    public Multimap<Category, UserPage> getPagesForUser(LiteYukonUser user);
    public List<UserPage> getAllPages();

    public void deletePagesForPao(PaoIdentifier paoIdentifier);
}