package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.model.RfnDevice;

public class Neighbor {
    
    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.neighborFlagType.";

    private RfnDevice device;
    private FeatureCollection location;
    private NeighborData data;
    private String commaDelimitedNeighborFlags;

    public Neighbor(RfnDevice device, FeatureCollection location, NeighborData data, MessageSourceAccessor accessor) {
        this.device = device;
        this.location = location;
        this.data = data;
        List<String> flags = new ArrayList<>();
        if (data.getNeighborFlags() != null && !data.getNeighborFlags().isEmpty()) {
            data.getNeighborFlags().forEach(flag -> flags.add(accessor.getMessage(nameKey + flag.name())));
            commaDelimitedNeighborFlags = String.join(", ", flags);
        }
    }

    public FeatureCollection getLocation() {
        return location;
    }

    public NeighborData getData() {
        return data;
    }

    public RfnDevice getDevice() {
        return device;
    }

    public String getCommaDelimitedNeighborFlags() {
        return commaDelimitedNeighborFlags;
    }
}
