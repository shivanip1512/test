package com.cannontech.amr.moveInMoveOut.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.deviceread.CalculatedPointResults;
import com.cannontech.amr.deviceread.dao.CalculatedPointService;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.amr.moveInMoveOut.tasks.MoveInTask;
import com.cannontech.amr.moveInMoveOut.tasks.MoveOutTask;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceEventEnum;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.exception.MeterReadRequestException;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.message.dispatch.message.PointData;

public class MoveInMoveOutServiceImpl implements MoveInMoveOutService {

    private final Logger logger = YukonLogManager.getLogger(MoveInMoveOutServiceImpl.class);
    private final int NEW_DAY_BUFFER_HRS = 2; // 2 hours
    private final int JOB_SCHEDULER_START_BUFFER = 4; // 4 minutes

    @Autowired private final JobManager jobManager = null;
    private YukonJobDefinition<MoveInTask> moveInDefinition = null;
    private YukonJobDefinition<MoveOutTask> moveOutDefinition = null;
    @Autowired private AttributeService attributeService;
    @Autowired private CalculatedPointService calculatedPointService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupProviderDao deviceGroupDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private MeterDao meterDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private PaoCommandAuthorizationService paoCommandAuthorizationService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    
    @Override
    public MoveInResult moveIn(MoveInForm moveInFormObj) {

        MoveInResult moveInResult = new MoveInResult();

        transferMoveInFormToMoveInResults(moveInFormObj, moveInResult);
        moveInResult.setPreviousMeter(moveInFormObj.getPreviousMeter());
        moveInResult.setMoveInDate(moveInFormObj.getMoveInDate());
        moveInResult.setSubmissionType("moveIn");
        CalculatedPointResults resultHolder;
        
        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date currentDateWithBuffer = TimeUtil.addHours(currentDate, NEW_DAY_BUFFER_HRS);
        Date moveInDate = moveInFormObj.getMoveInDate();
        
        if(moveInDate.before(currentDateWithBuffer)) {
            // The move in is within a reasonable range to just read the meter.
            if (!moveInDate.before(currentDate)){
    
                resultHolder = new CalculatedPointResults();
                
                // Makes a call to the meter to find the usage,
                // which we use to calculate the point
                PlcMeter meter = moveInResult.getPreviousMeter();
                logger.info("Starting meter read for " + meter.toString());
                
				DeviceReadResult readResult = deviceAttributeReadService.initiateReadAndWait(meter,
						Collections.singleton(BuiltInAttribute.USAGE), DeviceRequestType.MOVE_IN_MOVE_OUT_USAGE_READ,
						moveInFormObj.getUserContext().getYukonUser());
				
				
				if (!readResult.isSuccess()) {

					logger.info("Move in for " + moveInResult.getPreviousMeter().toString() + " failed. "+ readResult.getErrors());
					moveInResult.setErrors(readResult.getErrors());

					return moveInResult;
				} 
                    
                PointValueHolder currentPVH = null;
                LitePoint lp = attributeService.getPointForAttribute(meter,
                                                                     BuiltInAttribute.USAGE);
    
                for (PointValueHolder pvh : readResult.getPointValues()) {
                     if (pvh.getId() == lp.getLiteID()) {
                         currentPVH = pvh;
                     }
                }
                
                moveInResult.setCurrentReading(currentPVH);
                moveInResult.setCalculatedDifference(new SimplePointValue(currentPVH.getId(), 
                                                                          currentPVH.getPointDataTimeStamp(), 
                                                                          currentPVH.getType(), 
                                                                          0.0));
                moveInResult.setCalculatedPreviousReading(currentPVH);
            } else {
                try {
                    resultHolder = calculatedPointService.calculatePoint(moveInResult.getPreviousMeter(),
                                                                         moveInFormObj.getMoveInDate(),
                                                                         DeviceRequestType.MOVE_IN_MOVE_OUT_USAGE_READ,
                                                                         moveInFormObj.getUserContext());
            
                    if (!resultHolder.getErrors().isEmpty()) {
                    	
                        logger.info("Move in for " + moveInResult.getPreviousMeter()
                                                                    .toString() + " failed. " + moveInResult.getErrors());
                        moveInResult.setErrors(resultHolder.getErrors());
                        return moveInResult;
                    }
                    
                    if (!StringUtils.isBlank(resultHolder.getDeviceError())) {
                    	
                    	logger.info("Move in for " + moveInResult.getPreviousMeter()
                                .toString() + " failed. " + resultHolder.getDeviceError());
						moveInResult.setErrorMessage(resultHolder.getDeviceError());
						return moveInResult;
                    }
                    
                    moveInResult.setCurrentReading(resultHolder.getCurrentPVH());
                    moveInResult.setCalculatedDifference(resultHolder.getDifferencePVH());
                    moveInResult.setCalculatedPreviousReading(resultHolder.getCalculatedPVH());
                
                    // Adds this point to rawPointHistory since it is calculated
                    insertDataPoint(moveInResult.getCurrentReading(),
                                    moveInResult.getCalculatedPreviousReading()
                                                   .getValue(),
                                    moveInFormObj.getMoveInDate());
    
                    archiveDataMoveIn(moveInResult, moveInFormObj.getUserContext().getYukonUser());
                    updateMeter(moveInFormObj.getPreviousMeter(),
                                moveInResult.getNewMeter());
                    removeServiceDeviceGroups(moveInResult);
                    logger.info("Move in for " + moveInResult.getPreviousMeter()
                                                                .toString() + " successful.");
                } catch (IllegalUseOfAttribute e) {
                    logger.info("Move in for " + moveInResult.getPreviousMeter().toString() + " failed.");
                    moveInResult.setErrorMessage("Point was not found.\n" + e.toString());
                    CTILogger.info(e);
                } catch (NotFoundException e) {
                    logger.info("Move in for " + moveInResult.getPreviousMeter().toString() + " failed.");
                    moveInResult.setErrorMessage("Meter was not found.\n" + e.toString());
                    CTILogger.info(e);
                } catch (MeterReadRequestException e) {
                    logger.info("Move in for " + moveInResult.getPreviousMeter().toString() + " failed.");
                    moveInResult.setErrorMessage("Meter is not able to be read.  The Meter may not be responding or the connection to Port Control Service may be down.\n" + e.toString());
                    CTILogger.info(e);
                }
            }
        } else {
            throw new IllegalArgumentException("The move in date is in the future.  Please check the scheduled date. ");
        }

        return moveInResult;
    }

