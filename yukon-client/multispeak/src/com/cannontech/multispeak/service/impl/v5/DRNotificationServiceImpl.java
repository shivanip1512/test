package com.cannontech.multispeak.service.impl.v5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfDRProgramEnrollment;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.Extensions;
import com.cannontech.msp.beans.v5.commontypes.ObjectID;
import com.cannontech.msp.beans.v5.commontypes.ServicePointID;
import com.cannontech.msp.beans.v5.commontypes.SingleIdentifier;
import com.cannontech.msp.beans.v5.enumerations.DRProgramEnrollmentStatus;
import com.cannontech.msp.beans.v5.enumerations.DRProgramEnrollmentStatusKind;
import com.cannontech.msp.beans.v5.enumerations.ServiceKind;
import com.cannontech.msp.beans.v5.multispeak.DRProgramEnrollment;
import com.cannontech.msp.beans.v5.not_server.DRProgramEnrollmentsNotification;
import com.cannontech.msp.beans.v5.not_server.DRProgramUnenrollmentsNotification;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.NOTClient;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.extension.DRLoadGroupName;
import com.cannontech.multispeak.extension.Relay;
import com.cannontech.multispeak.service.DRNotificationService;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.jms.notification.message.DRNotificationMessageType;
import com.cannontech.stars.dr.jms.notification.message.EnrollmentNotificationMessage;
import com.cannontech.stars.dr.jms.notification.message.OptOutInNotificationMessage;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;

public class DRNotificationServiceImpl implements DRNotificationService {

    @Autowired private NOTClient notClient;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private AssignedProgramDao assignedProgramDao;

    private AtomicLong atomicLong = new AtomicLong();
    private static ImmutableMap<MultispeakVendor, List<String>> vendorsToSendDRMsg;

    private static final String ENROLLMENT_METHOD = "DRProgramEnrollmentsNotification";
    private static final String UNENROLLMENT_METHOD = "DRProgramUnenrollmentsNotification";
    private static final String LCR_INDENTIFIER_NAME = "lcrSerial";
    private static final String LCR_INDENTIFIER_LABEL = "LCR Serial";
    
    

    private Executor executor = Executors.newCachedThreadPool();

