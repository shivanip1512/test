package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Vector;

public class EnergyExchangeCustomer extends CiCustomerBase {

    private Vector<EnergyExchangeCustomerReply> energyExchangeCustomerReplies = null;

    public java.util.Vector<EnergyExchangeCustomerReply> getEnergyExchangeCustomerReplies() {
        return energyExchangeCustomerReplies;
    }

    public void setEnergyExchangeCustomerReplies(Vector<EnergyExchangeCustomerReply> newEnergyExchangeCustomerReplies) {
        energyExchangeCustomerReplies = newEnergyExchangeCustomerReplies;
    }
}
