package com.cannontech.multispeak.service.impl.v5;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.service.RelayLogInterval;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfDRProgramEnrollment;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfFormattedBlock;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfIntervalData;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfMeterReading;
import com.cannontech.msp.beans.v5.commontypes.Duration;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.Extensions;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.ObjectID;
import com.cannontech.msp.beans.v5.commontypes.ServicePointID;
import com.cannontech.msp.beans.v5.commontypes.SingleIdentifier;
import com.cannontech.msp.beans.v5.enumerations.DRProgramEnrollmentStatus;
import com.cannontech.msp.beans.v5.enumerations.DRProgramEnrollmentStatusKind;
import com.cannontech.msp.beans.v5.enumerations.FieldNameKind;
import com.cannontech.msp.beans.v5.enumerations.TimeUnits;
import com.cannontech.msp.beans.v5.multispeak.Blocks;
import com.cannontech.msp.beans.v5.multispeak.Channels;
import com.cannontech.msp.beans.v5.multispeak.Chs;
import com.cannontech.msp.beans.v5.multispeak.DB;
import com.cannontech.msp.beans.v5.multispeak.DRProgramEnrollment;
import com.cannontech.msp.beans.v5.multispeak.EndReading;
import com.cannontech.msp.beans.v5.multispeak.EndReadings;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlock;
import com.cannontech.msp.beans.v5.multispeak.IntervalBlock;
import com.cannontech.msp.beans.v5.multispeak.IntervalChannel;
import com.cannontech.msp.beans.v5.multispeak.IntervalData;
import com.cannontech.msp.beans.v5.multispeak.IntervalProfile;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.Profiles;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCode;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeItem;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeItems;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeOption;
import com.cannontech.msp.beans.v5.multispeak.ReadingValue;
import com.cannontech.msp.beans.v5.multispeak.ReadingValues;
import com.cannontech.msp.beans.v5.not_server.DRProgramEnrollmentsNotification;
import com.cannontech.msp.beans.v5.not_server.DRProgramUnenrollmentsNotification;
import com.cannontech.msp.beans.v5.not_server.FormattedBlockNotification;
import com.cannontech.msp.beans.v5.not_server.IntervalDataNotification;
import com.cannontech.msp.beans.v5.not_server.MeterReadingsNotification;
import com.cannontech.multispeak.block.data.status.v5.ProgramStatusBlock;
import com.cannontech.multispeak.block.data.status.v5.ProgramStatusValList;
import com.cannontech.multispeak.block.data.v5.FormattedBlockBase;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncsBase;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.NOTClient;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.extension.DRLoadGroupName;
import com.cannontech.multispeak.extension.Relay;
import com.cannontech.multispeak.service.DrJmsMessageService;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.DrProgramStatusJmsMessage;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;
import com.cannontech.stars.dr.jms.message.OptOutOptInJmsMessage;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class DrJmsMessageListener implements DrJmsMessageService {

    @Autowired private NOTClient notClient;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private AssignedProgramDao assignedProgramDao;

    private static final Logger log = YukonLogManager.getLogger(DrJmsMessageListener.class);
    private AtomicLong atomicLong = new AtomicLong();
    private Executor executor = Executors.newCachedThreadPool();

    private ImmutableList<MultispeakVendor> vendorsToSendEnrollmentOrOptInMsg = ImmutableList.of();
    private ImmutableList<MultispeakVendor> vendorsToSendUnEnrollmentOrOptOutMsg = ImmutableList.of();
    private ImmutableList<MultispeakVendor> vendorsToSendIntervalDataMsg = ImmutableList.of();
    private ImmutableList<MultispeakVendor> vendorsToSendVoltageDataMsg = ImmutableList.of();
    private ImmutableList<MultispeakVendor> vendorsToSendProgramStatusMsg = ImmutableList.of();

    private static final String ENROLLMENT_METHOD = "DRProgramEnrollmentsNotification";
    private static final String UNENROLLMENT_METHOD = "DRProgramUnenrollmentsNotification";
    private static final String INTERVALDATA_METHOD = "IntervalDataNotification";
    private static final String VOLTAGEREADINGS_METHOD = "MeterReadingsNotification";
    private static final String PROGRAMSTATUS_METHOD = "FormattedBlockNotification";

    private static final String LCR_INDENTIFIER_NAME = "lcrSerial";
    private static final String LCR_INDENTIFIER_LABEL = "LCR Serial";
    private static final String PROGRAM_INDENTIFIER_NAME = "programName";
    private static final String PROGRAM_INDENTIFIER_LABEL = "Program Name";

    private static final QName QNAME_BEGIN_TEMPORARY_OPTOUT = new QName("beginTemporaryOptOut");
    private static final QName QNAME_END_TEMPORARY_OPTOUT = new QName("endTemporaryOptOut");

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.MULTISPEAK, (event) -> {
            loadDrSupportedVendors();
        });
        // To make this call asynchronous, added new thread for calling method.
        executor.execute(() -> {
            loadDrSupportedVendors();
        });

    }

    /**
     * Load vendors from database and add vendors into list based on NOT getMethod response.
     */

    private void loadDrSupportedVendors() {
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors(true);

        ImmutableList.Builder<MultispeakVendor> supportsEnrollmentOrOptIn = ImmutableList.builder();
        ImmutableList.Builder<MultispeakVendor> supportsUnEnrollmentOrOptOut = ImmutableList.builder();
        ImmutableList.Builder<MultispeakVendor> supportsIntervalData = ImmutableList.builder();
        ImmutableList.Builder<MultispeakVendor> supportsVoltageData = ImmutableList.builder();
        ImmutableList.Builder<MultispeakVendor> supportsProgramStatus = ImmutableList.builder();

        for (MultispeakVendor mspVendor : allVendors) {
            Pair<String, MultiSpeakVersion> keyPair = MultispeakVendor.buildMapKey(MultispeakDefines.NOT_Server_DR_STR, MultiSpeakVersion.V5);
            if (mspVendor.getMspInterfaceMap().get(keyPair) != null) {
                String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
                try {
                    List<String> mspMethodNames = notClient.getMethods(mspVendor, endpointUrl);

                    addSupportedVendors(mspMethodNames, ENROLLMENT_METHOD, mspVendor, supportsEnrollmentOrOptIn);
                    addSupportedVendors(mspMethodNames, UNENROLLMENT_METHOD, mspVendor, supportsUnEnrollmentOrOptOut);
                    addSupportedVendors(mspMethodNames, INTERVALDATA_METHOD, mspVendor, supportsIntervalData);
                    addSupportedVendors(mspMethodNames, VOLTAGEREADINGS_METHOD, mspVendor, supportsVoltageData);
                    addSupportedVendors(mspMethodNames, PROGRAMSTATUS_METHOD, mspVendor, supportsProgramStatus);

                } catch (MultispeakWebServiceClientException e) {
                    log.warn("caught exception in initialize", e);
                }
            }
        }

        vendorsToSendUnEnrollmentOrOptOutMsg = supportsEnrollmentOrOptIn.build();
        vendorsToSendEnrollmentOrOptInMsg = supportsUnEnrollmentOrOptOut.build();
        vendorsToSendIntervalDataMsg = supportsIntervalData.build();
        vendorsToSendVoltageDataMsg = supportsVoltageData.build();
        vendorsToSendProgramStatusMsg = supportsProgramStatus.build();
    }

    /**
     * Add into supportedVendors if Methods received from response are supported.
     */
    private void addSupportedVendors(List<String> supportedMethods, String mspMethod, MultispeakVendor mspVendor,
            ImmutableList.Builder<MultispeakVendor> supportedVendors) {
        boolean isSupportedMethod = supportedMethods.stream().anyMatch(mspMethod::equalsIgnoreCase);
        if (isSupportedMethod) {
            supportedVendors.add(mspVendor);
        }
    }
    
    /**
     * Check if supportedVendors is empty or not.
     */
    private boolean isVendorsConfigured(ImmutableList<MultispeakVendor> supportedVendors) {
        if (supportedVendors.isEmpty()) {
            log.debug("Received multispeak Dr message: not generating message because no vendors are configured");
            return false;
        }
        return true;

    }
 
    public void logEvent(String serialNumber, String messageType, String mspMethod, MultispeakVendor mspVendor, String transactionId, String endpointUrl)
            throws MultispeakWebServiceClientException {
        List<ErrorObject> errObjects = new ArrayList<>();
        errObjects = multispeakFuncs.getErrorObjectsFromResponse();

        multispeakEventLogService.drNotificationReponse(mspMethod,
                                                        mspVendor.getCompanyName(),
                                                        serialNumber,
                                                        transactionId,
                                                        messageType,
                                                        CollectionUtils.size(errObjects),
                                                        endpointUrl);
        if (CollectionUtils.isNotEmpty(errObjects)) {
            multispeakFuncs.logErrorObjects(endpointUrl, mspMethod, errObjects);
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                DrJmsMessage drMessage = (DrJmsMessage) objMessage.getObject();

                switch (drMessage.getMessageType()) {
                    case ENROLLMENT:
                        enrollmentNotification((EnrollmentJmsMessage) drMessage);
                        break;
                    case UNENROLLMENT:
                        unenrollmentNotification((EnrollmentJmsMessage) drMessage);
                        break;
                    case OPTOUT:
                        optOutNotification((OptOutOptInJmsMessage) drMessage);
                        break;
                    case STOPOPTOUT:
                        optInNotification((OptOutOptInJmsMessage) drMessage);
                        break;
                    case RELAYDATA:
                        intervalDataNotification((DrAttributeDataJmsMessage) drMessage);
                        break;
                    case VOLTAGEDATA:
                        voltageMeterReadingsNotification((DrAttributeDataJmsMessage) drMessage);
                        break;
                    case EVENT:
                        break;
                    case PROGRAMSTATUS:
                        programStatusNotificationNotification((DrProgramStatusJmsMessage) drMessage);
                        break;
                    default:
                        break;
                }
            } catch (JMSException e) {
                log.warn("Unable to extract multispeak Dr message", e);
            }
        }

    }

    @Override
    public void enrollmentNotification(EnrollmentJmsMessage enrollmentJmsMessage) {

        if (!isVendorsConfigured(vendorsToSendEnrollmentOrOptInMsg)) {
            return;
        }

        vendorsToSendEnrollmentOrOptInMsg.forEach(mspVendor -> {
            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
            String serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(enrollmentJmsMessage.getInventoryId());

            log.info("Sending " + ENROLLMENT_METHOD + ", Serial Number : " + serialNumber + " with Message Type : " + enrollmentJmsMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);
            DRProgramEnrollmentsNotification drProgramEnrollmentsNotification = new DRProgramEnrollmentsNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());

            ArrayOfDRProgramEnrollment arrayOfDrProgramEnrollment = buildArrayOfDrProgramEnrollment(enrollmentJmsMessage, serialNumber);
            drProgramEnrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDrProgramEnrollment);
            drProgramEnrollmentsNotification.setTransactionID(transactionId);
            try {
                notClient.drProgramEnrollmentsNotification(mspVendor, endpointUrl, drProgramEnrollmentsNotification);

                logEvent(serialNumber, enrollmentJmsMessage.getMessageType().toString(), ENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);
            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, ENROLLMENT_METHOD, enrollmentJmsMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending enrollmentNotification.", e);
            }

        });

    }

    @Override
    public void unenrollmentNotification(EnrollmentJmsMessage unEnrollmentMessage) {

        if (!isVendorsConfigured(vendorsToSendUnEnrollmentOrOptOutMsg)) {
            return;
        }

        vendorsToSendUnEnrollmentOrOptOutMsg.forEach(mspVendor -> {

            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
            String serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(unEnrollmentMessage.getInventoryId());

            log.info("Sending " + UNENROLLMENT_METHOD + ", Serial Number : " + serialNumber + " with Message Type : " + unEnrollmentMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

            DRProgramUnenrollmentsNotification drProgramUnenrollmentsNotification = new DRProgramUnenrollmentsNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());

            ArrayOfDRProgramEnrollment arrayOfDrProgramEnrollment = buildArrayOfDrProgramEnrollment(unEnrollmentMessage, serialNumber);
            drProgramUnenrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDrProgramEnrollment);
            drProgramUnenrollmentsNotification.setTransactionID(transactionId);

            try {
                notClient.drProgramUnenrollmentsNotification(mspVendor, endpointUrl, drProgramUnenrollmentsNotification);

                logEvent(serialNumber, unEnrollmentMessage.getMessageType().toString(), UNENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);

            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, UNENROLLMENT_METHOD, unEnrollmentMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending unenrollmentNotification.", e);
            }

        });

    }

    @Override
    public void optOutNotification(OptOutOptInJmsMessage optOutOptInJmsMessage) {

        if (!isVendorsConfigured(vendorsToSendUnEnrollmentOrOptOutMsg)) {
            return;
        }
        vendorsToSendUnEnrollmentOrOptOutMsg.forEach(mspVendor -> {

            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
            String serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(optOutOptInJmsMessage.getInventoryId());

            log.info("Sending " + ENROLLMENT_METHOD + ", Serial Number : " + serialNumber + " with Message Type : " + optOutOptInJmsMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);
            DRProgramUnenrollmentsNotification drProgramUnEnrollmentsNotification = new DRProgramUnenrollmentsNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());

            ArrayOfDRProgramEnrollment arrayOfDrProgramEnrollment = buildArrayOfDrOptInOut(optOutOptInJmsMessage, serialNumber);
            drProgramUnEnrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDrProgramEnrollment);
            drProgramUnEnrollmentsNotification.setTransactionID(transactionId);
            try {
                notClient.drProgramUnenrollmentsNotification(mspVendor, endpointUrl, drProgramUnEnrollmentsNotification);

                logEvent(serialNumber, optOutOptInJmsMessage.getMessageType().toString(), ENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);

            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, ENROLLMENT_METHOD, optOutOptInJmsMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending optOutNotification.", e);
            }

        });
    }

    @Override
    public void optInNotification(OptOutOptInJmsMessage optOutOptInJmsMessage) {
        if (!isVendorsConfigured(vendorsToSendEnrollmentOrOptInMsg)) {
            return;
        }

        vendorsToSendEnrollmentOrOptInMsg.forEach(mspVendor -> {

            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);
            String serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryId(optOutOptInJmsMessage.getInventoryId());

            log.info("Sending " + UNENROLLMENT_METHOD + ", Serial Number : " + serialNumber + " with Message Type : " + optOutOptInJmsMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

            DRProgramEnrollmentsNotification drProgramEnrollmentsNotification = new DRProgramEnrollmentsNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());

            ArrayOfDRProgramEnrollment arrayOfDrProgramEnrollment = buildArrayOfDrOptInOut(optOutOptInJmsMessage, serialNumber);
            drProgramEnrollmentsNotification.setArrayOfDRProgramEnrollment(arrayOfDrProgramEnrollment);
            drProgramEnrollmentsNotification.setTransactionID(transactionId);

            try {
                notClient.drProgramEnrollmentsNotification(mspVendor, endpointUrl, drProgramEnrollmentsNotification);


                logEvent(serialNumber, optOutOptInJmsMessage.getMessageType().toString(), UNENROLLMENT_METHOD, mspVendor, transactionId, endpointUrl);

            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, UNENROLLMENT_METHOD, optOutOptInJmsMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending optInNotification.", e);
            }

        });
    }
    

    @Override
    public void intervalDataNotification(DrAttributeDataJmsMessage drDataJmsMessage) {
        if (!isVendorsConfigured(vendorsToSendIntervalDataMsg)) {
            return;
        }

        vendorsToSendIntervalDataMsg.forEach(mspVendor -> {

            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);

            PaoIdentifier paoIdentifier = drDataJmsMessage.getPaoPointIdentifier().getPaoIdentifier();
            String serialNumber = lmHardwareBaseDao.getSerialNumberForDevice(paoIdentifier.getPaoId());

            log.info("Sending " + INTERVALDATA_METHOD + ", Serial Number : " + serialNumber + " with Message Type : "  + drDataJmsMessage.getMessageType() +  " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

            IntervalDataNotification intervalDataNotification = new IntervalDataNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());

            ArrayOfIntervalData arrayOfIntervalData = buildArrayOfIntervalData(drDataJmsMessage, serialNumber);
            intervalDataNotification.setArrayOfIntervalData(arrayOfIntervalData);
            intervalDataNotification.setTransactionID(transactionId);

            try {
                notClient.intervalDataNotification(mspVendor, endpointUrl, intervalDataNotification);

                logEvent(serialNumber, drDataJmsMessage.getMessageType().toString(), INTERVALDATA_METHOD, mspVendor, transactionId, endpointUrl);

            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, INTERVALDATA_METHOD, drDataJmsMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending intervalDataNotification.", e);
            }
        });

    }

    @Override
    public void voltageMeterReadingsNotification(DrAttributeDataJmsMessage drAttributeDataJmsMessage) {
        if (!isVendorsConfigured(vendorsToSendVoltageDataMsg)) {
            return;
        }

        vendorsToSendVoltageDataMsg.forEach(mspVendor -> {
            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);

            PaoIdentifier paoIdentifier = drAttributeDataJmsMessage.getPaoPointIdentifier().getPaoIdentifier();
            String serialNumber = lmHardwareBaseDao.getSerialNumberForDevice(paoIdentifier.getPaoId());

            log.info("Sending " + VOLTAGEREADINGS_METHOD + ", Serial Number : " + serialNumber + " with Message Type : " + drAttributeDataJmsMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

            MeterReadingsNotification meterReadingsNotification = new MeterReadingsNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());
            meterReadingsNotification.setTransactionID(transactionId);

            ArrayOfMeterReading arrayOfMeterReading = buildArrayOfMeterReadingsData(drAttributeDataJmsMessage, serialNumber);
            meterReadingsNotification.setArrayOfMeterReading(arrayOfMeterReading);

            try {
                notClient.meterReadingsNotification(mspVendor, endpointUrl, meterReadingsNotification);

                logEvent(serialNumber, drAttributeDataJmsMessage.getMessageType().toString(), VOLTAGEREADINGS_METHOD, mspVendor, transactionId, endpointUrl);
            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, VOLTAGEREADINGS_METHOD, drAttributeDataJmsMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending voltageMeterReadingsNotification.", e);
            }
        });

    }

    @Override
    public void programStatusNotificationNotification(DrProgramStatusJmsMessage drProgramStatusJmsMessage) {
        if (!isVendorsConfigured(vendorsToSendProgramStatusMsg)) {
            return;
        }

        vendorsToSendProgramStatusMsg.forEach(mspVendor -> {

            String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.NOT_Server_DR_STR);

            String programName = drProgramStatusJmsMessage.getProgramName();
            log.info("Sending " + PROGRAMSTATUS_METHOD + ", Program Name : " + programName + " with Message Type : " + drProgramStatusJmsMessage.getMessageType() + " (" + mspVendor.getCompanyName() + ") " + endpointUrl);

            FormattedBlockNotification formattedBlockNotification = new FormattedBlockNotification();
            String transactionId = String.valueOf(atomicLong.getAndIncrement());

            ArrayOfFormattedBlock arrayOfFormattedBlock = buildArrayOfFormattedBlock(drProgramStatusJmsMessage);
            formattedBlockNotification.setArrayOfFormattedBlock(arrayOfFormattedBlock);

            try {
                notClient.formattedBlockNotification(mspVendor, endpointUrl, formattedBlockNotification);

                List<ErrorObject> errObjects = new ArrayList<>();
                errObjects = multispeakFuncs.getErrorObjectsFromResponse();

                multispeakEventLogService.drProgramStatusNotificationReponse(PROGRAMSTATUS_METHOD,
                                                                mspVendor.getCompanyName().toString(),
                                                                programName,
                                                                transactionId,
                                                                drProgramStatusJmsMessage.getMessageType().toString(),
                                                                drProgramStatusJmsMessage.getProgramGearHistId(),
                                                                CollectionUtils.size(errObjects),
                                                                endpointUrl);
                if (CollectionUtils.isNotEmpty(errObjects)) {
                    multispeakFuncs.logErrorObjects(endpointUrl, PROGRAMSTATUS_METHOD, errObjects);
                }

            } catch (MultispeakWebServiceClientException e) {
                log.error("TargetService: {} - {} with type {} ({}).", endpointUrl, PROGRAMSTATUS_METHOD, drProgramStatusJmsMessage.getMessageType(), mspVendor.getCompanyName());
                log.error("Error sending programStatusNotification.", e);
            }
        });
    }

    /**
     * Building ArrayOfDRProgramEnrollment that includes building of request fields from enrollmentJmsMessage.
     */
    private ArrayOfDRProgramEnrollment buildArrayOfDrProgramEnrollment(EnrollmentJmsMessage enrollmentJmsMessage, String serialNumber) {

        ArrayOfDRProgramEnrollment arrayOfDrProgramEnrollment = new ArrayOfDRProgramEnrollment();

        List<DRProgramEnrollment> drProgramEnrollments = arrayOfDrProgramEnrollment.getDRProgramEnrollment();

        DRProgramEnrollment programEnrollment = new DRProgramEnrollment();

        programEnrollment.setObjectGUID(MultispeakFuncs.DEFAULT_OBJECT_GUID);

        SingleIdentifier lcrPrimaryIdentifier = MultispeakFuncs.buildSingleIdentifier(LCR_INDENTIFIER_NAME, LCR_INDENTIFIER_LABEL, serialNumber);
        programEnrollment.setPrimaryIdentifier(lcrPrimaryIdentifier);

        AssignedProgram program = assignedProgramDao.getById(enrollmentJmsMessage.getProgramId());
        ObjectID drProgramId = MultispeakFuncs.buildDrProgramID(MultispeakFuncs.getObjectGuid(program.getProgramId()),
                                                PROGRAM_INDENTIFIER_NAME,
                                                PROGRAM_INDENTIFIER_LABEL,
                                                program.getProgramName());
        programEnrollment.setDRProgramID(drProgramId);

        programEnrollment.setDRProgramName(program.getProgramName());

        ServicePointID servicePointId = MultispeakFuncs.buildDrServicePointID();
        programEnrollment.setServicePointID(servicePointId);

        DRProgramEnrollmentStatus status = new DRProgramEnrollmentStatus();
        if (enrollmentJmsMessage.getMessageType() == DrJmsMessageType.ENROLLMENT) {
            status.setValue(DRProgramEnrollmentStatusKind.ACTIVE);
        } else {
            status.setValue(DRProgramEnrollmentStatusKind.INACTIVE);
        }
        programEnrollment.setDRProgramEnrollmentStatus(status);

        XMLGregorianCalendar xmlGregorianCalendarStartTime = MultispeakFuncs.toXMLGregorianCalendar(enrollmentJmsMessage.getEnrollmentStartTime());
        if (xmlGregorianCalendarStartTime != null) {
            programEnrollment.setDRProgramParticStartDate(xmlGregorianCalendarStartTime);
        }

        if (enrollmentJmsMessage.getMessageType() == DrJmsMessageType.UNENROLLMENT) {

            XMLGregorianCalendar  XmlGregorianCalendarStopTime = MultispeakFuncs.toXMLGregorianCalendar(enrollmentJmsMessage.getEnrollmentStopTime());
            if (XmlGregorianCalendarStopTime != null) {
                programEnrollment.setDRProgramParticEndDate(XmlGregorianCalendarStopTime);
            }
        }

        LiteYukonPAObject loadGroup = databaseCache.getAllPaosMap().get(enrollmentJmsMessage.getLoadGroupId());
        
        Extensions extensions = buildProgramEnrollmentExtensions(enrollmentJmsMessage.getRelayNumber(), loadGroup.getPaoName());
        programEnrollment.setExtensions(extensions);

        drProgramEnrollments.add(programEnrollment);

        return arrayOfDrProgramEnrollment;

    }

    /**
     * Building Relay and Program extension for enrollment/unenrollment.
     */
    private Extensions buildProgramEnrollmentExtensions(Integer relayNumber, String loadGroupName) {

        Extensions extensions = new Extensions();
        List<Object> any = extensions.getAny();

        Relay relay = new Relay(relayNumber);
        any.add(relay);

        DRLoadGroupName group = new DRLoadGroupName(loadGroupName);
        any.add(group);
        return extensions;

    }


    /**
     * Building ArrayOfDRProgramEnrollment that includes building of request fields from optOutOptInJmsMessage.
     */
    private ArrayOfDRProgramEnrollment buildArrayOfDrOptInOut(OptOutOptInJmsMessage optOutOptInJmsMessage, String serialNumber) {

        ArrayOfDRProgramEnrollment arrayOfDrProgramEnrollment = new ArrayOfDRProgramEnrollment();

        List<DRProgramEnrollment> drProgramEnrollments = arrayOfDrProgramEnrollment.getDRProgramEnrollment();

        DRProgramEnrollment programEnrollment = new DRProgramEnrollment();
        programEnrollment.setObjectGUID(MultispeakFuncs.DEFAULT_OBJECT_GUID);
        if (optOutOptInJmsMessage.getMessageType() == DrJmsMessageType.OPTOUT) {
            programEnrollment.getOtherAttributes().put(QNAME_BEGIN_TEMPORARY_OPTOUT, "true");
            XMLGregorianCalendar xmlGregorianCalendarStartDate =  MultispeakFuncs.toXMLGregorianCalendar(optOutOptInJmsMessage.getStartDate());
            if (xmlGregorianCalendarStartDate != null) {
                programEnrollment.setDRProgramParticStartDate(xmlGregorianCalendarStartDate);
            }
        } else {
            programEnrollment.getOtherAttributes().put(QNAME_END_TEMPORARY_OPTOUT, "true");
        }

        SingleIdentifier lcrPrimaryIdentifier = MultispeakFuncs.buildSingleIdentifier(LCR_INDENTIFIER_NAME, LCR_INDENTIFIER_LABEL, serialNumber);
        programEnrollment.setPrimaryIdentifier(lcrPrimaryIdentifier);

        ObjectID drProgramId = MultispeakFuncs.buildDrProgramID(MultispeakFuncs.DEFAULT_OBJECT_GUID, "N/A", "N/A", "N/A");
        programEnrollment.setDRProgramID(drProgramId);

        programEnrollment.setDRProgramName("N/A");

        ServicePointID servicePointId = MultispeakFuncs.buildDrServicePointID();
        programEnrollment.setServicePointID(servicePointId);

        XMLGregorianCalendar xmlGregorianCalendarStopDate =  MultispeakFuncs.toXMLGregorianCalendar(optOutOptInJmsMessage.getStopDate());
        if (xmlGregorianCalendarStopDate != null) {
            programEnrollment.setDRProgramParticEndDate(xmlGregorianCalendarStopDate);
        }
        drProgramEnrollments.add(programEnrollment);
        return arrayOfDrProgramEnrollment;

    }

    /**
     * Building ArrayOfIntervalData that includes building of request fields from drAttributeDataJmsMessage.
     */
    private ArrayOfIntervalData buildArrayOfIntervalData(DrAttributeDataJmsMessage drAttributeDataJmsMessage, String serialNumber) {

        Set<BuiltInAttribute> attributes = getBuiltInAttributesFromDrMessage(drAttributeDataJmsMessage);

        Multimap<RelayLogInterval, BuiltInAttribute> intervalAttributesMap = HashMultimap.create();

        attributes.forEach(relayAttribute -> {
            RelayIntervalData relayIntervalData = RelayIntervalData.getRelayIntervalData(relayAttribute);
            RelayLogInterval logInterval = relayIntervalData.getRelayLogInterval(relayAttribute);
            intervalAttributesMap.put(logInterval, relayAttribute);
        });

        ArrayOfIntervalData arrayOfIntervalData = new ArrayOfIntervalData();
        List<IntervalData> intervalDataList = arrayOfIntervalData.getIntervalData();

        IntervalData intervalData = new IntervalData();

        Profiles profiles = getProfiles(intervalAttributesMap.asMap());
        intervalData.setProfiles(profiles);

        Blocks blocks = getBlocks(drAttributeDataJmsMessage, serialNumber);
        intervalData.setBlocks(blocks);
 
      //  intervalData.setIntervalDelimiter(",");
      //  intervalData.setStatusDelimiter("^");

        intervalDataList.add(intervalData);
        return arrayOfIntervalData;

    }

    
    /**
     * Filtering attributes from jms data messages.
     */
    private Set<BuiltInAttribute> getBuiltInAttributesFromDrMessage(DrAttributeDataJmsMessage drAttributeDataJmsMessage) {
        Set<BuiltInAttribute> attributes = drAttributeDataJmsMessage.getAttributeDataList()
                                                                    .stream()
                                                                    .map(message -> message.getAttribute())
                                                                    .collect(Collectors.toSet());
        return attributes;
    }

    /**
     * Creating Profiles object that contains request fields( Duration, Index(Relay Number), Relay extension).
     */
    private Profiles getProfiles(Map<RelayLogInterval, Collection<BuiltInAttribute>> intervalAttributesMap) {
        Profiles profiles = new Profiles();
        List<IntervalProfile> profileList = profiles.getProfile();

        intervalAttributesMap.entrySet().forEach(entry -> {
            RelayLogInterval logInterval = entry.getKey();

            IntervalProfile intervalProfile = new IntervalProfile();

            Duration duration = new Duration();
            duration.setUnits(TimeUnits.MINUTES);
            duration.setValue(logInterval.getDuration().getStandardMinutes());
            intervalProfile.setIntervalLength(duration);

            Channels channels = new Channels();
            List<IntervalChannel> channelList = channels.getChannel();

            entry.getValue().stream().forEach(attr -> {
                RelayIntervalData relayIntervalData = RelayIntervalData.getRelayIntervalData(attr);

                IntervalChannel intervalChannel = new IntervalChannel();
                intervalChannel.setIndex(BigInteger.valueOf(relayIntervalData.getRelayNumber()));
                ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                readingTypeCode.setValue(relayIntervalData.getRelayDataReadingTypeCodeString());
                intervalChannel.setReadingTypeCode(readingTypeCode);

                Extensions extensions = MultispeakFuncs.getRelayExtension(relayIntervalData.getRelayNumber());
                intervalChannel.setExtensions(extensions);

                channelList.add(intervalChannel);
            });

            intervalProfile.setChannels(channels);
            profileList.add(intervalProfile);
        });

        return profiles;
        
    }

    /**
     * Creating Blocks object that contains request fields (EndReadings, MeterID and IntervalStart).
     */
    private Blocks getBlocks(DrAttributeDataJmsMessage drAttributeDataJmsMessage, String serialNumber){
        
        Blocks blocks = new Blocks();
        List<IntervalBlock> blockList = blocks.getBlock();
        IntervalBlock intervalBlock = new IntervalBlock();

        EndReadings endReadings = getEndReadings(drAttributeDataJmsMessage);
        intervalBlock.setEndReadings(endReadings);

        MeterID meterId = MultispeakFuncs.getDrMeterID(serialNumber);
        intervalBlock.setMeterID(meterId);

        intervalBlock.setIntervalStart(MultispeakFuncsBase.toXMLGregorianCalendar(drAttributeDataJmsMessage.getAttributeDataList().get(0).getTimeStamp()));
        intervalBlock.setDB(getDB());

        blockList.add(intervalBlock);
        return blocks;
    }

    /**
     * Creating DB object.
     */
    private DB getDB() {
        DB db = new DB();
        Chs chs = new Chs();
        db.setChs(chs);
        return db;
    }

    /**
     * Creating EndReadings object that contains request fields (data value, timestamp and relay number(channelIndex)).
     */
    private EndReadings getEndReadings(DrAttributeDataJmsMessage drDataJmsMessage) {

        EndReadings endReadings = new EndReadings();
        List<EndReading> endReadingList = endReadings.getEndReading();

        drDataJmsMessage.getAttributeDataList().forEach(message -> {
            RelayIntervalData relayIntervalData = RelayIntervalData.getRelayIntervalData(message.getAttribute());

            EndReading endReading = new EndReading();
            endReading.setChannelIndex(BigInteger.valueOf(relayIntervalData.getRelayNumber()));
            endReading.setReading(String.valueOf(message.getValue()));
            endReading.setReadingDate(MultispeakFuncsBase.toXMLGregorianCalendar(message.getTimeStamp()));
            endReadingList.add(endReading);
        });
        return endReadings;
    }
    
    /**
     * Building ArrayOfMeterReading that includes building of request fields from drAttributeDataJmsMessage.
     */
    private ArrayOfMeterReading buildArrayOfMeterReadingsData(DrAttributeDataJmsMessage drAttributeDataJmsMessage, String serialNumber) {

        ArrayOfMeterReading arrayOfMeterReading = new ArrayOfMeterReading();
        List<MeterReading> meterReadingList = arrayOfMeterReading.getMeterReading();

        MeterReading meterReading = new MeterReading();

        MeterID meterId = MultispeakFuncs.getDrMeterID(serialNumber);
        meterReading.setMeterID(meterId);

        ReadingTypeCodeItems readingTypeCodeItems = getReadingTypeCodeItems(drAttributeDataJmsMessage, meterReading);
        meterReading.setReadingTypeCodeItems(readingTypeCodeItems);

        ReadingValues readingValues = getReadingValues(drAttributeDataJmsMessage, meterReading);
        meterReading.setReadingValues(readingValues);

        meterReadingList.add(meterReading);

        return arrayOfMeterReading;
    }

    /**
     * Building ReadingValues for attributeDataJmsMessage.
     */
    private ReadingValues getReadingValues(DrAttributeDataJmsMessage attributeDataJmsMessage, MeterReading meterReading) {

        ReadingValues readingValues = new ReadingValues();
        List<ReadingValue> readingValue = readingValues.getReadingValue();

        attributeDataJmsMessage.getAttributeDataList().forEach(message -> {

            ReadingValue value = new ReadingValue();
            value.setValue(String.valueOf(message.getValue()));
            value.setTimeStamp(MultispeakFuncsBase.toXMLGregorianCalendar(message.getTimeStamp()));

            ReadingTypeCodeOption readingTypeCodeOption = new ReadingTypeCodeOption();
            ReadingTypeCode readingTypeCode = getReadingTypeCode(message.getAttribute());
            readingTypeCodeOption.setReadingTypeCode(readingTypeCode);
            readingTypeCodeOption.setReadingTypeCodeRef("N/A");
            value.setReadingTypeCodeOption(readingTypeCodeOption);

            readingValue.add(value);
        });

        return readingValues;

    }

    /**
     * Building ReadingTypeCodeItems for attributeDataJmsMessage.
     */
    private ReadingTypeCodeItems getReadingTypeCodeItems(DrAttributeDataJmsMessage attributeDataJmsMessage, MeterReading meterReading) {

        ReadingTypeCodeItems readingTypeCodeItems = new ReadingTypeCodeItems();
        List<ReadingTypeCodeItem> readingTypeCodeItemList = readingTypeCodeItems.getReadingTypeCodeItem();

        attributeDataJmsMessage.getAttributeDataList().forEach(message -> {
            ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
            ReadingTypeCode readingTypeCode = getReadingTypeCode(message.getAttribute());
            readingTypeCodeItem.setReadingTypeCode(readingTypeCode);
            readingTypeCodeItem.setReadingTypeCodeRef("N/A");
            readingTypeCodeItemList.add(readingTypeCodeItem);
        });

        return readingTypeCodeItems;
    }
    

    /**
     * Building ReadingTypeCode for attributeDataJmsMessage.
     */

    private ReadingTypeCode getReadingTypeCode(BuiltInAttribute attribute) {

        VoltageFieldKindName voltageFieldKindName = VoltageFieldKindName.getVoltageFieldKindNameData(attribute);
        ReadingTypeCode readingTypeCode = new ReadingTypeCode();

        if (FieldNameKind.OTHER != voltageFieldKindName.getfieldNameKind()) {
            readingTypeCode.setFieldName(voltageFieldKindName.getfieldNameKind());
        } else {
            readingTypeCode.setOtherFieldName(voltageFieldKindName.getfieldName());
        }

        return readingTypeCode;

    }
    
    
    /**
     * Build ArrayOfFormattedBlock that includes building of request fields from statusMessage.
     */

    private ArrayOfFormattedBlock buildArrayOfFormattedBlock(DrProgramStatusJmsMessage statusMessage) {

        ArrayOfFormattedBlock arrayOfFormattedBlock = new ArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();

        ProgramStatusBlock block = new ProgramStatusBlock(statusMessage.getProgramGearHistId(),
                                                          statusMessage.getProgramName(),
                                                          statusMessage.getGearName(),
                                                          statusMessage.getProgramStatusType().name(),
                                                          statusMessage.getStartDateTime(),
                                                          statusMessage.getStopDateTime(),
                                                          statusMessage.getGearChangeTime());
        ProgramStatusValList programStatusValList = new ProgramStatusValList(Collections.singletonList(block));
        
        // TODO Noun Type of program is to be decided 
        FormattedBlock formattedBlock = FormattedBlockBase.createMspFormattedBlock(programStatusValList,"Load Control");
        formattedBlockList.add(formattedBlock);

        return arrayOfFormattedBlock;
    }


}
