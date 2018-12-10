package com.cannontech.web.tools.points.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.events.loggers.PointEventLogService;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.LitePointModel;
import com.cannontech.web.tools.points.model.PointModel;
import com.cannontech.web.tools.points.service.PointEditorService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableList;

@Service
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

    protected static final Logger log = YukonLogManager.getLogger(PointEditorServiceImpl.class);

    @Override
    public PointModel getModelForId(int id) {
        
        PointBase base = pointDao.get(id);
        
        StaleData staleData = getStaleData(id);
        
        List<AlarmTableEntry> alarmTableEntries = getAlarmTableEntries(base);

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
    private StaleData getStaleData(int id) {
        
        StaleData staleData = new StaleData();
        
        try {
            PointPropertyValue timeProperty = pointPropertyValueDao.getByIdAndPropertyId(id, StaleData.TIME_PROPERTY);
            PointPropertyValue updateProperty = pointPropertyValueDao.getByIdAndPropertyId(id, StaleData.UPDATE_PROPERTY);

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
     */
    private List<AlarmTableEntry> getAlarmTableEntries(PointBase pointBase) {
        
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
        for (int j = 0; j < stateGroup.getStatesList().size(); j++) {
            stateNames[j] = stateGroup.getStatesList().get(j).toString();
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

                entry.setCondition(stateNames[j]);
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
        entry.setGenerate(liteAlarmCategory.getCategoryName());
        entry.setExcludeNotify(PointAlarming.getExcludeNotifyString(gen));
    }
    
    @Override
    public int save(PointModel model, YukonUserContext userContext) {
        
        PointBase base = model.getPointBase();
        Integer pointId = base.getPoint().getPointID();
        
        TransactionType type = TransactionType.UPDATE;
        if (pointId == null) {
            type = TransactionType.INSERT;
        }
        
        /* This one must be done BEFORE the base object */
        attachAlarms(base, model.getAlarmTableEntries());
        
        
        dBPersistentDao.performDBChange(base, type);
        
        pointId = base.getPoint().getPointID();
        
        DBChangeMsg dbChange = new DBChangeMsg(
            pointId,
            DBChangeMsg.CHANGE_POINT_DB,
            DBChangeMsg.CAT_POINT,
            base.getPoint().getPointType(),
            type == TransactionType.UPDATE ? DbChangeType.UPDATE : DbChangeType.ADD);

        dbChangeManager.processDbChange(dbChange);

        /* This one must be done AFTER for create */
        saveStaleData(pointId, model.getStaleData());
        LiteYukonPAObject pao = cache.getAllPaosMap().get(base.getPoint().getPaoID());
        eventLog.pointUpdated(pao.getPaoName(), base.getPoint().getPointName(), base.getPoint().getPointTypeEnum(),
            base.getPoint().getPointOffset(), userContext.getYukonUser());
        return pointId;
    }
    
    /**
     * Save the StaleData from the model object to the database
     */
    private void saveStaleData(int pointId, StaleData staleData) {
        
        if (staleData != null) {
            PointPropertyValue timeProperty = new PointPropertyValue(pointId, StaleData.TIME_PROPERTY, staleData.getTime());
            PointPropertyValue updateProperty = new PointPropertyValue(pointId, StaleData.UPDATE_PROPERTY, staleData.getUpdateStyle());
            
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
            alarmStates += (char) alarmCatDao.getAlarmCategoryId(entry.getGenerate());
            exclNotify += PointAlarming.getExcludeNotifyChar(entry.getExcludeNotify());
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
    public void delete(int id, YukonUserContext userContext) throws AttachedException {
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
        StaleData stateDataToCopy = getStaleData(pointModel.getPointId());
        StaleData newStaleData = populateStaleDataObjectToCopy(stateDataToCopy);
        saveStaleData(newPoint.getPoint().getPointID(), newStaleData);

        return newPoint.getPoint().getPointID();
    }
    
    private StaleData populateStaleDataObjectToCopy(StaleData stateDataToCopy) {
        StaleData staleData = new StaleData();
        staleData.setEnabled(stateDataToCopy.isEnabled());
        staleData.setTime(stateDataToCopy.getTime());
        staleData.setUpdateStyle(stateDataToCopy.getUpdateStyle());
        return staleData;
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

}
