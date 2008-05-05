package com.cannontech.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public strictfp class DurationFormattingServiceImpl implements DurationFormattingService {
    private static final int DAYS = 86400;
    private static final int HOURS = 3600;
    private static final int MINUTES = 60;
    private static final int SECONDS = 0;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public String formatDuration(final long duration, final DurationFormat type,
            final YukonUserContext yukonUserContext) {

        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        Object[] args = getArgs(duration, type);
        
        String result = messageSourceAccessor.getMessage(type, args); 
        return result;
    }
    
    private Object[] getArgs(long durationInSeconds, DurationFormat type) {
        switch (type) {
            case DHMS : {
                Holder daysHolder = getHolder(durationInSeconds, DAYS);
                Holder hoursHolder = getHolder(daysHolder.remainder, HOURS);
                Holder minutesHolder = getHolder(hoursHolder.remainder, MINUTES);
                Holder secondsHolder = getHolder(minutesHolder.remainder, SECONDS);
                return new Object[] { daysHolder.value, hoursHolder.value, minutesHolder.value, secondsHolder.value };
            }

            case HMS : {
                Holder hoursHolder = getHolder(durationInSeconds, HOURS);
                Holder minutesHolder = getHolder(hoursHolder.remainder, MINUTES);
                Holder secondsHolder = getHolder(minutesHolder.remainder, SECONDS);
                return new Object[] { hoursHolder.value, minutesHolder.value, secondsHolder.value };
            }

            case HM : {
                Holder hoursHolder = getHolder(durationInSeconds, HOURS);
                Holder minutesHolder = getHolder(hoursHolder.remainder, MINUTES);
                return new Object[] { hoursHolder.value, minutesHolder.value };
            }

            case H : {
                Holder hoursHolder = getHolder(durationInSeconds, HOURS);
                return new Object[] { hoursHolder.value };
            }
            
            case M : {
                Holder minutesHolder = getHolder(durationInSeconds, MINUTES);
                return new Object[] { minutesHolder.value };
            }
            
            default : throw new UnsupportedOperationException("Unsupported DurationFormat: " + type);
        }
    }
    
    private Holder getHolder(long durationInSeconds, int divider) {
        final Holder holder = new Holder();

        if (divider == SECONDS) {
            holder.value = durationInSeconds;
            holder.remainder = 0;
            return holder;
        }
        
        holder.value = durationInSeconds / divider;
        holder.remainder = durationInSeconds % divider;
        return holder;
    }
    
    private class Holder {
        long value;
        long remainder;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

}
