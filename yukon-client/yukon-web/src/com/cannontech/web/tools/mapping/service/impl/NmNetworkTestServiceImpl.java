package com.cannontech.web.tools.mapping.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.message.network.NeighborFlagType;
import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.message.network.RouteData;
import com.cannontech.common.rfn.message.network.RouteFlagType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkTestService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.Lists;

public class NmNetworkTestServiceImpl implements NmNetworkTestService {

    @Autowired private PaoLocationService paoLocationService;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    private Map<RfnIdentifier, NeighborData> meters = new HashMap<>();
    private Map<RfnIdentifier, RouteData> routes = new HashMap<>();

    RfnIdentifier meter1 = new RfnIdentifier("500000", "LGYR", "FocusRXR-SD");
    RfnIdentifier meter2 = new RfnIdentifier("500001", "LGYR", "FocusRXR-SD");
    RfnIdentifier meter3 = new RfnIdentifier("500002", "LGYR", "FocusRXR-SD");
    RfnIdentifier waterMeter1 = new RfnIdentifier("500003", "Eka", "water_node");
    RfnIdentifier waterMeter2 = new RfnIdentifier("500004", "Eka", "water_node");
    
    @PostConstruct
    public void initialize() {

        
        Set<NeighborFlagType> types;
        Set<RouteFlagType> routeTypes;
        
        NeighborData data1 = new NeighborData();
        data1.setEtxBand((short) 3);
        data1.setLastCommTime(new Date().getTime());
        data1.setLinkPower("125 mWatt");
        data1.setLinkRate("4x");
        data1.setNeighborAddress("00:14:08:03:FA:A2");
        data1.setNeighborDataTimestamp(new Date().getTime());
        types = new HashSet<>();
        types.add(NeighborFlagType.BN);
        types.add(NeighborFlagType.IN);
        data1.setNeighborFlags(types);
        data1.setNeighborLinkCost((short) 3.3);
        data1.setNextCommTime(new Date().getTime());
        data1.setNumSamples(1);
        data1.setRfnIdentifier(meter1);
        data1.setSerialNumber("123");

        RouteData routeData1 = new RouteData();
        routeData1.setDestinationAddress("00:14:08:03:FA:A2");
        routeData1.setHopCount(1);
        routeData1.setNextHopAddress("00:14:08:03:FA:A2");
        routeData1.setRfnIdentifier(meter1);
        routeData1.setRouteColor(1);
        routeData1.setRouteDataTimestamp(new Date().getTime());
        routeTypes = new HashSet<>();
        routeTypes.add(RouteFlagType.BR);
        routeTypes.add(RouteFlagType.GC);
        routeData1.setRouteFlags(routeTypes);
        routeData1.setRouteTimeout(new Date().getTime());
        routeData1.setSerialNumber("101");
        routeData1.setTotalCost(2);

        meters.put(meter1, data1);
        routes.put(meter1, routeData1);

        NeighborData data2 = new NeighborData();
        data2.setEtxBand((short) 3);
        data2.setLastCommTime(new Date().getTime());
        data2.setLinkPower("0.5 mWatt");
        data2.setLinkRate("1x");
        data2.setNeighborAddress("11:14:08:03:FA:A2");
        data2.setNeighborDataTimestamp(new Date().getTime());
        types = new HashSet<>();
        types.add(NeighborFlagType.S1);
        types.add(NeighborFlagType.S2);
        data2.setNeighborFlags(types);
        data2.setNeighborLinkCost((short) 10.3);
        data2.setNextCommTime(new Date().getTime());
        data2.setNumSamples(2);
        data2.setRfnIdentifier(meter1);
        data2.setSerialNumber("456");

        RouteData routeData2 = new RouteData();
        routeData2.setDestinationAddress("00:14:08:03:FA:A2");
        routeData2.setHopCount(2);
        routeData2.setNextHopAddress("00:14:08:03:FA:A2");
        routeData2.setRfnIdentifier(meter2);
        routeData2.setRouteColor(2);
        routeData2.setRouteDataTimestamp(new Date().getTime());
        routeTypes = new HashSet<>();
        routeTypes.add(RouteFlagType.BR);
        routeData2.setRouteFlags(routeTypes);
        routeData2.setRouteTimeout(new Date().getTime());
        routeData2.setSerialNumber("102");
        routeData2.setTotalCost(3);

        meters.put(meter2, data2);
        routes.put(meter2, routeData2);

        NeighborData data3 = new NeighborData();
        data3.setEtxBand((short) 3);
        data3.setLastCommTime(new Date().getTime());
        data3.setLinkPower("0.5 mWatt");
        data3.setLinkRate("2x");
        data3.setNeighborAddress("17:14:08:03:FA:A2");
        data3.setNeighborDataTimestamp(new Date().getTime());
        types = new HashSet<>();
        types.add(NeighborFlagType.S2);
        data3.setNeighborFlags(types);
        data3.setNeighborLinkCost((short) 10.3);
        data3.setNextCommTime(new Date().getTime());
        data3.setNumSamples(3);
        data3.setRfnIdentifier(meter1);
        data3.setSerialNumber("789");

        RouteData routeData3 = new RouteData();
        routeData3.setDestinationAddress("00:14:08:03:FA:A2");
        routeData3.setHopCount(2);
        routeData3.setNextHopAddress("00:14:08:03:FA:A2");
        routeData3.setRfnIdentifier(meter2);
        routeData3.setRouteColor(2);
        routeData3.setRouteDataTimestamp(new Date().getTime());
        routeTypes = new HashSet<>();
        routeTypes.add(RouteFlagType.BR);
        routeData3.setRouteFlags(routeTypes);
        routeData3.setRouteTimeout(new Date().getTime());
        routeData3.setSerialNumber("102");
        routeData3.setTotalCost(3);

        meters.put(meter3, data3);
        routes.put(meter3, routeData3);
        
        RouteData routeData4 = new RouteData();
        routeData4.setDestinationAddress("00:14:08:03:FA:A2");
        routeData4.setHopCount(2);
        routeData4.setNextHopAddress("00:14:08:03:FA:A2");
        routeData4.setRfnIdentifier(waterMeter1);
        routeData4.setRouteColor(2);
        routeData4.setRouteDataTimestamp(new Date().getTime());
        routeTypes = new HashSet<>();
        routeTypes.add(RouteFlagType.TO);
        routeData4.setRouteFlags(routeTypes);
        routeData4.setRouteTimeout(new Date().getTime());
        routeData4.setSerialNumber("105");
        routeData4.setTotalCost(3);
        
        routes.put(waterMeter1, routeData4);
        
        RouteData routeData5 = new RouteData();
        routeData5.setDestinationAddress("00:14:08:03:FA:A2");
        routeData5.setHopCount(2);
        routeData5.setNextHopAddress("00:14:08:03:FA:A2");
        routeData5.setRfnIdentifier(waterMeter1);
        routeData5.setRouteColor(2);
        routeData5.setRouteDataTimestamp(new Date().getTime());
        routeTypes = new HashSet<>();
        routeTypes.add(RouteFlagType.TO);
        routeData5.setRouteFlags(routeTypes);
        routeData5.setRouteTimeout(new Date().getTime());
        routeData5.setSerialNumber("105");
        routeData5.setTotalCost(3);
        
        routes.put(waterMeter2, routeData5);
        
    }

