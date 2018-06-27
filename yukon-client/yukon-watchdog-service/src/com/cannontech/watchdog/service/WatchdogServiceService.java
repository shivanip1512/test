package com.cannontech.watchdog.service;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;

public interface WatchdogServiceService {

    /**
     * Return list of user subscribed for smart notification.
     * If no user is subscribed it throws NotFoundException
     */
    List<String> getSubscribedUsersEmailId() throws NotFoundException;

}
