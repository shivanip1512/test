package com.cannontech.common.rfn.simulation.service.impl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.message.gateway.ConflictType;
import com.cannontech.common.rfn.message.gateway.ConnectionType;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.GatewaySaveData;
import com.cannontech.common.rfn.message.gateway.LastCommStatus;
import com.cannontech.common.rfn.message.gateway.Radio;
import com.cannontech.common.rfn.message.gateway.RadioType;
import com.cannontech.common.rfn.message.gateway.SequenceBlock;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
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
    private static final LastCommStatus lastCommStatus = LastCommStatus.SUCCESSFUL;
    private static final String upperStackVersion = "1.0";
    private static final String softwareVersion = "2.0";
    private static final String releaseVersion = "9.0.0";
    private static final String radioVersion = "4.0";
    private static final String hardwareVersion = "5.0";
    private static final Set<ConflictType> versionConflicts = new HashSet<>();
    private static final String collectionSchedule = "0 0 * * * ?";
    private static final AppMode mode = AppMode.NORMAL;
    private static final RadioType radioType = RadioType.EKANET_915;
    private static final String radioMacAddress = "01:23:45:67:89:ab";
    private static final String commRadioVersion = "V_10_10";
    private static final int dataSequenceCompletionPercentage = 100;
    public static final double maxDataStreamingLoading = 100;
    private static final String ipv6Prefix = null;
    private static final int gwTotalNotReadyNodes = 500;
    private static final int gwTotalReadyNodes = 1000;
      
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
    public static GatewayDataResponse buildDataResponse(RfnIdentifier rfnId, GatewaySaveData customData, 
            SimulatedGatewayDataSettings settings) {
        
        GatewayDataResponse response = new GatewayDataResponse();
        response.setRfnIdentifier(rfnId);
        
        response.setIpv6Prefix(ipv6Prefix);

        response.setAdmin(customData.getAdmin());
        response.setIpAddress(customData.getIpAddress());
        response.setSuperAdmin(customData.getSuperAdmin());
        response.setUpdateServerLogin(customData.getUpdateServerLogin());
        response.setUpdateServerUrl(customData.getUpdateServerUrl());
        
        response.setPort(port);
        response.setConnectionType(connectionType);
        
        response.setConnectionStatus(settings.getConnectionStatus()); 

        response.setLastCommStatus(lastCommStatus);
        response.setLastCommStatusTimestamp(Instant.now().getMillis());
        
        response.setUpperStackVersion(upperStackVersion);
        response.setSoftwareVersion(softwareVersion);
        response.setReleaseVersion(releaseVersion);
        response.setRadioVersion(radioVersion);
        response.setHardwareVersion(hardwareVersion);
        response.setVersionConflicts(versionConflicts);
        
        response.setCollectionSchedule(collectionSchedule);
        Random rand = new Random(); 
        response.setRouteColor((short) rand.nextInt(999));
        Set<Radio> radios = new HashSet<>();
        Radio radio = new Radio();
        radio.setType(radioType);
        radio.setTimestamp(Instant.now().getMillis());
        radio.setMacAddress(radioMacAddress);
        radio.setVersion(commRadioVersion);
        radios.add(radio);
        response.setRadios(radios);
        
        Set<DataSequence> sequences = new HashSet<>();
        for (DataType dataType : DataType.values()) {
            sequences.add(buildDataSequence(dataType));
        }
        response.setSequences(sequences);
        
        response.setCurrentDataStreamingLoading(settings.getCurrentDataStreamingLoading());
        response.setMaxDataStreamingCapacity(maxDataStreamingLoading);
        
        //"Ready nodes" and "not ready nodes" are configurable.
        //Currently, all other values are derived from these two.
        int totalNodes = 0;
        if (settings.getNumberOfNotReadyNodes() == null) {
            response.setGwTotalNotReadyNodes(gwTotalNotReadyNodes);
            totalNodes += gwTotalNotReadyNodes;
        } else {
            response.setGwTotalNotReadyNodes(settings.getNumberOfNotReadyNodes());
            totalNodes += settings.getNumberOfNotReadyNodes();
        }
        if (settings.getNumberOfReadyNodes() == null) {
            response.setGwTotalReadyNodes(gwTotalReadyNodes);
            totalNodes += gwTotalReadyNodes;
        } else {
            response.setGwTotalReadyNodes(settings.getNumberOfReadyNodes());
            totalNodes += settings.getNumberOfReadyNodes();
        }
        
        response.setGwTotalNodes(totalNodes);
        response.setGwTotalNodesWithInfo(totalNodes);
        response.setGwTotalNodesWithSN(totalNodes);
        response.setGwTotalNodesNoInfo(0);
        
        if (settings.isFailsafeMode()) {
            response.setMode(AppMode.FAIL_SAFE);
        } else {
            response.setMode(mode);
        }
        
        return response;
    }
    
    public static GatewaySaveData getDefaultGatewayData() {
        GatewaySaveData data = new GatewaySaveData();
        data.setAdmin(admin);
        data.setIpAddress(ipAddress);
        data.setSuperAdmin(superAdmin);
        data.setUpdateServerLogin(updateServerAdmin);
        data.setUpdateServerUrl(updateServerUrl);
        return data;
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
    
    private static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
}