    @Override
    public MoveInResult scheduleMoveIn(MoveInForm moveInFormObj) {
        MoveInResult moveInResultObj = new MoveInResult();
        transferMoveInFormToMoveInResults(moveInFormObj, moveInResultObj);
        moveInResultObj.setSubmissionType("moveIn");
        moveInResultObj.setScheduled(true);

        MoveInTask moveInTask = moveInDefinition.createBean();
        moveInTask.setEmailAddress(moveInFormObj.getEmailAddress());
        moveInTask.setMoveInDate(moveInFormObj.getMoveInDate());
        moveInTask.setMeter(moveInFormObj.getPreviousMeter());
        moveInTask.setNewMeterName(moveInFormObj.getMeterName());
        moveInTask.setNewMeterNumber(moveInFormObj.getMeterNumber());


        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date tomorrowDate = TimeUtil.addDays(currentDate,1);
        Date scheduledDate = null;
        if (moveInFormObj.getMoveInDate().before(tomorrowDate)){
        	scheduledDate = TimeUtil.addMinutes(tomorrowDate, JOB_SCHEDULER_START_BUFFER);
        } else { 
        	scheduledDate = TimeUtil.addMinutes(moveInFormObj.getMoveInDate(), JOB_SCHEDULER_START_BUFFER);
        }
        jobManager.scheduleJob(moveInDefinition,
                               moveInTask,
                               scheduledDate,
                               moveInFormObj.getUserContext());

        logger.info("Move in for " + moveInResultObj.getPreviousMeter()
                                                    .toString() + " scheduled.");

        return moveInResultObj;
    }

