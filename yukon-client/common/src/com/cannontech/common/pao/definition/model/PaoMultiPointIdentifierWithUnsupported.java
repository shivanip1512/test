package com.cannontech.common.pao.definition.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;

/**
 * This class represents a Set of points which all pertain to the same PAO.
 */
public class PaoMultiPointIdentifierWithUnsupported {

    private List<PaoMultiPointIdentifier> devicesAndPoints = new ArrayList<>();
    private Map<YukonPao, Set<Attribute>> unsupportedDevices = new HashMap<>();

    public void add(PaoMultiPointIdentifier id) {
        devicesAndPoints.add(id);
    }

    public void addUnsupportedDevice(YukonPao pao, Attribute attribute) {
        Set<Attribute> set = unsupportedDevices.get(pao);
        if (set == null) {
            set = new HashSet<>();
            unsupportedDevices.put(pao, set);
        }

        set.add(attribute);
    }

    public List<PaoMultiPointIdentifier> getDevicesAndPoints() {
        return devicesAndPoints;
    }

    public Map<YukonPao, Set<Attribute>> getUnsupportedDevices() {
        return unsupportedDevices;
    }
}
