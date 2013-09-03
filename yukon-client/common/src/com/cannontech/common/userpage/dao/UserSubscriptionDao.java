package com.cannontech.common.userpage.dao;

import java.util.List;

import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserSubscriptionDao {

    /**
     * @return true IFF there is a subscription of that type and id for the user
     */
    public boolean contains(UserSubscription subscription);

    /**
     * @return subscription with id updated to match the database.
     * If an insert is performed, this id is generated.
     */
    public UserSubscription save(UserSubscription subscription);

    /**
     * Removes the page from the database
     */
    public void delete(UserSubscription subscription);

    /**
     * returns a list of Subscriptions of all types for the specified user.
     */
    public List<UserSubscription> getSubscriptionsForUser(LiteYukonUser user);

    /**
     * Deletes subscription to the item of specified type and refId for all users.
     * Intended to be called when item is deleted.
     */
    public void deleteSubscriptionsForItem(SubscriptionType type, Integer refId);
}
