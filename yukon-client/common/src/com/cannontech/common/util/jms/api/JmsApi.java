package com.cannontech.common.util.jms.api;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a complete JMS messaging "feature", covering the related request and responses for that feature. This is 
 * intended to make all JMS messaging self-documenting.<br><br>
 * 
 * Requires that all APIs have a name, description, communicationPattern, sender, receiver, queue and requestMessage 
 * specified. Additionally, you will need to specify ackQueue, responseQueue, ackMessage and responseMessage if the 
 * communicationPattern involves ack or response. Multiple senders and receivers may also be specified. (For example, 
 * if NM or a Yukon simulator can both receive a particular message.)<br><br>
 * 
 * To define any messaging that is sent over a temp queue, use {@code JmsQueue.TEMP_QUEUE}.
 */
public class JmsApi<Rq extends Serializable,A extends Serializable,Rp extends Serializable> {
    private String name;
    private String description;
    private final JmsCommunicationPattern pattern;
    private final Set<JmsCommunicatingService> senders;
    private final Set<JmsCommunicatingService> receivers;
    private final JmsQueue queue;
    private final Optional<JmsQueue> ackQueue;
    private final Optional<JmsQueue> responseQueue;
    private final Class<Rq> requestMessage;
    private final Optional<Class<A>> ackMessage;
    private final Optional<Class<Rp>> responseMessage;
    
