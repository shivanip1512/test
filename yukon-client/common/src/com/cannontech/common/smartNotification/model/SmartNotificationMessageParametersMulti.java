package com.cannontech.common.smartNotification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Level;

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
        ToStringBuilder tsb = getLogMsg();
        tsb.append("messageParameters", messageParameters);
        return tsb.toString();
    }

    private ToStringBuilder getLogMsg() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("sendAllInOneEmail", sendAllInOneEmail);
        tsb.append("intervalMinutes", intervalMinutes);
        return tsb;
    }
    
    public String loggingString(Level level) {
        ToStringBuilder tsb = getLogMsg();
        tsb.append("messageParameters", messageParameters.stream()
                .map(p -> p.loggingString(level))
                .collect(Collectors.joining(",")));
        return tsb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + intervalMinutes;
        result = prime * result + ((messageParameters == null) ? 0 : messageParameters.hashCode());
        result = prime * result + (sendAllInOneEmail ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SmartNotificationMessageParametersMulti other = (SmartNotificationMessageParametersMulti) obj;
        if (intervalMinutes != other.intervalMinutes)
            return false;
        if (messageParameters == null) {
            if (other.messageParameters != null)
                return false;
        } else if (!messageParameters.equals(other.messageParameters))
            return false;
        if (sendAllInOneEmail != other.sendAllInOneEmail)
            return false;
        return true;
    }
}
