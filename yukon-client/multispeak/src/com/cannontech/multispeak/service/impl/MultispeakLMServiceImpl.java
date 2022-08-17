package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.msp.beans.v3.ControlEventType;
import com.cannontech.msp.beans.v3.ControlItem;
import com.cannontech.msp.beans.v3.ControlledItems;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectRef;
import com.cannontech.msp.beans.v3.QualityDescription;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspLMGroupDao;
import com.cannontech.multispeak.dao.MspLmInterfaceMappingDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.MspLMGroupCommunications;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMGroupStatus;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMProgramMode;
import com.cannontech.multispeak.db.MspLmMapping;
import com.cannontech.multispeak.db.MspLmMappingColumn;
import com.cannontech.multispeak.db.MspLmMappingComparator;
import com.cannontech.multispeak.db.MspLoadControl;
import com.cannontech.multispeak.service.v3.MultispeakLMService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class MultispeakLMServiceImpl extends MultispeakLMServiceBase implements MultispeakLMService {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private MspLmInterfaceMappingDao mspLMInterfaceMappingDao;
    @Autowired private FdrTranslationDao fdrTranslationDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private LoadControlClientConnection loadControlClientConnection;
    @Autowired private MspLMGroupDao mspLMGroupDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private List<? extends String> strategiesToExcludeInReport;

    private static Logger log = YukonLogManager.getLogger(MultispeakLMServiceImpl.class);

    @Override
    public List<ErrorObject> buildMspLoadControl(LoadManagementEvent loadManagementEvent, MspLoadControl mspLoadControl, MultispeakVendor vendor) {

        // Set the start date
        Calendar scheduleDateTime = loadManagementEvent.getScheduleDateTime().toGregorianCalendar();
        Date startTime = new Date(); // default to now.
        if (scheduleDateTime != null) {
            startTime.setTime(scheduleDateTime.getTimeInMillis());
        }
        mspLoadControl.setStartTime(startTime);

        // Set the stop date
        Date stopTime = null; // default to null, will act as a never end
        Float duration = loadManagementEvent.getDuration();
        if (scheduleDateTime != null && duration != null) {
            int paddedDuration = duration.intValue();
            scheduleDateTime.add(Calendar.MINUTE, paddedDuration);
            stopTime = new Date(scheduleDateTime.getTimeInMillis());
        }
        mspLoadControl.setStopTime(stopTime);
        CTILogger.info("Start: " + mspLoadControl.getStartTime() + "  -  Stop:" + mspLoadControl.getStopTime());

        // Set the control event type
        mspLoadControl.setControlEventType(loadManagementEvent.getControlEventType());

        // build the mspLMInterfaceMapping from strategy and substation names
        String strategyName = loadManagementEvent.getStrategy().getStrategyName();
        List<MspLmMapping> lmInterfaces = new ArrayList<MspLmMapping>();
        List<ObjectRef> substations = loadManagementEvent.getStrategy().getApplicationPointList().getApplicationPoint();

        List<ErrorObject> errorObjects = Lists.newArrayList();
        for (ObjectRef substationRef : substations) {
            String substationName = substationRef.getName();
            MspLmMapping lmInterface = new MspLmMapping();
            try {
                lmInterface = mspLMInterfaceMappingDao.getForStrategyAndSubstation(strategyName, substationName);
                lmInterfaces.add(lmInterface);
            } catch (NotFoundException e) {
                mspObjectDao.logMSPActivity("buildMspLoadControl", e.getMessage(), vendor.getCompanyName());
                ErrorObject err = mspObjectDao.getErrorObject(loadManagementEvent.getObjectID(), e.getMessage(), "loadManagementEvent", "buildMspLoadControl", vendor.getCompanyName());
                errorObjects.add(err);
            }
        }
        mspLoadControl.setMspLmInterfaceMappings(lmInterfaces);

        return errorObjects;
    }

    @Override
    public ErrorObject control(MspLoadControl mspLoadControl, LiteYukonUser liteYukonUser) {
        ErrorObject errorObject = null;
        for (MspLmMapping mspLMInterfaceMapping : mspLoadControl.getMspLmInterfaceMappings()) {
            try {
                LiteYukonPAObject liteYukonPAObject = databaseCache.getAllPaosMap().get(mspLMInterfaceMapping.getPaobjectId());
                if (paoDefinitionDao.isTagSupported(liteYukonPAObject.getPaoType(), PaoTag.LM_PROGRAM)) {
                    String programName = liteYukonPAObject.getPaoName();
                    ProgramStatus programStatus = null;
                    if (mspLoadControl.getControlEventType() == ControlEventType.INITIATE) {
                        programStatus = startControlByProgramName(programName, mspLoadControl.getStartTime(), mspLoadControl.getStopTime(), liteYukonUser);
                    } else if (mspLoadControl.getControlEventType() == ControlEventType.RESTORE) {
                        programStatus = stopControlByProgramName(programName, mspLoadControl.getStopTime(), liteYukonUser);
                    }
                    CTILogger.info("Control Status: " + programStatus.toString());
                } else if (liteYukonPAObject.getPaoType() == PaoType.LM_SCENARIO) {
                    String scenarioName = liteYukonPAObject.getPaoName();
                    ScenarioStatus scenarioStatus = null;
                    if (mspLoadControl.getControlEventType() == ControlEventType.INITIATE) {
                        scenarioStatus = startControlByControlScenario(scenarioName, mspLoadControl.getStartTime(), mspLoadControl.getStopTime(), liteYukonUser);
                    } else if (mspLoadControl.getControlEventType() == ControlEventType.RESTORE) {
                        scenarioStatus = stopControlByControlScenario(scenarioName, mspLoadControl.getStopTime(), liteYukonUser);
                    }
                    CTILogger.info("Control Status: " + scenarioStatus.toString());
                }
            } catch (TimeoutException e) {
                errorObject = mspObjectDao.getErrorObject(null,
                                                          mspLMInterfaceMapping.getSubstationName() + "/" + mspLMInterfaceMapping.getStrategyName() + " - " + e.getMessage() +
                                                          ". TimeoutException. Verify the scheduedStartTime (" + mspLoadControl.getStartTime() + ") is not in the past.",
                                                          "LoadManagementEvent", "control", liteYukonUser.getUsername());
            } catch (NotAuthorizedException | NotFoundException | BadServerResponseException | ConnectionException e) {
                errorObject = mspObjectDao.getErrorObject(null,
                                                          mspLMInterfaceMapping.getSubstationName() + "/" + mspLMInterfaceMapping.getStrategyName() + " - " + e.getMessage(),
                                                          "LoadManagementEvent", "control", liteYukonUser.getUsername());
            } catch (Exception e) {
                errorObject = mspObjectDao.getErrorObject(null,
                                                          mspLMInterfaceMapping.getSubstationName() + "/" + mspLMInterfaceMapping.getStrategyName() + " - " + e.getMessage(),
                                                          "LoadManagementEvent", "control", liteYukonUser.getUsername());
                log.error(e.getMessage(), e);
            }
        }
        return errorObject;
    }

    @Override
    public PointQuality getPointQuality(QualityDescription qualityDescription) {

        if (qualityDescription == QualityDescription.MEASURED) {
            return PointQuality.Normal;
        } else if (qualityDescription == QualityDescription.ESTIMATED) {
            return PointQuality.Manual;
        } else if (qualityDescription == QualityDescription.FAILED) {
            return PointQuality.NonUpdated; // Failed from SCADA means could not object the current reading
        } else if (qualityDescription == QualityDescription.INITIAL) {
            return PointQuality.InitDefault;
        } else if (qualityDescription == QualityDescription.CALCULATED) {
            return PointQuality.Estimated;
        } else if (qualityDescription == QualityDescription.LAST) {
            return PointQuality.InitLastKnown;
        } else {// if (qualityDescription == QualityDescription.Default)
            return PointQuality.Normal;
        }
    }
    
    @Override
    public PointData buildPointData(int pointId, ScadaAnalog scadaAnalog, String userName) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setValue(scadaAnalog.getValue());
        pointData.setPointQuality(getPointQuality(scadaAnalog.getQuality()));
        pointData.setType(PointType.Analog.getPointTypeId());
        pointData.setStr("MultiSpeak ScadaAnalog Analog point update.");
        pointData.setUserName(userName);
        if (scadaAnalog.getTimeStamp() != null) {
            pointData.setTime(scadaAnalog.getTimeStamp().toGregorianCalendar().getTime());
        }
        return pointData;
    }

    @Override
    public ErrorObject writeAnalogPointData(ScadaAnalog scadaAnalog, LiteYukonUser liteYukonUser) {
        String objectId = scadaAnalog.getObjectID().trim();
        String translationStr = buildFdrMultispeakLMTranslation(objectId);
        List<FdrTranslation> fdrTranslations = fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM, translationStr);
        if (!fdrTranslations.isEmpty()) {
            for (FdrTranslation fdrTranslation : fdrTranslations) {
                if (fdrTranslation.getDirection() == FdrDirection.RECEIVE) {
                    PointData pointData = buildPointData(fdrTranslation.getPointId(), scadaAnalog, liteYukonUser.getUsername());
                    simplePointAccessDao.writePointData(pointData);
                    CTILogger.debug("PointData update sent to Dispatch (" + pointData.toString() + ")");
                }
            }
        } else {
            return mspObjectDao.getErrorObject(objectId,
                                               "No point mapping found in Yukon for objectId:" + objectId,
                                               "ScadaAnalog", "writeAnalogPointData", liteYukonUser.getUsername());
        }
        return null;
    }

    @Override
    public List<SubstationLoadControlStatus> getActiveLoadControlStatus() throws ConnectionException, NotFoundException {
        List<SubstationLoadControlStatus> substationLoadControlStatusList = new ArrayList<SubstationLoadControlStatus>();
        List<ControlItem> controlledItemsList = new ArrayList<ControlItem>();

        Map<Integer, Integer> programCounts = enrollmentDao.getActiveEnrollmentExcludeOptOutCount(new Date(), new Date());
        List<MspLmMapping> mspLmInterfaceMappingList = mspLMInterfaceMappingDao.getAllMappings();
        Collections.sort(mspLmInterfaceMappingList, new MspLmMappingComparator(MspLmMappingColumn.SUBSTATION, true));

        List<LMProgramBase> lmProgramBases = new ArrayList<LMProgramBase>();
        Set<MspLMGroupStatus> allStatus = new HashSet<MspLMGroupStatus>();
        Set<MspLMProgramMode> allModes = new HashSet<MspLMProgramMode>();

        String prevSubstationName = null;
        boolean exclude = false;
        for (MspLmMapping mspLMInterfaceMapping : mspLmInterfaceMappingList) {

            String substationName = mspLMInterfaceMapping.getSubstationName();
            exclude = strategiesToExcludeInReport.contains(mspLMInterfaceMapping.getStrategyName().toUpperCase());

            // Don't report on excluded strategy names
            if (!exclude) {

                // not the first iteration and substation name has changed
                if (prevSubstationName != null && !substationName.equals(prevSubstationName)) {
                    // Add the previous object
                    SubstationLoadControlStatus substationLoadControlStatus = buildSubstationLoadControlStatus(prevSubstationName, controlledItemsList);

                    // Get unique/master status
                    substationLoadControlStatus.setStatus(mspLMGroupDao.getMasterStatus(allStatus).toString());
                    // Get unique/master mode
                    substationLoadControlStatus.setMode(mspLMGroupDao.getMasterMode(allModes).toString());

                    substationLoadControlStatusList.add(substationLoadControlStatus);

                    // Clear the list and start again.
                    controlledItemsList.clear();
                    allStatus.clear();
                    allModes.clear();
                }

                // Build a list of all the programs for the controllable object's device type
                lmProgramBases = new ArrayList<LMProgramBase>();
                int paobjectId = mspLMInterfaceMapping.getPaobjectId();
                LiteYukonPAObject liteYukonPAObject = databaseCache.getAllPaosMap().get(paobjectId);
                if (paoDefinitionDao.isTagSupported(liteYukonPAObject.getPaoType(), PaoTag.LM_PROGRAM)) {
                    LMProgramBase program = loadControlClientConnection.getProgramSafe(paobjectId);
                    lmProgramBases.add(program);
                } else if (liteYukonPAObject.getPaoType() == PaoType.LM_SCENARIO) {
                    List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(paobjectId);
                    lmProgramBases = loadControlClientConnection.getProgramsForProgramIds(programIds);
                }

                ControlItem controlledItem = buildSubstationLoadControlStatusControlledItemsControlItem(mspLMInterfaceMapping.getStrategyName(), lmProgramBases, programCounts);
                controlledItemsList.add(controlledItem);

                // Loop through all programs and load the status and mode values.
                for (LMProgramBase programBase : lmProgramBases) {
                    // Loop through all groups within the program
                    List<LMGroupBase> loadGroups = programBase.getLoadControlGroupVector();
                    for (LMGroupBase groupBase : loadGroups) {
                        List<MspLMGroupCommunications> mspLMGroupCommunications = mspLMGroupDao.getLMGroupCommunications(groupBase);
                        allStatus.add(mspLMGroupDao.getStatus(mspLMGroupCommunications));
                    }
                    allModes.add(mspLMGroupDao.getMode(programBase));
                }

                prevSubstationName = substationName;
            }
        }

        // add the last object
        if (!controlledItemsList.isEmpty()) {
            if (prevSubstationName != null) {
                SubstationLoadControlStatus substationLoadControlStatus = buildSubstationLoadControlStatus(prevSubstationName, controlledItemsList);
                // Get unique/master status
                substationLoadControlStatus.setStatus(mspLMGroupDao.getMasterStatus(allStatus).toString());
                // Get unique/master mode
                substationLoadControlStatus.setMode(mspLMGroupDao.getMasterMode(allModes).toString());
                substationLoadControlStatusList.add(substationLoadControlStatus);
            }
        }
        return substationLoadControlStatusList;
    }

    @Override
    public QualityDescription getQualityDescription(PointQuality pointQuality) {

        if (PointQuality.Normal.equals(pointQuality)) {
            return QualityDescription.MEASURED;
        } else if (PointQuality.Manual.equals(pointQuality)) {
            return QualityDescription.ESTIMATED;
        } else if (PointQuality.NonUpdated.equals(pointQuality)) {
            return QualityDescription.FAILED;
        } else if (PointQuality.InitDefault.equals(pointQuality)) {
            return QualityDescription.INITIAL;
        } else if (PointQuality.Estimated.equals(pointQuality)) {
            return QualityDescription.CALCULATED;
        } else if (PointQuality.InitLastKnown.equals(pointQuality)) {
            return QualityDescription.LAST;
        } else {
            return QualityDescription.DEFAULT;
        }
    }

    /**
     * Builds a SubstationLaodControlStatusControlledItemsControlItem for the strategyName and program information provided.
     * Includes loading of the itemCount and controlledItemCounts.
     * @param strategyName
     * @param lmProgramBases
     * @param programCounts
     * @return
     */
    private ControlItem buildSubstationLoadControlStatusControlledItemsControlItem(String strategyName,
            List<LMProgramBase> lmProgramBases, Map<Integer, Integer> programCounts) {

        ControlItem controlledItem = new ControlItem();
        controlledItem.setDescription(strategyName);

        // Set the total devices active count
        Integer count = getActiveCount(lmProgramBases, programCounts);
        controlledItem.setNumberOfItems(BigInteger.valueOf(count));

        // Set the total devices active count for active programs
        Integer controlledCount = getActiveControlledCount(lmProgramBases, programCounts);
        controlledItem.setNumberOfControlledItems(BigInteger.valueOf(controlledCount));
        return controlledItem;
    }

    /**
     * Helper method to build a SubstationLaodControlStatus for the substation and controlItems provided.
     * @param substationName
     * @param controlledItemsList
     * @return
     */
    private SubstationLoadControlStatus buildSubstationLoadControlStatus(String substationName, List<ControlItem> controlledItemsList) {

        SubstationLoadControlStatus substationLoadControlStatus = new SubstationLoadControlStatus();
        substationLoadControlStatus.setObjectID(substationName);
        substationLoadControlStatus.setSubstationName(substationName);
        ControlItem[] controlledItemsArray = new ControlItem[controlledItemsList.size()];
        controlledItemsArray = controlledItemsList.toArray(controlledItemsArray);
        ControlledItems controlledItems = new ControlledItems();
        List<ControlItem> controlItems = controlledItems.getControlItem();
        controlItems.addAll(controlledItemsList);
        substationLoadControlStatus.setControlledItems(controlledItems);
        return substationLoadControlStatus;
    }

    @Required
    public void setStrategiesToExcludeInReport(List<? extends String> strategiesToExcludeInReport) {
        this.strategiesToExcludeInReport = strategiesToExcludeInReport;
    }
}