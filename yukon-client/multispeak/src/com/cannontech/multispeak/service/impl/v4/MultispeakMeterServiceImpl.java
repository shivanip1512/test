package com.cannontech.multispeak.service.impl.v4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.search.dao.MeterSearchDao;
import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MeterSearchOrderBy;
import com.cannontech.amr.meter.search.model.StandardFilterBy;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.exception.InsufficientMultiSpeakDataException;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakManagePaoLocation;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.message.porter.message.Request;
import com.cannontech.msp.beans.v4.ArrayOfExtensionsItem;
import com.cannontech.msp.beans.v4.ArrayOfModule;
import com.cannontech.msp.beans.v4.ElectricMeter;
import com.cannontech.msp.beans.v4.ElectricService;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ExtensionsItem;
import com.cannontech.msp.beans.v4.GasService;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.Module;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.MspObject;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.msp.beans.v4.WaterService;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.data.v4.MspErrorObjectException;
import com.cannontech.multispeak.event.v4.MeterReadEvent;
import com.cannontech.multispeak.event.v4.MultispeakEvent;
import com.cannontech.multispeak.service.MultispeakMeterServiceBase;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.Lists;

public class MultispeakMeterServiceImpl extends MultispeakMeterServiceBase implements MultispeakMeterService {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    private BasicServerConnection porterConnection;

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private MeterSearchDao meterSearchDao;
    @Autowired private GlobalSettingDao globalSettingDao;

    private static final String SERV_LOC_CHANGED_STRING = "ServiceLocationChangedNotification";

    /** Singleton incrementor for messageIDs to send to porter connection */
    private static long messageID = 1;

    /** A map of Long(userMessageID) to MultispeakEvent values */
    private static Map<Long, MultispeakEvent> eventsMap = Collections.synchronizedMap(new HashMap<Long, MultispeakEvent>());

    /**
     * generate a unique messageId, don't let it be negative
     */
    private synchronized long generateMessageID() {
        if (++messageID == Long.MAX_VALUE) {
            messageID = 1;
        }
        return messageID;
    }

    /**
     * Returns true if event processes without timing out, false if event times
     * out.
     * 
     * @param event
     * @return
     */
    private boolean waitOnEvent(MultispeakEvent event, long timeout) {

        long millisTimeOut = 0; //
        while (!event.isPopulated() && millisTimeOut < timeout) // quit after
                                                                // timeout
        {
            try {
                Thread.sleep(10);
                millisTimeOut += 10;
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        if (millisTimeOut >= timeout) {// this broke the loop, more than likely,
                                       // have to kill it sometime
            return false;
        }
        return true;
    }

    private static Map<Long, MultispeakEvent> getEventsMap() {
        return eventsMap;
    }

    /**
     * This method still does not support attributes.
     * The issue is that SEDC does not support the ability to receive ReadingChangedNotification messages.
     * Therefore, just for them, we initiate a real time read, wait, and return.
     * This DOES need to be changed in future versions.
     */
    @Override
    public MeterReading getLatestReadingInterrogate(MultispeakVendor mspVendor, YukonMeter meter, String responseUrl) {
        long id = generateMessageID();
        MeterReadEvent event = new MeterReadEvent(mspVendor, id, meter, responseUrl);

        getEventsMap().put(new Long(id), event);

        String commandStr = "getvalue kwh";
        if (DeviceTypesFuncs.isMCT4XX(meter.getPaoIdentifier().getPaoType())) {
            commandStr = "getvalue peak"; // getvalue peak returns the peak kW and the total kWh
        }

        final String meterNumber = meter.getMeterNumber();
        log.info("Received " + meterNumber + " for LatestReadingInterrogate from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterRead(meterNumber, meter, "N/A", "GetLatestReadingByMeterID",
                mspVendor.getCompanyName());

        // Writes a request to pil for the meter and commandStr using the id for mspVendor.
        commandStr += " update noqueue";
        Request pilRequest = new Request(meter.getPaoIdentifier().getPaoId(), commandStr, id);
        pilRequest.setPriority(13);
        porterConnection.write(pilRequest);

        systemLog("GetLatestReadingByMeterID",
                "(ID:" + meter.getPaoIdentifier().getPaoId() + ") MeterNumber (" + meterNumber + ") - " + commandStr, mspVendor);

        synchronized (event) {
            boolean timeout = !waitOnEvent(event, mspVendor.getRequestMessageTimeout());
            if (timeout) {
                mspObjectDao.logMSPActivity("GetLatestReadingByMeterID",
                        "MeterNumber (" + meterNumber + ") - Reading Timed out after " +
                                (mspVendor.getRequestMessageTimeout() / 1000) + " seconds.  No reading collected.",
                        mspVendor.getCompanyName());
            }
        }

        return event.getDevice().getMeterReading();
    }

    /**
     * Extra SystemLog logging that will be completely removed upon completion of MultiSpeak EventLogs.
     * Only use this method if you intend for the logging to be removed with EventLogs completion.
     */
    @Deprecated
    private void systemLog(String mspMethod, String description, MultispeakVendor mspVendor) {
        mspObjectDao.logMSPActivity(mspMethod, description, mspVendor.getCompanyName());
    }

    @Override
    public List<ErrorObject> initiateUsageMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs) {

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterNo()).collect(Collectors.toList());

        return addMetersToGroup(mspMeters, SystemGroupEnum.USAGE_MONITORING, "InitiateUsageMonitoring", mspVendor);
    }

