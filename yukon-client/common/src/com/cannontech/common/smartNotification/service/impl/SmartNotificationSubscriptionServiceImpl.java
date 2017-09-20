package com.cannontech.common.smartNotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.core.dao.YukonUserDao;

public class SmartNotificationSubscriptionServiceImpl implements SmartNotificationSubscriptionService {

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private SmartNotificationEventLogService smartNotificationEventLogService;

    @Transactional
    @Override
    public int saveSubscription(SmartNotificationSubscription subscription) {
        if (subscription.getId() == 0) {
            smartNotificationEventLogService.subscribe(yukonUserDao.getLiteYukonUser(subscription.getUserId()));
        }
        else {
            smartNotificationEventLogService.update(yukonUserDao.getLiteYukonUser(subscription.getUserId()));
        }
        return subscriptionDao.saveSubscription(subscription);
    }

    @Transactional
    @Override
    public void deleteSubscription(int id) {
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        smartNotificationEventLogService.unsubscribe(yukonUserDao.getLiteYukonUser(subscription.getUserId()));
        subscriptionDao.deleteSubscription(id);
    }
}
