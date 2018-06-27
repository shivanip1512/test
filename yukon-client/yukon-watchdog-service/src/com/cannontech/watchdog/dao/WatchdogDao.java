package com.cannontech.watchdog.dao;

import java.util.List;

/**
 * Any database calls used by watchdog framework will be in here.
 */
public interface WatchdogDao {

    /**
     * Get email id of users who have subscribed for smart notification
     */
    List<String> getSubscribedUsersEmailId();
}
