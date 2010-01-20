package com.cannontech.services.points;

import java.util.Date;
import java.util.Map;

import org.springframework.integration.annotation.Transformer;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;

public class MapToRichPointDataTransformer {
    @Transformer
    public RichPointData transform(Map<String, Object> map) {
        Integer paoId = (Integer) map.get("paoId");
        String paoTypeStr = (String) map.get("paoType");
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, PaoType.valueOf(paoTypeStr));
        
        Integer pointOffset = (Integer) map.get("pointOffset");
        Integer pointTypeId = (Integer) map.get("pointTypeId");
        PointIdentifier pointIdentifier = new PointIdentifier(pointTypeId, pointOffset);
        PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, pointIdentifier);
        
        PointValueBuilder builder = PointValueBuilder.create();
        builder.withType(pointTypeId);
        Integer pointId = (Integer) map.get("pointId");
        builder.withPointId(pointId);
        Double pointValue = (Double) map.get("pointValue");
        builder.withValue(pointValue);
        String pointQualityStr = (String) map.get("pointQuality");
        builder.withPointQuality(PointQuality.valueOf(pointQualityStr));
        Long pointTimeStampJavaMs = (Long) map.get("pointTimeStampJavaMs");
        builder.withTimeStamp(new Date(pointTimeStampJavaMs));
        PointValueQualityHolder build = builder.build();
        
        RichPointData result = new RichPointData(build, paoPointIdentifier);
        
        return result;
    }
}