    /**
     * Invoked by the builder.
     */
    private JmsApi(String name,
                   String description,
                   JmsCommunicationPattern pattern, 
                   Set<JmsCommunicatingService> senders,
                   Set<JmsCommunicatingService> receivers,
                   JmsQueue queue,
                   JmsQueue ackQueue,
                   JmsQueue responseQueue,
                   Class<Rq> requestMessage,
                   Class<A> ackMessage,
                   Class<Rp> responseMessage) {
        if (name == null || StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name must be specified");
        }
        this.name = name;
        
        if (description == null || StringUtils.isBlank(description)) {
            throw new IllegalArgumentException(name + ": Description must be specified");
        }
        this.description = description;
        
        if (pattern == null) {
            throw new IllegalArgumentException(name + ": JMS communication pattern cannot be null");
        }
        this.pattern = pattern;
        
        if (senders == null || senders.size() == 0) {
            throw new IllegalArgumentException(name + ": At least one sender must be specified");
        }
        this.senders = senders;
        
        if (receivers == null || receivers.size() == 0) {
            throw new IllegalArgumentException(name + ": At least one receiver must be specified");
        }
        this.receivers = receivers;
        
        if (queue == null) {
            throw new IllegalArgumentException(name + ": Queue cannot be null");
        } else if (StringUtils.isBlank(queue.getName())) {
            throw new IllegalArgumentException(name + ": Queue name cannot be empty or whitespace");
        }
        this.queue = queue;
        
        if (pattern == JmsCommunicationPattern.REQUEST_ACK_RESPONSE && ackQueue == null) {
            throw new IllegalArgumentException(name + ": Ack Queue must be specified for REQUEST_ACK_RESPONSE pattern");
        } else if (ackQueue != null && StringUtils.isBlank(ackQueue.getName())) {
            throw new IllegalArgumentException(name + ": Ack Queue name cannot be empty or whitespace");
        } else {
            this.ackQueue = Optional.ofNullable(ackQueue);
        }
        
        if (pattern != JmsCommunicationPattern.NOTIFICATION && responseQueue == null) {
            throw new IllegalArgumentException(name + ": Response Queue must be specified for REQUEST_RESPONSE or " +
                                               "REQUEST_ACK_RESPONSE pattern");
        } else if (responseQueue != null && StringUtils.isBlank(responseQueue.getName())) {
            throw new IllegalArgumentException(name + ": Response Queue name cannot be empty or whitespace");
        } else {
            this.responseQueue = Optional.ofNullable(responseQueue);
        }
        
        if (requestMessage == null) {
            throw new IllegalArgumentException(name + ": Request message cannot be null");
        }
        this.requestMessage = requestMessage;
        
        if (pattern == JmsCommunicationPattern.REQUEST_ACK_RESPONSE && ackMessage == null) {
            throw new IllegalArgumentException(name + ": Ack Message must be specified for REQUEST_ACK_RESPONSE pattern");
        } else {
            this.ackMessage = Optional.ofNullable(ackMessage);
        }
        
        if (pattern != JmsCommunicationPattern.NOTIFICATION && responseMessage == null) {
            throw new IllegalArgumentException(name + ": Response Message must be specified for REQUEST_RESPONSE or " +
                                               "REQUEST_ACK_RESPONSE pattern");
        } else {
            this.responseMessage = Optional.ofNullable(responseMessage);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public JmsCommunicationPattern getPattern() {
        return pattern;
    }
    
    public Set<JmsCommunicatingService> getSenders() {
        return senders;
    }
    
    public String getSendersString() {
        return senders.stream()
                      .map(JmsCommunicatingService::toString)
                      .collect(Collectors.joining(", "));
    }
    
    public Set<JmsCommunicatingService> getReceivers() {
        return receivers;
    }
    
    public String getReceiversString() {
        return receivers.stream()
                        .map(JmsCommunicatingService::toString)
                        .collect(Collectors.joining(", "));
    }
    
    public JmsQueue getQueue() {
        return queue;
    }
    
    public Optional<JmsQueue> getAckQueue() {
        return ackQueue;
    }
    
    public Optional<JmsQueue> getResponseQueue() {
        return responseQueue;
    }
    
    public Class<Rq> getRequestMessage() {
        return requestMessage;
    }
    
    public Optional<Class<A>> getAckMessage() {
        return ackMessage;
    }
    
    public Optional<Class<Rp>> getResponseMessage() {
        return responseMessage;
    }
    
    public static <Rq extends Serializable,A extends Serializable,Rp extends Serializable> Builder<Rq,A,Rp> builder(Class<Rq> requestClass, Class<A> ackClass, Class<Rp> responseClass) {
        return JmsApi.Builder.<Rq,A,Rp> get();
    }
    
    public static <Rq extends Serializable,A extends Serializable,Rp extends Serializable> Builder<Rq,A,Rp> builder(Class<Rq> requestClass, Class<Rp> responseClass) {
        return JmsApi.Builder.<Rq,A,Rp> get();
    }
    
    public static <Rq extends Serializable,A extends Serializable,Rp extends Serializable> Builder<Rq,A,Rp> builder(Class<Rq> requestClass) {
        return JmsApi.Builder.<Rq,A,Rp> get();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ackMessage == null) ? 0 : ackMessage.hashCode());
        result = prime * result + ((ackQueue == null) ? 0 : ackQueue.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        result = prime * result + ((queue == null) ? 0 : queue.hashCode());
        result = prime * result + ((receivers == null) ? 0 : receivers.hashCode());
        result = prime * result + ((requestMessage == null) ? 0 : requestMessage.hashCode());
        result = prime * result + ((responseMessage == null) ? 0 : responseMessage.hashCode());
        result = prime * result + ((responseQueue == null) ? 0 : responseQueue.hashCode());
        result = prime * result + ((senders == null) ? 0 : senders.hashCode());
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
        JmsApi<?,?,?> other = (JmsApi<?,?,?>) obj;
        if (ackMessage == null) {
            if (other.ackMessage != null) {
                return false;
            }
        } else if (!ackMessage.equals(other.ackMessage)) {
            return false;
        }
        if (ackQueue == null) {
            if (other.ackQueue != null) {
                return false;
            }
        } else if (!ackQueue.equals(other.ackQueue)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (pattern != other.pattern) {
            return false;
        }
        if (queue == null) {
            if (other.queue != null) {
                return false;
            }
        } else if (!queue.equals(other.queue)) {
            return false;
        }
        if (receivers == null) {
            if (other.receivers != null) {
                return false;
            }
        } else if (!receivers.equals(other.receivers)) {
            return false;
        }
        if (requestMessage == null) {
            if (other.requestMessage != null) {
                return false;
            }
        } else if (!requestMessage.equals(other.requestMessage)) {
            return false;
        }
        if (responseMessage == null) {
            if (other.responseMessage != null) {
                return false;
            }
        } else if (!responseMessage.equals(other.responseMessage)) {
            return false;
        }
        if (responseQueue == null) {
            if (other.responseQueue != null) {
                return false;
            }
        } else if (!responseQueue.equals(other.responseQueue)) {
            return false;
        }
        if (senders == null) {
            if (other.senders != null) {
                return false;
            }
        } else if (!senders.equals(other.senders)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }

    public static class Builder<Rq extends Serializable,A extends Serializable,Rp extends Serializable> {
        private String name;
        private String description;
        private JmsCommunicationPattern pattern;
        private Set<JmsCommunicatingService> senders;
        private Set<JmsCommunicatingService> receivers;
        private JmsQueue queue;
        private JmsQueue ackQueue;
        private JmsQueue responseQueue;
        private Class<Rq> requestMessage;
        private Class<A> ackMessage;
        private Class<Rp> responseMessage;
        
        public static <Rq extends Serializable,A extends Serializable,Rp extends Serializable> Builder<Rq,A,Rp> get() {
            return new Builder<>();
        }
        
        public JmsApi<Rq,A,Rp> build() {
            return new JmsApi<>(name, description, pattern, senders, receivers, queue, ackQueue, responseQueue, 
                              requestMessage, ackMessage, responseMessage);
        }
        
        public Builder<Rq,A,Rp> name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder<Rq,A,Rp> description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder<Rq,A,Rp> communicationPattern(JmsCommunicationPattern pattern) {
            this.pattern = pattern;
            return this;
        }
        
        public Builder<Rq,A,Rp> sender(JmsCommunicatingService sender) {
            if (senders == null) {
                senders = new TreeSet<>();
            }
            senders.add(sender);
            return this;
        }
        
        public Builder<Rq,A,Rp> receiver(JmsCommunicatingService receiver) {
            if (receivers == null) {
                receivers = new TreeSet<>();
            }
            receivers.add(receiver);
            return this;
        }
        
        public Builder<Rq,A,Rp> queue(JmsQueue queue) {
            this.queue = queue;
            return this;
        }
        
        public Builder<Rq,A,Rp> ackQueue(JmsQueue ackQueue) {
            this.ackQueue = ackQueue;
            return this;
        }
        
        public Builder<Rq,A,Rp> responseQueue(JmsQueue responseQueue) {
            this.responseQueue = responseQueue;
            return this;
        }
        
        public Builder<Rq,A,Rp> requestMessage(Class<Rq> requestMessage) {
            this.requestMessage = requestMessage;
            return this;
        }
        
        public Builder<Rq,A,Rp> ackMessage(Class<A> ackMessage) {
            this.ackMessage = ackMessage;
            return this;
        }
        
        public Builder<Rq,A,Rp> responseMessage(Class<Rp> responseMessage) {
            this.responseMessage = responseMessage;
            return this;
        }
    }
}
