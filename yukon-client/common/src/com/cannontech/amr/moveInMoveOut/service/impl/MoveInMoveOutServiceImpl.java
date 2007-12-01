package com.cannontech.amr.moveInMoveOut.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResultObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResultObj;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceEventEnum;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.peakReport.model.PeakReportPeakType;
import com.cannontech.common.device.peakReport.model.PeakReportResult;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.device.peakReport.service.PeakReportService;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.jobs.tasks.MoveInTask;
import com.cannontech.jobs.tasks.MoveOutTask;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class MoveInMoveOutServiceImpl implements MoveInMoveOutService {

    private Logger logger = YukonLogManager.getLogger(MoveInMoveOutServiceImpl.class);

    private JobManager jobManager = null;
    private YukonJobDefinition<MoveInTask> moveInDefinition = null;
    private YukonJobDefinition<MoveOutTask> moveOutDefinition = null;

    private AttributeService attributeService;
    private AuthDao authDao;
    private DeviceGroupProviderDao deviceGroupDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DynamicDataSource dynamicDataSource;
    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private PeakReportService peakReportService;
    private SimpleJdbcOperations jdbcTemplate = null;

    public MoveInResultObj moveIn(MoveInFormObj moveInFormObj) {

        CommandResultHolder meterReadResults = null;
        MoveInResultObj moveInResultObj = new MoveInResultObj();
        PeakReportResult peakReportResults = null;

        transferMoveInFormToMoveInResults(moveInFormObj, moveInResultObj);

        moveInResultObj.setPreviousMeter(moveInFormObj.getPreviousMeter());

        // Makes a call to the meter to find the usage,
        // which we use to calculate the point
        logger.info("Starting meter read for " + moveInFormObj.toString());
        meterReadResults = meterReadService.readMeter(moveInFormObj.getPreviousMeter(),
                                                      Collections.singleton(BuiltInAttribute.USAGE),
                                                      moveInFormObj.getLiteYukonUser());

        if (meterReadResults.isErrorsExist()) {
            moveInResultObj.setErrors(meterReadResults.getErrors());
            return moveInResultObj;
        } else {
            LitePoint lp = attributeService.getPointForAttribute(moveInFormObj.getPreviousMeter(),
                                                                 BuiltInAttribute.USAGE);
            for (PointValueHolder pvh : meterReadResults.getValues()) {
                if (pvh.getId() == lp.getLiteID()) {
                    moveInResultObj.setCurrentReading(pvh);
                }
            }
        }

        // profile peak calculating out the average usage
        logger.info("Starting peak report request for " + moveInFormObj.toString());
        peakReportResults = peakReportService.requestPeakReport(moveInFormObj.getPreviousMeter()
                                                                             .getDeviceId(),
                                                                PeakReportPeakType.DAY,
                                                                PeakReportRunType.PRE,
                                                                1,
                                                                moveInFormObj.getMoveInDate(),
                                                                new Date(),
                                                                false,
                                                                moveInFormObj.getLiteYukonUser());

        double calculatedDifferenceUsage = 0;
        if (peakReportResults.getErrors().isEmpty()) {
            calculatedDifferenceUsage = peakReportResults.getTotalUsage();
        } else {
            moveInResultObj.setErrors(peakReportResults.getErrors());
        }

        double calculatedUsageValue = moveInResultObj.getCurrentReading()
                                                     .getValue() - calculatedDifferenceUsage;

        SimplePointValue tempPrev = new SimplePointValue(moveInResultObj.getCurrentReading()
                                                                        .getId(),
                                                         moveInFormObj.getMoveInDate(),
                                                         moveInResultObj.getCurrentReading()
                                                                        .getType(),
                                                         calculatedUsageValue);
        moveInResultObj.setCalculatedPreviousReading(tempPrev);

        SimplePointValue tempDiff = new SimplePointValue(moveInResultObj.getCurrentReading()
                                                                        .getId(),
                                                         moveInResultObj.getCurrentReading()
                                                                        .getPointDataTimeStamp(),
                                                         moveInResultObj.getCurrentReading()
                                                                        .getType(),
                                                         calculatedDifferenceUsage);
        moveInResultObj.setCalculatedDifference(tempDiff);

        if (moveInResultObj.getErrors().isEmpty()) {
            // Adds this point to rawPointHistory since it is calculated
            insertDataPoint(moveInResultObj.getCurrentReading(),
                            calculatedUsageValue,
                            moveInFormObj.getMoveInDate());

            archieveDataMoveIn(moveInResultObj,
                               moveInFormObj.getLiteYukonUser());
            updateMeter(moveInFormObj.getPreviousMeter(),
                        moveInResultObj.getNewMeter());
            addMeterToServiceDeviceGroups(moveInResultObj);
            logger.info("Move in for " + moveInResultObj.getPreviousMeter()
                                                        .toString() + " successful.");
        } else {
            logger.info("Move in for " + moveInResultObj.getPreviousMeter()
                                                        .toString() + " failed. " + moveInResultObj.getErrors());
        }

        moveInResultObj.setSubmissionType("moveIn");
        return moveInResultObj;
    }

    public MoveInResultObj scheduleMoveIn(MoveInFormObj moveInFormObj) {
        MoveInResultObj moveInResultObj = new MoveInResultObj();
        transferMoveInFormToMoveInResults(moveInFormObj, moveInResultObj);
        moveInResultObj.setSubmissionType("moveIn");
        moveInResultObj.setScheduled(true);

        MoveInTask moveInTask = moveInDefinition.createBean();
        moveInTask.setEmailAddress(moveInFormObj.getEmailAddress());
        moveInTask.setMoveInDate(moveInFormObj.getMoveInDate());
        moveInTask.setMeter(moveInFormObj.getPreviousMeter());
        moveInTask.setNewMeterName(moveInFormObj.getMeterName());
        moveInTask.setNewMeterNumber(moveInFormObj.getMeterNumber());
        moveInTask.setRunAsUser(moveInFormObj.getLiteYukonUser());

        jobManager.scheduleJob(moveInDefinition,
                               moveInTask,
                               moveInFormObj.getMoveInDate(),
                               moveInFormObj.getLiteYukonUser());

        logger.info("Move in for " + moveInResultObj.getPreviousMeter()
                                                    .toString() + " scheduled.");

        return moveInResultObj;
    }

    public MoveOutResultObj moveOut(MoveOutFormObj moveOutFormObj) {

        CommandResultHolder meterReadResults = null;
        MoveOutResultObj moveOutResultObj = new MoveOutResultObj();
        PeakReportResult peakReportResults = null;
        moveOutResultObj.setEmailAddress(moveOutFormObj.getEmailAddress());
        moveOutResultObj.setPreviousMeter(moveOutFormObj.getMeter());
        
        // Makes a call to the meter to find the usage,
        // which we use to calculate the point
        logger.info("Starting meter read for " + moveOutFormObj.toString());
        meterReadResults = meterReadService.readMeter(moveOutFormObj.getMeter(),
                                                      Collections.singleton(BuiltInAttribute.USAGE),
                                                      moveOutFormObj.getLiteYukonUser());

        if (meterReadResults.isErrorsExist()) {
            moveOutResultObj.setErrors(meterReadResults.getErrors());
            return moveOutResultObj;
        } else {
            LitePoint lp = attributeService.getPointForAttribute(moveOutFormObj.getMeter(),
                                                                 BuiltInAttribute.USAGE);
            for (PointValueHolder pvh : meterReadResults.getValues()) {
                if (pvh.getId() == lp.getLiteID()) {
                    moveOutResultObj.setCurrentReading(pvh);
                }
            }
        }

            // profile peak calculating out the average usage
            logger.info("Starting peak report request for " + moveOutFormObj.toString());
            peakReportResults = peakReportService.requestPeakReport(moveOutFormObj.getMeter()
                                                                                  .getDeviceId(),
                                                                    PeakReportPeakType.DAY,
                                                                    PeakReportRunType.PRE,
                                                                    1,
                                                                    new Date(moveOutFormObj.getMoveOutDate().getTime()+1),
                                                                    new Date(),
                                                                    false,
                                                                    moveOutFormObj.getLiteYukonUser());

            double calculatedDifferenceUsage = 0;
            if (peakReportResults.getErrors().isEmpty()) {
                calculatedDifferenceUsage = peakReportResults.getTotalUsage();
            } else {
                moveOutResultObj.setErrors(peakReportResults.getErrors());
                return moveOutResultObj;
            }

            // Check the relative position of the wanted move out
            // reading in comparison with the previous reading
            double calculatedUsageValue = 0;
            calculatedUsageValue = moveOutResultObj.getCurrentReading()
                                                   .getValue() - calculatedDifferenceUsage;

            SimplePointValue tempCalc = new SimplePointValue(moveOutResultObj.getCurrentReading()
                                                                             .getId(),
                                                             moveOutFormObj.getMoveOutDate(),
                                                             moveOutResultObj.getCurrentReading()
                                                                             .getType(),
                                                             calculatedUsageValue);
            moveOutResultObj.setCalculatedReading(tempCalc);

            SimplePointValue tempDiff = new SimplePointValue(moveOutResultObj.getCurrentReading()
                                                                             .getId(),
                                                             moveOutResultObj.getCurrentReading()
                                                                             .getPointDataTimeStamp(),
                                                             moveOutResultObj.getCurrentReading()
                                                                             .getType(),
                                                             calculatedDifferenceUsage);
            moveOutResultObj.setCalculatedDifference(tempDiff);

            if (moveOutResultObj.getErrors().isEmpty()) {
                // Adds this point to rawPointHistory since it is calculated
                insertDataPoint(moveOutResultObj.getCurrentReading(),
                                calculatedUsageValue,
                                moveOutFormObj.getMoveOutDate());

                archieveDataMoveOut(moveOutResultObj,
                                    moveOutFormObj.getLiteYukonUser());

                removeMeterFromServiceDeviceGroups(moveOutResultObj);
                logger.info("Move out for " + moveOutResultObj.getPreviousMeter()
                                                              .toString() + " successful.");
            } else {
                logger.info("Move out for " + moveOutResultObj.getPreviousMeter()
                                                              .toString() + " failed. " + moveOutResultObj.getErrors());
            }
            moveOutResultObj.setSubmissionType("moveOut");
        return moveOutResultObj;

    }

    public MoveOutResultObj scheduleMoveOut(MoveOutFormObj moveOutFormObj) {
        MoveOutResultObj moveOutResultObj = new MoveOutResultObj();
        moveOutResultObj.setPreviousMeter(moveOutFormObj.getMeter());
        moveOutResultObj.setEmailAddress(moveOutFormObj.getEmailAddress());
        moveOutResultObj.setSubmissionType("moveOut");
        moveOutResultObj.setScheduled(true);

        MoveOutTask moveOutTask = moveOutDefinition.createBean();
        moveOutTask.setEmailAddress(moveOutFormObj.getEmailAddress());
        moveOutTask.setMoveOutDate(moveOutFormObj.getMoveOutDate());
        moveOutTask.setMeter(moveOutFormObj.getMeter());
        moveOutTask.setRunAsUser(moveOutFormObj.getLiteYukonUser());

        jobManager.scheduleJob(moveOutDefinition,
                               moveOutTask,
                               moveOutFormObj.getMoveOutDate(),
                               moveOutFormObj.getLiteYukonUser());

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
        pointData.setQuality(PointQualities.ESTIMATED_QUALITY);
        pointData.setTime(new Date(calculatedDate.getTime() + 1));
        pointData.setValue(calculatedValue);
        pointData.setTags(PointData.TAG_POINT_MUST_ARCHIVE);
        pointData.setMillis(0);
        dynamicDataSource.putValue(pointData);
    }

    public void updateMeter(Meter oldMeter, Meter newMeter) {
        if (!oldMeter.getMeterNumber()
                     .equalsIgnoreCase(newMeter.getMeterNumber()) || !oldMeter.getName()
                                                                              .equalsIgnoreCase(newMeter.getName())) {

            meterDao.update(oldMeter, newMeter);

            sendDBChangeMsg(oldMeter);
        }
    }

    public void sendDBChangeMsg(Meter meter) {
        IServerConnection connToDispatch = ConnPool.getInstance()
                                                   .getDefDispatchConn();
        DBChangeMsg changeMsgPao = new DBChangeMsg(meter.getDeviceId(),
                                                   DBChangeMsg.CHANGE_PAO_DB,
                                                   PAOGroups.STRING_CAT_DEVICE,
                                                   DBChangeMsg.CHANGE_TYPE_DELETE);
        connToDispatch.write(changeMsgPao);
    }

    private void transferMoveInFormToMoveInResults(MoveInFormObj moveInFormObj,
            MoveInResultObj moveInResultObj) {
        moveInResultObj.setPreviousMeter(moveInFormObj.getPreviousMeter());
        moveInResultObj.setNewMeter((Meter) moveInFormObj.getPreviousMeter()
                                                         .clone());
        moveInResultObj.getNewMeter().setName(moveInFormObj.getMeterName());
        moveInResultObj.getNewMeter()
                       .setMeterNumber(moveInFormObj.getMeterNumber());
        moveInResultObj.setEmailAddress(moveInFormObj.getEmailAddress());
    }

    public void addMeterToServiceDeviceGroups(MoveInResultObj moveInResultObj) {

        Meter newMeter = moveInResultObj.getNewMeter();

        StoredDeviceGroup disconnectGroup = deviceGroupEditorDao.getGroup(SystemGroupEnum.DISCONNECTSTATUS);
        if (deviceGroupDao.isDeviceInGroup(disconnectGroup, newMeter)) {
            deviceGroupMemberEditorDao.removeDevices(disconnectGroup, newMeter);
            moveInResultObj.getDeviceGroupsRemoved().add(disconnectGroup);
        }

        StoredDeviceGroup usageMonitoringGroup = deviceGroupEditorDao.getGroup(SystemGroupEnum.USAGEMONITORING);
        if (deviceGroupDao.isDeviceInGroup(usageMonitoringGroup, newMeter)) {
            deviceGroupMemberEditorDao.removeDevices(usageMonitoringGroup,
                                                     newMeter);
            moveInResultObj.getDeviceGroupsRemoved().add(usageMonitoringGroup);
        }
    }

    public void removeMeterFromServiceDeviceGroups(
            MoveOutResultObj moveOutResultObj) {
        Meter oldMeter = moveOutResultObj.getPreviousMeter();

        StoredDeviceGroup disconnectGroup = deviceGroupEditorDao.getGroup(SystemGroupEnum.DISCONNECTSTATUS);
        if (!deviceGroupDao.isDeviceInGroup(disconnectGroup, oldMeter)) {
            deviceGroupMemberEditorDao.addDevices(disconnectGroup, oldMeter);
            moveOutResultObj.getDeviceGroupsAdded().add(disconnectGroup);
        }

        StoredDeviceGroup usageMonitoringGroup = deviceGroupEditorDao.getGroup(SystemGroupEnum.USAGEMONITORING);
        if (!deviceGroupDao.isDeviceInGroup(usageMonitoringGroup, oldMeter)) {
            deviceGroupMemberEditorDao.addDevices(usageMonitoringGroup,
                                                  oldMeter);
            moveOutResultObj.getDeviceGroupsAdded().add(usageMonitoringGroup);
        }

    }

    private void archieveDataMoveIn(MoveInResultObj moveInResultObj,
            LiteYukonUser liteYukonUser) {
        addArchieveEntryDatabase(moveInResultObj.getPreviousMeter(),
                                 moveInResultObj.getCalculatedPreviousReading()
                                                .getPointDataTimeStamp(),
                                 DeviceEventEnum.MOVE_IN,
                                 liteYukonUser);

        addArchieveEntryFile(liteYukonUser,
                             moveInResultObj.getPreviousMeter(),
                             moveInResultObj.getNewMeter(),
                             moveInResultObj.getCalculatedPreviousReading(),
                             moveInResultObj.getCalculatedDifference(),
                             DeviceEventEnum.MOVE_IN);

    }

    private void archieveDataMoveOut(MoveOutResultObj moveOutResultObj,
            LiteYukonUser liteYukonUser) {
        addArchieveEntryDatabase(moveOutResultObj.getPreviousMeter(),
                                 moveOutResultObj.getCalculatedReading()
                                                 .getPointDataTimeStamp(),
                                 DeviceEventEnum.MOVE_OUT,
                                 liteYukonUser);

        addArchieveEntryFile(liteYukonUser,
                             moveOutResultObj.getPreviousMeter(),
                             null,
                             moveOutResultObj.getCalculatedReading(),
                             moveOutResultObj.getCalculatedDifference(),
                             DeviceEventEnum.MOVE_OUT);

    }

    private void addArchieveEntryDatabase(Meter oldMeter, Date moveDate,
            DeviceEventEnum deviceEvent, LiteYukonUser liteYukonUser) {

        boolean autoArchivingEnabled = Boolean.parseBoolean(authDao.getRolePropertyValue(liteYukonUser.getUserID(),
                                                                                         MeteringRole.AUTO_ARCHIVING_ENABLED));

        if (autoArchivingEnabled) {

            String sql = " INSERT INTO DeviceEvent (DeviceId, Timestamp, Comment) " + " VALUES (?, ?, ?)";

            jdbcTemplate.update(sql,
                                oldMeter.getDeviceId(),
                                new Date(moveDate.getTime() + 1),
                                deviceEvent.getEventType());
        }

    }

    private boolean addArchieveEntryFile(LiteYukonUser liteYukonUser,
            Meter prevMeter, Meter newMeter, PointValueHolder pvhCalc,
            PointValueHolder pvhDiff, DeviceEventEnum deviceEventEnum) {
        // String filePath = "../Export/DeviceEventArchive.csv";
        String filePath = "C:/Yukon/Server/Export/DeviceEventArchive.csv";
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
            logger.error(fnfe.toString());
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            logger.error(ioe.toString());
            ioe.printStackTrace();
        }
        return true;
    }

    // These methods are used to keep the email and the page very similiar.
    public String createMoveInErrorMsg(String deviceNameRepresentation) {
        String msg = "Not able to process the move in request for " + deviceNameRepresentation + " for the following reasons.";
        return msg;
    }

    public String createMoveOutErrorMsg(String deviceNameRepresentation) {
        String msg = "Not able to process the move out request for " + deviceNameRepresentation + " for the following reasons.";
        return msg;
    }

    public String createMoveInSuccessMsg(String deviceNameRepresentation) {
        String msg = "Move in request for " + deviceNameRepresentation + " is complete.";
        return msg;
    }

    public String createMoveOutSuccessMsg(String deviceNameRepresentation) {
        String msg = "Move out request for " + deviceNameRepresentation + "  is complete.";
        return msg;
    }

    @Required
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Required
    public void setDeviceGroupEditorDao(
            DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Required
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }

    @Required
    public void setPeakReportService(PeakReportService peakReportService) {
        this.peakReportService = peakReportService;
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    @Required
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
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
