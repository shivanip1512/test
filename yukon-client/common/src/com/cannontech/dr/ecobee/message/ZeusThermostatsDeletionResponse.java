package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZeusThermostatsDeletionResponse {

    @JsonProperty("deleted")
    private Integer deletedThermostatsCount;

    public Integer getDeletedThermostatsCount() {
        return deletedThermostatsCount;
    }

    public void setDeletedThermostatsCount(Integer deletedThermostatsCount) {
        this.deletedThermostatsCount = deletedThermostatsCount;
    }

}
