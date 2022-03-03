package com.cannontech.web.tools.points.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.device.dao.DevicePointDao;
import com.cannontech.common.device.dao.DevicePointDao.SortBy;
import com.cannontech.common.device.model.DevicePointDetail;
import com.cannontech.common.device.model.DevicePointsFilter;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.events.loggers.PointEventLogService;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointAlarming.AlarmNotificationTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PaoPointModel;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.PointCopy;
import com.cannontech.web.tools.points.model.PointInfoModel;
import com.cannontech.web.tools.points.model.PointModel;
import com.cannontech.web.tools.points.service.PointEditorService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

public class PointEditorServiceImpl implements PointEditorService {
    
    @Autowired private AlarmCatDao alarmCatDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DBPersistentDao dBPersistentDao;
    @Autowired private PointDao pointDao;
    @Autowired private PointPropertyValueDao pointPropertyValueDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PointEventLogService eventLog;
    @Autowired private IDatabaseCache cache;
    @Autowired protected DeviceDao deviceDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DevicePointDao devicePointDao;

    protected static final Logger log = YukonLogManager.getLogger(PointEditorServiceImpl.class);

    @Override
    public PointModel getModelForId(int id) {
        
        PointBase base = pointDao.get(id);
        
        StaleData staleData = getStaleData(id);
        
        List<AlarmTableEntry> alarmTableEntries = getAlarmTableEntries(base, false);

        PointModel<PointBase> model = new PointModel<>(base, staleData, alarmTableEntries);
        
        return model;
    }

    @Override
    public int create(int pointType, int paoId, YukonUserContext userContext) {

        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);
        List<PointBase> pointsOnDevice = pointDao.getPointsForPao(paoId);

        List<String> existingPointNames = pointsOnDevice.stream().map(new Function<PointBase, String>() {

            @Override
            public String apply(PointBase pointBase) {
                return pointBase.getPoint().getPointName();
            }
        }).collect(Collectors.toList());
        
        
        String pointName = messageAccessor.getMessage("yukon.common.point.new");

        int duplicateNumber = 1;
        while (existingPointNames.contains(pointName)) {
            duplicateNumber++;
            pointName = messageAccessor.getMessage("yukon.common.point.new.duplicate", duplicateNumber);
        }

