package com.cannontech.dr.itron.service.impl;

import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;

import org.apache.logging.log4j.Logger;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddD2GAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceGroupType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceIdentifierAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditD2GAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditESIType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ErrorFault;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.NullableString;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.StaticGroupMemberListType;
import com.cannontech.stars.dr.account.model.AccountDto;

public class DeviceManagerHelper implements SoapFaultParser {
    
    /**
     * <urn:AddHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:ESI>
     *          <urn:DeviceIdentifiers>
     *              <urn:MacID>00beef0a00000002</urn:MacID>
     *          </urn:DeviceIdentifiers>
     *      </urn:ESI>
     *      <urn:ServicePointUtilID>ABC</urn:ServicePointUtilID>
     * </urn:D2GAttributes>
     * 
     * DeviceManager::addHANDevice request with Utility Service Point ID
     */
    public static AddHANDeviceRequest buildAddRequestWithServicePoint(String macAddress, AccountDto account) {
        //Create outer AddHANDeviceRequest
        AddHANDeviceRequest request = new AddHANDeviceRequest();
        
        //Create inner D2GAttributes
        AddD2GAttributeType d2gAttribute = new AddD2GAttributeType();
        
        //Create ESIType and add it to D2GAttributes
        DeviceIdentifierAttributeType identifier = new DeviceIdentifierAttributeType();
        identifier.setMacID(macAddress);
        ESIType esi = new ESIType();
        esi.setDeviceIdentifiers(identifier);
        d2gAttribute.setESI(esi);
        
        //Set ServicePointUtilID on D2GAttributes
        d2gAttribute.setServicePointUtilID(account.getAccountNumber());
        
        //Add D2GAttributes to request
        request.setD2GAttributes(d2gAttribute);
        
        return request;
    }
       
    /**
     * <urn:EditHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:ESI>
     *          <urn:MacID>66bbbeee00000200</urn:MacID>
     *      </urn:ESI>
     *      <urn:ServicePointUtilID Null="false">ABC</urn:ServicePointUtilID>
     *  </urn:D2GAttributes>
     * </urn:EditHANDeviceRequest>
     * 
     * DeviceManager::editHANDevice request – assigning a Service Point to an existing device
     */
    public static EditHANDeviceRequest buildEditRequestWithServicePoint(String macAddress, AccountDto account) {
        //Create outer EditHANDeviceRequest 
        EditHANDeviceRequest request = new EditHANDeviceRequest();
        
        //Create inner D2GAttributes 
        EditD2GAttributeType d2GAttributes = new EditD2GAttributeType();
        
        //Create EditESIType and add it to D2GAttributes
        EditESIType esi = new EditESIType();
        esi.setMacID(macAddress);
        d2GAttributes.setESI(esi);
        
        //Create ServicePointUtilID and add it to D2GAttributes
        NullableString servicePointUtilId = new NullableString();
        servicePointUtilId.setNull(false);
        servicePointUtilId.setValue(account.getAccountNumber());
        d2GAttributes.setServicePointUtilID(servicePointUtilId);
        
        //Add D2GAttributes to request
        request.setD2GAttributes(d2GAttributes);
        
        return request;
    }
    
    /**
     * <urn:EditHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:ESI>
     *          <urn:MacID>66bbbeee00000200</urn:MacID>
     *      </urn:ESI>
     *      <urn:ServicePointUtilID Null="true"> 
     *  </urn:D2GAttributes>
     * </urn:EditHANDeviceRequest>
     * 
     * DeviceManager::editHANDevice request - remove existing Device from Service Point
     */
    public static EditHANDeviceRequest buildEditRequestRemoveServicePoint(String macAddress) {
        
        //Create outer EditHANDeviceRequest
        EditHANDeviceRequest request = new EditHANDeviceRequest();
        
        //Create inner D2GAttributes
        EditD2GAttributeType d2gAttribute = new EditD2GAttributeType();
        
        //Create EditESIType and add it to D2GAttributes
        EditESIType esi = new EditESIType();
        esi.setMacID(macAddress);
        d2gAttribute.setESI(esi);
        
        //Create null ServicePointUtilID and add it to D2GAttributes
        NullableString servicePointUtilId = new NullableString();
        servicePointUtilId.setNull(true);
        d2gAttribute.setServicePointUtilID(servicePointUtilId);
        
        //Add D2GAttributes to request
        request.setD2GAttributes(d2gAttribute);
        
        return request;
    }
    
    /**
     * <urn:AddHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:ESI>
     *          <urn:DeviceIdentifiers>
     *              <urn:MacID>00beef0a00000002</urn:MacID>
     *          </urn:DeviceIdentifiers>
     *      </urn:ESI>
     * </urn:D2GAttributes>
     * </urn:AddHANDeviceRequest>
     * 
     * DeviceManager::addHANDevice request without Utility Service Point ID, including Switch MAC ID
     */
    public static AddHANDeviceRequest buildAddRequestWithoutServicePoint(String macAddress) {
        
        //Create outer AddHANDeviceRequest
        AddHANDeviceRequest request = new AddHANDeviceRequest();
        
        //Create inner D2GAttributes
        AddD2GAttributeType d2gAttribute = new AddD2GAttributeType();
        
        //Create ESIType and add it to D2GAttributes
        DeviceIdentifierAttributeType identifier = new DeviceIdentifierAttributeType();
        identifier.setMacID(macAddress);
        ESIType esi = new ESIType();
        esi.setDeviceIdentifiers(identifier);
        d2gAttribute.setESI(esi);
        
        //Add D2GAttributes to request
        request.setD2GAttributes(d2gAttribute);
        
        return request;
    }
    
    /**
     * <urn:EditESIGroupRequest>
     *   <urn:GroupName>4756</ns2:GroupName>
     *   <urn:GroupType>STATIC_GROUP</ns2:GroupType>
     *   <urn:StaticGroupMemberList>
     *     <urn:MacID>00:13:50:05:00:92:86:34</ns2:MacID>
     *     <urn:MacID>00:13:50:05:00:92:86:37</ns2:MacID>
     *   </urn:StaticGroupMemberList>
     * </urn:EditESIGroupRequest>
     */
    public static ESIGroupRequestType buildGroupEditRequest(String itronGroupName, List<String> macAddresses) {
        
        //Create outer ESIGroupRequest
        ESIGroupRequestType request = new ESIGroupRequestType();
        
        //Add GroupName to request
        request.setGroupName(itronGroupName);
        
        //Add GroupType to request
        request.setGroupType(DeviceGroupType.STATIC_GROUP);
        
        //Add StaticGroupMemberList to request
        StaticGroupMemberListType type = new StaticGroupMemberListType();
        type.getMacIDs().addAll(macAddresses);
        request.setStaticGroupMemberList(type);
        
        return request;
    }
    
    @Override
    public void handleSoapFault(SoapFaultClientException e, Set<String> faultCodesToIgnore, Logger log) {
        SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
        soapFaultDetail.getDetailEntries().forEachRemaining(detail -> {
            SoapFaultDetailElement detailElementChild =
                soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            ErrorFault fault = (ErrorFault) ItronEndpointManager.DEVICE.getMarshaller().unmarshal(detailSource);
            log.debug(XmlUtils.getPrettyXml(fault));
            fault.getErrors().forEach(error -> checkIfErrorShouldBeIgnored(error.getErrorCode(),
                error.getErrorMessage(), faultCodesToIgnore, log));
        });
    }

    @Override
    public boolean isSupported(ItronEndpointManager manager) {
        return ItronEndpointManager.DEVICE == manager;
    }
}
