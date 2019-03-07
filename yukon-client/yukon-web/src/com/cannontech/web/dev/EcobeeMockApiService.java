package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;

public class EcobeeMockApiService {
    private static final Duration fiveMinutes = Duration.standardMinutes(5);
    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    

    public HierarchyResponse getHierarchyList() {
        List<SetNode> setNodes = new ArrayList<SetNode>();
        long thermostat = 222222;
        String node = "Node";
        for (int i = 0; i < 300; i++, thermostat++) {
            List<String> thermostats = new ArrayList<String>();
            thermostats.add(new Long(thermostat).toString());
            SetNode setNode = new SetNode(node + i, "\\data", null, thermostats);
            setNodes.add(setNode);
        }

        HierarchyResponse hierarchyResponse = new HierarchyResponse(setNodes, new Status(ecobeeDataConfiguration.getHierarchy(), "Hierarchy Tested"));
        return hierarchyResponse;
    }

}
