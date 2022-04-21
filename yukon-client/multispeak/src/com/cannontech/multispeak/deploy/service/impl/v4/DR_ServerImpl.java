package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.DR_Server;
import com.cannontech.multispeak.service.v4.MspValidationService;
import com.cannontech.multispeak.service.v4.MultispeakLMService;
import com.google.common.collect.Lists;

@Service("DR_ServerImplV4")
public class DR_ServerImpl implements DR_Server {
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspValidationService mspValidationService;

    private final Logger log = YukonLogManager.getLogger(DR_ServerImpl.class);

    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }

    @Override
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }

    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods = new String[] {
                "PingURL",
                "GetMethods",
                "SCADAAnalogChangedNotification" };

        return multispeakFuncs.getMethods(MultispeakDefines.DR_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ErrorObject> SCADAAnalogChangedNotification(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        multispeakFuncs.getMultispeakVendorFromHeader();
        LiteYukonUser liteYukonUser = multispeakFuncs.authenticateMsgHeader();

        List<ErrorObject> errorObjects = Lists.newArrayList();
        for (ScadaAnalog scadaAnalog : scadaAnalogs) {
            ErrorObject errorObject = mspValidationService.isValidScadaAnalog(scadaAnalog);
            if (errorObject == null) {
                errorObject = multispeakLMService.writeAnalogPointData(scadaAnalog, liteYukonUser);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }
}
