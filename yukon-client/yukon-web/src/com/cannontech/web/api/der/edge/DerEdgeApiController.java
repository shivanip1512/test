package com.cannontech.web.api.der.edge;

import java.math.BigInteger;

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
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.der.edge.service.DerEdgeCommunicationService;
import com.cannontech.web.security.annotation.CheckCparm;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RestController
@CheckRoleProperty(YukonRoleProperty.DER_EDGE_COORDINATOR_PERMISSION)
@CheckCparm(MasterConfigBoolean.DER_EDGE_COORDINATOR)
//TODO - @CheckRoleProperty(YukonRoleProperty.EDGE_DR_PERMISSION)
//TODO - after YUK-26189 merged - The endpoint should only accept requests if master.cfg setting is true
//@CheckCparm(MasterConfigBoolean.DER_EDGE_COORDINATOR)
public class DerEdgeApiController {
    
    @Autowired private DerEdgeUnicastValidator unicastValidator;
    @Autowired private DerEdgeCommunicationService derEdgeCommunicationService;
    @Autowired private PaoDao paoDao;
    
    @PostMapping("/unicastMessage")
    public ResponseEntity<Object> create(@Valid @RequestBody EdgeUnicastRequest edgeUnicastRequest, YukonUserContext userContext) {
        
        //TODO: does the API return something nice when an invalid PaoType is used? could use findYukonPao to throw ourselves
        //ApiFieldError DOES_NOT_EXISTS
        YukonPao pao = paoDao.getYukonPao(edgeUnicastRequest.getName(), edgeUnicastRequest.getType());
        
        //Convert payload string into byte[] for porter
        byte[] payload = null;
        try {
            payload = convertPayloadToBytes(edgeUnicastRequest.getPayload());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid unicast payload.", e);
        }
        
        short e2eId = derEdgeCommunicationService.sendUnicastRequest(pao, payload, userContext);
        
        //TODO later - correlate E2E IDs with response GUID
        //for now, always return this temp GUID
        String tempGUID = "692c8e7d-bd82-49f2-ace2-a6a9600f6347";
        return new ResponseEntity<>(new EdgeUnicastResponse(tempGUID), HttpStatus.OK);
    }
    
    /**
     * Accept a DER Edge unicast payload as a hex string and return the equivalent byte array. 
     * @param stringPayload a DER Edge unicast payload as a hex string
     * @return the payload in the form of a byte array
     */
    private byte[] convertPayloadToBytes(String stringPayload) {
        //TODO Java 17 - HexFormat.parseHex?
        BigInteger intValue = new BigInteger(stringPayload, 16 /*parse as hex*/);
        return intValue.toByteArray();
    }
    
    @InitBinder("edgeUnicastRequest")
    public void setBinder(WebDataBinder binder) {
        binder.addValidators(unicastValidator);
    }
    

}
