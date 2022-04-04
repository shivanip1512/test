package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.DR_Server;

@Service("DR_ServerImplV4")
public class DR_ServerImpl implements DR_Server {
    @Autowired private MultispeakFuncs multispeakFuncs;

    private final Logger log = YukonLogManager.getLogger(DR_ServerImpl.class);
    private final static String[] methods = new String[] { "PingURL", 
                                                           "GetMethods" };

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
        return multispeakFuncs.getMethods(MultispeakDefines.DR_Server_STR, Arrays.asList(methods));
    }
}
