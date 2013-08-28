package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserSubscriptionDao {

    public boolean contains(UserSubscription subscription);
    public UserSubscription save(UserSubscription subscription);
    public void delete(UserSubscription subscription);
    public List<UserSubscription> getSubscriptionsForUser(LiteYukonUser user);
}
