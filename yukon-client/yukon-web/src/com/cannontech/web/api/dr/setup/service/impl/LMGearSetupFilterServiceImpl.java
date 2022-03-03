package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.common.dr.gear.setup.Setpoint;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFieldsBuilder;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.dao.impl.LMGearSetupDaoImpl;
import com.cannontech.web.api.dr.setup.model.GearFilteredResult;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class LMGearSetupFilterServiceImpl implements LMSetupFilterService<GearFilteredResult> {
    @Autowired private LMGearSetupDaoImpl setupDao;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private ProgramGearFieldsBuilder gearFieldsBuilder;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DurationFormattingService durationFormattingService;
    @Autowired private DateFormattingService dateFormattingService;

    private static final String baseKey = "yukon.web.modules.dr.setup.gear.";
    private static final String rampInFieldName = "rampIn";
    private static final String setpointFieldName = "setpoint";
    private static final String refreshShedType = "refreshShedType";
    private static final String shedTime = "shedTime";
    private static final String modeFieldName = "mode";
    private static final String maxShedTime = "maxShedTime";
    private static final String fixedShedTime = "fixedShedTime";

    private static Set<String> excludeGearFields = Set.of("groupSelectionMethod", "stopCommandRepeat", "capacityReduction", "numberOfGroups");

    private static Set<String> gearFieldsInDHMSFormat = Set.of("sendRate", "shedTime");
    private static Set<String> gearFieldsInSecondsFormat = Set.of("rampInIntervalInSeconds", "rampOutIntervalInSeconds");
    private static Set<String> gearFieldsInMinutesFormat = Set.of("whenToChangeFields.changeDurationInMinutes");
    private static Set<String> gearFieldsInPercentFormat = Set.of("controlPercent", "rampInPercent", "rampOutPercent", "dutyCyclePercent");
    private static Set<String> gearFieldsInFahrenheitFormat = Set.of("setpointOffset");
    private static Set<String> noneSupportedGearFields = Set.of("sendRate", "maxCycleCount");

    private static Set<String> gearDateFields = Set.of("preOpTimeInMinutes",
                                                       "preOpHoldInMinutes",
                                                       "maxRuntimeInMinutes",
                                                       "randomStartTimeInMinutes",
                                                       "rampOutTimeInMinutes");

    private static Set<String> gearRampInFields = Set.of("rampInPercent", "rampInIntervalInSeconds");

    private static Map<String, String> gearAbsFields = Map.of("valueB", "absB", "valueD" , "absD", "valueF" ,"absF");
    private static Map<String, String> gearDeltaFields = Map.of("valueB", "deltaB", "valueD" , "deltaD", "valueF" ,"deltaF");
    private static Map<String, String> gearHeatingOffsetFields = Map.of("offset", "heatingOffset");
    private static Map<String, String> gearCoolingOffsetFields = Map.of("offset", "coolingOffset");

    @Override
    public SearchResults<GearFilteredResult> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {

        List<LMProgramDirectGear> directGears = setupDao.getDetails(filterCriteria);

        List<GearFilteredResult> filteredResultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(directGears)) {

            for (LMProgramDirectGear directGear : directGears) {
                GearFilteredResult result = new GearFilteredResult();

                result.setControlMethod(directGear.getControlMethod());
                result.setGearId(directGear.getGearId());
                result.setGearName(directGear.getGearName());
                result.setGearNumber(directGear.getGearNumber());

                ProgramGearFields fields = gearFieldsBuilder.getProgramGearFields(directGear);
                if (fields != null) {
                    result.setGearDetails(buildGearDetailsInString(fields, directGear.getControlMethod(), userContext));
                }

                LMDto program = new LMDto();
                LiteYukonPAObject pao = cache.getAllPaosMap().get(directGear.getProgramId());
                program.setId(directGear.getProgramId());
                program.setName(pao.getPaoName());
                result.setLoadProgram(program);

                filteredResultList.add(result);
            }
        }
        int totalHitCount = setupDao.getTotalCount(filterCriteria);
        SearchResults<GearFilteredResult> searchResults = SearchResults.pageBasedForSublist(filteredResultList,
                                                                                            filterCriteria.getPagingParameters(),
                                                                                            totalHitCount);
        return searchResults;
    }

    @Override
    public LmSetupFilterType getFilterType() {
        return LmSetupFilterType.GEAR;
    }

    /**
     * Build comma separated fieldName/Value pair for fields. Example : (Command Resend Rate: 1 hour, Control Percent:
     * 50%, Cycle Count Send Type: Fixed Count, Cycle Period: 30, How To Stop Control: Stop Cycle, Max Cycle Count:
     * None, No Ramp (Expresscom only): No, Starting Period Count: 8, When to Change: Manually Only)
     */
    private String buildGearDetailsInString(ProgramGearFields fields, GearControlMethod controlMethod, YukonUserContext userContext) {
        Map<String, String> fieldValueMap = new LinkedHashMap<>();
        List<String> stringValueTypeFields = new ArrayList<>();
        objectToStringFields("", new ObjectMapper().valueToTree(fields), fieldValueMap, stringValueTypeFields);
        addRampInField(controlMethod, userContext, fieldValueMap);

        String fieldsString = fieldValueMap.entrySet()
                                           .stream()
                                           .filter(entry -> !excludeGearFields.contains(entry.getKey()))
                                           .map(entry -> getField(controlMethod, entry.getKey(), entry.getValue(), userContext, stringValueTypeFields, fieldValueMap))
                                           .collect(Collectors.joining(", "));

        return fieldsString;

    }

    /**
     * Build fieldName/Value based on i18n.
     */
    private String getField(GearControlMethod controlMethod, String key, String value, YukonUserContext userContext, List<String> stringValueTypeFields, Map<String, String> fieldValueMap) {

        String fieldValue = null;
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        String fieldName = getFieldName(controlMethod, accessor, key, fieldValueMap);

        if (stringValueTypeFields.contains(value)) {
            fieldValue = getFieldValue(accessor, value);
        } else if (gearFieldsInDHMSFormat.contains(key)) {
            fieldValue = getFieldValueWithTimeUnit(value, DurationFormat.DHMS_REDUCED, TimeUnit.SECONDS, userContext);
        }  else if (gearFieldsInSecondsFormat.contains(key)) {
            fieldValue = getFieldValueWithTimeUnit(value, DurationFormat.S, TimeUnit.SECONDS, userContext);
        } else if (gearFieldsInMinutesFormat.contains(key)) {
            fieldValue = getFieldValueWithTimeUnit(value, DurationFormat.M, TimeUnit.MINUTES, userContext);
        } else if (gearDateFields.contains(key)) {
            fieldValue = getFieldValueWithFormattedDate(value, userContext);
        } else if (gearFieldsInPercentFormat.contains(key)) {
            fieldValue = getFieldValueInPercentage(value, accessor);
        } else if (gearFieldsInFahrenheitFormat.contains(key)) {
            fieldValue = getFieldValueInFahrenheit(value, accessor);
        }  else if (Boolean.TRUE.toString().equals(value) || Boolean.FALSE.toString().equals(value)) {
            fieldValue = getBooleanField(accessor, value);
        } else {
            fieldValue = value;
        }

        if (noneSupportedGearFields.contains(key)) {
            if (StringUtils.isNumeric(value) && Integer.valueOf(value) == 0) {
                fieldValue = accessor.getMessage("yukon.common.none");
            }
        }

        return fieldName + ": " + fieldValue;

    }

    /**
     * Convert JsonTree structure to String fields Map.
     */
    private void objectToStringFields(String fieldPath, JsonNode jsonNode, Map<String, String> fieldValueMap, List<String> stringValueTypeFields) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;

            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = fieldPath.isEmpty() ? "" : fieldPath + ".";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                objectToStringFields(pathPrefix + entry.getKey(), entry.getValue(), fieldValueMap, stringValueTypeFields);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                objectToStringFields(fieldPath + "[" + i + "]", arrayNode.get(i), fieldValueMap, stringValueTypeFields);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            fieldValueMap.put(fieldPath, valueNode.asText());
            if (jsonNode.isTextual()) {
                stringValueTypeFields.add(valueNode.asText());
            }
        }
    }

    /**
     * Get FieldName based on fieldPath.
     */
    private String getFieldName(GearControlMethod controlMethod, MessageSourceAccessor accessor, String fieldPath, Map<String, String> fieldValueMap) {

        if (controlMethod == GearControlMethod.ThermostatRamping || controlMethod == GearControlMethod.SepTemperatureOffset) {
            fieldPath = getFieldName(controlMethod, fieldPath, fieldValueMap);
        } else if (controlMethod == GearControlMethod.TimeRefresh && StringUtils.equals(fieldPath, shedTime)) {
            CycleCountSendType cycleCountSendType = CycleCountSendType.valueOf(fieldValueMap.get(refreshShedType));
            if (cycleCountSendType == CycleCountSendType.DynamicShedTime) {
                fieldPath = maxShedTime;
            } else {
                fieldPath = fixedShedTime;
            }
        }
        String fieldName = accessor.getMessage(new YukonMessageSourceResolvable(baseKey + fieldPath));
        return "<b>" + fieldName + "</b>";

    }

    /**
     * Get FieldValue on fieldPath.
     */
    private String getFieldValue(MessageSourceAccessor accessor , String value) {
        String fieldValue = accessor.getMessage(new YukonMessageSourceResolvable(baseKey + value));
        return fieldValue;
    }

    /**
     * Get Boolean FieldValue based on fieldPath.
     */
    private String getBooleanField(MessageSourceAccessor accessor, String currentPath) {

        String fieldValue = null;
        if (Boolean.TRUE.toString().equals(currentPath)) {
            fieldValue = getFieldValue(accessor, "yes");
        } else if (Boolean.FALSE.toString().equals(currentPath)) {
            fieldValue = getFieldValue(accessor, "no");
        }
        return fieldValue;

    }

    /**
     * Get fieldValue with time unit (Example - 8 Mintues or 10 Seconds Based on durationFormat).
     */
    private String getFieldValueWithTimeUnit(String value, DurationFormat durationFormat, TimeUnit unit, YukonUserContext userContext) {
        String formattedDuration = durationFormattingService.formatDuration(Long.parseLong(value), unit, durationFormat, userContext);
        return formattedDuration;
    }

    /**
     * Get fieldValue in Date format(Example - 08:00).
     */
    private String getFieldValueWithFormattedDate(String value, YukonUserContext userContext) {
        String formattedDate = dateFormattingService.format(Long.parseLong(value), DateFormatEnum.TIME_OFFSET, userContext);
        return formattedDate;
    }

    /**
     * Get fieldValue in percentage format(Example - 5%).
     */
    private String getFieldValueInPercentage(String value, MessageSourceAccessor accessor) {
        return accessor.getMessage("yukon.common.percent", value);
    }

    /**
     * Add new RampIn field in gearDetails for TimeRefresh/MasterCycle. This field is generated based on
     * rampInPercent/rampOutPercent
     */
    private void addRampInField(GearControlMethod controlMethod, YukonUserContext userContext, Map<String, String> fieldValueMap) {
        if (controlMethod == GearControlMethod.TimeRefresh || controlMethod == GearControlMethod.MasterCycle) {
            boolean isRampInField = fieldValueMap.keySet().stream().anyMatch(key -> gearRampInFields.contains(key));
            if (isRampInField) {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String fieldValue = accessor.getMessage(new YukonMessageSourceResolvable(baseKey + "yes"));
                fieldValueMap.put(rampInFieldName, fieldValue);
            } else {
                MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
                String fieldValue = accessor.getMessage(new YukonMessageSourceResolvable(baseKey + "no"));
                fieldValueMap.put(rampInFieldName, fieldValue);
            }
        }
    }

    /**
     * Get fieldValue in Fahrenheit format(Example - 5°F).
     */
    private String getFieldValueInFahrenheit(String value, MessageSourceAccessor accessor) {
        return accessor.getMessage("yukon.common.fahrenheitWithValue", value);
    }

    /**
     * Get fieldName for Thermostat Ramping/SEP Temperature Offset (Example Heating Offset: 0.0 , Cooling Offset: 0.0
     * Abs B: 0, Abs D: 0, Abs F: 0, Delta B: 0, Delta D: 0, Delta F: 0)
     */

    private String getFieldName(GearControlMethod controlMethod, String fieldPath, Map<String, String> fieldValueMap) {
        if (controlMethod == GearControlMethod.ThermostatRamping) {
            String setpoint = fieldValueMap.get(setpointFieldName);
            if (setpoint != null) {
                if (setpoint.equalsIgnoreCase(Setpoint.ABSOLUTE.name()) && gearAbsFields.containsKey(fieldPath)) {
                    fieldPath = gearAbsFields.get(fieldPath);
                }
                if (setpoint.equalsIgnoreCase(Setpoint.DELTA.name()) && gearDeltaFields.containsKey(fieldPath)) {
                    fieldPath = gearDeltaFields.get(fieldPath);
                }
            }
        }

        if (controlMethod == GearControlMethod.SepTemperatureOffset) {
            String mode = fieldValueMap.get(modeFieldName);
            if (mode != null) {
                if (mode.equalsIgnoreCase(Mode.COOL.name()) && gearCoolingOffsetFields.containsKey(fieldPath)) {
                    fieldPath = gearCoolingOffsetFields.get(fieldPath);
                }
                if (mode.equalsIgnoreCase(Mode.HEAT.name()) && gearHeatingOffsetFields.containsKey(fieldPath)) {
                    fieldPath = gearHeatingOffsetFields.get(fieldPath);
                }
            }
        }
        return fieldPath;
    }

}
