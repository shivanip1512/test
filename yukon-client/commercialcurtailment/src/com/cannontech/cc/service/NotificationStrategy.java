package com.cannontech.cc.service;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.CurtailmentChangeBuilder;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface NotificationStrategy extends CICurtailmentStrategy {

    public CurtailmentBuilder createBuilder(Program program, YukonUserContext yukonUserContext);

    public CurtailmentChangeBuilder createChangeBuilder(CurtailmentEvent event);

    public void verifyTimes(CurtailmentBuilder builder) throws EventCreationException;

    public CurtailmentEvent createEvent(final CurtailmentBuilder builder)
        throws EventCreationException;

    public Boolean canEventBeCancelled(CurtailmentEvent event, LiteYukonUser user);

    public Boolean canEventBeAdjusted(CurtailmentEvent event, LiteYukonUser user);

    public Boolean canEventBeChanged(CurtailmentEvent event, LiteYukonUser user);

    public Boolean canEventBeDeleted(CurtailmentEvent event, LiteYukonUser user);

    /**
     * Future use for now < notif time
     * @param event
     * @param user
     */
    @Transactional
    public void changeEvent(CurtailmentChangeBuilder builder, LiteYukonUser user);

    public CurtailmentEvent adjustEvent(final CurtailmentChangeBuilder builder,
                                        final LiteYukonUser user) throws EventModificationException;

    @Transactional
    public void deleteEvent(final CurtailmentEvent event, LiteYukonUser user);

    public void cancelEvent(CurtailmentEvent event, LiteYukonUser user);
}