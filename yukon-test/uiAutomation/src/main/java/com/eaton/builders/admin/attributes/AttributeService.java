package com.eaton.builders.admin.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class AttributeService {

    public static Pair<JSONObject, JSONObject> createAttribute(Optional<String> name) {
        return new AttributesCreateBuilder.Builder(name)
                .create();
    }
    
    private static Pair<JSONObject, JSONObject> createAttributeAssignment(Integer attributeId, Optional<AttributeAsgmtTypes.PaoTypes> paoType, Optional<AttributeAsgmtTypes.PointTypes> pointType) {
        return new AttributeAsgmtCreateBuilder.Builder(attributeId)
                .withPaoType(paoType)
                .withPointType(pointType)
                .withMultiplier(Optional.empty())
                .create();
    }    
    
    public Map<String, Pair<JSONObject, JSONObject>> createAttributeWithCalcAnalogPointTypeAssignments(List<AttributeAsgmtTypes.PaoTypes> paoTypes) {
        HashMap<String, Pair<JSONObject, JSONObject>> map = new HashMap<>();

        Pair<JSONObject, JSONObject> attributePair = createAttribute(Optional.empty());

        JSONObject response = attributePair.getValue1();
        Integer attributeId = response.getInt("customAttributeId");
        map.put("Attribute", attributePair);
        Integer index = 1;
        
        for (AttributeAsgmtTypes.PaoTypes type : paoTypes) {
            Pair<JSONObject, JSONObject> attributeAsgmtPair = new AttributeAsgmtCreateBuilder.Builder(attributeId)
                    .withPaoType(Optional.of(type))
                    .withPointType(Optional.of(AttributeAsgmtTypes.PointTypes.CALC_ANALOG))
                    .withMultiplier(Optional.empty())
                    .create();

            map.put("AttributeAsgmt" + index, attributeAsgmtPair);
            index++;
        }

        return map;
    }
    
    public static Map<String, Pair<JSONObject, JSONObject>> createAttributeWithAssignment(AttributeAsgmtTypes.PaoTypes paoType, AttributeAsgmtTypes.PointTypes pointType, Integer pointOffset, Optional<String> name) {
        HashMap<String, Pair<JSONObject, JSONObject>> map = new HashMap<>();
        Pair<JSONObject, JSONObject> attributePair = createAttribute(name);
        
        JSONObject response = attributePair.getValue1();
        Integer attributeId = response.getInt("customAttributeId");
        
        map.put("Attribute", attributePair);
        
        Pair<JSONObject, JSONObject> attributeAsgmtPair = new AttributeAsgmtCreateBuilder.Builder(attributeId)
                .withPaoType(Optional.of(paoType))
                .withPointType(Optional.of(pointType))
                .withMultiplier(Optional.of(pointOffset))
                .create();
        
        map.put("AttributeAsgmt", attributeAsgmtPair);
        
        return map;
    }
}
