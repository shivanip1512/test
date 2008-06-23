package com.cannontech.web.stars.dr.consumer.displayable.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.dao.AbstractDisplayableDao;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableScheduledOptOutDao;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableScheduledOptOut;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableScheduledOptOut.DisplayableScheduledOptOutType;

@Repository
public class DisplayableScheduledOptOutDaoImpl extends AbstractDisplayableDao implements DisplayableScheduledOptOutDao {
    
    @Override
    public DisplayableScheduledOptOut getLastDisplayableScheduledOptOut(
            final CustomerAccount customerAccount, final YukonUserContext yukonUserContext) {
        try {
            ScheduledOptOut scheduledOptOut = scheduledOptOutDao.getLastScheduledOptOut(customerAccount,
                                                                                        yukonUserContext);
            Date startDate = scheduledOptOut.getStartDate();
            Date endDate = scheduledOptOut.getEndDate();

            int duration = scheduledOptOut.getDurationInHours();
            DisplayableScheduledOptOutType type = (duration > 24) ? 
                    DisplayableScheduledOptOutType.FROM : DisplayableScheduledOptOutType.FOR;

            return new DisplayableScheduledOptOut(startDate, endDate, type);
        } catch (NotFoundException e) {
            return null;
        }
    }

}
