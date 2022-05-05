package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.v4.MspScadaAnalogReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.SCADA_Server;

@Service("SCADA_ServerImplV4")
public class SCADA_ServerImpl implements SCADA_Server {

    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;

    private final Logger log = YukonLogManager.getLogger(SCADA_ServerImpl.class);
    private final static String[] methods = new String[] { "PingURL",
            "GetMethods",
            "GetAllSCADAAnalogs" };

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
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<ScadaAnalog> getAllSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException {
        init();
        LiteYukonUser user = multispeakFuncs.authenticateMsgHeader();
        MultispeakVendor mspVendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetAllSCADAAnalogs", mspVendor.getCompanyName());

        Date timerStart = new Date();

        MspScadaAnalogReturnList scadaAnalogList = mspRawPointHistoryDao.retrieveLatestScadaAnalogs(user);

        multispeakFuncs.updateResponseHeader(scadaAnalogList);
        List<ScadaAnalog> scadaAnalogs = scadaAnalogList.getScadaAnalogs();
        log.info("Returning All Scada Analog data (" + scadaAnalogs.size() + " points). ("
                + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(scadaAnalogs.size(), scadaAnalogList.getObjectsRemaining(),
                "ScadaAnalog", scadaAnalogList.getLastSent(), "GetAllSCADAAnalogs", mspVendor.getCompanyName());
        return scadaAnalogs;
    }
}
