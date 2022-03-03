package com.cannontech.web.api.route.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.route.RepeaterRoute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepeaterRouteModel implements DBPersistentConverter<RepeaterRoute> {

    private Integer repeaterId;
    private Integer variableBits;
    private Integer repeaterOrder;

    public Integer getRepeaterId() {
        return repeaterId;
    }

    public void setRepeaterId(Integer repeaterId) {
        this.repeaterId = repeaterId;
    }

    public Integer getVariableBits() {
        return variableBits;
    }

    public void setVariableBits(Integer variableBits) {
        this.variableBits = variableBits;
    }

    public Integer getRepeaterOrder() {
        return repeaterOrder;
    }

    public void setRepeaterOrder(Integer repeaterOrder) {
        this.repeaterOrder = repeaterOrder;
    }

    @Override
    public void buildModel(RepeaterRoute repeaterRoute) {
        setRepeaterId(repeaterRoute.getDeviceID());
        setVariableBits(repeaterRoute.getVariableBits());
        setRepeaterOrder(repeaterRoute.getRepeaterOrder());
    }

    @Override
    public void buildDBPersistent(RepeaterRoute repeaterRoute) {

        if (getRepeaterId() != null) {
            repeaterRoute.setDeviceID(getRepeaterId());
        }
        if (getVariableBits() != null) {
            repeaterRoute.setVariableBits(getVariableBits());
        }
        if (getRepeaterOrder() != null) {
            repeaterRoute.setRepeaterOrder(getRepeaterOrder());
        }
    }
}
