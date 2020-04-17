package com.cannontech.services.systemDataPublisher.yaml.model;

import java.io.Serializable;
import java.util.List;

public class CloudDataConfigurations implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CloudDataConfiguration> configurations;

    public List<CloudDataConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<CloudDataConfiguration> configurations) {
        this.configurations = configurations;
    }

}
