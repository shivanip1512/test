package com.cannontech.multispeak.service.v5;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.monitors.message.OutageJmsMessage;
import com.cannontech.amr.statusPointMonitoring.model.OutageActionType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfEndDeviceState;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.ObjectRef;
import com.cannontech.msp.beans.v5.enumerations.EndDeviceStateKind;
import com.cannontech.msp.beans.v5.enumerations.EndDeviceStateType;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceState;
import com.cannontech.msp.beans.v5.not_server.EndDeviceStatesNotification;
import com.cannontech.msp.beans.v5.not_server.ObjectFactory;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.NOTClient;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.service.MspIdentifiablePaoService;
import com.cannontech.multispeak.service.OutageJmsMessageService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OutageJmsMessageListener extends OutageJmsMessageService {

    @Autowired private MultispeakDao multispeakDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MspIdentifiablePaoService mspIdentifiablePaoService;
    @Autowired private NOTClient notClient;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private OutageEventLogService outageEventLogService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

    private ImmutableList<MultispeakVendor> vendorsToSendOutageMsg = ImmutableList.of();
    private AtomicLong atomicLong = new AtomicLong();
    private ImmutableMap<OutageActionType, EndDeviceStateKind> outageMap = ImmutableMap.of();
    private static final Logger log = YukonLogManager.getLogger(OutageJmsMessageListener.class);

    public enum EndDeviceType {

        METER("Meter"), 
        OTHER("Other");
        private final String value;

        EndDeviceType(String v) {
            value = v;
        }

        public String value() {
            return value;
        }
    }

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.MULTISPEAK,
            new DatabaseChangeEventListener() {
                @Override
                public void eventReceived(DatabaseChangeEvent event) {
                    loadOutageSupportedVendors();
                }
            });

        ImmutableMap.Builder<OutageActionType, EndDeviceStateKind> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(OutageActionType.NoResponse, EndDeviceStateKind.NO_RESPONSE);
        mapBuilder.put(OutageActionType.Outage, EndDeviceStateKind.OUTAGED);
        mapBuilder.put(OutageActionType.Restoration, EndDeviceStateKind.STARTING_UP);
        outageMap = mapBuilder.build();
        // To make this call asynchronous, added new thread for calling method.
        new Thread(() -> {
            loadOutageSupportedVendors();
        }).start();

    }

    private void loadOutageSupportedVendors(){
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors(true);
        ImmutableList.Builder<MultispeakVendor> supportsOutage = ImmutableList.builder();
        for (MultispeakVendor mspVendor : allVendors) {
            Pair<String, MultiSpeakVersion> keyPair =
                MultispeakVendor.buildMapKey(MultispeakDefines.NOT_Server_STR, MultiSpeakVersion.V5);
            if (mspVendor.getMspInterfaceMap().get(keyPair) != null) {
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_STR);
                try {
                    List<String> mspMethodNames = notClient.getMethods(mspVendor, endpointUrl);
                    // not sure where a static variable containing this method exists.. doing this for now
                    if (mspMethodNames.stream().anyMatch("EndDeviceStatesNotification"::equalsIgnoreCase)) {
                        supportsOutage.add(mspVendor);
                        log.info("Added OMS vendor to receive Status Point Monitor messages: "
                            + mspVendor.getCompanyName());
                    }
                } catch (MultispeakWebServiceClientException e) {
                    log.warn("caught exception in initialize " + e);
                }
            }
        }

        vendorsToSendOutageMsg = supportsOutage.build();
    }

    @Override
    public void handleMessage(OutageJmsMessage outageJmsMessage) {

        if (vendorsToSendOutageMsg.isEmpty()) {
            log.debug("Received outage message from jms queue: not generating message because no vendors are configured");
            return;
        }
        log.debug("Received outage message from jms queue: " + outageJmsMessage);

        EndDeviceState deviceState = getEndDeviceState(outageJmsMessage);

        for (MultispeakVendor mspVendor : vendorsToSendOutageMsg) {
            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_STR);

            log.info("Sending EndDeviceStatesNotification (" + endpointUrl + "): ObjectID: "
                + deviceState.getReferableID() + " Type: " + deviceState.getDeviceState().getValue());

            try {
                String transactionId = String.valueOf(atomicLong.getAndIncrement());
                EndDeviceStatesNotification endDeviceStatesNotification = objectFactory.createEndDeviceStatesNotification();
                ArrayOfEndDeviceState arrayOfEndDeviceState = new ArrayOfEndDeviceState();
                List<EndDeviceState> endDeviceStateList = arrayOfEndDeviceState.getEndDeviceState();
                endDeviceStateList.add(deviceState);
                endDeviceStatesNotification.setTransactionID(transactionId);
                endDeviceStatesNotification.setArrayOfEndDeviceState(arrayOfEndDeviceState);
                notClient.endDeviceStatesNotification(mspVendor, endpointUrl, endDeviceStatesNotification);

                List<ErrorObject> errObjects = new ArrayList<>();
                errObjects = multispeakFuncs.getErrorObjectsFromResponse();
                if (CollectionUtils.isNotEmpty(errObjects)) {
                    multispeakFuncs.logErrorObjects(endpointUrl, "EndDeviceStatesNotification", errObjects);
                } else {
                    outageEventLogService.mspMessageSentToVendor(outageJmsMessage.getSource(),
                                                                 deviceState.getDeviceState().getValue().value(), 
                                                                 deviceState.getReferableID(),
                                                                 getEndDeviceType(outageJmsMessage.getPaoIdentifier()).value(), 
                                                                 mspVendor.getCompanyName(),
                                                                 "EndDeviceStatesNotification");
                }

            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: " + endpointUrl + " - EndDeviceStatesNotification (" + mspVendor.getCompanyName()
                    + ")");
                log.error("MultispeakWebServiceClientException: " + e.getMessage());
            }
        }
    }

    private EndDeviceState getEndDeviceState(OutageJmsMessage outageJmsMessage) {
        PaoIdentifier paoIdentifier = outageJmsMessage.getPaoIdentifier();

        EndDeviceState deviceState = new EndDeviceState();
        EndDeviceStateType endDeviceStateType = new EndDeviceStateType();
        endDeviceStateType.setValue(outageMap.get(outageJmsMessage.getActionType()));
        deviceState.setDeviceState(endDeviceStateType);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(outageJmsMessage.getPointValueQualityHolder().getPointDataTimeStamp());
        deviceState.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(cal));
        String objectId = mspIdentifiablePaoService.getObjectId(paoIdentifier);
        deviceState.setReferableID(objectId);

        if (paoIdentifier.getPaoType().isMeter()) {
            ObjectRef objectRef = new ObjectRef();
            objectRef.setNoun(new QName("http://www.multispeak.org/V5.0/commonTypes", "objectRef", "com"));
            objectRef.setPrimaryIdentifierValue(objectId);
            objectRef.setValue(objectId);
            objectRef.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
            objectRef.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
            deviceState.setDeviceReference(objectRef);
        }

        outageEventLogService.outageEventGenerated(deviceState.getDeviceState().getValue().value(),
                                                   deviceState.getTimeStamp().toGregorianCalendar().getTime(), 
                                                   getEndDeviceType(paoIdentifier).value(),
                                                   deviceState.getReferableID(), 
                                                  "EndDeviceStatesNotification");

        return deviceState;
    }

    private EndDeviceType getEndDeviceType(YukonPao paoIdentifier) {
        if (isMeter(paoIdentifier)) {
            return EndDeviceType.METER;
        }
        return EndDeviceType.OTHER;
    }

}