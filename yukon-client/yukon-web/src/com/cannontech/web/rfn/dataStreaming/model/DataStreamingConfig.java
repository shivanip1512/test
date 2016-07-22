package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.i18n.MessageSourceAccessor;

public class DataStreamingConfig {
    
    private static final String nameKey= "yukon.web.modules.tools.bulk.dataStreaming.configuration.name";
    
    private int id;
    private boolean newConfiguration;
    private int selectedInterval;
    private int selectedConfiguration;
    private String name;
    private String commaDelimitedAttributes;
    private List<DataStreamingAttribute> attributes = new ArrayList<>();
    private MessageSourceAccessor accessor;
    
    public boolean isNewConfiguration() {
        return newConfiguration;
    }

    public void setNewConfiguration(boolean newConfiguration) {
        this.newConfiguration = newConfiguration;
    }

    public int getSelectedInterval() {
        return selectedInterval;
    }

    public void setSelectedInterval(int selectedInterval) {
        this.selectedInterval = selectedInterval;
    }

    public int getSelectedConfiguration() {
        return selectedConfiguration;
    }

    public void setSelectedConfiguration(int selectedConfiguration) {
        this.selectedConfiguration = selectedConfiguration;
    }
    
    /**
     * Returns name
     * 
     * kVar - 10 minutes
     * kVar, Demand - 10 minutes
     * kVar - 20 minutes
     * Demand - 30 minutes
     * kVar, Demand and 2 others - 10 minutes
     * 
     * 
     * If there are 1 or 2 attributes list the attributes (comma separated) followed by � and interval
     * If there are more than 2 attributes, display the first 2 attributes followed by �and # others� followed by �
     * and interval
     * 
     */
    public String getName() {
        if (accessor != null && attributes.size() > 0) {
            String attrName = accessor.getMessage(attributes.get(0).getAttribute());
            int interval = attributes.get(0).getInterval();
            if (attributes.size() == 1) {
                name = accessor.getMessage(nameKey, attrName, 0, interval);
            } else {
                String attrName2 = accessor.getMessage(attributes.get(1).getAttribute());
                name = accessor.getMessage(nameKey, attrName + ", " + attrName2, attributes.size() - 2, interval);
            }
        }
        return name;
    }
    
    /**
     * Returns comma delimited list of attributes
     */
    public String getCommaDelimitedAttributes() {
        List<String> attList = new ArrayList<String>();
        attributes.forEach(att -> attList.add(accessor.getMessage(att.getAttribute())));
        commaDelimitedAttributes = String.join(", ",  attList);
        return commaDelimitedAttributes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DataStreamingAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DataStreamingAttribute> attributes) {
        this.attributes = attributes;
    }
    
    public void addAttribute(DataStreamingAttribute attribute) {
        attributes.add(attribute);
    }
    
    public void setAccessor(MessageSourceAccessor accessor) {
        this.accessor = accessor;
    }
}
