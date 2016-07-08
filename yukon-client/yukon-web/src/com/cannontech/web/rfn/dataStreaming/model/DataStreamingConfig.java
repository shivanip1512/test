package com.cannontech.web.rfn.dataStreaming.model;

import java.util.ArrayList;
import java.util.List;

public class DataStreamingConfig {
    private int id;
    private boolean newConfiguration;
    private int selectedInterval;
    private int selectedConfiguration;
    private String name;
    private List<DataStreamingAttribute> attributes = new ArrayList<DataStreamingAttribute>();

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

    public String getName() {
        StringBuffer configName = new StringBuffer();
        int interval = 0;
        int count = 1;
        for (DataStreamingAttribute att : getAttributes()) {
            if (count > 2) {
                int totalCount = getAttributes().size();
                int countLeft = totalCount - count + 1;
                configName.append(" and " + countLeft + " other");
                if (countLeft > 1) {
                    configName.append("s");
                }
                break;
            }
            if (count > 1) {
                configName.append(", ");
            }
            configName.append(att.getName());
            
            interval = att.getInterval();
            count++;
        }
        configName.append(" - " + interval + " minute");
        if (interval > 1) {
            configName.append("s");
        }
        name = configName.toString();
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
