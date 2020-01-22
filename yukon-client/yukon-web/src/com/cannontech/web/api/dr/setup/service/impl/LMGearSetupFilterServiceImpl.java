package com.cannontech.web.api.dr.setup.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFieldsBuilder;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
    private static final String baseKey = "yukon.web.modules.dr.setup.gear.";

    @Override
    public SearchResults<GearFilteredResult> filter(FilterCriteria<LMSetupFilter> filterCriteria, YukonUserContext userContext) {

        List<LMProgramDirectGear> directGears = setupDao.getDetails(filterCriteria);
        List<GearFilteredResult> filteredResultList = new ArrayList<>();

        for (LMProgramDirectGear directGear : directGears) {
            GearFilteredResult result = new GearFilteredResult();

            result.setControlMethod(directGear.getControlMethod());
            result.setGearId(directGear.getGearId());
            result.setGearName(directGear.getGearName());
            result.setGearNumber(directGear.getGearNumber());

            ProgramGearFields fields = gearFieldsBuilder.getProgramGearFields(directGear);
            if (fields != null) {
                result.setGearDetails(buildGearDetailsInString(fields, userContext));
            }

            LMDto program = new LMDto();
            LiteYukonPAObject pao = cache.getAllPaosMap().get(directGear.getProgramId());
            program.setId(directGear.getProgramId());
            program.setName(pao.getPaoName());
            result.setLoadProgram(program);

            filteredResultList.add(result);
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
     * Build comma separated fieldName/Value pair for fields.
     */
    private String buildGearDetailsInString(ProgramGearFields fields, YukonUserContext userContext) {
        Map<String, String> fieldValueMap = new LinkedHashMap<>();
        List<String> stringValueTypeFields = new ArrayList<>();
        objectToStringFields("", new ObjectMapper().valueToTree(fields), fieldValueMap, stringValueTypeFields);
        String fieldsString = fieldValueMap.entrySet()
                                           .stream()
                                           // TODO exclude fields based on Jess feedback.
                                           .map(entry -> getField(entry.getKey(), entry.getValue(), userContext, stringValueTypeFields))
                                           .collect(Collectors.joining(", "));

        return fieldsString;

    }

    /**
     * Build fieldName/Value based on i18n.
     */
    private String getField(String key, String value, YukonUserContext userContext, List<String> stringValueTypeFields) {

        String fieldValue = null;
        String fieldName = getFieldName(userContext, key);

        if (stringValueTypeFields.contains(value)) {
            fieldValue = getFieldValue(userContext, value);
        } else {
            if (Boolean.TRUE.toString().equals(value) || Boolean.FALSE.toString().equals(value)) {
                fieldValue = getBooleanField(userContext, value);
            } else {
                fieldValue = value;
            }
        }
        
        //TODO Idenity time fields and display with units ?

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
    private String getFieldName(YukonUserContext userContext, String fieldPath) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String fieldName = accessor.getMessage(new YukonMessageSourceResolvable(baseKey + fieldPath));

        return "<b>" + fieldName + "</b>";

    }

    /**
     * Get FieldValue on fieldPath.
     */
    private String getFieldValue(YukonUserContext userContext, String fieldPath) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String fieldValue = accessor.getMessage(new YukonMessageSourceResolvable(baseKey + fieldPath));

        return fieldValue;

    }

    /**
     * Get Boolean FieldValue based on fieldPath.
     */
    private String getBooleanField(YukonUserContext userContext, String currentPath) {

        String fieldValue = null;
        if (Boolean.TRUE.toString().equals(currentPath)) {
            fieldValue = getFieldValue(userContext, "yes");
        } else if (Boolean.FALSE.toString().equals(currentPath)) {
            fieldValue = getFieldValue(userContext, "no");
        }
        return fieldValue;

    }

}
