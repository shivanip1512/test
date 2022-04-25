package com.cannontech.multispeak.service.impl.v4;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.msp.beans.v4.ArrayOfControlItem;
import com.cannontech.msp.beans.v4.ControlItem;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.msp.beans.v4.SubstationLoadControlStatus;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.dao.MspLMGroupDao;
import com.cannontech.multispeak.dao.MspLmInterfaceMappingDao;
import com.cannontech.multispeak.db.MspLMGroupCommunications;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMGroupStatus;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMProgramMode;
import com.cannontech.multispeak.db.MspLmMapping;
import com.cannontech.multispeak.db.MspLmMappingColumn;
import com.cannontech.multispeak.db.MspLmMappingComparator;
import com.cannontech.multispeak.service.impl.MultispeakLMServiceBase;
import com.cannontech.multispeak.service.v4.MultispeakLMService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.yukon.IDatabaseCache;

public class MultispeakLMServiceImpl extends MultispeakLMServiceBase implements MultispeakLMService {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private FdrTranslationDao fdrTranslationDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspLMGroupDao mspLMGroupDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private LoadControlClientConnection loadControlClientConnection;
    private List<? extends String> strategiesToExcludeInReport;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private MspLmInterfaceMappingDao mspLMInterfaceMappingDao;

    @Override
    public PointData buildPointData(int pointId, ScadaAnalog scadaAnalog, String userName) {
        PointData pointData = new PointData();
        pointData.setId(pointId);
        pointData.setValue(scadaAnalog.getValue().getValue());
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
        List<FdrTranslation> fdrTranslations = fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM,
                translationStr);
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
        ArrayOfControlItem controlledItems = new ArrayOfControlItem();
        List<ControlItem> controlItems = controlledItems.getControlItem();
        controlItems.addAll(controlledItemsList);
        substationLoadControlStatus.setControlledItems(controlledItems);
        return substationLoadControlStatus;
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

                ControlItem controlledItem = buildSubstationLoadControlStatusControlledItems(mspLMInterfaceMapping.getStrategyName(), lmProgramBases, programCounts);
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

    /**
     * Builds a SubstationLaodControlStatusControlledItemsControlItem for the strategyName and program information provided.
     * Includes loading of the itemCount and controlledItemCounts.
     * @param strategyName
     * @param lmProgramBases
     * @param programCounts
     * @return
     */
    private ControlItem buildSubstationLoadControlStatusControlledItems(String strategyName,
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
}
