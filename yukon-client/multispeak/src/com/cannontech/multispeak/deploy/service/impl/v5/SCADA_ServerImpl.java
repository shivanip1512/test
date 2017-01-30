package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.client.v5.UserDetailHolder;
import com.cannontech.multispeak.dao.v5.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.v5.MspScadaAnalogReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.SCADA_Server;

@Service("SCADA_ServerImplV5")
public class SCADA_ServerImpl implements SCADA_Server {

    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;

    private final Logger log = YukonLogManager.getLogger(SCADA_ServerImpl.class);
    private final static String[] methods = new String[] {
        "pingURL",
        "getMethods",
        "getLatestSCADAAnalogs",
    };

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
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR , Arrays.asList(methods));
    }

    @Override
    public List<SCADAAnalog> getLatestSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor mspVendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("getLatestSCADAAnalogs", mspVendor.getCompanyName());

        Date timerStart = new Date();

        MspScadaAnalogReturnList scadaAnalogList = mspRawPointHistoryDao.retrieveLatestScadaAnalogs(UserDetailHolder.getYukonUser());
        
        multispeakFuncs.updateResponseHeader(scadaAnalogList);
        List<SCADAAnalog> scadaAnalogs = scadaAnalogList.getScadaAnalogs();
        log.info("Returning All Scada Analog data (" + scadaAnalogs.size() + " points). (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        multispeakEventLogService.returnObjects(scadaAnalogs.size(), scadaAnalogList.getObjectsRemaining(), "SCADAAnalog", scadaAnalogList.getLastSent(),
                                                "getAllSCADAAnalogs", mspVendor.getCompanyName());
        return scadaAnalogs;
    }

}