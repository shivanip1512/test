package com.cannontech.dr.itron.service.impl;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.dr.itron.service.ItronSimulatorService;

@WebService(endpointInterface = "com.cannontech.dr.itron.service.ItronSimulatorService")
public class ItronSimulatorServiceImpl implements ItronSimulatorService{

    private Endpoint endPoint;

    @Override
    public void startSimulator() {
        if(!isSimulatorRunning()) {
            endPoint = Endpoint.create(new ItronSimulatorServiceImpl());
            endPoint.publish("http://localhost:8083/itronSimulatorServer");      
        }
    }
    
    @Override
    public void stopSimulator() {
       if(endPoint != null)    {
           endPoint.stop();
           endPoint = null;
       }
    }
    
    @Override
    public boolean isSimulatorRunning() {
        return endPoint != null;
     }

    @Override
    public AddHANDeviceResponse addHANDevice(AddHANDeviceRequest request) {
        System.out.println(request.getDeviceIdentifiers().getDeviceName() + "  " + request.getDeviceIdentifiers().getMacID());
        AddHANDeviceResponse response = new AddHANDeviceResponse();
        // get response options from simulator settings
        
        // possible responses:

        // success
        response.setMacID("1");

        // error

        // ErrorFault
        return response;
    }
    
    @Override
    public EditHANDeviceResponse editHANDevice(EditHANDeviceRequest request) {
        EditHANDeviceResponse response = new EditHANDeviceResponse();
        System.out.println(request.getD2GAttributes().getServicePointUtilID().getValue());
        // get response options from simulator settings
        
        // possible responses:

        // success
        response.setMacID("2");

        // error

        // ErrorFault
        return response;
    }
}