    @Override
    public MoveOutResult moveOut(MoveOutForm moveOutFormObj) {

        MoveOutResult moveOutResult = new MoveOutResult();
        moveOutResult.setEmailAddress(moveOutFormObj.getEmailAddress());
        moveOutResult.setPreviousMeter(moveOutFormObj.getMeter());
        moveOutResult.setMoveOutDate(moveOutFormObj.getMoveOutDate());
        moveOutResult.setSubmissionType("moveOut");

        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date currentDateWithBuffer = TimeUtil.addHours(currentDate, NEW_DAY_BUFFER_HRS);
        Date moveOutDate = moveOutFormObj.getMoveOutDate();
        CalculatedPointResults resultHolder;
        
        if(moveOutDate.before(currentDateWithBuffer)) {
            
            // The move in is within a reasonable range to just read the meter.
            if (!moveOutDate.before(currentDate)){
    
                resultHolder = new CalculatedPointResults();
                
                // Makes a call to the meter to find the usage,
                // which we use to calculate the point
                PlcMeter meter = moveOutResult.getPreviousMeter();
                logger.info("Starting meter read for " + meter.toString());

				DeviceReadResult readResult = deviceAttributeReadService.initiateReadAndWait(meter,
						Collections.singleton(BuiltInAttribute.USAGE), DeviceRequestType.MOVE_IN_MOVE_OUT_USAGE_READ,
						 moveOutFormObj.getUserContext().getYukonUser());
               
                if (!readResult.isSuccess()) {
                    
					logger.info("Move in for " + moveOutResult.getPreviousMeter().toString() + " failed. "
							+ readResult.getErrors());
					moveOutResult.setErrors(readResult.getErrors());

                    return moveOutResult;
                    
                } else {
                    
                    PointValueHolder currentPVH = null;
    
                    LitePoint lp = attributeService.getPointForAttribute(meter,
                                                                         BuiltInAttribute.USAGE);
                    for (PointValueHolder pvh : readResult.getPointValues()) {
                         if (pvh.getId() == lp.getLiteID()) {
                             currentPVH = pvh;
                         }
                    }
                    
                    moveOutResult.setCurrentReading(currentPVH);
                    moveOutResult.setCalculatedDifference(new SimplePointValue(currentPVH.getId(), 
                                                                               currentPVH.getPointDataTimeStamp(), 
                                                                               currentPVH.getType(), 
                                                                               0.0));
                    moveOutResult.setCalculatedReading(currentPVH);
                }
            } else {
                try {
                    resultHolder = calculatedPointService.calculatePoint(moveOutResult.getPreviousMeter(),
                                                                         moveOutFormObj.getMoveOutDate(),
                                                                         DeviceRequestType.MOVE_IN_MOVE_OUT_USAGE_READ,
                                                                         moveOutFormObj.getUserContext());
                    if (!resultHolder.getErrors().isEmpty()) {
                        logger.info("Move out for " + moveOutResult.getPreviousMeter()
                                                                      .toString() + " failed. " + moveOutResult.getErrors());
                        moveOutResult.setErrors(resultHolder.getErrors());
                        return moveOutResult;
                    }
                    
                    if (!StringUtils.isBlank(resultHolder.getDeviceError())) {
                        logger.info("Move out for " + moveOutResult.getPreviousMeter()
                                                                      .toString() + " failed. " + resultHolder.getDeviceError());
                        moveOutResult.setErrorMessage(resultHolder.getDeviceError());
                        return moveOutResult;
                    }
            
                    moveOutResult.setCurrentReading(resultHolder.getCurrentPVH());
                    moveOutResult.setCalculatedDifference(resultHolder.getDifferencePVH());
                    moveOutResult.setCalculatedReading(resultHolder.getCalculatedPVH());
            
                    // if (moveOutResultObj.getErrors().isEmpty()) {
                    // Adds this point to rawPointHistory since it is calculated
                    insertDataPoint(moveOutResult.getCurrentReading(),
                                    moveOutResult.getCalculatedReading().getValue(),
                                    moveOutFormObj.getMoveOutDate());
    
                    archiveDataMoveOut(moveOutResult, moveOutFormObj.getUserContext().getYukonUser());
    
                    addServiceDeviceGroups(moveOutResult);
                    logger.info("Move out for " + moveOutResult.getPreviousMeter()
                                                                  .toString() + " successful.");
                } catch (IllegalUseOfAttribute e) {
                    logger.info("Move in for " + moveOutResult.getPreviousMeter().toString() + " failed.");
                    moveOutResult.setErrorMessage("Point was not found.\n" + e.toString());
                    CTILogger.info(e);
                } catch (NotFoundException e) {
                    logger.info("Move in for " + moveOutResult.getPreviousMeter().toString() + " failed.");
                    moveOutResult.setErrorMessage("Meter was not found.\n" + e.toString());
                    CTILogger.info(e);
                } catch (MeterReadRequestException e) {
                    logger.info("Move in for " + moveOutResult.getPreviousMeter().toString() + " failed.");
                    moveOutResult.setErrorMessage("Meter is not able to be read.  The Meter may not be responding or the connection to Port Control Service may be down.\n" + e.toString());
                    CTILogger.info(e);
                }
            }
        } else {
            throw new IllegalArgumentException("The move out date is in the future.  Please check the scheduled date. ");
        }
        
        return moveOutResult;

    }

