package com.cannontech.common.smartNotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;

public class SmartNotificationSubscriptionServiceImpl implements SmartNotificationSubscriptionService {

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    
    @Transactional
    @Override
    public int saveSubscription(SmartNotificationSubscription subscription) {
        //event log
        return subscriptionDao.saveSubscription(subscription);
    }

    @Transactional
    @Override
    public void deleteSubscription(int id) {
        //event log
        subscriptionDao.deleteSubscription(id);
    }
}
