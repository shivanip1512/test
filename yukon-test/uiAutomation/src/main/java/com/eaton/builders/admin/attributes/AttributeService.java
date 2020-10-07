package com.eaton.builders.admin.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

public class AttributeService {

    public static Pair<JSONObject, JSONObject> buildAndCreateAttribute() {
        return new AttributesCreateBuilder.Builder(Optional.empty())
                .create();
    }
    
    public static Pair<JSONObject, JSONObject> buildAndCreateAttributeAssignment(Integer attributeId, Optional<AttributeAsgmtTypes.PaoTypes> paoType, Optional<AttributeAsgmtTypes.PointTypes> pointType) {
        return new AttributeAsgmtCreateBuilder.Builder(attributeId)
                .withPaoType(paoType)
                .withPointType(pointType)
                .withMultiplier(Optional.empty())
                .create();
    }
    
    public Map<String, Pair<JSONObject, JSONObject>> buildAndCreateAttributeWithCalcAnalogPointTypeAssignments(List<AttributeAsgmtTypes.PaoTypes> paoTypes) {
        HashMap<String, Pair<JSONObject, JSONObject>> map = new HashMap<>();

        Pair<JSONObject, JSONObject> attributePair = buildAndCreateAttribute();

        JSONObject response = attributePair.getValue1();
        Integer attributeId = response.getInt("id");
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
}
