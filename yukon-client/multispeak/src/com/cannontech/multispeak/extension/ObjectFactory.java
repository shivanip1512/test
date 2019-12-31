package com.cannontech.multispeak.extension;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public ObjectFactory() {
    }

    public Relay createRelay() {
        return new Relay();
    }

    public DRLoadGroupName createDRLoadGroupName() {
        return new DRLoadGroupName();
    }

}
