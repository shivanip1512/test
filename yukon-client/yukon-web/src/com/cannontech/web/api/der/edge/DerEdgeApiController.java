package com.cannontech.web.api.der.edge;

import java.math.BigInteger;
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

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.dao.impl.DeviceGroupProviderDaoMain;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.edgeDr.EdgeDrCommunicationException;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.service.DerEdgeCommunicationService;
import com.cannontech.web.api.error.model.YukonApiException;
import com.cannontech.web.api.error.model.YukonApiValidationException;
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
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @PostMapping("/unicastMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeUnicastRequest edgeUnicastRequest, YukonUserContext userContext) {
        YukonPao pao = paoDao.getYukonPao(edgeUnicastRequest.getName(), edgeUnicastRequest.getType());
        
        //Convert payload string into byte[] for porter
        byte[] payload = convertPayloadToBytes(edgeUnicastRequest.getPayload());
        
        EdgeUnicastPriority queuePriority = edgeUnicastRequest.getQueuePriority();
        EdgeUnicastPriority networkPriority = edgeUnicastRequest.getNetworkPriority();
        
        //Send the request to Porter and get back the E2E ID that will correlate with the response data when it comes back.
        String transactionGUID = "";
        try {
            transactionGUID = derEdgeCommunicationService.sendUnicastRequest(pao, payload, queuePriority, networkPriority, userContext);
        } catch (EdgeDrCommunicationException e) {
            throw new YukonApiException(e.getMessage(), e, ApiErrorDetails.COMMUNICATION_ERROR);
        }
        
        return new ResponseEntity<>(new EdgeUnicastResponse(transactionGUID), HttpStatus.OK);
    }

    @PostMapping("/multipointMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeMultipointRequest edgeMultipointRequest, YukonUserContext userContext) {
        // Convert payload string into byte[] for porter
        byte[] payload = convertPayloadToBytes(edgeMultipointRequest.getPayload());
        // Get the queue and network priority of the message
        EdgeUnicastPriority queuePriority = edgeMultipointRequest.getQueuePriority();
        EdgeUnicastPriority networkPriority = edgeMultipointRequest.getNetworkPriority();
        String edgeGroupName = "/Edge Addressing";
        String transactionGUID = "";

        try {
            // Lookup Edge Addressing Group
            DeviceGroup edgeAddressingGroup = deviceGroupService.resolveGroupName(edgeGroupName);
            // Lookup targeted group
            DeviceGroup targetedAddressingGroup = deviceGroupProviderDaoMain.getGroup(edgeAddressingGroup, edgeMultipointRequest.getGroupId());
            // Get list of all the device in this group
            Set<SimpleDevice> simpleDeviceList = deviceGroupProviderDaoMain.getDevices(targetedAddressingGroup);
            transactionGUID = sendMulticastRequest(simpleDeviceList, payload, queuePriority, networkPriority, userContext);
        } catch (NotFoundException e){
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            
            //The DER Edge Addressing group {0} could not be resolved.
            Object[] parameters = new Object[] { "/EdgeAddressing/" + edgeMultipointRequest.getGroupId() };
            ApiErrorDetails errorType = ApiErrorDetails.INVALID_VALUE;
            String i18nDetail = messageSourceAccessor.getMessage(getKeyForErrorType(errorType), parameters);
            
            throw new YukonApiValidationException(e, errorType, i18nDetail, "groupId", edgeMultipointRequest.getGroupId(), parameters);
        }

        return new ResponseEntity<>(new EdgeUnicastResponse(transactionGUID), HttpStatus.OK);
    }
    
    private String sendMulticastRequest(Set<SimpleDevice> simpleDeviceList, byte[] payload, 
            EdgeUnicastPriority queuePriority, EdgeUnicastPriority networkPriority, YukonUserContext userContext) {
        try {
            // Send the devices to the service layer for processing
            return derEdgeCommunicationService.sendMultiUnicastRequest(simpleDeviceList, payload, queuePriority, networkPriority, userContext);
        } catch (EdgeDrCommunicationException e) {
            throw new YukonApiException(e.getMessage(), e, ApiErrorDetails.COMMUNICATION_ERROR);
        }
    }

    @PostMapping("/broadcastMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeBroadcastRequest edgeBroadcastRequest,
            YukonUserContext userContext) {
        // Convert payload string into byte[] for porter
        byte[] payload = convertPayloadToBytes(edgeBroadcastRequest.getPayload());
        // Send the request to Porter.
        try {
            derEdgeCommunicationService.sendBroadcastRequest(payload, edgeBroadcastRequest.getPriority(), userContext);
        } catch (EdgeDrCommunicationException e) {
            throw new YukonApiException(e.getMessage(), e, ApiErrorDetails.COMMUNICATION_ERROR);
        }
        
        return new ResponseEntity<>(HttpStatus.OK);
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
    
    private String getKeyForErrorType(ApiErrorDetails apiErrorType) {
        return "yukon.web.api.der.edge.multipointMessage." + apiErrorType.getCode();
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
