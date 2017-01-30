package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfObjectRef;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.ObjectRef;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.MspRawPointHistoryDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;
import com.cannontech.multispeak.service.v5.OD_Server;

@Service("OD_ServerImplV5")
public class OD_ServerImpl implements OD_Server {

    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakMeterService multispeakMeterService;

    private LiteYukonUser init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        return multispeakFuncs.authenticateMsgHeader();
    }

    @Override
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }

    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods = null;
        methods = new String[] { "pingURL", "getMethods", "initiateEndDevicePings" };
        return multispeakFuncs.getMethods(MultispeakDefines.OD_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> initiateEndDevicePings(ArrayOfObjectRef arrayOfObjectRef,
            String responseURL, String transactionID, XMLGregorianCalendar expirationTime)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("initiateEndDevicePings", vendor.getCompanyName());
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor,
                                                                  responseURL,
                                                                  MultispeakDefines.NOT_Server_STR);
        List<ObjectRef> objectRefList = arrayOfObjectRef.getObjectRef();
        List<String> meterNos = new ArrayList<String>();
        for (ObjectRef objectRef : objectRefList) {
            String meterNumber = objectRef.getPrimaryIdentifierValue(); //TODO Please confirm that this field is mandatory 
                                                                       //so null check is not required
            meterNos.add(meterNumber);
        }
        List<ErrorObject> errorObjects = multispeakMeterService.odEvent(vendor,
                                                                        meterNos,
                                                                        transactionID,
                                                                        actualResponseUrl);

        multispeakFuncs.logErrorObjects(MultispeakDefines.NOT_Server_STR,
                                        "initiateEndDevicePings",
                                        errorObjects);

        return errorObjects;
    }
}