package com.cannontech.notif.voice;

import javax.annotation.PostConstruct;

import com.cannontech.database.data.lite.LiteEnergyCompany;

public interface DialerFactory {

    @PostConstruct
    public abstract Dialer createDialer(LiteEnergyCompany energyCompany);

}