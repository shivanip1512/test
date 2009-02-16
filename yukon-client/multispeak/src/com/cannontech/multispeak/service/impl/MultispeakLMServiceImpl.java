package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.multispeak.dao.MspLMInterfaceMappingDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.MspLMInterfaceMapping;
import com.cannontech.multispeak.db.MspLMInterfaceMappingStrategyNameComparator;
import com.cannontech.multispeak.db.MspLmInterfaceMappingColumnEnum;
import com.cannontech.multispeak.db.MspLoadControl;
import com.cannontech.multispeak.deploy.service.ControlEventType;
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

	public MspLMInterfaceMappingDao mspLMInterfaceMappingDao;
	public PaoDao paoDao;
	public LoadControlService loadControlService;
	public FdrTranslationDao fdrTranslationDao;
	public SimplePointAccessDao simplePointAccessDao;
	public MspObjectDao mspObjectDao;
    public EnrollmentDao enrollmentDao;
    public LoadControlClientConnection loadControlClientConnection;
    public LoadControlProgramDao loadControlProgramDao;
	
	@Override
	public MspLoadControl buildMspLoadControl(LoadManagementEvent loadManagementEvent) throws RemoteException {

		MspLoadControl mspLoadControl = new MspLoadControl();

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
        
        //Set the control event type
        mspLoadControl.setControlEventType(loadManagementEvent.getControlEventType());

        //build the mspLMInterfaceMapping from strategy and substation names
        String strategyName = loadManagementEvent.getStrategy().getStrategyName();
        List<MspLMInterfaceMapping> lmInterfaces = new ArrayList<MspLMInterfaceMapping>();
        ObjectRef[] substations = loadManagementEvent.getStrategy().getApplicationPointList();
        try { 
	    	for (ObjectRef substationRef : substations) {
				String substationName = substationRef.getName();
	    		MspLMInterfaceMapping lmInterface = new MspLMInterfaceMapping();
	    		lmInterface = mspLMInterfaceMappingDao.getForStrategyAndSubstation(strategyName, substationName);
	    		lmInterfaces.add(lmInterface);
			}
        } catch (NotFoundException e) {
        	mspObjectDao.logMSPActivity("buildMspLoadControl", e.getMessage(), null);
        	throw new RemoteException(e.getMessage());
        }
    	mspLoadControl.setMspLmInterfaceMappings(lmInterfaces);
    	
        return mspLoadControl;
	}
	
	@Override
	public ErrorObject control(MspLoadControl mspLoadControl, LiteYukonUser liteYukonUser) {
		ErrorObject errorObject = null;
        for (MspLMInterfaceMapping mspLMInterfaceMapping : mspLoadControl.getMspLmInterfaceMappings()) {
			try {
				LiteYukonPAObject liteYukonPAObject = paoDao.getLiteYukonPAO(mspLMInterfaceMapping.getPaobjectId());
				if ( liteYukonPAObject.getType() == DeviceTypes.LM_DIRECT_PROGRAM) {
					String programName = liteYukonPAObject.getPaoName();
					ProgramStatus programStatus = null;
			    	if(mspLoadControl.getControlEventType() == ControlEventType.Initiate) {
			    		programStatus = startControlByProgramName(programName, mspLoadControl.getStartTime(), mspLoadControl.getStopTime(), liteYukonUser);
			    	} else if(mspLoadControl.getControlEventType() == ControlEventType.Restore) {
			    		programStatus = stopControlByProgramName(programName, mspLoadControl.getStopTime(), liteYukonUser);
			    	}
			    	CTILogger.info("Control Status: " + programStatus.toString());
				} else if ( liteYukonPAObject.getType() == DeviceTypes.LM_SCENARIO) {
					String scenarioName = liteYukonPAObject.getPaoName();
					ScenarioStatus scenarioStatus = null;
			    	if(mspLoadControl.getControlEventType() == ControlEventType.Initiate) {
			    		scenarioStatus = startControlByControlScenario(scenarioName, mspLoadControl.getStartTime(), mspLoadControl.getStopTime(), liteYukonUser);
			    	} else if(mspLoadControl.getControlEventType() == ControlEventType.Restore) {
			    		scenarioStatus = stopControlByControlScenario(scenarioName, mspLoadControl.getStopTime(), liteYukonUser);
			    	}
			    	CTILogger.info("Control Status: " + scenarioStatus.toString());
				}
			} catch (NotFoundException e) {
	        	errorObject = mspObjectDao.getErrorObject(null, 
	        			mspLMInterfaceMapping.getSubstationName() + "/" + mspLMInterfaceMapping.getStrategyName() + " - " + e.getMessage(),
	        			"LoadManagementEvent", "control", liteYukonUser.getUsername());
	        } catch (TimeoutException e) {
	        	errorObject = mspObjectDao.getErrorObject(null, 
	        			mspLMInterfaceMapping.getSubstationName() + "/" + mspLMInterfaceMapping.getStrategyName() + " - " + e.getMessage(),
	        			"LoadManagementEvent", "control", liteYukonUser.getUsername());
	        } catch (NotAuthorizedException e) {
	        	errorObject = mspObjectDao.getErrorObject(null, 
	        			mspLMInterfaceMapping.getSubstationName() + "/" + mspLMInterfaceMapping.getStrategyName() + " - " + e.getMessage(),
	        			"LoadManagementEvent", "control", liteYukonUser.getUsername());
	        }
        }
        return errorObject;
	}

	@Override
	public ProgramStatus startControlByProgramName(String programName, Date startTime,
			Date stopTime, LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException  {
   		return loadControlService.startControlByProgramName(programName, startTime, stopTime, false, true, liteYukonUser);
	}

	@Override
	public ProgramStatus stopControlByProgramName(String programName, Date stopTime,
			LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException{
       	return loadControlService.stopControlByProgramName(programName, stopTime, false, true, liteYukonUser);
	}

	@Override
	public ScenarioStatus startControlByControlScenario(String scenarioName, Date startTime,
			Date stopTime, LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException {
   		return loadControlService.startControlByScenarioName(scenarioName, startTime, stopTime, false, true, liteYukonUser);
	}

	@Override
	public ScenarioStatus stopControlByControlScenario(String scenarioName, Date stopTime,
			LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException{
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
		pointData.setStr("MultiSpeak SCADA Server Analog point update.");
		pointData.setUserName(userName);
		return pointData;
	}

	@Override
	public ErrorObject writeAnalogPointData(ScadaAnalog scadaAnalog, LiteYukonUser liteYukonUser) {
    	String objectId = scadaAnalog.getObjectID().trim();
    	String translationStr = buildFdrMultispeakLMTranslation(objectId);
    	List<FdrTranslation> fdrTranslations =  fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM, translationStr);
    	if (!fdrTranslations.isEmpty()) {
	    	for (FdrTranslation fdrTranslation : fdrTranslations) {
				if( fdrTranslation.getDirection() == FdrDirection.Receive) {
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
	public SubstationLoadControlStatus[] getActiveLoadControlStatus() {
        List<SubstationLoadControlStatus> substationLoadControlStatusList = new ArrayList<SubstationLoadControlStatus>();
        List<SubstationLoadControlStatusControlledItemsControlItem> controlledItemsList = new ArrayList<SubstationLoadControlStatusControlledItemsControlItem>();
        
        Map<Integer, Integer> programCounts = enrollmentDao.getActiveEnrollmentExcludeOptOutCount(new Date(), new Date());
        List<MspLMInterfaceMapping> mspLmInterfaceMappingList = mspLMInterfaceMappingDao.getAllMappings();
        Collections.sort(mspLmInterfaceMappingList, new MspLMInterfaceMappingStrategyNameComparator(MspLmInterfaceMappingColumnEnum.SUBSTATION, true) );
        
        String prevSubstationName = null;
        for (MspLMInterfaceMapping mspLMInterfaceMapping : mspLmInterfaceMappingList) {
        	
        	String substationName = mspLMInterfaceMapping.getSubstationName();
        	
        	//Build a list of all the programs for the controlable object's device type
        	List<LMProgramBase> lmProgramBases = new ArrayList<LMProgramBase>();
        	int paobjectId = mspLMInterfaceMapping.getPaobjectId();
			LiteYukonPAObject liteYukonPAObject = paoDao.getLiteYukonPAO(paobjectId);
        	if ( liteYukonPAObject.getType() == DeviceTypes.LM_DIRECT_PROGRAM) {
				LMProgramBase program = loadControlClientConnection.getProgram(paobjectId);
				lmProgramBases.add(program);
        	} else if ( liteYukonPAObject.getType() == DeviceTypes.LM_SCENARIO) {
        		List<Integer> programIds = loadControlProgramDao.getProgramIdsByScenarioId(paobjectId);
        		lmProgramBases = loadControlClientConnection.getProgramsForProgramIds(programIds);
        	}

			//not the first iteration and substation name has changed
			if( prevSubstationName != null && substationName != prevSubstationName) {
				//Add the previous object
				SubstationLoadControlStatus substationLoadControlStatus = 
					buildSubstationLoadControlStatus(prevSubstationName, controlledItemsList);
				substationLoadControlStatusList.add(substationLoadControlStatus);

				//Clear the list and start again.
				controlledItemsList.clear();
			}

			SubstationLoadControlStatusControlledItemsControlItem controlledItem = 
				buildSubstationLoadControlStatusControlledItemsControlItem(
						mspLMInterfaceMapping.getStrategyName(), 
						lmProgramBases, 
						programCounts);
			controlledItemsList.add(controlledItem);
			
			prevSubstationName = substationName;
		}
        
        //add the last object
		if( prevSubstationName != null) {
			SubstationLoadControlStatus substationLoadControlStatus = 
				buildSubstationLoadControlStatus(prevSubstationName, controlledItemsList);
			substationLoadControlStatusList.add(substationLoadControlStatus);
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
		return getQualityDescription(pointQuality).getValue().substring(0, 1);
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
		substationLoadControlStatus.setSubstationName(substationName);
		
		SubstationLoadControlStatusControlledItemsControlItem [] controlledItemsArray = 
			new SubstationLoadControlStatusControlledItemsControlItem[controlledItemsList.size()];
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

	@Autowired
	public void setMspLMInterfaceMappingDao(
			MspLMInterfaceMappingDao mspLMInterfaceMappingDao) {
		this.mspLMInterfaceMappingDao = mspLMInterfaceMappingDao;
	}
    @Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
    @Autowired
	public void setLoadControlService(LoadControlService loadControlService) {
		this.loadControlService = loadControlService;
	}
    @Autowired
	public void setFdrTranslationDao(FdrTranslationDao fdrTranslationDao) {
		this.fdrTranslationDao = fdrTranslationDao;
	}
    @Autowired
	public void setSimplePointAccessDao(
			SimplePointAccessDao simplePointAccessDao) {
		this.simplePointAccessDao = simplePointAccessDao;
	}
    @Autowired
	public void setMspObjectDao(MspObjectDao mspObjectDao) {
		this.mspObjectDao = mspObjectDao;
	}
    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}
    @Autowired
    public void setLoadControlClientConnection(
			LoadControlClientConnection loadControlClientConnection) {
		this.loadControlClientConnection = loadControlClientConnection;
	}
    @Autowired
    public void setLoadControlProgramDao(
			LoadControlProgramDao loadControlProgramDao) {
		this.loadControlProgramDao = loadControlProgramDao;
	}
}
