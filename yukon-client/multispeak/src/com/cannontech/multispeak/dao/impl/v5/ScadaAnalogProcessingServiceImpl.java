package com.cannontech.multispeak.dao.impl.v5;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.msp.beans.v5.commontypes.SingleIdentifier;
import com.cannontech.msp.beans.v5.enumerations.QualityDescription;
import com.cannontech.msp.beans.v5.enumerations.QualityDescriptionKind;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.msp.beans.v5.multispeak.Value;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.ScadaAnalogProcessingService;

public class ScadaAnalogProcessingServiceImpl implements ScadaAnalogProcessingService {

    @Override
    public SCADAAnalog createScadaAnalog(LiteYukonPAObject litePAObject, LitePoint litePoint, PointValueQualityHolder pointValue) {
        
        SCADAAnalog scadaAnalog = new SCADAAnalog();
        // objectId identifier is limited to 52 characters by customer system, so take first 
        // 50 characters of pao name plus '.#' where # is the point offset, 1-4.
        String paoName = litePAObject.getPaoName();
        String objectId = StringUtils.left(paoName,  50) + "."+ litePoint.getPointOffset();
        SingleIdentifier identifier = new SingleIdentifier();
        identifier.setValue(objectId);
        scadaAnalog.setPrimaryIdentifier(identifier);

        String comments = "ProgramName: " + paoName + "; PointName: " + litePoint.getPointName();
        scadaAnalog.setComments(comments);

        scadaAnalog.setQuality(getQualityForPointValue(pointValue));

        Calendar cal = new GregorianCalendar();
        cal.setTime(pointValue.getPointDataTimeStamp());
        scadaAnalog.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(cal));
        Value value = new Value();
        value.setValue(new Float(pointValue.getValue()));
        scadaAnalog.setValue(value );
        return scadaAnalog;
    }
    
    /**
     * Convert Yukon point quality to a MultiSpeak QualityDescription.
     * Non-Update -> Failed
     * Else -> Measured ("normal")
     */
    private QualityDescription getQualityForPointValue(PointValueQualityHolder pointValue) {
        QualityDescription qualityDescription = new QualityDescription();
        if (pointValue.getPointQuality() == PointQuality.NonUpdated) {
            qualityDescription.setValue(QualityDescriptionKind.FAILED);
        } 
        qualityDescription.setValue(QualityDescriptionKind.MEASURED); // Corresponds to PointQuality.NORMAL.
        return qualityDescription;  
    }
}