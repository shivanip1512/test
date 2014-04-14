package com.cannontech.notif.voice;

import javax.annotation.PostConstruct;

import com.cannontech.stars.energyCompany.model.EnergyCompany;

public interface DialerFactory {

    @PostConstruct
    public abstract Dialer createDialer(EnergyCompany energyCompany);

}