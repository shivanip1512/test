package com.cannontech.services.points;

import java.util.Map;

import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;

import com.cannontech.core.dynamic.RichPointData;
import com.google.common.collect.Maps;

public class RichPointDataToMapTransformer {
    @Transformer
    public Message<Map<String, Object>> transform(RichPointData richPointData) {
        Map<String, Object> result = Maps.newHashMap();
        
        result.put("paoId", richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId());
        result.put("paoType", richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoType().name());
        result.put("paoCategory", richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoType().getPaoCategory().name());
        
        result.put("pointOffset", richPointData.getPaoPointIdentifier().getPointIdentifier().getOffset());
        result.put("pointTypeId", richPointData.getPaoPointIdentifier().getPointIdentifier().getPointType().getPointTypeId());
        
        result.put("pointId", richPointData.getPointValue().getId());
        result.put("pointValue", richPointData.getPointValue().getValue());
        result.put("pointQuality", richPointData.getPointValue().getPointQuality().name());
        result.put("pointTimeStampJavaMs", richPointData.getPointValue().getPointDataTimeStamp().getTime());
        
        // this method cannot simply return a Map because Spring handles that as a 
        // special case, instead we'll create an explicit Message object with our payload
        MessageBuilder<Map<String, Object>> builder = MessageBuilder.withPayload(result);
        
        return builder.build();
    }
}