        PointBase point = PointUtil.createPoint(pointType, pointName, paoId, false);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPoint().getPaoID());
        
        eventLog.pointCreated(pao.getPaoName(), point.getPoint().getPointName(), point.getPoint().getPointTypeEnum(),
            point.getPoint().getPointOffset(), userContext.getYukonUser());
        int id = point.getPoint().getPointID();

        return id;
    }

    /**
     * Retrieves the StaleData part of the model for a given point id from the database
     */
    private StaleData getStaleData(int pointId) {
        
        StaleData staleData = new StaleData(Integer.valueOf(pointId));
        
        try {
            PointPropertyValue timeProperty = pointPropertyValueDao.getByIdAndPropertyId(pointId, StaleData.TIME_PROPERTY);
            PointPropertyValue updateProperty = pointPropertyValueDao.getByIdAndPropertyId(pointId, StaleData.UPDATE_PROPERTY);
            
            staleData.setEnabled(true);
            staleData.setTime((int) timeProperty.getFloatValue());
            staleData.setUpdateStyle((int) updateProperty.getFloatValue());
            
        } catch (DataAccessException e) {
            
            staleData.setEnabled(false);
            staleData.setTime(5);
            staleData.setUpdateStyle(1);
        }
        
        return staleData;
        
    }
    
    /**
     * Retrieves the AlarmTableEntries part of the model from the pointBase 
     * 
     * If setRawStateInCondition is true then set stateId in condition field otherwise set state text 
     * if condition value is not match in IAlarmDefs.STATUS_ALARM_STATES and This is applicable for Status/CalcStatus PointType.
     */
    private List<AlarmTableEntry> getAlarmTableEntries(PointBase pointBase, boolean setRawStateInCondition ) {
        
        PointType ptType = PointType.getForString(pointBase.getPoint().getPointType());

        ArrayList<AlarmTableEntry> notifEntries = new ArrayList<>();
        
        // be sure we have a 32 character string
        String alarmStates = pointBase.getPointAlarming().getAlarmStates().length() != PointAlarming.ALARM_STATE_COUNT ?
            PointAlarming.DEFAULT_ALARM_STATES : 
            pointBase.getPointAlarming().getAlarmStates();

        String excludeNotifyStates = pointBase.getPointAlarming().getExcludeNotifyStates();

        // this drives what list of strings we will put into our table
        String[] alarm_cats = IAlarmDefs.OTHER_ALARM_STATES;
        if (ptType == PointType.Status || ptType == PointType.CalcStatus) {
            alarm_cats = IAlarmDefs.STATUS_ALARM_STATES;
        }
        LiteStateGroup stateGroup = stateGroupDao.getStateGroup(pointBase.getPoint().getStateGroupID());

        String[] stateNames = new String[stateGroup.getStatesList().size()];
        String[] rawStates = new String[stateGroup.getStatesList().size()];
        for (int j = 0; j < stateGroup.getStatesList().size(); j++) {
            if (setRawStateInCondition) {
                rawStates[j] = String.valueOf(stateGroup.getStatesList().get(j).getLiteID());
            } else {
                stateNames[j] = stateGroup.getStatesList().get(j).toString();
            }
        }
        // insert all the predefined states into the table
        int i = 0;
        for (i = 0; i < alarm_cats.length; i++) {
            AlarmTableEntry entry = new AlarmTableEntry();
            setupAlarmTableEntry(entry, excludeNotifyStates.toUpperCase().charAt(i), alarmStates.charAt(i));

            entry.setCondition(alarm_cats[i]);
            notifEntries.add(entry);
        }

        if (ptType == PointType.Status || ptType == PointType.CalcStatus) {

            for (int j = 0; j < stateNames.length; j++, i++) {
                if (i >= alarmStates.length()) {
                    throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates[" + i + "] while alarmStates.length()==" + alarmStates.length() + 
                        ", too many states for Status point "+ pointBase.getPoint().getPointName() + " defined.");
                }
                AlarmTableEntry entry = new AlarmTableEntry();
                setupAlarmTableEntry(entry, excludeNotifyStates.toUpperCase().charAt(i), alarmStates.charAt(i));
                if (setRawStateInCondition) {
                    entry.setCondition(rawStates[j]);
                } else {
                    entry.setCondition(stateNames[j]);
                }
                notifEntries.add(entry);
            }
        }
        return notifEntries;
    }
    
    /**
     * Helper method for use within getAlarmTableEntries
     */
    private void setupAlarmTableEntry(AlarmTableEntry entry, char gen, char category) {
        LiteAlarmCategory liteAlarmCategory = alarmCatDao.getAlarmCategory(category);
        entry.setCategory(liteAlarmCategory.getCategoryName());
        entry.setNotify(AlarmNotificationTypes.getAnalogControlTypeValue(PointAlarming.getExcludeNotifyString(gen)));
    }
    
    @Override
    public int save(PointBase base, List<AlarmTableEntry> alarmTableEntries, LiteYukonUser liteYukonUser) {
        
        Integer pointId = base.getPoint().getPointID();
        
        TransactionType type = TransactionType.UPDATE;
        if (pointId == null) {
            type = TransactionType.INSERT;
        }
        
        /* This one must be done BEFORE the base object */
        attachAlarms(base, alarmTableEntries);
        
        
        dBPersistentDao.performDBChange(base, type);
        
        pointId = base.getPoint().getPointID();
        
        DBChangeMsg dbChange = new DBChangeMsg(
            pointId,
            DBChangeMsg.CHANGE_POINT_DB,
            DBChangeMsg.CAT_POINT,
            base.getPoint().getPointType(),
            type == TransactionType.UPDATE ? DbChangeType.UPDATE : DbChangeType.ADD);

        dbChangeManager.processDbChange(dbChange);

        LiteYukonPAObject pao = cache.getAllPaosMap().get(base.getPoint().getPaoID());
        eventLog.pointUpdated(pao.getPaoName(), base.getPoint().getPointName(), base.getPoint().getPointTypeEnum(),
            base.getPoint().getPointOffset(), liteYukonUser);
        return pointId;
    }
    
    /**
     * Save the StaleData from the model object to the database
     */
    public void saveStaleData(StaleData staleData) {
        
        if (staleData != null) {
            PointPropertyValue timeProperty = new PointPropertyValue(staleData.getPointId(), StaleData.TIME_PROPERTY, staleData.getTime());
            PointPropertyValue updateProperty = new PointPropertyValue(staleData.getPointId(), StaleData.UPDATE_PROPERTY, staleData.getUpdateStyle());
                         
            pointPropertyValueDao.remove(timeProperty);
            pointPropertyValueDao.remove(updateProperty);
    
            if (staleData.isEnabled()) {
                pointPropertyValueDao.add(timeProperty);
                pointPropertyValueDao.add(updateProperty);
            }
        }
    }
    
    /**
     * Attach the alarms to the pointBase before saving the pointbase
     */
    private void attachAlarms(PointBase pointBase, List<AlarmTableEntry> alarmTableEntries) {
        
        String alarmStates = "";
        String exclNotify = "";

        for (AlarmTableEntry entry : alarmTableEntries) {
            alarmStates += (char) alarmCatDao.getAlarmCategoryId(entry.getCategory());
            exclNotify += PointAlarming.getExcludeNotifyChar(entry.getNotify().getDbString());
        }

        int numberAlarms = alarmTableEntries.size();
        // fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
        alarmStates += PointAlarming.DEFAULT_ALARM_STATES.substring(numberAlarms);
        exclNotify += PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(numberAlarms);

        pointBase.getPointAlarming().setAlarmStates(alarmStates);
        pointBase.getPointAlarming().setExcludeNotifyStates(exclNotify);
    }
    
    @Override
    public List<String> getDirectionsFor(FdrInterfaceType interfaceType) {
        List<String> directions =  interfaceType.getSupportedDirectionsList().stream().map(new Function<FdrDirection, String> () {
            @Override
            public String apply(FdrDirection t) {
                return t.getValue();
            }})
            
        .collect(Collectors.toList());
        
        return directions;
        
    }
    
    @Override
    public List<Map<String,Object>> getTranslationFieldsFor(FdrInterfaceType interfaceType, String pointType) {
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for  (FdrInterfaceOption field : interfaceType.getInterfaceOptionsList()) {
            
            Map<String, Object> optionInfo = new HashMap<>();
            
            optionInfo.put("name", field.getOptionLabel());
            
            switch (field.getOptionType()) {
            case COMBO:
                optionInfo.put("options", field.getOptionValues());
                optionInfo.put("value", field.getOptionValues()[0]);
                break;
            case TEXT:
                String[] value = field.getOptionValues();
                optionInfo.put("value", value == null ? "(none)" : value[0]);
                if (field == FdrInterfaceOption.INET_DESTINATION_SOURCE) {
                    optionInfo.put("maxLength", 256);
                }
                break;
            }
            result.add(optionInfo);
        }
        
        Map<String, Object> pointTypeOptionInfo = new HashMap<>();
        pointTypeOptionInfo.put("name", "POINTTYPE");
        pointTypeOptionInfo.put("hidden", true);
        pointTypeOptionInfo.put("options", ImmutableList.of(pointType));
        pointTypeOptionInfo.put("value", pointType);
        
        result.add(pointTypeOptionInfo);
        
        return result;
    }

    @Override
    public List<Map<String,Object>> breakIntoTranslationFields(String originalString, FdrInterfaceType interfaceType) {

        String[] kvPairs = originalString.split(";");

        Map<String, String> originalValues = new HashMap<>();
        for (String kvPair : kvPairs) {
            //Split on the first colon. Some property values have file paths.
            String[] kv = kvPair.split(":", 2);
            if (kv.length != 2) {
                log.warn("Unable to parse translation part: '" + kvPair + "' of translation " + originalString + "'");
                continue;
            }
            originalValues.put(kv[0], kv[1]);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for  (FdrInterfaceOption field : interfaceType.getInterfaceOptionsList()) {

            Map<String, Object> optionInfo = new HashMap<>();

            optionInfo.put("name", field.getOptionLabel());
            optionInfo.put("value", originalValues.get(field.getOptionLabel()));

            switch (field.getOptionType()) {
            case COMBO:
                optionInfo.put("options", field.getOptionValues());
                break;
            case TEXT:
                if (field == FdrInterfaceOption.INET_DESTINATION_SOURCE) {
                    optionInfo.put("maxLength", 256);
                }
                break;
            }
            result.add(optionInfo);
        }

        Map<String, Object> pointTypeOptionInfo = new HashMap<>();
        pointTypeOptionInfo.put("hidden", true);
        pointTypeOptionInfo.put("name", "POINTTYPE");
        pointTypeOptionInfo.put("options", ImmutableList.of(originalValues.get("POINTTYPE")));
        pointTypeOptionInfo.put("value", originalValues.get("POINTTYPE"));

        result.add(pointTypeOptionInfo);

        return result;
    }

    @Override
    public AttachmentStatus getAttachmentStatus(int id) {

        if (PointBase.hasCapControlSubstationBus(id)) return AttachmentStatus.SUBSTATION_BUS;
        if (PointBase.hasCapBank(id)) return AttachmentStatus.CAP_BANK;
        if (PointBase.hasLMTrigger(id)) return AttachmentStatus.LM_TRIGGER;
        if (PointBase.hasLMGroup(id)) return AttachmentStatus.LM_GROUP;
        if (PointBase.hasRawPointHistorys(id)) return AttachmentStatus.RAW_POINT_HISTORY;
        if (PointBase.hasSystemLogEntry(id)) return AttachmentStatus.SYSTEM_LOG;

        return AttachmentStatus.NO_CONFLICT;
    }

    @Override
    public int delete(int id, YukonUserContext userContext) throws AttachedException {
        AttachmentStatus attachmentStatus = getAttachmentStatus(id);
        if (!attachmentStatus.isDeletable()) {
            throw new AttachedException(attachmentStatus);
        }
        
        PointBase point = pointDao.get(id);
        dBPersistentDao.performDBChange(point, TransactionType.DELETE);
        
        DBChangeMsg dbChange = new DBChangeMsg(
            id,
            DBChangeMsg.CHANGE_POINT_DB,
            DBChangeMsg.CAT_POINT,
            point.getPoint().getPointType(),
            DbChangeType.DELETE);
        
        dbChangeManager.processDbChange(dbChange);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPoint().getPaoID());
        eventLog.pointDeleted(pao.getPaoName(), point.getPoint().getPointName(), point.getPoint().getPointTypeEnum(),
            point.getPoint().getPointOffset(), userContext.getYukonUser());
        return point.getPoint().getPointID();
    }

    @Override
    public int copy(LitePointModel pointModel) {
        LitePoint litePoint = new LitePoint(pointModel.getPointId(), 
                                            pointModel.getPointName(),
                                            pointModel.getPointType());
        
        PointBase newPoint = (PointBase) dBPersistentDao.retrieveDBPersistent(litePoint);
        newPoint.setPointID(pointDao.getNextPointId());

        if (pointModel.isPhysicalOffset()) {
            newPoint.getPoint().setPointOffset(pointModel.getPointOffset());
        } else {
            newPoint.getPoint().setPhysicalOffset(false);
            newPoint.getPoint().setPointOffset(0);
        }

        newPoint.getPoint().setPointName(pointModel.getPointName());
        newPoint.getPoint().setPaoID(pointModel.getPaoId());
        PointUtil.insertIntoDB(newPoint);

        // copy the StaleData
        StaleData newStaleData = populateStaleDataObjectToCopy(pointModel.getPointId(), newPoint.getPoint().getPointID());
        saveStaleData(newStaleData);

        return newPoint.getPoint().getPointID();
    }

    private StaleData populateStaleDataObjectToCopy(int pointIdToCopy, int newPointId) {
        StaleData stateDataToCopy = getStaleData(pointIdToCopy);
        StaleData newStaleData = new StaleData(Integer.valueOf(newPointId));
        newStaleData.setEnabled(stateDataToCopy.isEnabled());
        newStaleData.setTime(stateDataToCopy.getTime());
        newStaleData.setUpdateStyle(stateDataToCopy.getUpdateStyle());
        return newStaleData;
    }

    @Override
    public LitePointModel getLitePointModel(Integer pointId) {
        PointBase base = pointDao.get(pointId);
        
        LitePointModel copyPointModel = new LitePointModel(base.getPoint().getPointName(), 
                                                           base.getPoint().getPointID(), 
                                                           base.getPoint().isPhysicalOffset(), 
                                                           base.getPoint().getPointOffset(),
                                                           PointType.valueOf(base.getPoint().getPointType()),
                                                           base.getPoint().getPaoID());
        
        return copyPointModel;
    }

    @Override
    public PointBaseModel<? extends PointBase> create(PointBaseModel pointBaseModel, YukonUserContext userContext) {

        PointBase pointBase = PointModelFactory.createPoint(pointBaseModel);
        pointBaseModel.buildDBPersistent(pointBase);

        List<AlarmTableEntry> alarmTableEntries = buildOrderedAlarmTable(pointBaseModel.getAlarming().getAlarmTableList(),
                pointBaseModel.getPointType());
        int pointId = save(pointBase, alarmTableEntries, userContext.getYukonUser());
        // When Stale data object is not passed in Request, then with default values Stale data object  will be created
        StaleData staleData = new StaleData(Integer.valueOf(pointId));

        if (pointBaseModel.getStaleData() != null) {
            staleData = pointBaseModel.getStaleData().overwriteWith(new StaleData(Integer.valueOf(pointId)));
        }
        saveStaleData(staleData);

        buildPointBaseModel(pointBase, pointBaseModel,  getStaleData(pointId));
        return pointBaseModel;
    }

    @Override
    public PointBaseModel<? extends PointBase> update(int pointId, PointBaseModel pointBaseModel, YukonUserContext userContext) {

        PointBase pointBase = pointDao.get(pointId);

        pointBaseModel.buildDBPersistent(pointBase);

        PointType ptType = PointType.getForString(pointBase.getPoint().getPointType());

        List<AlarmTableEntry> alarmTableEntries = updateExistingAlarmTableEntries(pointBase, pointBaseModel, ptType);
        
        save(pointBase, alarmTableEntries, userContext.getYukonUser());

        StaleData staleData = getStaleData(pointId);
        if (pointBaseModel.getStaleData() != null) {
            staleData = pointBaseModel.getStaleData().overwriteWith(staleData);
        }
        saveStaleData(staleData);
        
        buildPointBaseModel(pointBase, pointBaseModel, staleData);
        return pointBaseModel;
    }

    @Override
    public PointBaseModel<? extends PointBase> retrieve(int pointId) {

        PointBase pointBase = pointDao.get(pointId);
        StaleData staleData = getStaleData(pointId);

        PointType ptType = PointType.getForString(pointBase.getPoint().getPointType());
        PointBaseModel pointBaseModel = PointModelFactory.getModel(ptType); 

        if (pointBaseModel != null) {
            buildPointBaseModel(pointBase, pointBaseModel, staleData);
        }
        return pointBaseModel;
    }

    @Override
    public PointBaseModel<? extends PointBase> copy(int pointId, PointCopy pointCopy) {
        PointBase pointBase = pointDao.get(pointId);
        pointBase.setPointID(null);
        pointCopy.buildDBPersistent(pointBase);
        dBPersistentDao.performDBChange(pointBase, TransactionType.INSERT);

        PointType ptType = pointBase.getPoint().getPointTypeEnum();
        PointBaseModel pointBaseModel = PointModelFactory.getModel(ptType);

        // copy the StaleData
        StaleData newStaleData = populateStaleDataObjectToCopy(pointId, pointBase.getPoint().getPointID());
        saveStaleData(newStaleData);

        buildPointBaseModel(pointBase, pointBaseModel, newStaleData);
        return pointBaseModel;
    }

    @Override
    public PaoPointModel getDevicePointDetail(int paoId, DevicePointsFilter devicePointsFilter, Direction direction,
            SortBy sortBy, PagingParameters paging) {
        List<PointInfoModel> listOfPointInfoModel = new ArrayList<>();
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        if (pao == null) {
            throw new NotFoundException("Pao Id not found " + paoId);
        }

        SearchResults<DevicePointDetail> devicePointDetails = devicePointDao.getDevicePointDetail(List.of(paoId),
                                                              devicePointsFilter.getPointNames(), devicePointsFilter.getTypes(), direction, sortBy, paging);

        for (DevicePointDetail devicePointDetail : devicePointDetails.getResultList()) {
            Set<BuiltInAttribute> attributes = paoDefinitionDao.findAttributeForPaoTypeAndPoint(PaoTypePointIdentifier.of(pao.getPaoType(),
                                               devicePointDetail.getPaoPointIdentifier().getPointIdentifier()));
            listOfPointInfoModel.add(PointInfoModel.of(devicePointDetail, attributes));
        }
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, pao.getPaoType());
        return PaoPointModel.of(paoIdentifier, listOfPointInfoModel);
    }

    /**
     *  Build Ordered Alarm Table Entries.
     */
    private List<AlarmTableEntry> buildOrderedAlarmTable(List<AlarmTableEntry> entries, PointType pointType) {
        List<AlarmTableEntry> orderedAlarmTableEntries = new ArrayList<>();

        List<String> alarmStates = Arrays.asList(IAlarmDefs.OTHER_ALARM_STATES);
        if (pointType != null && (pointType == PointType.CalcStatus || pointType == PointType.Status)) {
            alarmStates = new ArrayList<String>(Arrays.asList(IAlarmDefs.STATUS_ALARM_STATES));
            // Maintain Order of Alarm States which is not defined in IAlarmDefs.
            List<String> rawAlarmStates = entries.stream()
                                                 .filter(entry -> !(Arrays.asList(IAlarmDefs.STATUS_ALARM_STATES).contains(entry.getCondition())))
                                                 .map(entry -> entry.getCondition())
                                                 .sorted()
                                                 .collect(Collectors.toList());
            alarmStates.addAll(rawAlarmStates);
        }

        // Iterate over all alarm state entries to maintain order and set default values if category and notify are null.
        for (String alarmState : alarmStates) {
            AlarmTableEntry entry = entries.stream()
                                           .filter(e -> e.getCondition().equals(alarmState))
                                           .findFirst()
                                           .orElse(new AlarmTableEntry(alarmState));
            orderedAlarmTableEntries.add(setDefaultsForAlarmEntry(entry));
        }
        return orderedAlarmTableEntries;
    }

    /**
     * Set default values for AlarmTableEntry if category or notify are null.
     */
    private AlarmTableEntry setDefaultsForAlarmEntry(AlarmTableEntry entry) {
        if (entry.getCategory() == null) {
            entry.setCategory(CtiUtilities.STRING_NONE);
        }

        if (entry.getNotify() == null) {
            entry.setNotify(AlarmNotificationTypes.NONE);
        }
        return entry;
    }

    private void buildPointBaseModel(PointBase pointBase, PointBaseModel pointBaseModel, StaleData staleData) {
        pointBaseModel.buildModel(pointBase);
        pointBaseModel.setStaleData(staleData);
        pointBaseModel.getAlarming().setAlarmTableList(getAlarmTableEntries(pointBase, true));
    }
    

    /**
     * Update existing alarm table entries with new Entries. In the case of Status/Calc Status PointType, Some Condition
     * fields (Other than Non-updated, Abnormal, Uncommanded State Change,Command Failure,Stale) are dependent on State
     * Group in Alarming functionality.
     * If we will change the state group, condition fields will be changed but values
     * of (Other than Non-updated, Abnormal, Uncommanded State Change,Command Failure,Stale) unchanged in edit/update
     * operation.
     */
    private List<AlarmTableEntry> updateExistingAlarmTableEntries(PointBase pointBase, PointBaseModel<?> pointBaseModel, PointType ptType) {

        List<AlarmTableEntry> existingEntries = getAlarmTableEntries(pointBase, true);
        List<AlarmTableEntry> newEntries = pointBaseModel.getAlarming().getAlarmTableList();

        boolean isStateGroupChanged = pointBaseModel.getStateGroupId() != null ? pointBaseModel.getStateGroupId() != pointBase.getPoint().getStateGroupID()
                : false;

        Map<String, AlarmTableEntry> newEntryMap = newEntries.stream().collect(Collectors.toMap(e -> e.getCondition(), e -> e));

        // Filter Status Alarm States if State Group has changed.
        if (ptType != null && (ptType == PointType.CalcStatus || ptType == PointType.Status) && isStateGroupChanged) {
            existingEntries = existingEntries.stream()
                                             .filter(entry -> (Arrays.asList(IAlarmDefs.STATUS_ALARM_STATES).contains(entry.getCondition())))
                                             .collect(Collectors.toList());
        }

        // Update existing AlarmTableEntry based on the new entries.
        for (AlarmTableEntry entry : existingEntries) {
            if (newEntryMap.get(entry.getCondition()) != null) {
                if (newEntryMap.get(entry.getCondition()).getCategory() != null) {
                    entry.setCategory(newEntryMap.get(entry.getCondition()).getCategory());
                }
                if (newEntryMap.get(entry.getCondition()).getNotify() != null) {
                    entry.setNotify(newEntryMap.get(entry.getCondition()).getNotify());
                }
            }
        }
        // Add new Status Alarm States and also set Default value if State Group has changed (Which is not defined in IAlarmDefs).
        if (ptType != null && (ptType == PointType.CalcStatus || ptType == PointType.Status) && isStateGroupChanged) {
            List<AlarmTableEntry> rawAlarmStates = newEntries.stream()
                                                             .filter(entry -> !(Arrays.asList(IAlarmDefs.STATUS_ALARM_STATES).contains(entry.getCondition())))
                                                             .map(entry -> setDefaultsForAlarmEntry(entry))
                                                             .sorted()
                                                             .collect(Collectors.toList());
            existingEntries.addAll(rawAlarmStates);
        }

        return existingEntries;
    }

    @Override
    public List<LMDto> retrieveStates(int pointId) {
        List<LMDto> lmDtoList = new ArrayList<>();
        List<LiteState> states = stateGroupDao.getStateList(pointId);
        for (LiteState state : states) {
            lmDtoList.add(new LMDto(state.getLiteID(), state.getStateText()));
        }
        return lmDtoList;
    }

}