    @Override
    public MoveOutResult scheduleMoveOut(MoveOutForm moveOutFormObj) {
        MoveOutResult moveOutResultObj = new MoveOutResult();
        moveOutResultObj.setPreviousMeter(moveOutFormObj.getMeter());
        moveOutResultObj.setEmailAddress(moveOutFormObj.getEmailAddress());
        moveOutResultObj.setSubmissionType("moveOut");
        moveOutResultObj.setScheduled(true);

        MoveOutTask moveOutTask = moveOutDefinition.createBean();
        moveOutTask.setEmailAddress(moveOutFormObj.getEmailAddress());
        moveOutTask.setMoveOutDate(moveOutFormObj.getMoveOutDate());
        moveOutTask.setMeter(moveOutFormObj.getMeter());

        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Date tomorrowDate = TimeUtil.addDays(currentDate,1);
        Date scheduledDate = null;
        if (moveOutFormObj.getMoveOutDate().before(tomorrowDate)){
        	scheduledDate = TimeUtil.addMinutes(tomorrowDate,JOB_SCHEDULER_START_BUFFER);
        } else { 
        	scheduledDate = TimeUtil.addMinutes(moveOutFormObj.getMoveOutDate(), JOB_SCHEDULER_START_BUFFER);
        }
        jobManager.scheduleJob(moveOutDefinition,
                               moveOutTask,
                               scheduledDate,
                               moveOutFormObj.getUserContext());
        
        logger.info("Move out for " + moveOutResultObj.getPreviousMeter()
                                                      .toString() + " scheduled.");

        return moveOutResultObj;
    }

    /**
     * @param pvh
     * @param calculatedValue
     * @param calculatedDate
     */
    public void insertDataPoint(PointValueHolder pvh, double calculatedValue,
            Date calculatedDate) {
        PointData pointData = new PointData();
        pointData.setId(pvh.getId());
        pointData.setType(pvh.getType());
        pointData.setPointQuality(PointQuality.Estimated);
        pointData.setTime(new Date(calculatedDate.getTime() + 1));
        pointData.setValue(calculatedValue);
        pointData.setTagsPointMustArchive(true);
        pointData.setMillis(0);
        asyncDynamicDataSource.putValue(pointData);
    }

    private void updateMeter(PlcMeter oldMeter, PlcMeter newMeter) {
        if (!oldMeter.getMeterNumber()
                     .equalsIgnoreCase(newMeter.getMeterNumber()) || !oldMeter.getName()
                                                                              .equalsIgnoreCase(newMeter.getName())) {
            meterDao.update(newMeter);
        }
    }

    private void transferMoveInFormToMoveInResults(MoveInForm moveInFormObj,
            MoveInResult moveInResultObj) {
        moveInResultObj.setPreviousMeter(moveInFormObj.getPreviousMeter());
        moveInResultObj.setNewMeter(meterDao.getPlcMeterForId(moveInFormObj.getPreviousMeter().getDeviceId()));
        moveInResultObj.getNewMeter().setName(moveInFormObj.getMeterName());
        moveInResultObj.getNewMeter()
                       .setMeterNumber(moveInFormObj.getMeterNumber());
        moveInResultObj.setEmailAddress(moveInFormObj.getEmailAddress());
    }

    private void removeServiceDeviceGroups(MoveInResult moveInResultObj) {

        PlcMeter newMeter = moveInResultObj.getNewMeter();

        StoredDeviceGroup disconnectGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DISCONNECTED_STATUS);
        if (deviceGroupDao.isDeviceInGroup(disconnectGroup, newMeter)) {
            deviceGroupMemberEditorDao.removeDevices(disconnectGroup, newMeter);
            moveInResultObj.getDeviceGroupsRemoved().add(disconnectGroup);
        }

