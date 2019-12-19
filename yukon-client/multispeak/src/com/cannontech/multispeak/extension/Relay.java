package com.cannontech.multispeak.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "Relay")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relay", propOrder = { "relayNumber" })
public class Relay {
    @XmlValue
    private Integer relayNumber;

    public Relay() {
    }

    public Relay(Integer relayNumber) {
        this.relayNumber = relayNumber;
    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

}
