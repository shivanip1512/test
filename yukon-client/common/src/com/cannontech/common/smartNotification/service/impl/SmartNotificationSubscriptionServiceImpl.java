package com.cannontech.common.smartNotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class SmartNotificationSubscriptionServiceImpl implements SmartNotificationSubscriptionService {

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private SmartNotificationEventLogService smartNotificationEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Transactional
    @Override
    public int saveSubscription(SmartNotificationSubscription subscription) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        if (subscription.getId() == 0) {

            smartNotificationEventLogService.subscribe(yukonUserDao.getLiteYukonUser(subscription.getUserId()),
                                                       messageSourceAccessor.getMessage(subscription.getFrequency().getFormatKey()),
                                                       messageSourceAccessor.getMessage(subscription.getMedia().getFormatKey()),
                                                       messageSourceAccessor.getMessage(subscription.getType().getFormatKey())
                                                       );
        }
        else {
            smartNotificationEventLogService.update(yukonUserDao.getLiteYukonUser(subscription.getUserId()),
                                                    messageSourceAccessor.getMessage(subscription.getFrequency().getFormatKey()),
                                                    messageSourceAccessor.getMessage(subscription.getMedia().getFormatKey()),
                                                    messageSourceAccessor.getMessage(subscription.getType().getFormatKey())
                                                    );
        }
        return subscriptionDao.saveSubscription(subscription);
    }

    @Transactional
    @Override
    public void deleteSubscription(int id) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        smartNotificationEventLogService.unsubscribe(yukonUserDao.getLiteYukonUser(subscription.getUserId()),
                                                     messageSourceAccessor.getMessage(subscription.getType().getFormatKey()));
        subscriptionDao.deleteSubscription(id);
    }
}
