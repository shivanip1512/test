package com.cannontech.dr.itron.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.DeviceIdentifierAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditD2GAttributeType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.NullableString;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ItronCommunicationServiceImpl implements ItronCommunicationService{
    
    private String url;
    private String password;
    private String userName;
    private WebServiceTemplate wsDeviceManager;
    
    @Autowired
    public ItronCommunicationServiceImpl(@Qualifier("wsTemplateItronDeviceManagerTypes_v1_8") WebServiceTemplate templateDeviceManager,
            GlobalSettingDao settingDao) {
        this.wsDeviceManager = templateDeviceManager;
        // settingDao.getString(GlobalSettingType.ITRON_HCM_API_URL);
        userName = settingDao.getString(GlobalSettingType.ITRON_HCM_USERNAME);
        password = settingDao.getString(GlobalSettingType.ITRON_HCM_PASSWORD);
    }
      
    @Override
    public AddHANDeviceResponse addHANDevice() {   
        AddHANDeviceRequest request = new AddHANDeviceRequest();
        DeviceIdentifierAttributeType identifier = new DeviceIdentifierAttributeType();
        identifier.setDeviceName("Device");
        identifier.setMacID("1");
        request.setDeviceIdentifiers(identifier);
        url = "http://localhost:8080/itronSimulatorServer/addHANDevice";
        AddHANDeviceResponse response = (AddHANDeviceResponse) wsDeviceManager.marshalSendAndReceive(url, request);
        System.out.println(response.getMacID());
        return response;
    }
    
    @Override
    public EditHANDeviceResponse editHANDevice() {   
        EditHANDeviceRequest request = new EditHANDeviceRequest();
        EditD2GAttributeType type = new EditD2GAttributeType();
        NullableString str = new NullableString();
        str.setValue("test");
        type.setServicePointUtilID(str);
        request.setD2GAttributes(type);
        url = "http://localhost:8080/itronSimulatorServer/addHANDevice";
        EditHANDeviceResponse response =  (EditHANDeviceResponse) wsDeviceManager.marshalSendAndReceive(url, request);
        System.out.println(response.getMacID());
        return response;
    }
}
