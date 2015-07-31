package com.cannontech.web.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.models.PointModel;
import com.cannontech.web.capcontrol.service.PointEditorService;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.google.common.collect.ImmutableList;

@Service
public class PointEditorServiceImpl implements PointEditorService {
    
    @Autowired AlarmCatDao alarmCatDao;
    @Autowired DbChangeManager dbChangeManager;
    @Autowired DBPersistentDao dBPersistentDao;
    @Autowired PointDao pointDao;
    @Autowired PointPropertyValueDao pointPropertyValueDao;
    @Autowired StateDao stateDao;
    
    @Override
    public PointModel getModelForId(int id) {
        
        PointBase base = pointDao.get(id);
        
        StaleData staleData = getStaleData(id);
        
        List<AlarmTableEntry> alarmTableEntries = getAlarmTableEntries(base);

        PointModel model = new PointModel<PointBase>(base, staleData, alarmTableEntries);
        
        return model;
    }
    
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
    
    private List<AlarmTableEntry> getAlarmTableEntries(PointBase pointBase) {
        
        int ptType = PointTypes.getType(pointBase.getPoint().getPointType());

        ArrayList<AlarmTableEntry> notifEntries = new ArrayList<>();
        List<LiteAlarmCategory> allAlarmStates = DefaultDatabaseCache.getInstance().getAllAlarmCategories();
        
        // be sure we have a 32 character string
        String alarmStates = pointBase.getPointAlarming().getAlarmStates().length() != PointAlarming.ALARM_STATE_COUNT ?
            PointAlarming.DEFAULT_ALARM_STATES : 
            pointBase.getPointAlarming().getAlarmStates();

        String excludeNotifyStates = pointBase.getPointAlarming().getExcludeNotifyStates();

        // this drives what list of strings we will put into our table
        String[] alarm_cats = IAlarmDefs.OTHER_ALARM_STATES;
        if (ptType == PointTypes.STATUS_POINT || ptType == PointTypes.CALCULATED_STATUS_POINT) {
            alarm_cats = IAlarmDefs.STATUS_ALARM_STATES;
        }
        LiteStateGroup stateGroup = stateDao.getLiteStateGroup(pointBase.getPoint().getStateGroupID());

        String[] stateNames = new String[stateGroup.getStatesList().size()];
        for (int j = 0; j < stateGroup.getStatesList().size(); j++) {
            stateNames[j] = stateGroup.getStatesList().get(j).toString();
        }
        // insert all the predefined states into the table
        int i = 0;
        for (i = 0; i < alarm_cats.length; i++) {
            AlarmTableEntry entry = new AlarmTableEntry();
            setAlarmGenNotif(entry, alarmStates.charAt(i), allAlarmStates, excludeNotifyStates.toUpperCase().charAt(i));

            entry.setCondition(alarm_cats[i]);
            notifEntries.add(entry);
        }

        if (ptType == PointTypes.STATUS_POINT || ptType == PointTypes.CALCULATED_STATUS_POINT) {

            for (int j = 0; j < stateNames.length; j++, i++) {
                if (i >= alarmStates.length()) {
                    throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates[" + i + "] while alarmStates.length()==" + alarmStates.length() + 
                        ", too many states for Status point "+ pointBase.getPoint().getPointName() + " defined.");
                }
                AlarmTableEntry entry = new AlarmTableEntry();
                setAlarmGenNotif(entry, alarmStates.charAt(i), allAlarmStates, excludeNotifyStates.toUpperCase().charAt(i));

                entry.setCondition(stateNames[j]);
                notifEntries.add(entry);
            }
        }
        return notifEntries;
    }
    
    private static void setAlarmGenNotif(AlarmTableEntry entry, int alarmStateId, List<LiteAlarmCategory> allAlarmStates, char gen) {

        if ((alarmStateId - 1) < allAlarmStates.size()) {
            entry.setGenerate(allAlarmStates.get((alarmStateId - 1)).getCategoryName());
        } else {
            entry.setGenerate(allAlarmStates.get(0).getCategoryName());
        }
        entry.setExcludeNotify(PointAlarming.getExcludeNotifyString(gen));
    }
    
    @Override
    public int save(PointModel model) {
        
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
        
        return pointId;
    }
    
    private void saveStaleData(int pointId, StaleData staleData) {
        
        PointPropertyValue timeProperty = new PointPropertyValue(pointId, StaleData.TIME_PROPERTY, staleData.getTime());
        PointPropertyValue updateProperty = new PointPropertyValue(pointId, StaleData.UPDATE_PROPERTY, staleData.getUpdateStyle());
        
        pointPropertyValueDao.remove(timeProperty);
        pointPropertyValueDao.remove(updateProperty);

        if (staleData.isEnabled()) {
            pointPropertyValueDao.add(timeProperty);
            pointPropertyValueDao.add(updateProperty);
        }
    }
    
    private void attachAlarms(PointBase pointBase, List<AlarmTableEntry> alarmTableEntries) {
        
        String alarmStates = "";
        String exclNotify = "";

        for (AlarmTableEntry entry : alarmTableEntries) {
            alarmStates += (char) alarmCatDao.getAlarmCategoryIdFromCache(entry.getGenerate());
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
    public boolean delete(int id) {
        AttachmentStatus attachmentStatus = getAttachmentStatus(id);
        if (!attachmentStatus.isDeletable()) return false;
        
        PointBase point = pointDao.get(id);
        dBPersistentDao.performDBChange(point, TransactionType.DELETE);
        
        DBChangeMsg dbChange = new DBChangeMsg(
            id,
            DBChangeMsg.CHANGE_POINT_DB,
            DBChangeMsg.CAT_POINT,
            point.getPoint().getPointType(),
            DbChangeType.DELETE);
        
        dbChangeManager.processDbChange(dbChange);
        
        return true;
    }

}
