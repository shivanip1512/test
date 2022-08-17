package com.cannontech.services.smartNotification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class MessageParametersHelper {

    /**
     * Creates smart notification parameters.
     */
    public static List<SmartNotificationMessageParameters> getMessageParameters(SmartNotificationEventType type,
            SetMultimap<SmartNotificationSubscription, SmartNotificationEvent> subscriptionToEvent, int eventPeriodMinutes) {
        List<SmartNotificationMessageParameters> params = new ArrayList<>();
        if(subscriptionToEvent.isEmpty()){
            return params;
        }
        SetMultimap<MediaVerbosity, SmartNotificationSubscription> split = HashMultimap.create();
        subscriptionToEvent.keys().forEach(s -> {
            split.put(new MediaVerbosity(s), s);
        });

        for (MediaVerbosity mv : split.keySet()) {
            Set<SmartNotificationSubscription> subscriptions = split.get(mv);
            List<String> recipients = subscriptions.stream().map(s -> s.getRecipient()).collect(Collectors.toList());
            
            Set<SmartNotificationEvent> events = subscriptions.stream()
                    .map(subscriptionToEvent::get)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            SmartNotificationMessageParameters param = new SmartNotificationMessageParameters(type, mv.media,
                mv.verbosity, recipients, new ArrayList<>(events), eventPeriodMinutes);
            params.add(param);
        }
        return params;
    }
    
    private static final class MediaVerbosity {
        SmartNotificationMedia media;
        SmartNotificationVerbosity verbosity;

        public MediaVerbosity(SmartNotificationSubscription subscription) {
            media = subscription.getMedia();
            verbosity = subscription.getVerbosity();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((media == null) ? 0 : media.hashCode());
            result = prime * result + ((verbosity == null) ? 0 : verbosity.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MediaVerbosity other = (MediaVerbosity) obj;
            if (media != other.media) {
                return false;
            }
            if (verbosity != other.verbosity) {
                return false;
            }
            return true;
        }        
    }
}
