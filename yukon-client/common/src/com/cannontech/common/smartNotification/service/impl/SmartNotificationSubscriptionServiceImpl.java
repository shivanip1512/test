package com.cannontech.common.smartNotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;

public class SmartNotificationSubscriptionServiceImpl implements SmartNotificationSubscriptionService {

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    
    @Override
    public int saveSubscription(SmartNotificationSubscription subscription) {
        //event log
        return subscriptionDao.saveSubscription(subscription);
    }

    @Override
    public void deleteSubscription(int id) {
        //event log
        subscriptionDao.deleteSubscription(id);
    }
}
