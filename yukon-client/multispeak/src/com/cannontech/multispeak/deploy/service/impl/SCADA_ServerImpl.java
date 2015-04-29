package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.MspScadaAnalogReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.cannontech.multispeak.service.SCADA_Server;

@Service
public class SCADA_ServerImpl implements SCADA_Server {

    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakLMService multispeakLMService;

    private final Logger log = YukonLogManager.getLogger(SCADA_ServerImpl.class);
    private final static String[] methods = new String[] {
        "pingURL",
        "getMethods",
        "getAllSCADAAnalogs",
    };

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
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR , Arrays.asList(methods));
    }

    @Override
    public List<ScadaAnalog> getAllSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException {
        LiteYukonUser user = init();
        MultispeakVendor mspVendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getAllSCADAAnalogs", mspVendor.getCompanyName());

        Date timerStart = new Date();

        MspScadaAnalogReturnList scadaAnalogList = mspRawPointHistoryDao.retrieveLatestScadaAnalogs(user);
        
        multispeakFuncs.updateResponseHeader(scadaAnalogList);
        List<ScadaAnalog> scadaAnalogs = scadaAnalogList.getScadaAnalogs();
        log.info("Returning All Scada Analog data (" + scadaAnalogs.size() + " points). (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        multispeakEventLogService.returnObjects(scadaAnalogs.size(), scadaAnalogList.getObjectsRemaining(), "ScadaAnalog", scadaAnalogList.getLastSent(),
                                                "getAllSCADAAnalogs", mspVendor.getCompanyName());
        return scadaAnalogs;
    }

}