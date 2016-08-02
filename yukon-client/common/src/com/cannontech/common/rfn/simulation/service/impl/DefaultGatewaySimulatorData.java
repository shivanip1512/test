package com.cannontech.common.rfn.simulation.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.ConnectionType;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.message.gateway.RadioType;
import com.cannontech.common.rfn.message.gateway.SequenceBlock;
import com.google.common.collect.Sets;

/**
 * Holds the default data values for the RFN gateway simulator, and handles the generation of data responses by
 * combining the defaults with any device-specific values available.
 */
public class DefaultGatewaySimulatorData {
    private static final String ipAddress = "123.123.123.123";
    private static final String port = "1234";
    private static final Authentication admin = new Authentication();
    private static final String adminUser = "admin";
    private static final String adminPass = "password";
    private static final Authentication superAdmin = new Authentication();
    private static final String superAdminUser = "superAdmin";
    private static final String superAdminPass = "superPassword";
    private static final Authentication updateServerAdmin = new Authentication();
    private static final String updateServerUser = "updateAdmin";
    private static final String updateServerPass = "updatePassword";
    private static final String updateServerUrl = "http://127.0.0.1:8081/simulatedUpdateServer/latest/";
    private static final ConnectionType connectionType = ConnectionType.TCP_IP;
    private static final ConnectionStatus connectionStatus = ConnectionStatus.CONNECTED;
    private static final LastCommStatus lastCommStatus = LastCommStatus.SUCCESSFUL;
    private static final String upperStackVersion = "1.0";
    private static final String softwareVersion = "2.0";
    private static final String releaseVersion = "3.0";
    private static final String radioVersion = "4.0";
    private static final String hardwareVersion = "5.0";
    private static final Set<ConflictType> versionConflicts = new HashSet<>();
    private static final String collectionSchedule = "0 0 * * * ?";
    private static final short routeColor = 123;
    private static final AppMode mode = AppMode.NORMAL;
    private static final RadioType radioType = RadioType.EKANET_915;
    private static final String radioMacAddress = "01:23:45:67:89:ab";
    private static final int dataSequenceCompletionPercentage = 100;
    
    static {
        admin.setUsername(adminUser);
        admin.setPassword(adminPass);
        superAdmin.setUsername(superAdminUser);
        superAdmin.setPassword(superAdminPass);
        updateServerAdmin.setUsername(updateServerUser);
        updateServerAdmin.setPassword(updateServerPass);
    }
    
    /**
     * Build the data response using any provided custom data for the device, and filling in the remainder with default
     * values.
     */
    public static GatewayDataResponse buildDataResponse(RfnIdentifier rfnId, GatewaySaveData customData) {
        GatewayDataResponse response = new GatewayDataResponse();
        response.setRfnIdentifier(rfnId);
        
        boolean hasCustomData = customData != null;
        
        if (hasCustomData && customData.getAdmin() != null) {
            response.setAdmin(customData.getAdmin());
        } else {
            response.setAdmin(admin);
        }
        if (hasCustomData && customData.getIpAddress() != null) {
            response.setIpAddress(customData.getIpAddress());
        } else {
            response.setIpAddress(ipAddress);
        }
        if (hasCustomData && customData.getSuperAdmin() != null) {
            response.setSuperAdmin(customData.getSuperAdmin());
        } else {
            response.setSuperAdmin(superAdmin);
        }
        if (hasCustomData && customData.getUpdateServerLogin() != null) {
            response.setUpdateServerLogin(customData.getUpdateServerLogin());
        } else {
            response.setUpdateServerLogin(updateServerAdmin);
        }
        if (hasCustomData && customData.getUpdateServerUrl() != null) {
            response.setUpdateServerUrl(customData.getUpdateServerUrl());
        } else {
            response.setUpdateServerUrl(updateServerUrl);
        }
        
        response.setPort(port);
        response.setConnectionType(connectionType);
        
        response.setConnectionStatus(connectionStatus);
        response.setLastCommStatus(lastCommStatus);
        response.setLastCommStatusTimestamp(Instant.now().getMillis());
        
        response.setUpperStackVersion(upperStackVersion);
        response.setSoftwareVersion(softwareVersion);
        response.setReleaseVersion(releaseVersion);
        response.setRadioVersion(radioVersion);
        response.setHardwareVersion(hardwareVersion);
        response.setVersionConflicts(versionConflicts);
        
        response.setCollectionSchedule(collectionSchedule);
        response.setRouteColor(routeColor);
        response.setMode(mode);
        
        Set<Radio> radios = new HashSet<>();
        Radio radio = new Radio();
        radio.setType(radioType);
        radio.setTimestamp(Instant.now().getMillis());
        radio.setMacAddress(radioMacAddress);
        radios.add(radio);
        response.setRadios(radios);
        
        Set<DataSequence> sequences = new HashSet<>();
        for (DataType dataType : DataType.values()) {
            sequences.add(buildDataSequence(dataType));
        }
        response.setSequences(sequences);
        
        response.setCurrentGatewayDataStreamingLoading(50);
        response.setMaxGatewayDataStreamingCapacity(100);
        
        return response;
    }
    
    private static DataSequence buildDataSequence(DataType dataType) {
        DataSequence dataSequence = new DataSequence();
        dataSequence.setType(dataType);
        dataSequence.setCompletionPercentage(dataSequenceCompletionPercentage);
        
        SequenceBlock sequenceBlock = new SequenceBlock();
        long sevenDaysAgoMillis = Instant.now().minus(Duration.standardDays(7)).getMillis();
        sequenceBlock.setStart(sevenDaysAgoMillis);
        sequenceBlock.setEnd(Instant.now().getMillis());
        dataSequence.setBlocks(Sets.newHashSet(sequenceBlock));
        
        return dataSequence;
    }
}
