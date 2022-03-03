package com.cannontech.web.api.route.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.route.CarrierRoute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class CarrierRouteModel implements DBPersistentConverter<CarrierRoute> {

    private Integer busNumber;
    private Integer ccuFixBits;
    private Integer ccuVariableBits;
    private boolean userLocked;
    private boolean resetRptSettings;

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

    public Integer getCcuFixBits() {
        return ccuFixBits;
    }

    public void setCcuFixBits(Integer ccuFixBits) {
        this.ccuFixBits = ccuFixBits;
    }

    public Integer getCcuVariableBits() {
        return ccuVariableBits;
    }

    public void setCcuVariableBits(Integer ccuVariableBits) {
        this.ccuVariableBits = ccuVariableBits;
    }

    public boolean getUserLocked() {
        return userLocked;
    }

    public void setUserLocked(boolean userLocked) {
        this.userLocked = userLocked;
    }

    public boolean getResetRptSettings() {
        return resetRptSettings;
    }

    public void setResetRptSettings(boolean resetRptSettings) {
        this.resetRptSettings = resetRptSettings;
    }

    @Override
    public void buildModel(CarrierRoute carrierRoute) {
        setBusNumber(carrierRoute.getBusNumber());
        setCcuFixBits(carrierRoute.getCcuFixBits());
        setCcuVariableBits(carrierRoute.getCcuVariableBits());
        setResetRptSettings((carrierRoute.getResetRptSettings().equals("N")) ? false : true);
        setUserLocked((carrierRoute.getUserLocked().equals("N")) ? false : true);
    }

    @Override
    public void buildDBPersistent(CarrierRoute carrierRoute) {

        if (getBusNumber() != null) {
            carrierRoute.setBusNumber(getBusNumber());
        }

        if (getCcuFixBits() != null) {
            carrierRoute.setCcuFixBits(getCcuFixBits());
        }

        if (getCcuVariableBits() != null) {
            carrierRoute.setCcuVariableBits(getCcuVariableBits());
        }

        if (getResetRptSettings()) {
            carrierRoute.setResetRptSettings((getResetRptSettings() == false) ? "N" : "T");
        }

        if (getUserLocked()) {
            carrierRoute.setUserLocked((getUserLocked() == false) ? "N" : "T");
        }
    }
}