    /**
     * Helper method to add meterNos to systemGroup
     */
    private List<ErrorObject> addMetersToGroup(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {
        return addToGroupAndDisable(meterNos, systemGroup, mspMethod, mspVendor, false);
    }

    /**
     * Helper method to add meterNos to systemGroup
     * 
     * @param disable - when true, the meter will be disabled. Else no change.
     */
    private List<ErrorObject> addToGroupAndDisable(List<String> meterNos, SystemGroupEnum systemGroup,
            String mspMethod, MultispeakVendor mspVendor, boolean disable) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                if (disable && !meter.isDisabled()) {
                    deviceUpdateService.disableDevice(meter);
                }
                addMeterToGroup(meter, systemGroup, mspMethod, mspVendor);
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, mspMethod, mspVendor.getCompanyName());
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID", "addToGroup",
                        mspVendor.getCompanyName(), e.getMessage());
                errorObjects.add(err);
                log.error(e);
            }
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> cancelUsageMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs) {

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterNo()).collect(Collectors.toList());

        return removeMetersFromGroup(mspMeters, SystemGroupEnum.USAGE_MONITORING, "CancelUsageMonitoring", mspVendor);
    }

    /**
     * Helper method to remove meterNos from systemGroup
     */
    private List<ErrorObject> removeMetersFromGroup(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {
        return removeMetersFromGroupAndEnable(meterNos, systemGroup, mspMethod, mspVendor, false);
    }

    /**
     * Helper method to remove meterNos from systemGroup
     * 
     * @param disable - when true, the meter will be enabled. Else no change.
     */
    private List<ErrorObject> removeMetersFromGroupAndEnable(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor, boolean enable) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                if (enable && meter.isDisabled()) {
                    deviceDao.enableDevice(meter);
                }
                removeFromGroup(meter, systemGroup, mspMethod, mspVendor);
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, mspMethod, mspVendor.getCompanyName());
                ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNumber,
                                                                      "MeterNumber",
                                                                      "MeterID",
                                                                      "removeFromGroup",
                                                                      mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(e);
            }
        }

        return errorObjects;
    }

    @Override
    public List<ErrorObject> serviceLocationChanged(MultispeakVendor mspVendor, List<ServiceLocation> serviceLocations) {
        final ArrayList<ErrorObject> errorObjects = new ArrayList<>();
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        for (final ServiceLocation serviceLocation : serviceLocations) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        boolean isMeterFound = false;
                        if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {

                            List<MspObject> mspObjects = new ArrayList<>();
                            if (serviceLocation.getElectricServiceList() != null) {
                                mspObjects.addAll(serviceLocation.getElectricServiceList().getElectricService());
                            }
                            if (serviceLocation.getWaterServiceList() != null) {
                                mspObjects.addAll(serviceLocation.getWaterServiceList().getWaterService());
                            }
                            if (serviceLocation.getGasServiceList() != null) {
                                mspObjects.addAll(serviceLocation.getGasServiceList().getGasService());
                            }

                            for (MspObject mspObject : mspObjects) {
                                String meterNo = null;
                                MspMeter mspMeter = null;
                                String billingCycle = null;
                                if (mspObject instanceof ElectricService) {
                                    ElectricService electricService = (ElectricService) mspObject;
                                    meterNo = electricService.getElectricMeterID();
                                    mspMeter = electricService.getMeterBase().getElectricMeter();
                                    billingCycle = electricService.getBillingCycle();
                                } else if (mspObject instanceof WaterService) {
                                    WaterService waterService = (WaterService) mspObject;
                                    meterNo = waterService.getWaterMeterID();
                                    mspMeter = waterService.getWaterMeter();
                                    billingCycle = waterService.getBillingCycle();
                                } else if (mspObject instanceof GasService) {
                                    GasService gasService = (GasService) mspObject;
                                    meterNo = gasService.getGasMeterID();
                                    mspMeter = gasService.getGasMeter();
                                    billingCycle = gasService.getBillingCycle();
                                }

                                // if above both optional fields (meterID/mspMeter ) are not present in ServiceLocation then
                                // should we need to send any error message.
                                if (meterNo == null && mspMeter == null) {
                                    ErrorObject err = mspObjectDao.getNotFoundErrorObject("MeterID and MspMeter", "Meter",
                                            "ServiceLocation", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName(),
                                            "not present in ServiceLocation");
                                    errorObjects.add(err);
                                } else {
                                    YukonMeter meter = null;
                                    // TODO probably need to update code after confirmation on actual meter field population
                                    // (parent
                                    // or child field)
                                    if (meterNo != null) {
                                        try {
                                            meter = getMeterByMeterNumber(meterNo);
                                            isMeterFound = true;
                                        } catch (NotFoundException e) {
                                            multispeakEventLogService.meterNotFound(meterNo,
                                                    SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            ErrorObject err = mspObjectDao.getNotFoundErrorObject(meterNo,
                                                    "MeterNumber",
                                                    "ServiceLocation",
                                                    SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            errorObjects.add(err);
                                            multispeakEventLogService.errorObject(err.getErrorString(),
                                                    SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            log.error(e);
                                        }

                                    }

                                    if (!isMeterFound && mspMeter != null) {
                                        try {
                                            meter = getMeterByMeterNumber(mspMeter.getMeterNo());
                                        } catch (NotFoundException e) {
                                            multispeakEventLogService.meterNotFound(
                                                    mspMeter.getMeterNo(), SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            ErrorObject err = mspObjectDao.getNotFoundErrorObject(
                                                    mspMeter.getMeterNo(),
                                                    "MeterNumber",
                                                    "ServiceLocation",
                                                    SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            errorObjects.add(err);
                                            multispeakEventLogService.errorObject(err.getErrorString(),
                                                    SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            log.error(e);
                                        }
                                    }

                                    if (meter != null) {
                                        // update the billing group from request
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING,
                                                mspVendor);
                                        updatePaoLocation(serviceLocation, meter, SERV_LOC_CHANGED_STRING);
                                        // using null for mspMeter. See comments in getSubstationNameFromMspMeter(...)
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, null, SERV_LOC_CHANGED_STRING);
                                    }

                                }
                            }
                        } else {
                            // Must get meters from MSP CB call to process.
                            List<MspMeter> mspMeters = mspObjectDao.getMspMetersByServiceLocation(serviceLocation, mspVendor);

                            if (!mspMeters.isEmpty()) {

                                for (MspMeter mspMeter : mspMeters) {
                                    try {
                                        YukonMeter meter = getMeterByMeterNumber(mspMeter.getMeterNo());

                                        // MeterNumber should not have changed, nor communication
                                        // address...only paoName possibly
                                        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
                                        verifyAndUpdatePaoName(newPaoName, meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                        String mspMeterDeviceClass = getDeviceClassForMspMeter(mspMeter);

                                        updateCISDeviceClassGroup(mspMeter.getMeterNo(), mspMeterDeviceClass, meter,
                                                SERV_LOC_CHANGED_STRING, mspVendor);

                                        String billingCycle = mspMeter.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter, SERV_LOC_CHANGED_STRING,
                                                mspVendor);
                                        updatePaoLocation(serviceLocation, meter, SERV_LOC_CHANGED_STRING);
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, mspMeter,
                                                SERV_LOC_CHANGED_STRING);
                                    } catch (NotFoundException e) {
                                        multispeakEventLogService.meterNotFound(mspMeter.getMeterNo(),
                                                SERV_LOC_CHANGED_STRING,
                                                mspVendor.getCompanyName());
                                        ErrorObject err = mspObjectDao.getNotFoundErrorObject(mspMeter.getMeterNo(),
                                                "MeterNumber",
                                                "MspMeter",
                                                SERV_LOC_CHANGED_STRING,
                                                mspVendor.getCompanyName());
                                        errorObjects.add(err);
                                        multispeakEventLogService.errorObject(err.getErrorString(),
                                                SERV_LOC_CHANGED_STRING,
                                                mspVendor.getCompanyName());
                                        log.error(e);
                                    }
                                }
                            } else {
                                multispeakEventLogService.objectNotFoundByVendor(serviceLocation.getObjectID(),
                                        "GetMetersByServiceLocationIDs",
                                        SERV_LOC_CHANGED_STRING,
                                        mspVendor.getCompanyName());
                                ErrorObject err = mspObjectDao.getErrorObject(
                                        serviceLocation.getObjectID(),
                                        paoAlias.getDisplayName() + " ServiceLocation("
                                                + serviceLocation.getObjectID()
                                                + ") - No meters returned from vendor for location.",
                                        "ServiceLocation",
                                        SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING,
                                        mspVendor.getCompanyName());
                            }
                        }
                    }
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getErrorString(), SERV_LOC_CHANGED_STRING,
                        mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(serviceLocation.getObjectID(),
                        "X Exception: (ServLoc:" + serviceLocation.getObjectID() + ")-" + ex.getMessage(),
                        "ServiceLocation",
                        SERV_LOC_CHANGED_STRING,
                        mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err = mspObjectDao.getErrorObject(serviceLocation.getObjectID(),
                        "X Error: (ServLoc:" + serviceLocation.getObjectID() + ")-" + ex.getMessage(),
                        "ServiceLocation",
                        SERV_LOC_CHANGED_STRING,
                        mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getErrorString(), SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                log.error(ex);
            }
        }

        return errorObjects;
    }

    /**
     * @param mspMeter - multispeak meter
     * @return returns Device class for particular multispeak meter
     */
    private String getDeviceClassForMspMeter(MspMeter mspMeter) {
        String deviceClass = null;
        ArrayOfModule moduleList = mspMeter.getModuleList();

        if (moduleList != null) {
            List<Module> ListOfModule = moduleList.getModule();
            if (CollectionUtils.isNotEmpty(ListOfModule)) {
                Module module = ListOfModule.get(0);
                deviceClass = null != module ? module.getDeviceClass() : null;
            }
        }
        return deviceClass;
    }

    /**
     * Update the (CIS) Substation Group.
     * If changed, update route (perform route locate).
     * If substationName is blank, do nothing.
     * 
     * @param meter     - the meter to modify
     * @param mspVendor
     * @param mspMeter  - the multispeak meter to process (if null, most likely will not change substation and
     *                  routing info)
     *                  - See {@link #getSubstationNameFromMspObjects(MspMeter, ServiceLocation, MultispeakVendor)}
     */
    private void verifyAndUpdateSubstationGroupAndRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor,
            MspMeter mspMeter, String mspMethod) {

        String meterNumber = meterToUpdate.getMeterNumber();

        // Verify substation name
        String substationName = getSubstationNameFromMspObjects(mspMeter, mspVendor);
        // Validate, verify and update substationName
        checkAndUpdateSubstationName(substationName, meterNumber, mspMethod, mspVendor, meterToUpdate);
    }

    /**
     * Return the substation name of a Meter. If the
     * Meter does not contain a substation name in its utility info, return
     * null.
     * 
     * @return String substationName
     */
    private String getSubstationNameFromMspObjects(MspMeter mspMeter, MultispeakVendor mspVendor) {
        if (mspMeter instanceof ElectricMeter) {
            ElectricMeter electricMeter = (ElectricMeter) mspMeter;
            if (electricMeter.getElectricLocationFields() == null
                    || electricMeter.getElectricLocationFields().getSubstationName() == null || StringUtils.isBlank(
                            electricMeter.getElectricLocationFields().getSubstationName())) {
                return null;
            } else {
                return electricMeter.getElectricLocationFields().getSubstationName();
            }
        }
        return null;
    }

    /**
     * Update the paolocation coordinates based on SERVICELOCATION object.
     */
    private void updatePaoLocation(ServiceLocation mspServiceLocation, YukonMeter meterToUpdate, String mspMethod) {
        MultispeakManagePaoLocation managePaoLocation = globalSettingDao.getEnum(GlobalSettingType.MSP_MANAGE_PAO_LOCATION,
                MultispeakManagePaoLocation.class);
        if (managePaoLocation == MultispeakManagePaoLocation.SERVICE_LOCATION) {
            if (mspServiceLocation.getGPSLocation() != null) {
                double longitude = mspServiceLocation.getGPSLocation().getLongitude();
                double latitude = mspServiceLocation.getGPSLocation().getLatitude();
                PaoLocation paoLocation = new PaoLocation(meterToUpdate.getPaoIdentifier(), latitude, longitude,
                        Origin.MULTISPEAK, Instant.now());
                updatePaoLocation(meterToUpdate.getMeterNumber(), meterToUpdate.getName(), paoLocation);
            }
        }
    }

    /**
     * Returns the PaoName alias value for the MultiSpeak mspMeter object.
     * 
     * @throws InsufficientMultiSpeakDataException - when paoName not found (null)
     */
    private String getPaoNameFromMspMeter(MspMeter mspMeter, MultispeakVendor mspVendor) {

        String paoName = null;
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        if (paoAlias == MspPaoNameAliasEnum.ACCOUNT_NUMBER) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getAccountNumber())) {
                paoName = mspMeter.getUtilityInfo().getAccountNumber();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getServiceLocationID())) {
                paoName = mspMeter.getUtilityInfo().getServiceLocationID();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.CUSTOMER_ID) {
            if (mspMeter.getUtilityInfo() != null && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getCustomerID())) {
                paoName = mspMeter.getUtilityInfo().getCustomerID();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.EA_LOCATION) {
            // updating paoName for EA_LOCATION in Electric meter
            if (mspMeter instanceof ElectricMeter) {
                ElectricMeter electricMeter = (ElectricMeter) mspMeter;
                if (electricMeter.getElectricLocationFields() != null
                        && electricMeter.getElectricLocationFields().getEaLoc() != null
                        && StringUtils.isNotBlank(electricMeter.getElectricLocationFields().getEaLoc().getValue())) {
                    paoName = electricMeter.getElectricLocationFields().getEaLoc().getValue();
                }
            }
        } else if (paoAlias == MspPaoNameAliasEnum.GRID_LOCATION) {
            // TODO present in mspDevice

        } else if (paoAlias == MspPaoNameAliasEnum.METER_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getMeterNo())) {
                paoName = mspMeter.getMeterNo();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.POLE_NUMBER) {
            // updating paoName for pole number in Electric meter
            if (mspMeter instanceof ElectricMeter) {
                ElectricMeter electricMeter = (ElectricMeter) mspMeter;
                if (electricMeter.getElectricLocationFields() != null
                        && StringUtils.isNotBlank(electricMeter.getElectricLocationFields().getPoleNo())) {
                    paoName = electricMeter.getElectricLocationFields().getPoleNo();
                }
            }
        }

        if (paoName == null) {
            throw new InsufficientMultiSpeakDataException("Message does not contain sufficient data for Yukon Device Name.");
        }

        String extensionValue = getExtensionValue(mspMeter);
        return multispeakFuncs.buildAliasWithQuantifier(paoName, extensionValue, mspVendor);

    }

    /**
     * Returns the value of the paoName alias extension field from Meter object.
     * If no value is provided in the Meter object, then null is returned. NOTE:
     * meterno - this extension will return mspMeter.primaryIdentifier directly.
     */
    private String getExtensionValue(MspMeter mspMeter) {

        boolean usesExtension = multispeakFuncs.usesPaoNameAliasExtension();

        if (usesExtension) {
            String extensionName = multispeakFuncs.getPaoNameAliasExtension();
            if (extensionName.equalsIgnoreCase("meterno")) { // specific field
                return mspMeter.getMeterNo();
            } else if (extensionName.equalsIgnoreCase("deviceclass")) { // specific field (WHE custom)
                return getDeviceClassForMspMeter(mspMeter);
            } else { // use extensions
                return getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
            }
        }
        return null;
    }

    /**
     * Helper method to load extension value from extensionItems for
     * extensionName
     */
    private String getExtensionValue(ArrayOfExtensionsItem extensionsArr, String extensionName, String defaultValue) {
        log.debug("Attempting to load extension value for key:" + extensionName);
        List<ExtensionsItem> extensionsItem = extensionsArr != null ? extensionsArr.getExtensionsItem() : null;
        for (ExtensionsItem eItem : extensionsItem) {
            String extName = eItem.getExtName();
            if (extName.equalsIgnoreCase(extensionName)) {
                return eItem.getExtValue().getValue();
            }
        }
        log.warn("Extension " + extensionName + " key was not found. Returning default value: " + defaultValue);
        return defaultValue;
    }

    /**
     * Helper method to search devices by PaoName for filterValue. Performs
     * (starts with) search on PaoName. If MSP_EXACT_SEARCH_PAONAME is set, then
     * an exact lookup of paoName for fitlerValue is performed.
     */
    private List<YukonMeter> searchForMetersByPaoName(String filterValue) {

        boolean exactSearch = configurationSource.getBoolean(MasterConfigBoolean.MSP_EXACT_SEARCH_PAONAME);
        List<YukonMeter> meters = Lists.newArrayList();
        if (exactSearch) {
            YukonMeter meter = meterDao.findForPaoName(filterValue);
            if (meter != null) {
                meters.add(meter);
            }
        } else {
            List<FilterBy> searchFilter = new ArrayList<>(1);
            FilterBy filterBy = new StandardFilterBy("deviceName", MeterSearchField.PAONAME);
            filterBy.setFilterValue(filterValue);
            searchFilter.add(filterBy);
            MeterSearchOrderBy orderBy = new MeterSearchOrderBy(MeterSearchField.PAONAME.toString(), true);
            SearchResults<YukonMeter> result = meterSearchDao.search(searchFilter, orderBy, 0, 25);
            meters.addAll(result.getResultList());
        }
        return meters;
    }

}