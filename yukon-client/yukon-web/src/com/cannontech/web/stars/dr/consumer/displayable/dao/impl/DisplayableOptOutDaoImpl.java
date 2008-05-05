package com.cannontech.web.stars.dr.consumer.displayable.dao.impl;

import java.util.Date;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.ScheduledOptOut;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.displayable.dao.AbstractDisplayableDao;
import com.cannontech.web.stars.dr.consumer.displayable.dao.DisplayableOptOutDao;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableOptOut;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableOptOut.DisplayableOptOutType;

public class DisplayableOptOutDaoImpl extends AbstractDisplayableDao implements DisplayableOptOutDao {
    
    @Override
    public DisplayableOptOut getDisplayableOptOut(
            final CustomerAccount customerAccount, final YukonUserContext yukonUserContext) {
        try {
            ScheduledOptOut scheduledOptOut = scheduledOptOutDao.getByCustomerAccount(customerAccount, yukonUserContext);
            if (scheduledOptOut == null) return null;

            Date startDate = scheduledOptOut.getStartDate();
            Date endDate = scheduledOptOut.getEndDate();

            int duration = scheduledOptOut.getDuration();
            DisplayableOptOutType type = (duration > 24) ? 
                    DisplayableOptOutType.FROM : DisplayableOptOutType.FOR;

            DisplayableOptOut displayableOptOut = new DisplayableOptOut(startDate, endDate, type);
            return displayableOptOut;
        } catch (NotFoundException e) {
            return null;
        }
    }

}
