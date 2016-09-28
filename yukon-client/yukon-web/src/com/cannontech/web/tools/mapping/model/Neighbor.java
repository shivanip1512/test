package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.network.NeighborData;

public class Neighbor {
    
    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.neighborFlagType.";

    private int deviceId;
    private FeatureCollection location;
    private NeighborData data;
    private String commaDelimitedNeighborFlags;

    public Neighbor(int deviceId, FeatureCollection location, NeighborData data, MessageSourceAccessor accessor) {
        this.deviceId = deviceId;
        this.location = location;
        this.data = data;
        List<String> flags = new ArrayList<>();
        data.getNeighborFlags().forEach(flag -> flags.add(accessor.getMessage(nameKey + flag.name())));
        commaDelimitedNeighborFlags = String.join(", ", flags);
    }

    public FeatureCollection getLocation() {
        return location;
    }

    public NeighborData getData() {
        return data;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getCommaDelimitedNeighborFlags() {
        return commaDelimitedNeighborFlags;
    }
}
