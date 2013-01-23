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
import java.util.Vector;

import org.apache.log4j.Logger;
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
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspLMGroupDao;
import com.cannontech.multispeak.dao.MspLMInterfaceMappingDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.MspLMGroupCommunications;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMGroupStatus;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMProgramMode;
import com.cannontech.multispeak.db.MspLMInterfaceMapping;
import com.cannontech.multispeak.db.MspLMInterfaceMappingStrategyNameComparator;
import com.cannontech.multispeak.db.MspLmInterfaceMappingColumnEnum;
import com.cannontech.multispeak.db.MspLoadControl;
import com.cannontech.multispeak.deploy.service.ControlEventType;
import com.cannontech.multispeak.deploy.service.ControlItem;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadManagementEvent;
import com.cannontech.multispeak.deploy.service.ObjectRef;
import com.cannontech.multispeak.deploy.service.QualityDescription;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;
import com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus;
import com.cannontech.multispeak.deploy.service.SubstationLoadControlStatusControlledItemsControlItem;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;

public class MultispeakLMServiceImpl implements MultispeakLMService {

    @Autowired private MspLMInterfaceMappingDao mspLMInterfaceMappingDao;
	@Autowired private PaoDao paoDao;
	@Autowired private LoadControlService loadControlService;
	@Autowired private FdrTranslationDao fdrTranslationDao;
	@Autowired private SimplePointAccessDao simplePointAccessDao;
	@Autowired private MspObjectDao mspObjectDao;
	@Autowired private EnrollmentDao enrollmentDao;
    @Autowired private LoadControlClientConnection loadControlClientConnection;
    @Autowired private LoadControlProgramDao loadControlProgramDao;
    @Autowired private MspLMGroupDao mspLMGroupDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private ProgramService programService;
    @Autowired private RolePropertyDao rolePropertyDao;

    private List<? extends String> strategiesToExcludeInReport;
    
    private static Logger log = YukonLogManager.getLogger(MultispeakLMServiceImpl.class);
	
