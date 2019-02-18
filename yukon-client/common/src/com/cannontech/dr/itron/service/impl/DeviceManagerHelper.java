package com.cannontech.dr.itron.service.impl;

import java.util.List;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddD2GAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceIdentifierAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditD2GAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditPrimaryHANDeviceType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.NullableString;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.StaticGroupMemberListType;
import com.cannontech.stars.dr.account.model.AccountDto;

public class DeviceManagerHelper {
    
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
    public static AddHANDeviceRequest buildAddRequestWithServicePoint(Hardware hardware, AccountDto account) {
        AddHANDeviceRequest request = new AddHANDeviceRequest();
        DeviceIdentifierAttributeType identifier = new DeviceIdentifierAttributeType();
        identifier.setMacID(hardware.getMacAddress());
        ESIType type = new ESIType();
        type.setDeviceIdentifiers(identifier);
        AddD2GAttributeType attribute = new AddD2GAttributeType();
        attribute.setESI(type);
        attribute.setServicePointUtilID(account.getAccountNumber());
        request.setD2GAttributes(attribute);
        return request;
    }
       
    /**
     * <urn:EditHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:PrimaryAttributes>
     *          <urn:MacID>66bbbeee00000200</urn:MacID>
     *      </urn:PrimaryAttributes>
     *      <urn:ServicePointUtilID Null="false">ABC</urn:ServicePointUtilID>
     *  </urn:D2GAttributes>
     * </urn:EditHANDeviceRequest>
     * 
     * DeviceManager::editHANDevice request – assigning a Service Point to an existing device
     */
    public static EditHANDeviceRequest buildEditRequestWithServicePoint(String macAddress, AccountDto account) {
        EditHANDeviceRequest request = new EditHANDeviceRequest();
        EditPrimaryHANDeviceType editType = new EditPrimaryHANDeviceType();
        editType.setMacID(macAddress);
        EditD2GAttributeType attribute = new EditD2GAttributeType();
        NullableString nullableString = new NullableString();
        nullableString.setNull(false);
        nullableString.setValue(account.getAccountNumber());
        attribute.setServicePointUtilID(nullableString);
        request.setD2GAttributes(attribute);
        return request;
    }
    
    /**
     * <urn:EditHANDeviceRequest>
     *  <urn:D2GAttributes>
     *      <urn:PrimaryAttributes>
     *          <urn:MacID>66bbbeee00000200</urn:MacID>
     *      </urn:PrimaryAttributes>
     *      <urn:ServicePointUtilID Null="true"> 
     *  </urn:D2GAttributes>
     * </urn:EditHANDeviceRequest>
     * 
     * DeviceManager::editHANDevice request - remove existing Device from Service Point
     */
    public static EditHANDeviceRequest buildEditRequestRemoveServicePoint(String macAddress) {
        EditHANDeviceRequest request = new EditHANDeviceRequest();
        EditPrimaryHANDeviceType editType = new EditPrimaryHANDeviceType();
        editType.setMacID(macAddress);
        EditD2GAttributeType attribute = new EditD2GAttributeType();
        NullableString nullableString = new NullableString();
        nullableString.setNull(true);
        attribute.setPrimaryAttributes(editType);
        attribute.setServicePointUtilID(nullableString);
        request.setD2GAttributes(attribute);
        return request;
    }
    
    /**
     * <urn:AddHANDeviceRequest>
     *  <urn:DeviceIdentifiers>
     *      <urn:MacID>66bbbeee00000200</urn:MacID>
     *  </urn:DeviceIdentifiers>
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
    public static AddHANDeviceRequest buildAddRequestWithoutServicePoint(Hardware hardware) {
        AddHANDeviceRequest request = new AddHANDeviceRequest();
        DeviceIdentifierAttributeType identifier = new DeviceIdentifierAttributeType();
        identifier.setMacID(hardware.getMacAddress());
        ESIType type = new ESIType();
        type.setDeviceIdentifiers(identifier);
        AddD2GAttributeType attribute = new AddD2GAttributeType();
        attribute.setESI(type);
        request.setDeviceIdentifiers(identifier);
        request.setD2GAttributes(attribute);
        return request;
    }
    
    public static ESIGroupRequestType buildGroupEditRequest(LiteYukonPAObject lmGroup, List<String> macAddresses) {
        ESIGroupRequestType requestType = new ESIGroupRequestType();
        requestType.setGroupName(String.valueOf(lmGroup.getLiteID()));
        StaticGroupMemberListType type = new StaticGroupMemberListType();
        type.getMacIDs().addAll(macAddresses);
        requestType.setStaticGroupMemberList(type);
        return requestType;
    }

    public static ESIGroupRequestType buildGroupAddRequest(LiteYukonPAObject lmGroup) {
        ESIGroupRequestType requestType = new ESIGroupRequestType();
        requestType.setGroupName(String.valueOf(lmGroup.getLiteID()));
        return requestType;
    }
}
