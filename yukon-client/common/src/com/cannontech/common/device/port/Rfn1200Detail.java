package com.cannontech.common.device.port;

import com.cannontech.database.db.device.RfnAddress;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class Rfn1200Detail extends PortBase {

    private PortTiming timing;
    private RfnAddress rfnAddress;

    public PortTiming getTiming() {
        if (timing == null) {
            timing = new PortTiming();
        }
        return timing;
    }

    public void setTiming(PortTiming timing) {
        this.timing = timing;
    }
    

    public RfnAddress getRfnAddress() {
        return rfnAddress;
    }

    public void setRfnAddress(RfnAddress rfnAddress) {
        this.rfnAddress = rfnAddress;
    }

}