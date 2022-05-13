package com.cannontech.multispeak.service.impl.v4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.DeviceGroupInUseException;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.message.porter.message.Request;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterGroup;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.event.v4.MeterReadEvent;
import com.cannontech.multispeak.event.v4.MultispeakEvent;
import com.cannontech.multispeak.service.MultispeakMeterServiceBase;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;
import com.cannontech.yukon.BasicServerConnection;

public class MultispeakMeterServiceImpl extends MultispeakMeterServiceBase implements MultispeakMeterService {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    private BasicServerConnection porterConnection;

    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private DeviceUpdateService deviceUpdateService;

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
    public List<ErrorObject> addMetersToGroup(MeterGroup meterGroup, String mspMethod, MultispeakVendor mspVendor) {

        List<ErrorObject> errorObjects = new ArrayList<>();
        if (meterGroup != null && meterGroup.getGroupName() != null && meterGroup.getMeterList() != null) {
            // Convert MeterNumbers to YukonDevices
            List<SimpleDevice> yukonDevices = new ArrayList<>();
            for (MeterID meterNumber : CollectionUtils.emptyIfNull(meterGroup.getMeterList().getMeterID())) {
                try {
                    SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber.getMeterNo());
                    yukonDevices.add(yukonDevice);
                } catch (EmptyResultDataAccessException e) {
                    String exceptionMessage = "Unknown meter number";
                    ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber.getMeterNo(), "MeterNumber",
                            "Meter",
                            "addMetersToGroup",
                            mspVendor.getCompanyName(), exceptionMessage);
                    errorObjects.add(errorObject);
                    log.error(e);
                } catch (IncorrectResultSizeDataAccessException e) {
                    String exceptionMessage = "Duplicate meters were found for this meter number " + meterNumber;
                    ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber.getMeterNo(), "MeterNumber",
                            "Meter",
                            "addMetersToGroup",
                            mspVendor.getCompanyName(), exceptionMessage);
                    errorObjects.add(errorObject);
                    log.error(e);
                }
            }

            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(meterGroup.getGroupName(), true);
            deviceGroupMemberEditorDao.addDevices(storedGroup, yukonDevices);
            multispeakEventLogService.addMetersToGroup(yukonDevices.size(), storedGroup.getFullName(), mspMethod,
                    mspVendor.getCompanyName());
        }
        return errorObjects;
    }

    @Override
    public ErrorObject deleteGroup(String groupName, MultispeakVendor mspVendor) {
        ErrorObject errorObject = null;
        if (groupName != null) {
            try {
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
                deviceGroupEditorDao.removeGroup(storedGroup);
            } catch (NotFoundException e) {
                errorObject = mspObjectDao.getNotFoundErrorObject(groupName, "meterGroupId", "MeterGroup", "deleteGroup",
                        mspVendor.getCompanyName());
            } catch (DeviceGroupInUseException e) {
                errorObject = mspObjectDao.getErrorObject(groupName, "Cannot delete group, it is currently in use.", "MeterGroup",
                        "deleteGroup", mspVendor.getCompanyName());
            }
        }
        return errorObject;
    }

    @Override
    public List<ErrorObject> removeMetersFromGroup(String groupName, List<MeterID> meterIds, MultispeakVendor mspVendor) {
        List<ErrorObject> errorObjects = new ArrayList<>();
        List<SimpleDevice> yukonDevices = new ArrayList<>();

        try {
            if (groupName != null) {
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
                String meterNumber = null;
                if (meterIds != null) {
                    for (MeterID meterId : meterIds) {
                        try {
                            meterNumber = meterId.getMeterNo();
                            SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                            yukonDevices.add(yukonDevice);
                        } catch (EmptyResultDataAccessException e) {
                            String exceptionMessage = "Unknown meter number " + meterNumber;
                            ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterId",
                                    "removeMetersFromGroup", mspVendor.getCompanyName(), exceptionMessage);
                            errorObjects.add(errorObject);
                            log.error(e);
                        } catch (IncorrectResultSizeDataAccessException e) {
                            String exceptionMessage = "Duplicate meters were found for this meter number  " + meterNumber;
                            ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterId",
                                    "removeMetersFromGroup", mspVendor.getCompanyName(), exceptionMessage);
                            errorObjects.add(errorObject);
                            log.error(e);
                        }
                    }
                }

                deviceGroupMemberEditorDao.removeDevices(storedGroup, yukonDevices);
                multispeakEventLogService.removeMetersFromGroup(yukonDevices.size(), storedGroup.getFullName(),
                        "RemoveMetersFromMeterGroup", mspVendor.getCompanyName());
            }
        } catch (NotFoundException e) {
            ErrorObject errorObject = mspObjectDao.getNotFoundErrorObject(groupName, "GroupName", "meterGroupId",
                    "removeMetersFromGroup",
                    mspVendor.getCompanyName());
            errorObjects.add(errorObject);
            log.error(e);
        }
        return errorObjects;
    }
}