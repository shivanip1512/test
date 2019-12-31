package com.cannontech.multispeak.extension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "DRLoadGroupName")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DRLoadGroupName", propOrder = { "groupName" })
public class DRLoadGroupName {
    @XmlValue
    private String groupName;

    public DRLoadGroupName() {
    }

    public DRLoadGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

}