	@Override
	public ErrorObject[] buildMspLoadControl(LoadManagementEvent loadManagementEvent, MspLoadControl mspLoadControl, MultispeakVendor vendor) {

		//Set the start date
		Calendar scheduleDateTime = loadManagementEvent.getScheduleDateTime();
        Date startTime = new Date();	//default to now.
        if( scheduleDateTime != null) {
	        startTime.setTime(scheduleDateTime.getTimeInMillis());
        }
        mspLoadControl.setStartTime(startTime);

        //Set the stop date
        Date stopTime = null;	//default to null, will act as a never end
        Float duration = loadManagementEvent.getDuration();
        if( scheduleDateTime != null && duration != null) {
            int paddedDuration = duration.intValue();
            scheduleDateTime.add(Calendar.MINUTE, paddedDuration);
            stopTime = new Date(scheduleDateTime.getTimeInMillis());
        }
        mspLoadControl.setStopTime(stopTime);
        CTILogger.info("Start: " + mspLoadControl.getStartTime() + "  -  Stop:" + mspLoadControl.getStopTime());
        
        //Set the control event type
        mspLoadControl.setControlEventType(loadManagementEvent.getControlEventType());

        //build the mspLMInterfaceMapping from strategy and substation names
        String strategyName = loadManagementEvent.getStrategy().getStrategyName();
        List<MspLMInterfaceMapping> lmInterfaces = new ArrayList<MspLMInterfaceMapping>();
        ObjectRef[] substations = loadManagementEvent.getStrategy().getApplicationPointList();
        
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
    	for (ObjectRef substationRef : substations) {
			String substationName = substationRef.getName();
    		MspLMInterfaceMapping lmInterface = new MspLMInterfaceMapping();
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
    	
        return mspObjectDao.toErrorObject(errorObjects);
	}
	
	@Override
	public ErrorObject control(MspLoadControl mspLoadControl, LiteYukonUser liteYukonUser) {
		ErrorObject errorObject = null;
        for (MspLMInterfaceMapping mspLMInterfaceMapping : mspLoadControl.getMspLmInterfaceMappings()) {
			try {
				LiteYukonPAObject liteYukonPAObject = paoDao.getLiteYukonPAO(mspLMInterfaceMapping.getPaobjectId());
				if (paoDefinitionDao.isTagSupported(liteYukonPAObject.getPaoType(), PaoTag.LM_PROGRAM)) {
					String programName = liteYukonPAObject.getPaoName();
					ProgramStatus programStatus = null;
			    	if(mspLoadControl.getControlEventType() == ControlEventType.Initiate) {
			    		programStatus = startControlByProgramName(programName, mspLoadControl.getStartTime(), mspLoadControl.getStopTime(), liteYukonUser);
			    	} else if(mspLoadControl.getControlEventType() == ControlEventType.Restore) {
			    		programStatus = stopControlByProgramName(programName, mspLoadControl.getStopTime(), liteYukonUser);
			    	}
			    	CTILogger.info("Control Status: " + programStatus.toString());
				} else if ( liteYukonPAObject.getPaoType() == PaoType.LM_SCENARIO) {
					String scenarioName = liteYukonPAObject.getPaoName();
					ScenarioStatus scenarioStatus = null;
			    	if(mspLoadControl.getControlEventType() == ControlEventType.Initiate) {
			    		scenarioStatus = startControlByControlScenario(scenarioName, mspLoadControl.getStartTime(), mspLoadControl.getStopTime(), liteYukonUser);
			    	} else if(mspLoadControl.getControlEventType() == ControlEventType.Restore) {
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
	public ProgramStatus startControlByProgramName(String programName, Date startTime,
			Date stopTime, LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException  {
	    return programService.startProgramByName(programName, startTime, stopTime, null, false, true, liteYukonUser);
	}

	@Override
	public ProgramStatus stopControlByProgramName(String programName, Date stopTime,
			LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException {
	    return programService.scheduleProgramStopByProgramName(programName, stopTime, false, true);
	}

	@Override
	public ScenarioStatus startControlByControlScenario(String scenarioName, Date startTime,
			Date stopTime, LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException {
   		return loadControlService.startControlByScenarioName(scenarioName, startTime, stopTime, false, true, liteYukonUser);
	}

	@Override
	public ScenarioStatus stopControlByControlScenario(String scenarioName, Date stopTime,
			LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException{
       	return loadControlService.stopControlByScenarioName(scenarioName, stopTime, false, true, liteYukonUser);
	}

	@Override
	public String buildFdrMultispeakLMTranslation(String objectId) {
		String objectIdStr = "ObjectId:" + objectId + ";";
		String pointTypeStr = "POINTTYPE:Analog;";

		return objectIdStr + pointTypeStr;
	}
	
	@Override
	public PointQuality getPointQuality(QualityDescription qualityDescription) {

		if (qualityDescription == QualityDescription.Measured){
			return PointQuality.Normal;
		} else if( qualityDescription == QualityDescription.Estimated){
			return PointQuality.Manual;
		} else if( qualityDescription == QualityDescription.Failed){
			return PointQuality.NonUpdated;	//Failed from SCADA means could not object the current reading 
		} else if( qualityDescription == QualityDescription.Initial){
			return PointQuality.InitDefault;
		} else if( qualityDescription == QualityDescription.Calculated){
			return PointQuality.Estimated;
		} else if( qualityDescription == QualityDescription.Last){
			return PointQuality.InitLastKnown;
		} else {//if (qualityDescription == QualityDescription.Default)
			return PointQuality.Normal;
		}
	}
	
	@Override
	public PointData buildPointData(int pointId, ScadaAnalog scadaAnalog, String userName) {
		PointData pointData = new PointData();
		pointData.setId(pointId);
		pointData.setValue(scadaAnalog.getValue());
		pointData.setPointQuality(getPointQuality(scadaAnalog.getQuality()));
		pointData.setType(PointTypes.ANALOG_POINT);
		pointData.setStr("MultiSpeak ScadaAnalog Analog point update.");
		pointData.setUserName(userName);
		if (scadaAnalog.getTimeStamp() != null) {
			pointData.setTime(scadaAnalog.getTimeStamp().getTime());
		}
		return pointData;
	}

	@Override
	public ErrorObject writeAnalogPointData(ScadaAnalog scadaAnalog, LiteYukonUser liteYukonUser) {
    	String objectId = scadaAnalog.getObjectID().trim();
    	String translationStr = buildFdrMultispeakLMTranslation(objectId);
    	List<FdrTranslation> fdrTranslations =  fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM, translationStr);
    	if (!fdrTranslations.isEmpty()) {
	    	for (FdrTranslation fdrTranslation : fdrTranslations) {
				if( fdrTranslation.getDirection() == FdrDirection.RECEIVE) {
					PointData pointData = buildPointData(fdrTranslation.getPointId(), scadaAnalog, liteYukonUser.getUsername());
					simplePointAccessDao.writePointData(pointData);
					CTILogger.debug("PointData update sent to Dispatch (" + pointData.toString() + ")");
				}
			}
    	} else {
    		return mspObjectDao.getErrorObject(objectId, 
    				"No point mapping found in Yukon for objectId:" + objectId,
    				"ScadaAnalog",
    				"writeAnalogPointData", liteYukonUser.getUsername());
    	}
    	return null;
	}
	@Override
	public SubstationLoadControlStatus[] getActiveLoadControlStatus() throws ConnectionException, NotFoundException{
        List<SubstationLoadControlStatus> substationLoadControlStatusList = new ArrayList<SubstationLoadControlStatus>();
        List<SubstationLoadControlStatusControlledItemsControlItem> controlledItemsList = new ArrayList<SubstationLoadControlStatusControlledItemsControlItem>();
        
        Map<Integer, Integer> programCounts = enrollmentDao.getActiveEnrollmentExcludeOptOutCount(new Date(), new Date());
        List<MspLMInterfaceMapping> mspLmInterfaceMappingList = mspLMInterfaceMappingDao.getAllMappings();
        Collections.sort(mspLmInterfaceMappingList, new MspLMInterfaceMappingStrategyNameComparator(MspLmInterfaceMappingColumnEnum.SUBSTATION, true) );
        
        List<LMProgramBase> lmProgramBases = new ArrayList<LMProgramBase>();
		Set<MspLMGroupStatus> allStatus = new HashSet<MspLMGroupStatus>();
		Set<MspLMProgramMode> allModes= new HashSet<MspLMProgramMode>();

        String prevSubstationName = null;
        boolean exclude = false;
        for (MspLMInterfaceMapping mspLMInterfaceMapping : mspLmInterfaceMappingList) {
        	
        	String substationName = mspLMInterfaceMapping.getSubstationName();
        	exclude = strategiesToExcludeInReport.contains(mspLMInterfaceMapping.getStrategyName().toUpperCase());
        	
        	//Don't report on excluded strategy names
        	if (!exclude) {
	        	
				//not the first iteration and substation name has changed
				if( prevSubstationName != null && !substationName.equals(prevSubstationName)) {
					//Add the previous object
					SubstationLoadControlStatus substationLoadControlStatus = 
						buildSubstationLoadControlStatus(prevSubstationName, controlledItemsList);
					
					//Get unique/master status
					substationLoadControlStatus.setStatus(mspLMGroupDao.getMasterStatus(allStatus).toString());
					//Get unique/master mode
					substationLoadControlStatus.setMode(mspLMGroupDao.getMasterMode(allModes).toString());
					
					substationLoadControlStatusList.add(substationLoadControlStatus);
	
					//Clear the list and start again.
					controlledItemsList.clear();
					allStatus.clear();
					allModes.clear();
				}

	        	//Build a list of all the programs for the controllable object's device type
	        	lmProgramBases = new ArrayList<LMProgramBase>();
	        	int paobjectId = mspLMInterfaceMapping.getPaobjectId();
				LiteYukonPAObject liteYukonPAObject = paoDao.getLiteYukonPAO(paobjectId);
				if (paoDefinitionDao.isTagSupported(liteYukonPAObject.getPaoType(), PaoTag.LM_PROGRAM)) {
					LMProgramBase program = loadControlClientConnection.getProgramSafe(paobjectId);
					lmProgramBases.add(program);
	        	} else if ( liteYukonPAObject.getPaoType() == PaoType.LM_SCENARIO) {
	        		List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(paobjectId);
	        		lmProgramBases = loadControlClientConnection.getProgramsForProgramIds(programIds);
	        	}
	        	
				SubstationLoadControlStatusControlledItemsControlItem controlledItem = 
					buildSubstationLoadControlStatusControlledItemsControlItem(
							mspLMInterfaceMapping.getStrategyName(), 
							lmProgramBases, 
							programCounts);
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
        
        //add the last object
        if (!controlledItemsList.isEmpty()) {
			if( prevSubstationName != null) {
				SubstationLoadControlStatus substationLoadControlStatus = 
					buildSubstationLoadControlStatus(prevSubstationName, controlledItemsList);
				//Get unique/master status
				substationLoadControlStatus.setStatus(mspLMGroupDao.getMasterStatus(allStatus).toString());
				//Get unique/master mode
				substationLoadControlStatus.setMode(mspLMGroupDao.getMasterMode(allModes).toString());
				substationLoadControlStatusList.add(substationLoadControlStatus);
			}
        }
        
		//Convert to array for return
        if( !substationLoadControlStatusList.isEmpty()) {
        	SubstationLoadControlStatus[] substationLoadControlStatus = new SubstationLoadControlStatus[substationLoadControlStatusList.size()];
            substationLoadControlStatusList.toArray(substationLoadControlStatus);
            return substationLoadControlStatus;
        }
        return new SubstationLoadControlStatus[0];
	}
	
	public static QualityDescription getQualityDescription(PointQuality pointQuality) {
		
		if (PointQuality.Normal.equals(pointQuality)) {
			return QualityDescription.Measured;
		} else if (PointQuality.Manual.equals(pointQuality)) {
			return QualityDescription.Estimated;
		} else if (PointQuality.NonUpdated.equals(pointQuality)) {
			return QualityDescription.Failed;
		} else if (PointQuality.InitDefault.equals(pointQuality)) {
			return QualityDescription.Initial;
		} else if (PointQuality.Estimated.equals(pointQuality)) {
			return QualityDescription.Calculated;
		} else if (PointQuality.InitLastKnown.equals(pointQuality)) {
			return QualityDescription.Last;
		} else {
			return QualityDescription.Default;
		}
	}
	
	public static String getPointQualityLetter(PointQuality pointQuality) {
		
		if (PointQuality.NonUpdated.equals(pointQuality)) {
			return "F";
		} else if (PointQuality.Manual.equals(pointQuality)) {
			return "M";
		} else {
			return " ";
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
	private SubstationLoadControlStatusControlledItemsControlItem buildSubstationLoadControlStatusControlledItemsControlItem( 
			String strategyName, List<LMProgramBase> lmProgramBases, Map<Integer, Integer> programCounts){
		
		SubstationLoadControlStatusControlledItemsControlItem controlledItem = 
			new SubstationLoadControlStatusControlledItemsControlItem();
		controlledItem.setDescription(strategyName);
	
		//Set the total devices active count
		Integer count = getActiveCount(lmProgramBases, programCounts);
		controlledItem.setNumberOfItems(BigInteger.valueOf(count));
	
		//Set the total devices active count for active programs
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
	private SubstationLoadControlStatus buildSubstationLoadControlStatus(String substationName, 
			List<SubstationLoadControlStatusControlledItemsControlItem> controlledItemsList) {
		
		SubstationLoadControlStatus substationLoadControlStatus = new SubstationLoadControlStatus();
		substationLoadControlStatus.setObjectID(substationName);
		substationLoadControlStatus.setSubstationName(substationName);

		ControlItem [] controlledItemsArray = 
			new ControlItem[controlledItemsList.size()];
		controlledItemsArray = controlledItemsList.toArray(controlledItemsArray);
		substationLoadControlStatus.setControlledItems(controlledItemsArray);
		return substationLoadControlStatus;
	}
	
	/**
	 * Helper method to return the total count of devices in lmProgramBases
	 *  that are active (enrolled on an account) but are not currently opted out.
	 * If a program is null, a count of 0 is used
	 * @param lmProgramBases
	 * @param programCounts
	 * @return
	 */
	private Integer getActiveCount(List<LMProgramBase> lmProgramBases, Map<Integer, Integer> programCounts) {
		Integer count = 0;
		for (LMProgramBase program: lmProgramBases) {
			if( program != null) {
				//Combine counts for all programs in the scenario
				Integer programCount = programCounts.get(program.getYukonID());
				count += (programCount != null ? programCount : 0);
			}
		}
		return count;
	}
	
	/**
	 * Helper method to return the total count of devices in lmProgramBases
	 *  that are active (enrolled on an account) but are not currently opted out.
	 * Additionally, the lmProgramBase must be active for the count to be included.
	 * If a program is null, a count of 0 is used
	 * @param lmProgramBases
	 * @param programCounts
	 * @return
	 */
	private Integer getActiveControlledCount(List<LMProgramBase> lmProgramBases, Map<Integer, Integer> programCounts) {
		Integer controlledCount = 0;
		for (LMProgramBase program: lmProgramBases) {
			if( program != null) {
				//Combine counts for all programs in the scenario
				Integer programCount = programCounts.get(program.getYukonID());
				controlledCount += (program.isActive() && programCount != null ? programCount : 0);	//controlled items is 0 if not active program
			}
		}
		return controlledCount;
	}

    @Required
    public void setStrategiesToExcludeInReport(
			List<? extends String> strategiesToExcludeInReport) {
		this.strategiesToExcludeInReport = strategiesToExcludeInReport;
	}
}