    private static final Logger log = YukonLogManager.getLogger(DRNotificationServiceImpl.class);

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.MULTISPEAK, new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                loadDRSupportedVendors();
            }
        });
        // To make this call asynchronous, added new thread for calling method.
        executor.execute(() -> {
            loadDRSupportedVendors();
        });

    }

    private void loadDRSupportedVendors() {
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors(true);
        ImmutableMap.Builder<MultispeakVendor, List<String>> vendorToSupportedNotificationMethods = ImmutableMap.builder();
        for (MultispeakVendor mspVendor : allVendors) {
            Pair<String, MultiSpeakVersion> keyPair = MultispeakVendor.buildMapKey(MultispeakDefines.NOT_Server_DR_STR, MultiSpeakVersion.V5);
            if (mspVendor.getMspInterfaceMap().get(keyPair) != null) {
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
                try {
                    List<String> mspMethodNames = notClient.getMethods(mspVendor, endpointUrl);
                    vendorToSupportedNotificationMethods.put(mspVendor, mspMethodNames);
                } catch (MultispeakWebServiceClientException e) {
                    log.warn("caught exception in initialize", e);
                }
            }
        }

        vendorsToSendDRMsg = vendorToSupportedNotificationMethods.build();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof EnrollmentNotificationMessage) {
                    EnrollmentNotificationMessage drEnrollJmsMessage = (EnrollmentNotificationMessage) object;
                    if (drEnrollJmsMessage.getMessageType() == DRNotificationMessageType.ENROLLMENT) {
                        enrollmentNotification(drEnrollJmsMessage);
                    } else {
                        unenrollmentNotification(drEnrollJmsMessage);
                    }
                } else if (object instanceof OptOutInNotificationMessage) {
                    OptOutInNotificationMessage drOptOutInJmsMessage = (OptOutInNotificationMessage) object;

                    if (drOptOutInJmsMessage.getMessageType() == DRNotificationMessageType.OPTOUT) {
                        optOutNotification(drOptOutInJmsMessage);
                    } else {
                        optInNotification(drOptOutInJmsMessage);
                    }
                }
            } catch (JMSException e) {
                log.warn("Unable to extract multispeak DR message", e);
            }
        }

    }

    @Override
    public void enrollmentNotification(EnrollmentNotificationMessage notificationMessage) {

        if (!isvendorsConfigured()) {
            return;
        }

        vendorsToSendDRMsg.entrySet().forEach(entry -> {
            boolean isMatched = isSupportedMethod(entry.getValue(), ENROLLMENT_METHOD);
            if (isMatched) {
                MultispeakVendor mspVendor = entry.getKey();
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);

                log.info("Sending " + ENROLLMENT_METHOD + "with type " + notificationMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);
                DRProgramEnrollmentsNotification drProgramEnrollmentsNotification = new DRProgramEnrollmentsNotification();
                String transactionId = String.valueOf(atomicLong.getAndIncrement());

                ArrayOfDRProgramEnrollment arrayOfDRProgramEnrollment = buildArrayOfDRProgramEnrollment(notificationMessage);
                drProgramEnrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDRProgramEnrollment);
                drProgramEnrollmentsNotification.setTransactionID(transactionId);
                try {
                    notClient.drProgramEnrollmentsNotification(mspVendor, endpointUrl, drProgramEnrollmentsNotification);

                    String SerialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(notificationMessage.getInventoryId());

                    logDRNotificationResponse(SerialNumber, ENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);
                } catch (MultispeakWebServiceClientException e) {
                    log.error("TargetService: " + endpointUrl + " -" + ENROLLMENT_METHOD + "with type " + notificationMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") ");
                    log.error("MultispeakWebServiceClientException: " + e.getMessage());
                }
            }

        });

    }

    @Override
    public void unenrollmentNotification(EnrollmentNotificationMessage unEnrollmentMessage) {

        if (!isvendorsConfigured()) {
            return;
        }

        vendorsToSendDRMsg.entrySet().forEach(entry -> {
            boolean isMatched = isSupportedMethod(entry.getValue(), UNENROLLMENT_METHOD);
            if (isMatched) {
                MultispeakVendor mspVendor = entry.getKey();
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
                log.info("Sending " + UNENROLLMENT_METHOD + "with type " + unEnrollmentMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

                DRProgramUnenrollmentsNotification drProgramUnenrollmentsNotification = new DRProgramUnenrollmentsNotification();
                String transactionId = String.valueOf(atomicLong.getAndIncrement());

                ArrayOfDRProgramEnrollment arrayOfDRProgramEnrollment = buildArrayOfDRProgramEnrollment(unEnrollmentMessage);
                drProgramUnenrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDRProgramEnrollment);
                drProgramUnenrollmentsNotification.setTransactionID(transactionId);

                try {
                    notClient.drProgramUnenrollmentsNotification(mspVendor, endpointUrl, drProgramUnenrollmentsNotification);

                    String SerialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(unEnrollmentMessage.getInventoryId());
                    
                    logDRNotificationResponse(SerialNumber, UNENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);

                } catch (MultispeakWebServiceClientException e) {
                    log.error("TargetService: " + endpointUrl + " -" + UNENROLLMENT_METHOD + "with type " + unEnrollmentMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);
                    log.error("MultispeakWebServiceClientException: " + e.getMessage());
                }
            }

        });

    }

    private ArrayOfDRProgramEnrollment buildArrayOfDRProgramEnrollment(EnrollmentNotificationMessage notificationMessage) {

        ArrayOfDRProgramEnrollment arrayOfDRProgramEnrollment = new ArrayOfDRProgramEnrollment();

        List<DRProgramEnrollment> drProgramEnrollments = arrayOfDRProgramEnrollment.getDRProgramEnrollment();

        String objectGUID = multispeakFuncs.generateObjectGUID();
        DRProgramEnrollment programEnrollment = new DRProgramEnrollment();

        programEnrollment.setObjectGUID(objectGUID);

        String serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(notificationMessage.getInventoryId());
        SingleIdentifier lcrPrimaryIdentifier = buildSingleIdentifier(LCR_INDENTIFIER_NAME, LCR_INDENTIFIER_LABEL, serialNumber);
        programEnrollment.setPrimaryIdentifier(lcrPrimaryIdentifier);

        AssignedProgram program = assignedProgramDao.getById(notificationMessage.getProgramId());
        ObjectID drProgramID = buildDRProgramID(multispeakFuncs.getObjectGUID(program.getProgramId()),
                                                "programName",
                                                "Program Name",
                                                program.getProgramName());
        programEnrollment.setDRProgramID(drProgramID);

        programEnrollment.setDRProgramName(program.getProgramName());

        ServicePointID servicePointID = buildServicePointID();
        programEnrollment.setServicePointID(servicePointID);

        DRProgramEnrollmentStatus status = new DRProgramEnrollmentStatus();
        if (notificationMessage.getMessageType() == DRNotificationMessageType.ENROLLMENT) {
            status.setValue(DRProgramEnrollmentStatusKind.ACTIVE);
        } else {
            status.setValue(DRProgramEnrollmentStatusKind.INACTIVE);
        }
        programEnrollment.setDRProgramEnrollmentStatus(status);

        if (notificationMessage.getMessageType() == DRNotificationMessageType.ENROLLMENT) {
            XMLGregorianCalendar xmlGregorianCalendar = getDRProgramEnrollmentDate(notificationMessage.getEnrollmentTime());
            if (xmlGregorianCalendar != null) {
                programEnrollment.setDRProgramParticStartDate(xmlGregorianCalendar);
            }
        } else {
            XMLGregorianCalendar xmlGregorianCalendar = getDRProgramEnrollmentDate(notificationMessage.getEnrollmentTime());
            if (xmlGregorianCalendar != null) {
                programEnrollment.setDRProgramParticEndDate(xmlGregorianCalendar);
            }
        }

        LiteYukonPAObject loadGroup = databaseCache.getAllPaosMap().get(notificationMessage.getLoadGroupId());
        
        Extensions extensions = buildRelayAndGroupExtensions(notificationMessage.getRelayNumber(), loadGroup.getPaoName());
        programEnrollment.setExtensions(extensions);

        drProgramEnrollments.add(programEnrollment);

        return arrayOfDRProgramEnrollment;

    }

    public void logDRNotificationResponse(String serialNumber, String mspMethod, MultispeakVendor mspVendor, String transactionId, String endpointUrl)
            throws MultispeakWebServiceClientException {
        List<ErrorObject> errObjects = new ArrayList<>();
        errObjects = multispeakFuncs.getErrorObjectsFromResponse();

        multispeakEventLogService.drNotificationReponse(mspMethod,
                                                        mspVendor.getCompanyName(),
                                                        serialNumber,
                                                        transactionId,
                                                        "",
                                                        CollectionUtils.size(errObjects),
                                                        endpointUrl);
        if (CollectionUtils.isNotEmpty(errObjects)) {
            multispeakFuncs.logErrorObjects(endpointUrl, mspMethod, errObjects);
        }
    }

    private XMLGregorianCalendar getDRProgramEnrollmentDate(Instant enrollmentTime) {

        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTimeInMillis(enrollmentTime.getMillis());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }

    private SingleIdentifier buildSingleIdentifier(String identifierName, String identifierLabel, String value) {

        SingleIdentifier singleIdentifier = new SingleIdentifier();
        singleIdentifier.setIdentifierName(identifierName);
        singleIdentifier.setIdentifierLabel(identifierLabel);
        singleIdentifier.setValue(value);
        return singleIdentifier;
    }

    private ServicePointID buildServicePointID() {

        ServicePointID servicePointID = new ServicePointID();
        servicePointID.setServiceType(ServiceKind.ELECTRIC);
        servicePointID.setValue(multispeakFuncs.getDefaultObjectGUID());
        return servicePointID;

    }

    private ObjectID buildDRProgramID(String objectGUID, String identifierName, String identifierLabel, String value) {
        ObjectID drProgramID = new ObjectID();
        drProgramID.setObjectGUID(objectGUID);
        SingleIdentifier programPrimaryIdentifier = buildSingleIdentifier(identifierName, identifierLabel, value);
        drProgramID.setPrimaryIdentifier(programPrimaryIdentifier);
        return drProgramID;

    }

    private boolean isvendorsConfigured() {
        if (vendorsToSendDRMsg.isEmpty()) {
            log.debug("Received multispeak DR message: not generating message because no vendors are configured");
            return false;
        }
        return true;

    }

    private Extensions buildRelayAndGroupExtensions(Integer relayNumber, String loadGroupName) {

        Extensions extensions = new Extensions();
        List<Object> any = extensions.getAny();

        Relay relay = new Relay();
        relay.setRelayNumber(relayNumber);
        any.add(relay);

        DRLoadGroupName group = new DRLoadGroupName();
        group.setGroupName(loadGroupName);
        any.add(group);
        return extensions;

    }

    private boolean isSupportedMethod(List<String> supportedMethods, String mspMethod) {
        return supportedMethods.stream().anyMatch(mspMethod::equalsIgnoreCase);
    }

    @Override
    public void optOutNotification(OptOutInNotificationMessage optOutNotificationMessage) {

        if (!isvendorsConfigured()) {
            return;
        }
        vendorsToSendDRMsg.entrySet().forEach(entry -> {
            boolean isMatched = isSupportedMethod(entry.getValue(), ENROLLMENT_METHOD);
            if (isMatched) {
                MultispeakVendor mspVendor = entry.getKey();
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);

                log.info("Sending " + ENROLLMENT_METHOD + "with type " + optOutNotificationMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);
                DRProgramUnenrollmentsNotification drProgramUnEnrollmentsNotification = new DRProgramUnenrollmentsNotification();
                String transactionId = String.valueOf(atomicLong.getAndIncrement());

                ArrayOfDRProgramEnrollment arrayOfDRProgramEnrollment = buildArrayOfDROptInOut(optOutNotificationMessage);
                drProgramUnEnrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDRProgramEnrollment);
                drProgramUnEnrollmentsNotification.setTransactionID(transactionId);
                try {
                    notClient.drProgramUnenrollmentsNotification(mspVendor, endpointUrl, drProgramUnEnrollmentsNotification);

                    String SerialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(optOutNotificationMessage.getInventoryId());

                    logDRNotificationResponse(SerialNumber, ENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);

                } catch (MultispeakWebServiceClientException e) {
                    log.error("TargetService: " + endpointUrl + " -" + ENROLLMENT_METHOD + "with type " + optOutNotificationMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") ");
                    log.error("MultispeakWebServiceClientException: " + e.getMessage());
                }
            }

        });
    }

    @Override
    public void optInNotification(OptOutInNotificationMessage optOutNotificationMessage) {
        if (!isvendorsConfigured()) {
            return;
        }

        vendorsToSendDRMsg.entrySet().forEach(entry -> {
            boolean isMatched = isSupportedMethod(entry.getValue(), UNENROLLMENT_METHOD);
            if (isMatched) {
                MultispeakVendor mspVendor = entry.getKey();
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
                log.info("Sending " + UNENROLLMENT_METHOD + "with type " + optOutNotificationMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

                DRProgramEnrollmentsNotification drProgramEnrollmentsNotification = new DRProgramEnrollmentsNotification();
                String transactionId = String.valueOf(atomicLong.getAndIncrement());

                ArrayOfDRProgramEnrollment arrayOfDRProgramEnrollment = buildArrayOfDROptInOut(optOutNotificationMessage);
                drProgramEnrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDRProgramEnrollment);
                drProgramEnrollmentsNotification.setTransactionID(transactionId);

                try {
                    notClient.drProgramEnrollmentsNotification(mspVendor, endpointUrl, drProgramEnrollmentsNotification);

                    String SerialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(optOutNotificationMessage.getInventoryId());

                    logDRNotificationResponse(SerialNumber, UNENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);

                } catch (MultispeakWebServiceClientException e) {
                    log.error("TargetService: " + endpointUrl + " -" + UNENROLLMENT_METHOD + "with type " + optOutNotificationMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);
                    log.error("MultispeakWebServiceClientException: " + e.getMessage());
                }
            }

        });

    }

    private ArrayOfDRProgramEnrollment buildArrayOfDROptInOut(OptOutInNotificationMessage optOutNotificationMessage) {

        ArrayOfDRProgramEnrollment arrayOfDRProgramEnrollment = new ArrayOfDRProgramEnrollment();

        List<DRProgramEnrollment> drProgramEnrollments = arrayOfDRProgramEnrollment.getDRProgramEnrollment();

        DRProgramEnrollment programEnrollment = new DRProgramEnrollment();
        programEnrollment.setObjectGUID(multispeakFuncs.getDefaultObjectGUID());
        if (optOutNotificationMessage.getMessageType() == DRNotificationMessageType.OPTOUT) {
            programEnrollment.getOtherAttributes().put(new QName("beginTemporaryOptOut"), "true");
            XMLGregorianCalendar xmlGregorianCalendarStartDate = getDRProgramEnrollmentDate(optOutNotificationMessage.getStartDate());
            if (xmlGregorianCalendarStartDate != null) {
                programEnrollment.setDRProgramParticStartDate(xmlGregorianCalendarStartDate);
            }
        } else {
            programEnrollment.getOtherAttributes().put(new QName("endTemporaryOptOut"), "true");
        }

        String serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(optOutNotificationMessage.getInventoryId());
        SingleIdentifier lcrPrimaryIdentifier = buildSingleIdentifier(LCR_INDENTIFIER_NAME, LCR_INDENTIFIER_LABEL, serialNumber);
        programEnrollment.setPrimaryIdentifier(lcrPrimaryIdentifier);

        ObjectID drProgramID = buildDRProgramID(multispeakFuncs.getDefaultObjectGUID(), "N/A", "N/A", "N/A");
        programEnrollment.setDRProgramID(drProgramID);

        programEnrollment.setDRProgramName("N/A");

        ServicePointID servicePointID = buildServicePointID();
        programEnrollment.setServicePointID(servicePointID);

        XMLGregorianCalendar xmlGregorianCalendarStopDate = getDRProgramEnrollmentDate(optOutNotificationMessage.getStopDate());
        if (xmlGregorianCalendarStopDate != null) {
            programEnrollment.setDRProgramParticEndDate(xmlGregorianCalendarStopDate);
        }
        drProgramEnrollments.add(programEnrollment);
        return arrayOfDRProgramEnrollment;

    }

}
