package com.cannontech.web.api.der.edge;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.dao.impl.DeviceGroupProviderDaoMain;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.service.DerEdgeCommunicationService;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RestController
@CheckRoleProperty(YukonRoleProperty.DER_EDGE_COORDINATOR_PERMISSION)
@CheckCparm(MasterConfigBoolean.DER_EDGE_COORDINATOR)
public class DerEdgeApiController {
    
    @Autowired private DerEdgeUnicastValidator unicastValidator;
    @Autowired private DerEdgeMultipointValidator multipointValidator;
    @Autowired private DerEdgeBroadcastValidator broadcastValidator;
    @Autowired private DerEdgeCommunicationService derEdgeCommunicationService;
    @Autowired private DeviceGroupProviderDaoMain deviceGroupProviderDaoMain;
    @Autowired private PaoDao paoDao;
    
    @PostMapping("/unicastMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeUnicastRequest edgeUnicastRequest, YukonUserContext userContext) {
        YukonPao pao = paoDao.getYukonPao(edgeUnicastRequest.getName(), edgeUnicastRequest.getType());
        
        //Convert payload string into byte[] for porter
        byte[] payload = convertPayloadToBytes(edgeUnicastRequest.getPayload());
        
        EdgeUnicastPriority queuePriority = edgeUnicastRequest.getQueuePriority();
        EdgeUnicastPriority networkPriority = edgeUnicastRequest.getNetworkPriority();
        
        //Send the request to Porter and get back the E2E ID that will correlate with the response data when it comes back.
        Map<Integer, Short> e2eId = derEdgeCommunicationService.sendUnicastRequest(pao, payload, queuePriority, networkPriority, userContext);
        
        //TODO later - correlate E2E IDs with response GUID
        //for now, always return this temp GUID
        String tempGUID = "692c8e7d-bd82-49f2-ace2-a6a9600f6347";
        return new ResponseEntity<>(new EdgeUnicastResponse(tempGUID), HttpStatus.OK);
    }

    @PostMapping("/multipointMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeMultipointRequest edgeMultipointRequest, YukonUserContext userContext) {
        //// Look up group things
        try {
            //Convert payload string into byte[] for porter
            byte[] payload = convertPayloadToBytes(edgeMultipointRequest.getPayload());
            //Get the queue and network priority of the message
            EdgeUnicastPriority queuePriority = edgeMultipointRequest.getQueuePriority();
            EdgeUnicastPriority networkPriority = edgeMultipointRequest.getNetworkPriority();

            DeviceGroup edgeAddressingGroup = deviceGroupProviderDaoMain.getGroup(null, "Edge Addressing");
            if (edgeAddressingGroup.equals(null)) {
                // thow error that "Edge Addressing" device group doesn't exist
            }
            // Get the group's under "Edge Addressing"
            DeviceGroup targetedAddressingGroup = deviceGroupProviderDaoMain.getGroup(null, edgeMultipointRequest.getGroupId());
            // Check if provided device group is a child
            if (targetedAddressingGroup.equals(null) && targetedAddressingGroup.isChildOf(edgeAddressingGroup)) {
                // thow error that target group is not child of "Edge Addressing" device group
            }
            // Get list of all the device in this group
            Set<SimpleDevice> simpleDeviceList = deviceGroupProviderDaoMain.getDevices(targetedAddressingGroup);

            // Does this need to be threaded or something?
            // Create loop and send individual Unicast requests for the PAO's
            for (SimpleDevice device : simpleDeviceList) {
                Map<Integer, Short> e2eId = derEdgeCommunicationService.sendUnicastRequest(device.getPaoIdentifier(), payload,
                        queuePriority, networkPriority, userContext);
                
                //TODO later - correlate E2E IDs with response GUID
                //for now, always return this temp GUID
                String tempGUID = "692c8e7d-bd82-49f2-ace2-a6a9600f6347";
                return new ResponseEntity<>(new EdgeUnicastResponse(tempGUID), HttpStatus.OK);
            }
        } catch (Exception e) {

        }
        //TODO later - correlate E2E IDs with response GUID
        //for now, always return this temp GUID
        String tempGUID = "692c8e7d-bd82-49f2-ace2-a6a9600f6347";
        return new ResponseEntity<>(new EdgeUnicastResponse(tempGUID), HttpStatus.OK);
    }

    @PostMapping("/broadcastMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeBroadcastRequest edgeBroadcastRequest,
            YukonUserContext userContext) {
        // Convert payload string into byte[] for porter
        byte[] payload = convertPayloadToBytes(edgeBroadcastRequest.getPayload());
        // Send the request to Porter.
        derEdgeCommunicationService.sendBroadcastRequest(payload, edgeBroadcastRequest.getPriority(), userContext);

        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    /**
     * Accept a DER Edge payload as a hex string and return the equivalent byte array. 
     * @param stringPayload a DER Edge payload as a hex string
     * @return the payload in the form of a byte array
     * @throws IllegalArgumentException if an unexpected error occurs converting the payload string to a byte array.
     */
    private byte[] convertPayloadToBytes(String stringPayload) {
        try {
            //TODO Java 17 - HexFormat.parseHex?
            BigInteger intValue = new BigInteger(stringPayload, 16 /*parse as hex*/);
            return intValue.toByteArray();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid unicast payload.", e);
        }
    }

    @InitBinder("edgeUnicastRequest")
    public void setUnicastBinder(WebDataBinder binder) {
        binder.addValidators(unicastValidator);
    }

    @InitBinder("edgeMultipointRequest")
    public void setMultipointBinder(WebDataBinder binder) {
        binder.addValidators(multipointValidator);
    }

    @InitBinder("edgeBroadcastRequest")
    public void setBroadcastBinder(WebDataBinder binder) {
        binder.addValidators(broadcastValidator);
    }

}
