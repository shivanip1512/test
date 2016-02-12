package com.cannontech.dr.rfn.service;

import java.util.List;

import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper.PointMapper;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnDevice;
import com.google.common.collect.Multimap;

/**
 * This is a service that simulates a network of RFN Meters for use in
 * performance testing. The primary goal of the simulator is to simulate the
 * message as if they are coming from actual Network Manager. This simulator
 * does not require Network Manager to be installed, but it creates messages and
 * places them on the same queue as network manager, simulating the connection
 * between Yukon and NM.
 */
public interface RfnMeterDataSimulatorService {

    /**
     * Starts the RFN Meter data simulator.
     * @param rfnDeviceList - List of RFN devices for which messages have to be
     *            send.
     * @param pointMapperMap - Point Mapping Map to identify the point Mapping
     *            for a paoType.
     */
    void sendRfnMeterMessages(List<RfnDevice> rfnDeviceList,
            Multimap<PaoType, PointMapper> pointMapperMap);

    /**
     * Stops the RFN Meter data simulator.
     */
    void stopSimulator();

}
