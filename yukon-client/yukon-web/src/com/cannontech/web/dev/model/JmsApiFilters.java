package com.cannontech.web.dev.model;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.common.util.jms.api.JmsApi;
import com.cannontech.common.util.jms.api.JmsApiCategory;
import com.cannontech.common.util.jms.api.JmsCommunicatingService;
import com.cannontech.common.util.jms.api.JmsCommunicationPattern;

/**
 * Model object for JMS API filter info.
 */
public class JmsApiFilters {
    private JmsApiCategory filterCategory;
    private JmsCommunicationPattern communicationPattern;
    private JmsCommunicatingService sender;
    private JmsCommunicatingService receiver;
    private String queueSearch;
    
    /**
     * Filters the JMS API map based on the selected filter criteria.
     * @return the filtered map
     */
    public Map<JmsApiCategory, List<JmsApi<?,?,?>>> filter(Map<JmsApiCategory, List<JmsApi<?,?,?>>> queueDescriptionsMap) {
        // If filtering by category, remove all but the selected category
        if (filterCategory != null) {
            Map<JmsApiCategory, List<JmsApi<?,?,?>>> singleCategoryMap = new EnumMap<>(JmsApiCategory.class);
            singleCategoryMap.put(filterCategory, queueDescriptionsMap.get(filterCategory));
            queueDescriptionsMap = singleCategoryMap;
        }
        
        for (var apisForCategory : queueDescriptionsMap.values()) {
            Iterator<JmsApi<?,?,?>> apisIterator = apisForCategory.iterator();
            while (apisIterator.hasNext()) {
                JmsApi<?,?,?> api = apisIterator.next();
                if (filterApiByCommPattern(api) ||
                        filterApiBySenders(api) ||
                        filterApiByReceivers(api) ||
                        filterApiByQueueSearch(api)) {
                    
                    apisIterator.remove();
                }
            }
        }
        
        return queueDescriptionsMap;
    }
    
    private boolean filterApiByCommPattern(JmsApi<?,?,?> api) {
        return communicationPattern != null 
            && communicationPattern != api.getPattern();
    }
    
    private boolean filterApiBySenders(JmsApi<?,?,?> api) {
        return sender != null 
            && !api.getSenders().contains(sender);
    }
    
    private boolean filterApiByReceivers(JmsApi<?,?,?> api) {
        return receiver != null 
            && api.getReceivers() != null 
            && !api.getReceivers().contains(receiver);
    }
    
    private boolean filterApiByQueueSearch(JmsApi<?,?,?> api) {
        boolean queueMatch = api.getQueue().getName().contains(queueSearch);
        
        boolean responseQueueMatch = api.getResponseQueue()
                                        .map(queue -> queue.getName().contains(queueSearch))
                                        .orElse(false);
        
        boolean ackQueueMatch = api.getAckQueue()
                                   .map(queue -> queue.getName().contains(queueSearch))
                                   .orElse(false);
        
        return !queueMatch && !responseQueueMatch && !ackQueueMatch;
    }
    
    public JmsApiCategory getCategory() {
        return filterCategory;
    }
    
    public void setCategory(JmsApiCategory category) {
        filterCategory = category;
    }
    
    public JmsCommunicationPattern getCommunicationPattern() {
        return communicationPattern;
    }
    
    public void setCommunicationPattern(JmsCommunicationPattern communicationPattern) {
        this.communicationPattern = communicationPattern;
    }
    
    public JmsCommunicatingService getSender() {
        return sender;
    }
    
    public void setSender(JmsCommunicatingService sender) {
        this.sender = sender;
    }
    
    public JmsCommunicatingService getReceiver() {
        return receiver;
    }
    
    public void setReceiver(JmsCommunicatingService receiver) {
        this.receiver = receiver;
    }
    
    public String getQueueSearch() {
        return queueSearch;
    }
    
    public void setQueueSearch(String queueSearch) {
        this.queueSearch = queueSearch;
    }
    
}
