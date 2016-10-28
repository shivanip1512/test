package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.model.RfnDevice;

public class Neighbor extends MappingInfo{
    
    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.neighborFlagType.";

    private NeighborData data;
    private String commaDelimitedNeighborFlags;

    public Neighbor(RfnDevice device, FeatureCollection location, NeighborData data, MessageSourceAccessor accessor) {
        super(device, location, accessor);
        this.data = data;
        List<String> flags = new ArrayList<>();
        if (data.getNeighborFlags() != null && !data.getNeighborFlags().isEmpty()) {
            data.getNeighborFlags().forEach(flag -> flags.add(accessor.getMessage(nameKey + flag.name())));
            commaDelimitedNeighborFlags = String.join(", ", flags);
        }
    }

    public NeighborData getData() {
        return data;
    }

    public String getCommaDelimitedNeighborFlags() {
        return commaDelimitedNeighborFlags;
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
