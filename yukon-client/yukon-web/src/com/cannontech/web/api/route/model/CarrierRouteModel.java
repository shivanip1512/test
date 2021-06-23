package com.cannontech.web.api.route.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.route.CarrierRoute;

public class CarrierRouteModel implements DBPersistentConverter<CarrierRoute> {

    private Integer busNumber;
    private Integer ccuFixBits;
    private Integer ccuVariableBits;
    private String userLocked;
    private String resetRptSettings;

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

    public String getUserLocked() {
        return userLocked;
    }

    public void setUserLocked(String userLocked) {
        this.userLocked = userLocked;
    }

    public String getResetRptSettings() {
        return resetRptSettings;
    }

    public void setResetRptSettings(String resetRptSettings) {
        this.resetRptSettings = resetRptSettings;
    }

    @Override
    public void buildModel(CarrierRoute carrierRoute) {
        setBusNumber(carrierRoute.getBusNumber());
        setCcuFixBits(carrierRoute.getCcuFixBits());
        setCcuVariableBits(carrierRoute.getCcuVariableBits());
        setResetRptSettings(carrierRoute.getResetRptSettings());
        setUserLocked(carrierRoute.getUserLocked());
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
            ;
        }

        if (getResetRptSettings() != null) {
            carrierRoute.setResetRptSettings(getResetRptSettings());
        }

        if (getUserLocked() != null) {
            carrierRoute.setUserLocked(getUserLocked());
        }
    }
}
