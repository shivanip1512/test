package com.cannontech.common.smartNotification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class SmartNotificationSubscriptionServiceImpl implements SmartNotificationSubscriptionService {

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private SmartNotificationEventLogService smartNotificationEventLogService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Transactional
    @Override
    public int saveSubscription(SmartNotificationSubscription subscription, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        if (subscription.getId() == 0) {

            smartNotificationEventLogService.subscribe(userContext.getYukonUser(),
                                                       messageSourceAccessor.getMessage(subscription.getFrequency().getFormatKey()),
                                                       messageSourceAccessor.getMessage(subscription.getMedia().getFormatKey()),
                                                       messageSourceAccessor.getMessage(subscription.getType().getFormatKey())
                                                       );
        }
        else {
            smartNotificationEventLogService.update(userContext.getYukonUser(),
                                                    messageSourceAccessor.getMessage(subscription.getFrequency().getFormatKey()),
                                                    messageSourceAccessor.getMessage(subscription.getMedia().getFormatKey()),
                                                    messageSourceAccessor.getMessage(subscription.getType().getFormatKey())
                                                    );
        }
        return subscriptionDao.saveSubscription(subscription);
    }

    @Transactional
    @Override
    public void deleteSubscription(int id, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        SmartNotificationSubscription subscription = subscriptionDao.getSubscription(id);
        smartNotificationEventLogService.unsubscribe(userContext.getYukonUser(),
                                                     messageSourceAccessor.getMessage(subscription.getType().getFormatKey()));
        subscriptionDao.deleteSubscription(id);
    }
}
