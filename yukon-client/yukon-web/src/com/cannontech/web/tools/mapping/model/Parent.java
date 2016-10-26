package com.cannontech.web.tools.mapping.model;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.geojson.FeatureCollection;

import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.model.RfnDevice;

public class Parent extends MappingInfo {
    private ParentData data;

    public Parent(RfnDevice device, FeatureCollection location, ParentData data) {
        super(device, location);
        this.data = data;
    }

    public ParentData getData() {
        return data;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append(super.toString());
        builder.append("data", data);
        return builder.toString();
    }
}