    @Override
    public List<Neighbor> getNeighbors(int deviceId, MessageSourceAccessor accessor) {

        List<Neighbor> neighbors = new ArrayList<>();

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);

        Set<RfnIdentifier> identifiers = new HashSet<>();
        identifiers.addAll(meters.keySet());
        identifiers.remove(device.getRfnIdentifier());

        for (RfnIdentifier identifier : identifiers) {
            RfnDevice neighborDevice = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
            FeatureCollection location = paoLocationService.getLocationsAsGeoJson(Lists.newArrayList(neighborDevice));
            Neighbor neighbor =
                new Neighbor(neighborDevice.getPaoIdentifier().getPaoId(), location, meters.get(identifier), accessor);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    @Override
    public Parent getParent(int deviceId) {
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        ParentData data = new ParentData();
        data.setNodeMacAddress("17:14:08:03:FA:A2");
        data.setNodeSN("123");
        
        Parent parent;
        if(device.getRfnIdentifier().equals(waterMeter2)){
            data.setRfnIdentifier(waterMeter1);
            RfnDevice parentDevice = rfnDeviceDao.getDeviceForExactIdentifier(waterMeter1);
            FeatureCollection location = paoLocationService.getLocationsAsGeoJson(Lists.newArrayList(parentDevice));
            parent = new Parent(parentDevice.getPaoIdentifier().getPaoId(), location, data);
        }else{
            data.setRfnIdentifier(waterMeter2);
            RfnDevice parentDevice = rfnDeviceDao.getDeviceForExactIdentifier(waterMeter2);
            FeatureCollection location = paoLocationService.getLocationsAsGeoJson(Lists.newArrayList(parentDevice));
            parent = new Parent(parentDevice.getPaoIdentifier().getPaoId(), location, data);
        }
        return parent;
    }

    @Override
    public List<RouteInfo> getRoute(int deviceId, MessageSourceAccessor accessor) {

        List<RouteInfo> routes = new ArrayList<>();
        
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        Set<RfnIdentifier> identifiers = new HashSet<>();
        identifiers.addAll(meters.keySet());
        identifiers.remove(device.getRfnIdentifier());
        FeatureCollection location = paoLocationService.getLocationsAsGeoJson(Lists.newArrayList(device));
        RouteData data = this.routes.get(device.getRfnIdentifier());
        routes.add(new RouteInfo(deviceId, data, location, accessor));
        for (RfnIdentifier identifier : identifiers) {
            device = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
            location = paoLocationService.getLocationsAsGeoJson(Lists.newArrayList(device));
            data = this.routes.get(device.getRfnIdentifier());
            routes.add(new RouteInfo(device.getPaoIdentifier().getPaoId(), data, location, accessor));
        }
        
        return routes;
    }

}
