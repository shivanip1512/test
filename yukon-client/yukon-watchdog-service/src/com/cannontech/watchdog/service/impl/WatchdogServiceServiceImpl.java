package com.cannontech.watchdog.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.watchdog.dao.WatchdogDao;
import com.cannontech.watchdog.service.WatchdogServiceService;

public class WatchdogServiceServiceImpl implements WatchdogServiceService {
    @Autowired WatchdogDao watchdogDao;

    @Override
    public List<String> getSubscribedUsersEmailId() throws NotFoundException {
        List<String> emailAddresses = watchdogDao.getSubscribedUsersEmailId();
        if (emailAddresses.isEmpty()) {
            throw new NotFoundException("No user subscribed for notification for watchdog");
        }
        return emailAddresses;
    }
}