        StoredDeviceGroup usageMonitoringGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.USAGE_MONITORING);
        if (deviceGroupDao.isDeviceInGroup(usageMonitoringGroup, newMeter)) {
            deviceGroupMemberEditorDao.removeDevices(usageMonitoringGroup,
                                                     newMeter);
            moveInResultObj.getDeviceGroupsRemoved().add(usageMonitoringGroup);
        }
    }

    private void addServiceDeviceGroups(MoveOutResult moveOutResultObj) {
        PlcMeter oldMeter = moveOutResultObj.getPreviousMeter();

        StoredDeviceGroup disconnectGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DISCONNECTED_STATUS);
        if (!deviceGroupDao.isDeviceInGroup(disconnectGroup, oldMeter)) {
            deviceGroupMemberEditorDao.addDevices(disconnectGroup, oldMeter);
            moveOutResultObj.getDeviceGroupsAdded().add(disconnectGroup);
        }

        StoredDeviceGroup usageMonitoringGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.USAGE_MONITORING);
        if (!deviceGroupDao.isDeviceInGroup(usageMonitoringGroup, oldMeter)) {
            deviceGroupMemberEditorDao.addDevices(usageMonitoringGroup,
                                                  oldMeter);
            moveOutResultObj.getDeviceGroupsAdded().add(usageMonitoringGroup);
        }

    }

    private void archiveDataMoveIn(MoveInResult moveInResultObj,
            LiteYukonUser liteYukonUser) {
        addArchiveEntryDatabase(moveInResultObj.getPreviousMeter(),
                                moveInResultObj.getCalculatedPreviousReading()
                                               .getPointDataTimeStamp(),
                                DeviceEventEnum.MOVE_IN,
                                liteYukonUser);

        addArchiveEntryFile(liteYukonUser,
                            moveInResultObj.getPreviousMeter(),
                            moveInResultObj.getNewMeter(),
                            moveInResultObj.getCalculatedPreviousReading(),
                            moveInResultObj.getCalculatedDifference(),
                            DeviceEventEnum.MOVE_IN);

    }

    private void archiveDataMoveOut(MoveOutResult moveOutResultObj,
            LiteYukonUser liteYukonUser) {
        addArchiveEntryDatabase(moveOutResultObj.getPreviousMeter(),
                                moveOutResultObj.getCalculatedReading()
                                                .getPointDataTimeStamp(),
                                DeviceEventEnum.MOVE_OUT,
                                liteYukonUser);

        addArchiveEntryFile(liteYukonUser,
                            moveOutResultObj.getPreviousMeter(),
                            null,
                            moveOutResultObj.getCalculatedReading(),
                            moveOutResultObj.getCalculatedDifference(),
                            DeviceEventEnum.MOVE_OUT);

    }

    private void addArchiveEntryDatabase(PlcMeter oldMeter, Date moveDate,
            DeviceEventEnum deviceEvent, LiteYukonUser liteYukonUser) {

        boolean autoArchivingEnabled = rolePropertyDao.getPropertyBooleanValue(
                                                                                          YukonRoleProperty.MOVE_IN_MOVE_OUT_AUTO_ARCHIVING, 
                                                                                          liteYukonUser);

        if (autoArchivingEnabled) {

            String sql = " INSERT INTO DeviceEvent (DeviceEventId, DeviceId, Timestamp, DeviceEventComment) " + " VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                                nextValueHelper.getNextValue("DeviceEvent"),
                                oldMeter.getDeviceId(),
                                new Date(moveDate.getTime() + 1),
                                deviceEvent.getEventType());
        }

    }

    private boolean addArchiveEntryFile(LiteYukonUser liteYukonUser,
            PlcMeter prevMeter, PlcMeter newMeter, PointValueHolder pvhCalc,
            PointValueHolder pvhDiff, DeviceEventEnum deviceEventEnum) {
        String filePath = CtiUtilities.getYukonBase() + "/Server/Export/DeviceEventArchive.csv";
        File file = new File(filePath);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                fw = new FileWriter(filePath, false);
                bw = new BufferedWriter(fw);
                String headerLine = "Reading Date,Previous Meter Number,Previous Name,New Meter Number," + "New Name,Calculated Event Value, Calculated Event Value Difference,Type Of Entry";
                bw.write(headerLine);
                bw.newLine();
            } else {
                fw = new FileWriter(filePath, true);
                bw = new BufferedWriter(fw);
            }
            String entryLine = pvhCalc.getPointDataTimeStamp() + "," + prevMeter.getMeterNumber() + "," + prevMeter.getName() + ",";

            if (newMeter != null) {
                entryLine += newMeter.getMeterNumber() + "," + newMeter.getName() + ",";
            } else {
                entryLine += " ," + " ,";
            }
            entryLine += pvhCalc.getValue() + "," + pvhDiff.getValue() + "," + deviceEventEnum.getEventType();

            bw.append(entryLine);
            bw.newLine();
            bw.close();
        } catch (FileNotFoundException fnfe) {
            logger.error(file.getAbsolutePath() + " was not found while trying to archive the move information",
                         fnfe);
        } catch (IOException ioe) {
            logger.error("An i/o exception was thrown for the file " + file.getAbsolutePath() + " while trying to archive the move information",
                         ioe);
        }
        return true;
    }
        
    @Override
    public boolean isAuthorized(LiteYukonUser liteYukonUser, PlcMeter meter) {
    	
    	return paoCommandAuthorizationService.isAuthorized(liteYukonUser, "getvalue lp peak", meter);
    }


    @Required
    public void setMoveOutDefinition(
            YukonJobDefinition<MoveOutTask> moveOutDefinition) {
        this.moveOutDefinition = moveOutDefinition;
    }

    @Required
    public void setMoveInDefinition(
            YukonJobDefinition<MoveInTask> moveInDefinition) {
        this.moveInDefinition = moveInDefinition;
    }

}
