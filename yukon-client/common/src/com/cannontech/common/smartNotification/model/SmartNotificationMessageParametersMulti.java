package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Contains one to many related SmartNotificationMessageParameters objects.
 */
public class SmartNotificationMessageParametersMulti implements Serializable {
    private final static long serialVersionUID = 1L;
    private boolean sendAllInOneEmail;
    private final List<SmartNotificationMessageParameters> messageParameters;
    private final int intervalMinutes;

    public SmartNotificationMessageParametersMulti(List<SmartNotificationMessageParameters> messageParameters,
            int intervalMinutes, boolean sendAllInOneEmail) {
        
        if (messageParameters == null | messageParameters.size() == 0) {
            throw new IllegalStateException("At least one message parameters object must be included.");
        }
        
        this.messageParameters = new ArrayList<>(messageParameters);
        this.intervalMinutes = intervalMinutes;
        this.sendAllInOneEmail = sendAllInOneEmail;
    }

    /**
     * Indicates that all the contained message parameters should be combined into a single digest.
     */
    public boolean isSendAllInOneEmail() {
        return sendAllInOneEmail;
    }

    /**
     * The number of minutes this collection of events was coalesced from.
     */
    public int getIntervalMinutes() {
        return intervalMinutes;
    }
    
    public List<SmartNotificationMessageParameters> getMessageParameters() {
        return messageParameters;
    }
    
    /**
     * @return The media specified by the first message parameter.
     */
    public SmartNotificationMedia getMedia() {
        return messageParameters.get(0).getMedia();
    }
    
    /**
     * 
     */
    public int getTotalEvents() {
        return messageParameters.stream()
                                .mapToInt(parameters -> parameters.getEvents().size())
                                .sum();
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("sendAllInOneEmail", sendAllInOneEmail);
        tsb.append("intervalMinutes", intervalMinutes);
        tsb.append("messageParameters", messageParameters);
        return tsb.toString();
    }
}
